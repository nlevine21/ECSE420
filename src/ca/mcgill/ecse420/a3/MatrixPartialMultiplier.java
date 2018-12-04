package ca.mcgill.ecse420.a3;

/**
 * Class which handles the partial multiplication of two matrices
 */
public class MatrixPartialMultiplier implements Runnable {

    int startingRow;
    double[][] matrix;
    double[] vector;

    double[] result;

    int threadNumber;
    int numberThreads;

    /**
     * Constructor
     *
     * Initialize all properties
     *
     * @param a is the matrix A to be multiplied with B
     * @param b is the matrix B to be multiplied by A
     * @param c is the result matrix of A X B
     * @param threadNumber is the number of the executing thread
     * @param numberThreads is the total number of threads being executed
     */
    public MatrixPartialMultiplier(double[][] matrix, double[] vector, double[] result, int threadNumber, int numberThreads) {
        this.startingRow = threadNumber;
        this.matrix = matrix;
        this.vector = vector;
        this.threadNumber = threadNumber;
        this.numberThreads = numberThreads;
        this.result = result;
    }

    /**
     * Implemented run method
     *
     * Task will start by looking at the startingRow in Matrix A and will compute
     * the resulting row in the result Matrix C. It will then skip NUMBER_THREADS rows
     * as these rows will be computed by the other threads and repeat the process.
     *
     */
    public void run() {
        int numRows = matrix.length;
        int numColumns = matrix[0].length;

        // Only compute the required rows for the given thread
        for (int row = startingRow; row < numRows; row += numberThreads) {

            double entry = 0;

            for (int column = 0; column < numColumns; column++) {
                entry += matrix[row][column] * vector[column];
            }

            result[row] = entry;
            
        }
    }
}
