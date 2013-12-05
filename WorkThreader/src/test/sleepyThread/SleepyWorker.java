package test.sleepyThread;

import java.util.Random;

import workThreader.WorkerThread;

public class SleepyWorker extends WorkerThread<Integer, Integer> {
	
	// this class is an example of implementing a worker 
	SleepyWorker() {
		super();
	}

	SleepyWorker(Integer[] x) {
		super(x);
	}

	@Override
	public Integer work(Integer i) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return i;
	}

	@Override
	public void beforeRunning() {
		System.out.println(this.getName() + " starting");
	}

	@Override
	public void beforeExit() {
		System.out.println(this.getName() + " complete");
	}
	
	@Override
	public WorkerThread<Integer, Integer> clone() {
		return new SleepyWorker();
	}

}
