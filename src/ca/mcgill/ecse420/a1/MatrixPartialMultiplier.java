package ca.mcgill.ecse420.a1;

/**
 * Class which handles the partial multiplication of two matrices
 */
public class MatrixPartialMultiplier implements Runnable {

    int startingRow;
    double[][] a;
    double[][] b;

    double[][] c;

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
    public MatrixPartialMultiplier(double[][] a, double[][] b, double[][] c, int threadNumber, int numberThreads) {
        this.startingRow = threadNumber;
        this.a = a;
        this.b = b;
        this.c = c;
        this.numberThreads = numberThreads;
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
        int numRowsA = a.length;
        int numColumnsB = b[0].length;
        int numColumnsA = a[0].length;

        // Only compute the required rows for the given thread
        for (int rowA = startingRow; rowA < numRowsA; rowA += numberThreads) {

            for (int columnB = 0; columnB < numColumnsB; columnB++) {

                double cEntry = 0;

                for (int columnA = 0; columnA < numColumnsA; columnA++) {
                    cEntry += a[rowA][columnA] * b[columnA][columnB];
                }

                c[rowA][columnB] = cEntry;
            }
        }
    }
}
