package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

public class BoundedLockBasedQueue<T> {
	private ReentrantLock enqLock = new ReentrantLock();
	private ReentrantLock deqLock = new ReentrantLock();
	
	private int size;
	private int capacity;
	
	private T[] queue;
	
	public BoundedLockBasedQueue(int capacity){
		this.capacity = capacity;
		this.queue = (T[]) new Object[capacity];
		this.size = 0;
	}
	
	public void enqueue(T item){
		this.enqLock.lock();
		
		if(size+1 > capacity){
			System.out.println("The queue is full!");
			this.enqLock.unlock();
			return;
		}
		
		this.queue[size] = item;
		size++;
		this.enqLock.unlock();
	}
	
	public T dequeue(){
		this.deqLock.lock();
		
		if(size == 0) {
			System.out.println("The queue is empty!");
			this.deqLock.unlock();
			return null;
		}
		
		T item = this.queue[0];
		
		for (int i=1; i<size; i++) {
			this.queue[i-1] = this.queue[i];
		}
		
		size--;
		this.queue[size] = null;
		
		this.deqLock.unlock();
		
		return item;
	}
	
	public void printQueue(){
		for(int i = 0; i < size; i++){
			System.out.print(this.queue[i].toString() + " ");
		}
		System.out.println();
	}
}
