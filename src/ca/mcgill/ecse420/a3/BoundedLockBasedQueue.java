package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Queue structure that allows for thread-safe enqueuing and dequeuing of items
 * */
public class BoundedLockBasedQueue<T> {
	
	private ReentrantLock enqLock = new ReentrantLock();
	private ReentrantLock deqLock = new ReentrantLock();
	
	private int size;
	private int capacity;
	
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
		
		// Store the item at the end of the array 
		this.queue[size] = item;
		size++;
		
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
		
		// Obtain the item at the head of the queue
		T item = this.queue[0];
		
		// Move all queued items one position closer to the head of the queue
		for (int i=1; i<size; i++) {
			this.queue[i-1] = this.queue[i];
		}
		
		// Decrease the size of the queue and set the last value to null (as it has already been recorded
		// in the next position)
		size--;
		this.queue[size] = null;
		
		// Release the dequeue lock
		this.deqLock.unlock();
		
		return item;
	}
	
	/**
	 * Print the queued items
	 * */
	public void printQueue(){
		System.out.print("Current Queue: ");
		for(int i = 0; i < size; i++){
			System.out.print(this.queue[i].toString() + " ");
		}
		System.out.println();
	}
}
