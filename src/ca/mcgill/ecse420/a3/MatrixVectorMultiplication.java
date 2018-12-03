package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixVectorMultiplication {
	
	public static void main(String[] args) {
		double[][] matrix = generateRandomMatrix(2000, 2000);
		double[] vector = generateRandomVector(2000);
	
		
		long startTime;
		long endTime;
	
		// Start sequential time computation
		System.out.println("Computing sequentially...");
		startTime = System.currentTimeMillis();
		sequentialMatrixVectorMultiplication(matrix, vector);
		endTime = System.currentTimeMillis();
		System.out.println("Sequential computational time: "+ (endTime-startTime) + "ms");
		
		// Start parallel time computation
		System.out.println("Computing in parallel");
		MatrixVectorMultiplyTask t = new MatrixVectorMultiplyTask(matrix,vector);
		
		try {
			startTime = System.currentTimeMillis();
			t.call();
			endTime = System.currentTimeMillis();
			System.out.println("Parallel computational time: "+ (endTime-startTime) + "ms");
		} catch (Exception e) {
			System.out.println("Error with parallel multiplication");
		}
	
	}
	
	public static double[] sequentialMatrixVectorMultiplication(double[][] matrix, double[] vector) {

		// Verify that the number of columns in matrix matches the number of rows in vector
		int numColumnsMatrix = matrix[0].length;
		int numRowsVector = vector.length;

		// If the number of columns in matrix does not match the number of rows in vector, throw
		// an exception as you cannot multiply these matrices
		if (numColumnsMatrix != numRowsVector) {
			throw new IllegalArgumentException("Cannot multiply matrix and vector");
		}

		// The result of the multiplication will be a vector with length numRowsMatrix
		int numRowsMatrix = matrix.length;
		double result[] = new double[numRowsMatrix];


		// Compute the result of the multiplication 
		for (int rowM = 0; rowM < numRowsMatrix; rowM++) {

			double entry = 0;
	
			for (int columnM = 0; columnM < numColumnsMatrix; columnM++) {
				entry += matrix[rowM][columnM] * vector[columnM];
			}
	
			result[rowM] = entry;
			
		}

		return result;

	}
	
	private static double[][] generateRandomMatrix(int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				matrix[row][col] = (double) ((int) (Math.random() * 10.0));
			}
		}
		return matrix;
	}
	
	private static double[] generateRandomVector(int numRows) {
		double vector[] = new double[numRows];
		for (int row = 0; row < numRows; row++) {
			vector[row] = (double) ((int) (Math.random() * 10.0));
		}
		return vector;
	}
	
}
