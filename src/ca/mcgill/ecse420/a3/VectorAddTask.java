package ca.mcgill.ecse420.a3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class for parallel matrix-vector multiplication implementation 2 (M*V)
 * 
 * Recursively divides each vector into 2 sub-vectors and adds them
 * |A1| + |B1|
 * |A2|   |B2|
 * */
public class VectorAddTask implements Callable<double[]> {
	double[] a, b, c;
	ExecutorService exec = Executors.newCachedThreadPool();
	int start, end;
	
	/**
	 * Constructor
	 * 
	 * Initialize all properties for public class use
	 * 
	 * @param a is a n-sized vector of type double
	 * @param b is a n-sized vector of type double
	 * */
	public VectorAddTask(double[] a, double[] b) {
		this.a = a;
		this.b = b;
		
		this.c = new double[a.length];
		this.start = 0;
		this.end = a.length - 1;
	}
	
	/**
	 * Constructor
	 * 
	 * Initialize all properties for private class use
	 * 
	 * @param a is a n-sized vector of type double
	 * @param b is a m-sized vector of type double
	 * @param c is the resulting vector of the vector-vector addition
	 * @param start is the starting row
	 * @param end is the ending row
	 * */
	private VectorAddTask(double[] a, double[] b, double[] c, int start, int end){
		this.a = a;
		this.b = b;
		this.c = c;
		
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Call task
	 * 
	 * @return the resulting array of the vector-vector addition of type double[]
	 * */
	public double[] call() {
		
		// If the end row and start row are equivalent, then we are looking at the same row
		// and may perform the addition between the two vectors
		if(end == start){
			c[start] = a[start] + b[start];
			return c;
		}else{
			
			// Split each vector into two sub-vectors
			
			// Compute the row half-size that will correctly split each vectors
			// into 2 sub-vectors
			int halfSize = (((end+1)-start)/2) - 1;
			
			// Set the row start and end indices of the sub-vectors
			int add1Start = start;
			int add1End = start+halfSize;
			int add2Start = start+halfSize+1;
			int add2End = end;
			
			// Submit 2 tasks that will add the top and bottom halves of the given sub-vectors
			Future<?> topAdd = exec.submit(new VectorAddTask(a, b, c, add1Start, add1End));
			Future<?> bottomAdd = exec.submit(new VectorAddTask(a, b, c, add2Start, add2End));
			
			try {
				// Compute the tasks of adding the top and bottom halves of each vector respectively
				topAdd.get();
				bottomAdd.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			exec.shutdown();
			return c;
	
		}
	}
}
