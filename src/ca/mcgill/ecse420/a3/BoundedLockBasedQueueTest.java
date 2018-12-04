package ca.mcgill.ecse420.a3;

public class BoundedLockBasedQueueTest {
	// Create a BoundedLockBasedQueue with a speicific capacity
	static BoundedLockBasedQueue<Integer> q = new BoundedLockBasedQueue<Integer>(5);
	
	public static void main(String[] args){
		
		
		// Run queue/dequeue tests sequentially to make sure that items are being queued/dequeued correctly
		// in this array implementation of a queue
		
		// Attempt to dequeue, and expect to get an error message as there are not items in the queue
		dequeue();
		
		enqueue(1);
		enqueue(2);
		enqueue(3);
		enqueue(4);
		enqueue(5);
		
		// Attempt to enqueue another item, however the queue is full and thus expect to get an error message
		enqueue(6);
		
	}
	
	public static void enqueue(Integer item){
		System.out.println("Attempting to queue " + item);
		q.enqueue(item);
		q.printQueue();
	}
	
	public static void dequeue(){
		System.out.println("Attempting to dequeue item from the head of the queue");
		q.dequeue();
		q.printQueue();
	}
}
