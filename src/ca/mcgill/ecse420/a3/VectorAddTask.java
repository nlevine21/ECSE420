package ca.mcgill.ecse420.a3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VectorAddTask implements Callable<double[]> {
	double[] a, b, c;
	ExecutorService exec = Executors.newCachedThreadPool();
	int start, end;
	
	public VectorAddTask(double[] a, double[] b) {
		this.a = a;
		this.b = b;
		
		this.c = new double[a.length];
		this.start = 0;
		this.end = a.length - 1;
	}
	
	private VectorAddTask(double[] a, double[] b, double[] c, int start, int end){
		this.a = a;
		this.b = b;
		this.c = c;
		
		this.start = start;
		this.end = end;
	}
	
	
	public double[] call() {
		
		
		if(end == start){
			c[start] = a[start] + b[start];
			return c;
		}else{
		
			int halfSize = (((end+1)-start)/2) - 1;
			
			int add1Start = start;
			int add1End = start+halfSize;
			int add2Start = start+halfSize+1;
			int add2End = end;
			
			Future<?> topAdd = exec.submit(new VectorAddTask(a, b, c, add1Start, add1End));
			Future<?> bottomAdd = exec.submit(new VectorAddTask(a, b, c, add2Start, add2End));
			
			try {
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
