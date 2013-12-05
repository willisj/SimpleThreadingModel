package workThreader;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WorkerThread<inType, outType> extends Thread {
	public ConcurrentLinkedQueue<inType> inQueue;
	public ConcurrentLinkedQueue<outType> outQueue;
	AtomicBoolean running = new AtomicBoolean(false);
	AtomicBoolean exitOnEmptyPool = new AtomicBoolean(false);
	AtomicBoolean waiting = new AtomicBoolean(false);

	public WorkerThread() {
		inQueue = new ConcurrentLinkedQueue<inType>();
		outQueue = new ConcurrentLinkedQueue<outType>();
	}

	public WorkerThread(inType[] prePopulate) {
		inQueue = new ConcurrentLinkedQueue<inType>();
		outQueue = new ConcurrentLinkedQueue<outType>();

		for (inType i : prePopulate)
			inQueue.add(i);
	}

	public void run() {
		inType x;
		outType y;
		running.set(true);
		beforeRunning();
		while (running.get()) {
			x = inQueue.poll();
			if (x != null) {
				waiting.set(false);
				y = work(x);
				if (y != null)
					outQueue.add(y);
			} else if (exitOnEmptyPool.get())
				break;
			else
				waiting.set(true);
		}
		beforeExit();
	}

	public void startBehaviourExitOnEmptyPool() {
		exitOnEmptyPool.set(true);
	}

	public void stopBehaviourExitOnEmptyPool() {
		exitOnEmptyPool.set(false);
	}

	public void stopProcessing() {
		running.set(false);
	}

	public boolean isWaiting() {
		return waiting.get();
	}

	public abstract outType work(inType jobObject);

	public abstract void beforeExit();

	public abstract void beforeRunning();

	public abstract WorkerThread<inType, outType> clone();
}
