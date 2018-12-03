package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T> {
	Node<T> head;
	public FineGrainedList(ArrayList<T> items) {
		this.head = new Node<T>(items.get(0));
		Node<T> curr = this.head;
	
		for(int i = 1; i < items.size(); i++){
			curr.next = new Node<T>(items.get(i));
			curr = curr.next;
		}
	}
	
	public boolean contains(T item) {
		Node<T> pred, curr;
		
		pred = head;
		
		if (pred == null) {
			return false;
		}
		pred.lock();
		
		if (item == pred.item) {
			pred.unlock();
			return true;
		}
		
		curr = pred.next;
		
		while (curr != null) {
			curr.lock();
			if (item == curr.item) {
				curr.unlock();
				pred.unlock();
				return true;
			}
			
			pred.unlock();
			pred = curr;
			pred.lock();
			curr = curr.next;
		}

		pred.unlock();
		return false;
	}
	
	private class Node<T> {	
		T item;
		Node next;
		ReentrantLock lock = new ReentrantLock();
		
		private Node (T item) {
			this.item = item;
			next = null;
		}
		
		private void lock() {
			this.lock.lock();
		}
		
		private void unlock() {
			this.lock.unlock();
		}
				
	}
}

