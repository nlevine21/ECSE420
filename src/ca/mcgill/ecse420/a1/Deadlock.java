package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
	
	static ReentrantLock lock1 = new ReentrantLock();
	static ReentrantLock lock2 = new ReentrantLock();
	
	public static void main(String args[]){
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Task1 task1 = new Task1();
		Task2 task2 = new Task2();
		executor.execute(task1);
		executor.execute(task2);
	}
	
	public static class Task1 implements Runnable{
		public void run(){
			// Try to get lock 1
			System.out.println("Task 1 waiting for lock 1...");
			lock1.lock();
			System.out.println("Task 1 gets lock 1");
			
			// Wait for Task 2 to acquire Lock 2
			while (!lock2.isLocked());
			
			// Try to get lock 2
			System.out.println("Task 1 waiting for lock 2...");
			lock2.lock();
			System.out.println("Task 1 gets lock 2");
		}
	}
	public static class Task2 implements Runnable{
		public void run(){
			// Try to get lock 2
			System.out.println("Task 2 waiting for lock 2...");
			lock2.lock();
			System.out.println("Task 2 gets lock 2");
			
			// Wait for Task 1 to acquire Lock 1
			while (!lock1.isLocked());
			
			// Try to get lock 1
			System.out.println("Task 2 waiting for lock 1...");
			lock1.lock();
			System.out.println("Task 2 gets lock 1");
		}
	}
	
}
