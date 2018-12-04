package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * List structure of lockable Nodes that provides hand-over-hand safety on its methods
 * 
 * @property head is the head of the list
 * */
public class FineGrainedList<T> {
	Node<T> head;
	
	 /**
     * Constructor
     *
     * Initialize all properties
     *
     * @param items is a list of T items to be implemented as a chain of Nodes
     */
	public FineGrainedList(ArrayList<T> items) {
		
		// Set the head of the list to be the first item in the array
		this.head = new Node<T>(items.get(0));
		Node<T> curr = this.head;
	
		// Add each array element to the list of Nodes
		for(int i = 1; i < items.size(); i++){
			curr.next = new Node<T>(items.get(i));
			curr = curr.next;
		}
	}
	
	/**
	 * Checks whether the list contains an item of type T
	 * 
	 * @param item is the object being searched for in the list of Nodes
	 * @return a boolean dictating whether the list contains the item or not
	 */
	public boolean contains(T item) {
		Node<T> pred, curr;
		
		// Set the pred node to be the head of the list of Nodes
		pred = head;
		
		if (pred == null) {
			return false;
		}
		
		// Lock the predecessor Node as to make sure that no other thread can obtain/modify it
		pred.lock();
		
		// If the head is the item being searched for, return true and unlock it
		if (item == pred.item) {
			pred.unlock();
			return true;
		}
		
		// Set the current Node to be the Node next to the head
		curr = pred.next;
		
		while (curr != null) {
			
			// Lock the current Node
			curr.lock();
			
			// If the current node is the item being searched for, return true and unlock the current and previous Nodes.
			if (item == curr.item) {
				curr.unlock();
				pred.unlock();
				return true;
			}
			
			// Move onto the next Node in the list, and lock the new predecessor Node (which is the latest current Node)
			pred.unlock();
			pred = curr;
			pred.lock();
			curr = curr.next;
		}
		
		// If the program makes it to this point, the item was not found in the list. Return false and unlock the the last
		// node in the list.
		pred.unlock();
		return false;
	}
	
	/**
	 * Custom Node class of type T
	 * 
	 * @param item is an object of type T
	 * @param next is a Node that is subsequent to this particular Node
	 * @param lock is a lock associated to this particular Node
	 */
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

