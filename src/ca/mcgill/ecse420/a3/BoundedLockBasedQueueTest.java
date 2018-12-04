package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoundedLockBasedQueueTest {
	// Create a BoundedLockBasedQueue with a speicific capacity
	static BoundedLockBasedQueue<Integer> q = new BoundedLockBasedQueue<Integer>(5);
	static int step = 0;
	
	public static void main(String[] args){
		
		
		// Run queue/dequeue tests sequentially to make sure that items are being queued/dequeued correctly
		// in this array implementation of a queue
		
		System.out.println("SEQUENTIAL QUEUE TEST");
		
		// Attempt to dequeue, and expect to get an error message as there are not items in the queue
		dequeue();
		
		enqueue(1);
		enqueue(2);
		enqueue(3);
		enqueue(4);
		enqueue(5);
		
		// Attempt to enqueue another item, however the queue is full and thus expect to get an error message
		enqueue(6);
		
		dequeue();
		dequeue();
		dequeue();
		enqueue(6);
		enqueue(7);
		enqueue(8);
		enqueue(9);
		dequeue();
		dequeue();
		dequeue();
		dequeue();
		dequeue();
		dequeue();
		enqueue(10);
		enqueue(11);
		dequeue();
		dequeue();
		
		System.out.println("\nPARALLEL QUEUE TEST");
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		executor.execute(new Task1());
		executor.execute(new Task2());
		executor.execute(new Task3());
		executor.execute(new Task4());
		
		// Wait for all threads to terminate
		executor.shutdown();
		while (!executor.isTerminated());
		
		q.printQueue();
	}
	
	public static void enqueue(Integer item){
		System.out.println(step+") Queue " + item);
		step++;
		q.enqueue(item);
	}
	
	public static void dequeue(){
		System.out.println(step+") Dequeue item from the head of the queue");
		step++;
		q.dequeue();
	}
	
	private static class Task1 implements Runnable {
		public void run(){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			enqueue(1);
			enqueue(1);
			dequeue();
			dequeue();
			dequeue();
			enqueue(1);
			dequeue();
			dequeue();
		}
	}
	
	private static class Task2 implements Runnable {
		public void run(){
			enqueue(2);
			dequeue();
			dequeue();
			dequeue();
			enqueue(2);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			enqueue(2);
			dequeue();
			dequeue();
		}
	}
	
	private static class Task3 implements Runnable {
		public void run(){
			enqueue(3);
			enqueue(3);
			dequeue();
			dequeue();
			dequeue();
			enqueue(3);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			enqueue(3);
			dequeue();
			dequeue();
		}
	}
	
	private static class Task4 implements Runnable {
		public void run(){
			enqueue(4);
			dequeue();
			dequeue();
			dequeue();
			enqueue(4);
			dequeue();
			dequeue();
		}
	}
}
