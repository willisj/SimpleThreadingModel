package workThreader;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskDistributor<inType, outType, workerType> extends Thread {
	public ConcurrentLinkedQueue<inType> inQueue;
	public ConcurrentLinkedQueue<outType> outQueue;
	private HashMap<WorkerThread<inType, outType>, Thread> threadPool = new HashMap<WorkerThread<inType, outType>, Thread>();

	private final int maxThreads;
	private WorkerThread<inType, outType> threadTemplate;
	private AtomicBoolean exitOnEmpty = new AtomicBoolean(false);

	public TaskDistributor(int maxThreads,
			WorkerThread<inType, outType> threadTemplate) {
		this.maxThreads = maxThreads;
		this.threadTemplate = threadTemplate;
		inQueue = new ConcurrentLinkedQueue<inType>();
		outQueue = new ConcurrentLinkedQueue<outType>();
	}

	public void run() {

		inType x;
		WorkerThread<inType, outType> t;

		while (!(exitOnEmpty.get() && threadPool.size() == 0)) {
			// distribute a task
			x = inQueue.poll();
			if (x != null) {
				System.out.println("Distributing: " + x);
				t = getOptimalThread();
				if (t != null)
					t.inQueue.add(x);
				else
					System.out.println("Failed to get a thread");
			}

			removeTerminatedThreads();
			// take one output from each thread
			collectOneOutput();
		}
	}

	private void removeTerminatedThreads() {
		for (Entry<WorkerThread<inType, outType>, Thread> thread : threadPool
				.entrySet()) {
			if (thread.getValue().getState() == Thread.State.TERMINATED){
				System.out.println("Terminating "+thread.getValue().getName());
				collectAllOutput(thread.getKey());
				threadPool.remove(thread.getKey());
				
			}
		}

	}

	private void collectAllOutput(WorkerThread<inType, outType> thread) {
		outType x;
		do {
			x = thread.outQueue.poll();
			if (x != null)
				outQueue.add(x);
		} while (x != null);

	}

	private void collectOneOutput() {
		for (WorkerThread<inType, outType> w : threadPool.keySet()) {
			outType result = w.outQueue.poll();
			if (result != null) {
				outQueue.add(result);
				System.out.println("Collected " + result);
			}
		}
	}

	private WorkerThread<inType, outType> getOptimalThread() {
		WorkerThread<inType, outType> min = null;
		final boolean threadPoolFull = (threadPool.size() >= maxThreads);

		for (WorkerThread<inType, outType> t : threadPool.keySet()) {
			if (t.isWaiting())
				return t;

			// if the pool is full (we can't add) and either
			// ( min hasn't been set ) OR ( this is a thread with a smaller
			// queue)
			else if (threadPoolFull
					&& (min == null || min.inQueue.size() > t.inQueue.size()))
				min = t;
		}

		if (threadPoolFull)
			return min;
		else { // add a new thread
			WorkerThread<inType, outType> w = threadTemplate.clone();
			Thread t = new Thread(w);
			threadPool.put(w, t);
			t.start();

			return w;
		}
	}

	// set all threads to exit on empty
	public void setAllThreadsEOE() {
		for (WorkerThread w : threadPool.keySet())
			w.startBehaviourExitOnEmptyPool();
	}
}
