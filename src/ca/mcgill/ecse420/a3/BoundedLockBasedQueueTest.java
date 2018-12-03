package ca.mcgill.ecse420.a3;

public class BoundedLockBasedQueueTest {
	public static void main(String[] args){
		BoundedLockBasedQueue<Integer> q = new BoundedLockBasedQueue<Integer>(5);
		
		q.enqueue(2);
		q.printQueue();
		q.enqueue(4);
		q.printQueue();
		q.enqueue(10);
		q.printQueue();
		q.enqueue(23);
		q.printQueue();
		q.enqueue(23);
		q.printQueue();
		q.enqueue(23);
		q.printQueue();
		
		q.dequeue();
		q.printQueue();
		q.dequeue();
		q.printQueue();
		q.dequeue();
		q.printQueue();
		q.dequeue();
		q.printQueue();
		q.dequeue();
		
		q.printQueue();
	}
}
