package ca.mcgill.ecse420.a2;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LockTest {
	static int accountBalance = 0;
	static int numThreads = 40;
	
	public static void main(String[] args){
		
		// First show a bank account being accessed by multiple threads
		// with no locking mechanism
		ExecutorService executorNoLock = Executors.newFixedThreadPool(numThreads);
		for(int i = 0; i < numThreads; i++)
			executorNoLock.execute(new UnsafeDeposit(i));
		executorNoLock.shutdown();
		while(!executorNoLock.isTerminated()){}
		System.out.println("Account balance without a locking mechanism: "+accountBalance);
		
		// Reset the account balance
		accountBalance = 0;
		
		// Next show a bank account being accessed by multiple threads
		// with the Filter locking mechanism
		ExecutorService executorFilter = Executors.newFixedThreadPool(numThreads);
		Filter filter = new Filter(numThreads);
		for(int i = 0; i < numThreads; i++)
			executorFilter.execute(new FilterSafeDeposit(i,filter));
		executorFilter.shutdown();
		while(!executorFilter.isTerminated()){}
		System.out.println("Account balance using the Filter locking mechanism: "+accountBalance);
		
		// Reset the accountBalance
		accountBalance = 0;
		
		// Lastly show a bank account being accessed by multiple threads
		// with the Bakery locking mechanism
		ExecutorService executorBakery = Executors.newFixedThreadPool(numThreads);
		Bakery bakery = new Bakery(numThreads);
		for(int i = 0; i < numThreads; i++)
			executorBakery.execute(new BakerySafeDeposit(i,bakery));
		executorBakery.shutdown();
		while(!executorBakery.isTerminated()){}
		System.out.println("Account balance using the Bakery locking mechanism: "+accountBalance);
	}
	
	public static class UnsafeDeposit implements Runnable{
		int id;
		
		public UnsafeDeposit(int id){
			this.id = id;
		}
		
		public void run(){
			
			try {
				Thread.sleep(200);
				accountBalance++;
			} catch (Exception e) {
				
			}
			

		}
	}
	
	public static class FilterSafeDeposit implements Runnable{
		int id;
		Filter filter;
		public FilterSafeDeposit(int id, Filter filter){
			this.id = id;
			this.filter = filter;
		}
		
		public void run(){
			try {
				Thread.sleep(200);
				filter.lock(id);
				accountBalance++;
				filter.unlock(id);
			} catch (Exception e) {
				
			}
		}
	}
	
	public static class BakerySafeDeposit implements Runnable{
		int id;
		Bakery bakery;
		public BakerySafeDeposit(int id, Bakery bakery){
			this.id = id;
			this.bakery = bakery;
		}
		
		public void run(){
			try {
				Thread.sleep(200);
				bakery.lock(id);
				accountBalance++;
				bakery.unlock(id);
			} catch (Exception e) {
				
			}
		}
	}
}
