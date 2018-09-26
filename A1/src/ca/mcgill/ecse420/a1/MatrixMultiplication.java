package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

	private static final int NUMBER_THREADS = 4;
	private static final int MATRIX_SIZE = 2000;

	public static void main(String[] args) {

		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		double[][] c = sequentialMultiplyMatrix(a, b);
		double[][] d = parallelMultiplyMatrix(a, b);

		System.out.println(Arrays.deepToString(c));
		System.out.println(Arrays.deepToString(d));


	}

	/**
	 * Returns the result of a sequential matrix multiplication The two matrices are
	 * randomly generated
	 * 
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {

		// Verify that the number of columns in matrix A matches the number of rows in
		// matrix B
		int numColumnsA = a[0].length;
		int numRowsB = b.length;

		// If the number of rows in A does not match the number of columns in B, throw
		// an exception as you cannot multiply these matrices
		if (numColumnsA != numRowsB) {
			throw new IllegalArgumentException("Cannot multiply matrices A and B");
		}

		// The new matrix will have a final size of rowsA * columnsB
		int numRowsA = a.length;
		int numColumnsB = b[0].length;

		double c[][] = new double[numRowsA][numColumnsB];



		// Compute the result of the multiplication matrix
		for (int rowA = 0; rowA < numRowsA; rowA++) {

			for (int columnB = 0; columnB < numColumnsB; columnB++) {

				double cEntry = 0;

				for (int columnA = 0; columnA < numColumnsA; columnA++) {
					cEntry += a[rowA][columnA] * b[columnA][columnB];
				}

				c[rowA][columnB] = cEntry;
			}
		}



		return c;

	}

	/**
	 * Returns the result of a concurrent matrix multiplication The two matrices are
	 * randomly generated
	 * 
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {

		// Verify that the number of columns in matrix A matches the number of rows in
		// matrix B
		int numColumnsA = a[0].length;
		int numRowsB = b.length;

		// If the number of rows in A does not match the number of columns in B, throw
		// an exception as you cannot multiply these matrices
		if (numColumnsA != numRowsB) {
			throw new IllegalArgumentException("Cannot multiply matrices A and B");
		}

		// The new matrix will have a final size of rowsA * columnsB
		int numRowsA = a.length;
		int numColumnsB = b[0].length;

		double c[][] = new double[numRowsA][numColumnsB];

		// Create a Thread Pool to manage all threads
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		// Create and start the desired amount of threads
		for (int threadNumber=0; threadNumber<NUMBER_THREADS; threadNumber++) {

			// Create a MatrixPartialMultiplier task where the startingRow corresponds
			// to the task's thread number
			MatrixPartialMultiplier task = new MatrixPartialMultiplier(a, b, c, threadNumber, NUMBER_THREADS);
			executor.execute(task);
		}

		// Wait for all threads to terminate
		executor.shutdown();
		while (!executor.isTerminated());

		return c;
	}

	/**
	 * Populates a matrix of given size with randomly generated integers between
	 * 0-10.
	 * 
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix(int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				matrix[row][col] = (double) ((int) (Math.random() * 10.0));
			}
		}
		return matrix;
	}

}
