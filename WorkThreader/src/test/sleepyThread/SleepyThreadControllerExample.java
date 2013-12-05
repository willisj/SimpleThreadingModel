package test.sleepyThread;

import java.util.Random;

import workThreader.TaskDistributor;

public class SleepyThreadControllerExample {

	public static void main(String[] args) {
		Random r = new Random();
		SleepyWorker w = new SleepyWorker();
		TaskDistributor<Integer, Integer, SleepyWorker> threadController = new TaskDistributor<Integer, Integer, SleepyWorker>(
				5, w);
		
		for(int i = 0; i < 20; ++i)
			threadController.inQueue.add(i);
		threadController.setAllThreadsEOE();
		
		threadController.start();
		

	}

}
