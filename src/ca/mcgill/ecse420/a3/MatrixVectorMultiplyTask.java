package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixVectorMultiplyTask implements Callable<double[]> {
	
	double[][] matrix;
	double[] vector, result;
	
	int rowStart, rowEnd, columnStart, columnEnd;
	
	
	ExecutorService exec = Executors.newCachedThreadPool();
	
	public MatrixVectorMultiplyTask(double[][] matrix, double[] vector){
		this.matrix = matrix;
		this.vector = vector;
		
		this.result = new double[matrix.length];
		
		this.rowStart = 0;
		this.columnStart = 0;
		this.rowEnd = matrix.length - 1;
		this.columnEnd = matrix[0].length - 1;
		
	}
	
	private MatrixVectorMultiplyTask(double[][] matrix, double[] vector, double[] result, int rStart, int rEnd, int cStart, int cEnd){
		this.matrix = matrix;
		this.vector = vector;
		
		this.result = result;
		
		this.rowStart = rStart;
		this.rowEnd = rEnd;
		this.columnStart = cStart;
		this.columnEnd = cEnd;

	}
	
	public double[] call() {
		
			
		if (rowStart == rowEnd) {
			
			double[] partialResult = new double[1];
			for (int i=columnStart; i<=columnEnd; i++) {
				partialResult[0] += matrix[rowStart][i]*vector[i];
			}
			
			return partialResult;
			
		} else {

		
			int rowHalfSize = (((rowEnd+1)-rowStart)/2) - 1;
			int columnHalfSize = (((columnEnd+1)-columnStart)/2) - 1;

			int r1Start = rowStart;
			int r1End = rowStart + rowHalfSize;
			int r2Start = rowStart + rowHalfSize + 1;
			int r2End = rowEnd;
			
			int c1Start = columnStart;
			int c1End = columnStart + columnHalfSize;
			int c2Start = columnStart + columnHalfSize + 1;
			int c2End = columnEnd;
			
			try {
			
				Future<double[]> partialTop1 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r1Start, r1End, c1Start, c1End));
				Future<double[]> partialTop2 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r1Start, r1End, c2Start, c2End));			
				Future<double[]> partialBottom1 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r2Start, r2End, c1Start, c1End));
				Future<double[]> partialBottom2 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r2Start, r2End, c2Start, c2End));
				
				Future<double[]> top = exec.submit(new VectorAddTask(partialTop1.get(), partialTop2.get()));
				Future<double[]> bottom = exec.submit(new VectorAddTask(partialBottom1.get(), partialBottom2.get()));
				
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
				
				exec.shutdown();
		
				
				return result;
				
			} catch (Exception e) {
				System.out.println("Error with multiplication");
				return null;
			}
			
	
			
		}
		
		
	}
}

