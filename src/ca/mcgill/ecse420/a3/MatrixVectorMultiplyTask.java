package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class for parallel matrix-vector multiplication implementation 2 (M*V)
 * */
public class MatrixVectorMultiplyTask implements Callable<double[]> {
	
	double[][] matrix;
	double[] vector, result;
	
	int rowStart, rowEnd, columnStart, columnEnd;
	
	
	ExecutorService exec = Executors.newCachedThreadPool();
	
	/**
	 * Constructor
	 * 
	 * Initialize all properties for public class use
	 * 
	 * @param matrix is a matrix A to be multiplied with vector V
	 * @param vector is a vector V to be multiplied with matrix A
	 * */
	public MatrixVectorMultiplyTask(double[][] matrix, double[] vector){
		this.matrix = matrix;
		this.vector = vector;
		
		this.result = new double[matrix.length];
		
		this.rowStart = 0;
		this.columnStart = 0;
		this.rowEnd = matrix.length - 1;
		this.columnEnd = matrix[0].length - 1;
		
	}
	
	/**
	 * Constructor
	 * 
	 * Initialize all properties for private class use
	 * 
	 * @param matrix is a matrix A to be multiplied with vector V
	 * @param vector is a vector V to be multiplied with A
	 * @param result is the resulting vector of the matrix-vector multiplication A X V
	 * @param rStart is the starting row
	 * @param rEnd is the ending row
	 * @param cStart is the starting column
	 * @param cEnd is the ending column
	 * */
	private MatrixVectorMultiplyTask(double[][] matrix, double[] vector, double[] result, int rStart, int rEnd, int cStart, int cEnd){
		this.matrix = matrix;
		this.vector = vector;
		
		this.result = result;
		
		this.rowStart = rStart;
		this.rowEnd = rEnd;
		this.columnStart = cStart;
		this.columnEnd = cEnd;

	}
	
	/**
     * Implemented call method
     *
     * This task recursively divides matrix into 4 sub-matrices to multiply with 2 sub-vectors
     * |A1 A2| X |V1|
     * |A3 A4|	 |V2|
     * This is done in place using the matrix and vectors' indices to determine the sub-sections
     * to be multiplied together
     *
     */
	public double[] call() {
		
		// If the the starting row index is equivalent to the ending row index, then we
		// are looking at a single row of the matrix and can multiply it with the vector
		// at the associated start and end columns of the matrix and rows of the vector
		if (rowStart == rowEnd) {
			
			double[] partialResult = new double[1];
			for (int i=columnStart; i<=columnEnd; i++) {
				partialResult[0] += matrix[rowStart][i]*vector[i];
			}
			
			return partialResult;
			
		} else {

			// Split the matrix into four sub-matrices
			
			// Compute the row and column half-sizes that will correctly split the matrix
			// into 4 sub-matrices
			int rowHalfSize = (((rowEnd+1)-rowStart)/2) - 1;
			int columnHalfSize = (((columnEnd+1)-columnStart)/2) - 1;
			
			// Set the indices for the row sections
			int r1Start = rowStart;
			int r1End = rowStart + rowHalfSize;
			int r2Start = rowStart + rowHalfSize + 1;
			int r2End = rowEnd;
			
			// Set the indices for the column sections
			int c1Start = columnStart;
			int c1End = columnStart + columnHalfSize;
			int c2Start = columnStart + columnHalfSize + 1;
			int c2End = columnEnd;
			
			try {
			
				// Submit 4 sub-tasks that will multiply the 4 sub-matrices with the corresponding segment of the vector
				
				// A1*V1
				Future<double[]> partialTop1 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r1Start, r1End, c1Start, c1End));
				
				// A2*V2
				Future<double[]> partialTop2 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r1Start, r1End, c2Start, c2End));
				
				// A3*V1
				Future<double[]> partialBottom1 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r2Start, r2End, c1Start, c1End));
				
				// A4*V2
				Future<double[]> partialBottom2 = exec.submit(new MatrixVectorMultiplyTask(matrix, vector, result, r2Start, r2End, c2Start, c2End));
				
				// Submit 2 sub-tasks that will add the top 2 resultant sub-vectors and the bottom 2 resultant sub-vectors
				
				// A1*V1 + A2*V2
				Future<double[]> top = exec.submit(new VectorAddTask(partialTop1.get(), partialTop2.get()));
				
				// A3*V1 + A4*V2
				Future<double[]> bottom = exec.submit(new VectorAddTask(partialBottom1.get(), partialBottom2.get()));
				
				// Merge the calculated top and bottom sections
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

