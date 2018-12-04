package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Queue structure that allows for thread-safe enqueuing and dequeuing of items
 * */
public class BoundedLockBasedQueue<T> {
	
	private ReentrantLock enqLock = new ReentrantLock();
	private ReentrantLock deqLock = new ReentrantLock();
	private ReentrantLock size1Lock = new ReentrantLock();
	
	private int size;
	private int capacity;
	
	int head;
	int tail;
	
	private T[] queue;
	
	 /**
     * Constructor
     *
     * Initialize all properties
     *
     * @param capacity is the maximum number of items that the queue can hold
     */
	public BoundedLockBasedQueue(int capacity){
		this.capacity = capacity;
		this.queue = (T[]) new Object[capacity];
		this.size = 0;
		this.head = 0;
		this.tail = 0;
	}
	/**
	 * Enqueues an item of type T (puts the item at the back of the list)
	 * 
	 * @param item is the item that will be enqueued
	 * */
	public void enqueue(T item){
		
		// Secure the enqueue function by obtaining its lock
		this.enqLock.lock();
		
		// If adding an item exceeds the capacity of the queue  dont add it and release the enqueue lock
		if(size+1 > capacity){
			System.out.println("The queue is full!");
			this.enqLock.unlock();
			return;
		}
		
		// Set boolean for if the size1Lock is being locked as to unlock it later on
		boolean size1LockLocked = false;
		if(size < 2){
			this.size1Lock.lock();
			size1LockLocked = true;
		}
		
		if(size > 0){
			this.tail = (this.tail + 1) % capacity;
		}
		
		// Store the item at the end of the array 
		this.queue[this.tail] = item;
		size++;
		
		// If the size1Lock was locked, unlock it
		if(size1LockLocked){
			size1Lock.unlock();
		}
		
		// Release the enqueue lock
		this.enqLock.unlock();
	}
	
	/**
	 * Dequeues the item at the head of the queue (removes the item at the front of the queue)
	 * 
	 * @return the item of type T from the head of the queue
	 * */
	public T dequeue(){
		
		// Secure the dequeue function by obtaining its lock
		this.deqLock.lock();
		
		// If the queue is empty (the size is 0) there is no item to remove and release the dequeue lock
		if(size == 0) {
			System.out.println("The queue is empty!");
			this.deqLock.unlock();
			return null;
		}
		
		// Set boolean for if the size1Lock is being locked as to unlock it later on
		boolean size1LockLocked = false;
		if(size == 1){
			this.size1Lock.lock();
			size1LockLocked = true;
		}
		
		// Obtain the item at the head of the queue and set the current head position to null
		T item = this.queue[this.head];
		this.queue[this.head] = null;
		
		// Decrease the size of the queue
		size--;
		
		// If the size is greater than zero, push the head pointer to the following index
		if(size > 0){
			this.head = (this.head + 1) % capacity;
		}
		
		// If the size1Lock was locked, unlock it
		if(size1LockLocked){
			size1Lock.unlock();
		}
		
		// Release the dequeue lock
		this.deqLock.unlock();
		
		return item;
	}
	
	/**
	 * Print the queued items
	 * */
	public void printQueue(){
		System.out.print("Current Queue: ");
		int j = this.head;
		for(int i = 0; i < size; i++){
			System.out.print(this.queue[j].toString() + " ");
			j = (j + 1) % this.capacity;
		}
		System.out.println("\nHead Index: "+this.head+", Tail Index: "+this.tail);
	}
}
