package ca.mcgill.ecse420.a3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixVectorMultiplyTask implements Callable<double[]> {
	
	double[][] matrix;
	double[] vector;
	
	ExecutorService exec = Executors.newCachedThreadPool();
	
	public MatrixVectorMultiplyTask(double[][] matrix, double[] vector){
		this.matrix = matrix;
		this.vector = vector;
	}
	
	public double[] call() throws Exception {
		
			
		if (matrix.length == 1) {
			double[] result = new double[1];
			result[0] = 0;
			
			for (int i=0; i<matrix[0].length; i++) {
				result[0] += matrix[0][i]*vector[i];
			}
			
			return result;
		} else {
			double[][] a1, a2, a3, a4; 
			double[] v1, v2;
			
			int vectorHalfSize = vector.length/2;
			boolean evenVectorLength = vector.length % 2 == 0;
			
			int matrixRowHalfSize = matrix.length/2;
			int newTopMatrixRowSize, newBottomMatrixRowSize;
			
			if (matrix.length % 2 == 0) {
				newTopMatrixRowSize = matrixRowHalfSize;
			} else {
				newTopMatrixRowSize = matrixRowHalfSize+1;
			}
			
			newBottomMatrixRowSize = matrixRowHalfSize;
			
			if (evenVectorLength) {
				a1 = new double[newTopMatrixRowSize][vectorHalfSize];
				a2 = new double[newTopMatrixRowSize][vectorHalfSize];
				a3 = new double[newBottomMatrixRowSize][vectorHalfSize];
				a4 = new double[newBottomMatrixRowSize][vectorHalfSize];
				
				v1 = new double[vectorHalfSize];
				v2 = new double[vectorHalfSize];
			} else {
				a1 = new double[newTopMatrixRowSize][vectorHalfSize+1];
				a2 = new double[newTopMatrixRowSize][vectorHalfSize];
				a3 = new double[newBottomMatrixRowSize][vectorHalfSize+1];
				a4 = new double[newBottomMatrixRowSize][vectorHalfSize];
				
				v1 = new double[vectorHalfSize+1];
				v2 = new double[vectorHalfSize];
			}
			
			for (int i=0; i<matrix.length; i++) {
				for (int j=0; j<matrix[0].length; j++) {
					
					if (evenVectorLength) {
						if (i<newTopMatrixRowSize && j<vectorHalfSize) {
							a1[i][j] = matrix[i][j];
							v1[j] = vector[j];
							
						} else if (i<newTopMatrixRowSize && j>=vectorHalfSize) {
							a2[i][j-vectorHalfSize] = matrix[i][j];
							v2[j-vectorHalfSize] = vector[j];
							
						} else if (i>=newTopMatrixRowSize && j<vectorHalfSize) {
							a3[i-newTopMatrixRowSize][j] = matrix[i][j];
							v1[j] = vector[j];
							
						} else if (i>=newTopMatrixRowSize && j>=vectorHalfSize) {
							a4[i-newTopMatrixRowSize][j-vectorHalfSize] = matrix[i][j];
							v2[j-vectorHalfSize] = vector[j];
						}
					} else {
						if (i<newTopMatrixRowSize && j<(vectorHalfSize+1)) {
							a1[i][j] = matrix[i][j];
							v1[j] = vector[j];
							
						} else if (i<newTopMatrixRowSize && j>=(vectorHalfSize+1)) {
							a2[i][j-(vectorHalfSize+1)] = matrix[i][j];
							v2[j-(vectorHalfSize+1)] = vector[j];
							
						} else if (i>=newTopMatrixRowSize && j<(vectorHalfSize+1)) {
							a3[i-newTopMatrixRowSize][j] = matrix[i][j];
							v1[j] = vector[j];
							
						} else if (i>=newTopMatrixRowSize && j>=(vectorHalfSize+1)) {
							a4[i-newTopMatrixRowSize][j-(vectorHalfSize+1)] = matrix[i][j];
							v2[j-(vectorHalfSize+1)] = vector[j];
						}
						
					}
				}
			}
			
			

			Future<double[]> partialTop1 = exec.submit(new MatrixVectorMultiplyTask(a1, v1));
			Future<double[]> partialTop2 = exec.submit(new MatrixVectorMultiplyTask(a2, v2));			
			Future<double[]> partialBottom1 = exec.submit(new MatrixVectorMultiplyTask(a3, v1));
			Future<double[]> partialBottom2 = exec.submit(new MatrixVectorMultiplyTask(a4, v2));
			
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
			
		}
		
		
	}
}

