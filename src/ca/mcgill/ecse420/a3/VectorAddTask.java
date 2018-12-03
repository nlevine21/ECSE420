package ca.mcgill.ecse420.a3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VectorAddTask implements Callable<double[]> {
	double[] a, b;
	ExecutorService exec = Executors.newCachedThreadPool();
	
	public VectorAddTask(double[] a, double[] b){
		this.a = a;
		this.b = b;
	}
	
	public double[] call() throws Exception {
		
		if(a.length == 1){
			double[] c = new double[1];
			c[0] = a[0] + b[0];
			return c;
		}else{
			
			double[] a1, a2, b1, b2;
			
			int halfSize = a.length/2;
			boolean evenLength = a.length % 2 == 0;
			
			if (evenLength) {
				a1 = new double[halfSize];
				a2 = new double[halfSize];
				
				b1 = new double[halfSize];
				b2 = new double[halfSize];
			} else {
				a1 = new double[halfSize+1];
				a2 = new double[halfSize];
				
				b1 = new double[halfSize+1];
				b2 = new double[halfSize];
			}
			
			for (int i=0; i<a.length; i++) {
				if (evenLength) {
					if (i<halfSize) {
						a1[i] = a[i];
						b1[i] = b[i];
					} else {
						a2[i-halfSize] = a[i];
						b2[i-halfSize] = b[i];
					}
				} else {
					if (i<(halfSize+1)) {
						a1[i] = a[i];
						b1[i] = b[i];
					} else {
						a2[i-(halfSize+1)] = a[i];
						b2[i-(halfSize+1)] = b[i];
					}
				}
			}
			
			Future<double[]> top = exec.submit(new VectorAddTask(a1, b1));
			Future<double[]> bottom = exec.submit(new VectorAddTask(a2, b2));
			
			double[] topVec = top.get();
			double[] bottomVec = bottom.get();
			
			double[] result = new double[topVec.length + bottomVec.length];
			
			for (int i=0; i<result.length; i++) {
				if (i<topVec.length) {
					result[i] = topVec[i];
				} else {
					result[i] = bottomVec[i-topVec.length];
				}
			}
			
			return result;
	
		}
	}
}
