package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FineGrainedListTest {
	static ArrayList<Integer> list;
	static FineGrainedList<Integer> fList;
	public static void main(String[] args){
		
		// Create a list of integers
		list = new ArrayList<Integer>();
		for(int i = 0; i < 20; i++){
			list.add(i);
		}
		
		System.out.println("List to be tested:");
		System.out.println(list.toString());
		
		// Create a list of lockable Node's using the FineGrainedList class
		fList = new FineGrainedList<Integer>(list);
		
		// Run the contains method for both a value that exists and doesn't exist in the list
		System.out.println("List contains 18?: "+fList.contains(18));
		System.out.println("List contains 1823?: "+fList.contains(1823));
	}
}
