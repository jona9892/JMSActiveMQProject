package JMSREQREP;

import JMSPTPThreads.ProducerThread;

public class JMSApp {

	public static void main(String[] args) throws Exception {
		
		thread(new Requestor("Student 1"));
		thread(new Requestor("Student 2"));
		thread(new Requestor("Student 3"));
		thread(new Requestor("Student 4"));
		
		Thread.sleep(2000);
		
		thread(new Replier("Student 1", "Jonathan"));
		thread(new Replier("Student 2", "Mads"));
		thread(new Replier("Student 3", "Ivan"));
		thread(new Replier("Student 4", "Mohsen"));		

	}
	
	public static void thread(Runnable runnable) {
		Thread newThread = new Thread(runnable);
		newThread.start();
	}

}
