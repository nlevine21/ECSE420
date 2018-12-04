package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoundedLockBasedQueueTest {
	// Create a BoundedLockBasedQueue with a speicific capacity
	static BoundedLockBasedQueue<Integer> q;
	static int step = 0;
	
	public static void main(String[] args){
		
		
		// Run queue/dequeue tests sequentially to make sure that items are being queued/dequeued correctly
		// in this array implementation of a queue
		
		System.out.println("SEQUENTIAL QUEUE TEST");
		
		q = new BoundedLockBasedQueue<Integer>(5);
		
		// Attempt to dequeue, and expect to get an error message as there are not items in the queue
		dequeue();
		
		enqueue(1);
		enqueue(2);
		enqueue(3);
		enqueue(4);
		enqueue(5);
		
		// Attempt to enqueue another item, however the queue is full and thus expect to get a full-stack message
		enqueue(6);
		
		dequeue();
		dequeue();
		dequeue();
		
		System.out.println("\nPARALLEL QUEUE TEST");
		
		q = new BoundedLockBasedQueue<Integer>(5);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		executor.execute(new Task1());
		executor.execute(new Task2());
		
		// Wait for all threads to terminate
		executor.shutdown();
		while (!executor.isTerminated());
	}
	
	public static void enqueue(Integer item){
		q.enqueue(item);
		q.printQueue();
	}
	
	public static void dequeue(){
		q.dequeue();
		q.printQueue();
	}
	
	private static class Task1 implements Runnable {
		public void run(){
			enqueue(1);
			sleep();
			dequeue();
			sleep();
			dequeue();
		}
	}
	
	private static class Task2 implements Runnable {
		public void run(){
			enqueue(2);
			sleep();
			enqueue(3);
			sleep();
			dequeue();
			sleep();
			dequeue();
		}
	}
	
	private static void sleep(){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
