package ca.mcgill.ecse420.a1;

public class MatrixPartialMultiplier implements Runnable {
    int startingRow;
    double[][] a;
    double[][] b;
    double[][] c;
    int NUMBER_THREADS;

    public MatrixPartialMultiplier(double[][] a, double[][] b, double[][] c, int threadNumber, int NUMBER_THREADS) {
        this.startingRow = threadNumber;
        this.a = a;
        this.b = b;
        this.c = c;
        this.NUMBER_THREADS = NUMBER_THREADS;

    }

    public void run() {
        int numRowsA = a.length;
        int numColumnsB = b[0].length;
        int numColumnsA = a[0].length;

        // Compute the result of the multiplication matrix
        for (int rowA = startingRow; rowA < numRowsA; rowA += NUMBER_THREADS) {

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
