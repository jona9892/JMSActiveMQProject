package JMSPTPThreads;

public class JMSAppThread {
	
	public static void main(String[] args) throws Exception {

		thread(new ProducerThread(), false);
		thread(new ProducerThread(), false);
		thread(new ProducerThread(), false);			
		thread(new ProducerThread(), false);
		Thread.sleep(1000);		
		thread(new ConsumerThread(), false);
		thread(new ConsumerThread(), false);
		thread(new ProducerThread(), false);
		thread(new ConsumerThread(), false);
		thread(new ProducerThread(), false);
		thread(new ConsumerThread(), false);
		thread(new ProducerThread(), false);
		Thread.sleep(1000);
	}
	
	public static void thread(Runnable runnable, boolean daemon) {
		Thread newThread = new Thread(runnable);
		newThread.setDaemon(daemon);
		newThread.start();
	}
}
