package test.sleepyThread;

public class SleepyThreadExample {

	public static void main(String[] args) {
		
		// this example is to show how to use the thread template
		// to thread tasks that don't require output aggregation
		// perhaps the threads would all drop results to separate
		// files or results are accumulated after all threads complete
		// the main idea here is that if jobs can be pre-divided you
		// don't need to dynamically distribute jobs to your threads 
		// and you don't need the thread pool manager
		

		// create our arrays 
		Thread[] threadPool = new Thread[20];
		SleepyWorker[] workerPool = new SleepyWorker[20];
		Integer[] set = { 123, 1234, 12345, 123456 };

		System.out.println("Creating threads");
		
		// create the worker objects and add them to threads
		for (int i = 0; i < threadPool.length; ++i) {

			// allocate this thread's work
			workerPool[i] = new SleepyWorker(set); 

			// tell this thread to stop once it's out of work
			workerPool[i].startBehaviourExitOnEmptyPool();
			
			// add the constructed thread to our thread pool
			threadPool[i] = new Thread(workerPool[i]);
			
			// to improve performance start the thread as soon as it's constructed
		}

		System.out.println("Starting threads");
		
		// start each thread
		for (int i = 0; i < threadPool.length; ++i)
			threadPool[i].start();
		

		System.out.println("Waiting for threads to run");
		theWhile: while(true){
			for(Thread t: threadPool)
				if(t.getState() != Thread.State.TERMINATED){
					continue theWhile;
				}
			break;
		}
		System.out.println("Complete");
	}

}
