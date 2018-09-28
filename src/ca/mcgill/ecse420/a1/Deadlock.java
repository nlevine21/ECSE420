package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
	
	ReentrantLock lock1 = new ReentrantLock();
	ReentrantLock lock2 = new ReentrantLock();
	
	public static void main(String args[]){
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Deadlock deadlock = new Deadlock();
		Task1 task1 = deadlock.new Task1();
		Task2 task2 = deadlock.new Task2();
		executor.execute(task1);
		executor.execute(task2);
	}
	
	public class Task1 implements Runnable{
		public void run(){
			// Try to get lock 1
			lock1.lock();
			try {
				// Performs other random tasks...
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Try to get lock 2
			lock2.lock();
			System.out.println("Task 1 complete!");
		}
	}
	public class Task2 implements Runnable{
		public void run(){
			// Try to get lock 2
			lock2.lock();
			// Try to get lock 1
			lock1.lock();
			System.out.println("Task 2 complete!");
		}
	}
	
}
