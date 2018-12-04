package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.mcgill.ecse420.a3.MatrixPartialMultiplier;

/**
 * Test class for matrix-vector multiplication
 * */
public class MatrixVectorMultiplication {
	
	public static int MAX_THREADS = 4;
	public static int MATRIX_SIZE = 2000;
	
	public static void main(String[] args) {
		double[][] matrix = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[] vector = generateRandomVector(MATRIX_SIZE);
	
		
		long startTime;
		long endTime;
	
		// Start sequential time computation
		System.out.println("Computing sequentially...");
		startTime = System.currentTimeMillis();
		sequentialMatrixVectorMultiplication(matrix, vector);
		endTime = System.currentTimeMillis();
		System.out.println("Sequential computational time: "+ (endTime-startTime) + "ms\n");
		
		// Start parallel time computation
		System.out.println("Computing in parallel (Fast Execution)");
		startTime = System.currentTimeMillis();
		parallelMatrixVectorMultiplyFast(matrix,vector);
		endTime = System.currentTimeMillis();
		System.out.println("Parallel computational time: "+ (endTime-startTime) + "ms\n");
		
		// Start parallel time computation
		System.out.println("Computing in parallel (Slow Execution)");
		startTime = System.currentTimeMillis();
		parallelMatrixVectorMultiplySlow(matrix,vector);
		endTime = System.currentTimeMillis();
		System.out.println("Parallel computational time: "+ (endTime-startTime) + "ms");
	
		
	
	}
	
	/**
	 * Sequential matrix-vector multiplication
	 * 
	 *  @param matrix is an nxn matrix of type double
	 *  @param vector is an n-sized vector of type double
	 * */
	public static double[] sequentialMatrixVectorMultiplication(double[][] matrix, double[] vector) {

		// Verify that the number of columns in matrix matches the number of rows in vector
		int numColumnsMatrix = matrix[0].length;
		int vectorLength = vector.length;

		// If the number of columns in matrix does not match the number of rows in vector, throw
		// an exception as you cannot multiply these matrices
		if (numColumnsMatrix != vectorLength) {
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
	
	/**
	 * Parallel matrix-vector multiplication - implementation 1
	 * 
	 * @param matrix is an nxn-sized matrix of type double
	 * @param vector is an n-sized vector of type double
	 * */
	public static double[] parallelMatrixVectorMultiplyFast(double[][] matrix, double[] vector) {

		// Verify that the number of columns in matrix matches the number of rows in vector
		int numColumnsMatrix = matrix[0].length;
		int vectorLength = vector.length;

		// If the number of rows in A does not match the number of columns in B, throw
		// an exception as you cannot multiply these matrices
		if (numColumnsMatrix != vectorLength) {
			throw new IllegalArgumentException("Cannot multiply matrices A and B");
		}

		int numRowsMatrix = matrix.length;
		double result[] = new double[numRowsMatrix];

		// Create a Thread Pool to manage all threads
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

		// Create and start the desired amount of threads
		for (int threadNumber=0; threadNumber<MAX_THREADS; threadNumber++) {

			// Create a MatrixPartialMultiplier task where the startingRow corresponds
			// to the task's thread number
			if (threadNumber < numRowsMatrix) {
				MatrixPartialMultiplier task = new MatrixPartialMultiplier(matrix, vector, result, threadNumber, MAX_THREADS);
				executor.execute(task);
			} else {
				continue;
			}
		}

		// Wait for all threads to terminate
		executor.shutdown();
		while (!executor.isTerminated());

		return result;
	}
	
	/**
	 * Parallel matrix-vector multiplication - implementation 2
	 * 
	 * @param matrix is an nxn-sized matrix of type double
	 * @param vector is an n-sized vector of type double
	 * */
	public static double[] parallelMatrixVectorMultiplySlow(double[][] matrix, double[] vector) {
		MatrixVectorMultiplyTask t = new MatrixVectorMultiplyTask(matrix, vector);
		return t.call();
	}
	
	/**
	 * Random number matrix generator
	 * 
	 * @param numRows is the number of rows to be created for the randomly generated matrix
	 * @param numCols is the number of columns to be created for the randomly generated matrix
	 * */
	private static double[][] generateRandomMatrix(int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				matrix[row][col] = (double) ((int) (Math.random() * 10.0));
			}
		}
		return matrix;
	}
	
	/**
	 * Random number vector generator
	 * 
	 * @param numRows is the number of rows to be created for the randomly generated vector
	 * */
	private static double[] generateRandomVector(int numRows) {
		double vector[] = new double[numRows];
		for (int row = 0; row < numRows; row++) {
			vector[row] = (double) ((int) (Math.random() * 10.0));
		}
		return vector;
	}
	
}
