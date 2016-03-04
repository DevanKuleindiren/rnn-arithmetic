package com.devankuleindiren.rnnarithmetic;

public class RNNMaster {

    private RNNParameters rnnParameters;
    public final int numberOfThreads;

    private double[] errorValues;
    private double[] progressValues;
    private double minError;
    private double minProgress;
    private int bestIndex;
    private RNN[] rnns;

    public RNNMaster (RNNParameters rnnParameters, int numberOfThreads) {
        this.rnnParameters      = rnnParameters;
        this.numberOfThreads    = numberOfThreads;
        this.errorValues        = new double[numberOfThreads];
        this.progressValues     = new double[numberOfThreads];
        this.rnns               = new RNN[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            rnns[i] = new RNN(this, rnnParameters, i);
        }
    }

    public synchronized void updateError (double error, double progress, int threadID) {
        errorValues[threadID]       = error;
        progressValues[threadID]    = progress;

        double minProgress = progress;
        for (int i = 0; i < numberOfThreads; i++) {
            if (errorValues[i] < minError) {
                minError = errorValues[i];
                bestIndex = i;
            }
            if (progressValues[i] < minProgress) minProgress = progressValues[i];
        }

        printProgress(minProgress, minError);

        boolean allComplete = true;
        for (int i = 0; i < numberOfThreads; i++) {
            if (progressValues[i] < 1) allComplete = false;
        }
        if (allComplete) {
            System.out.println();
        }
    }

    private void printProgress (double progress, double error) {
        int quantisedProgress50     = (int) (progress * 50);
        int quantisedProgress100    = (int) (progress * 100);

        System.out.print("\r[");
        for (int i = 0; i < quantisedProgress50; i++) {
            System.out.print("#");
        }
        for (int i = 0; i < (50 - quantisedProgress50); i++) {
            System.out.print(" ");
        }
        System.out.print("] " + quantisedProgress100 + "%   Error: " + error);
    }

    public double train (Matrix[][] inputBatch) throws MatrixDimensionMismatchException {

        Thread[] trainThreads = new Thread[numberOfThreads];

        // Initialise the errors
        minError = Double.MAX_VALUE;
        minProgress = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            errorValues[i] = Double.MAX_VALUE;
            progressValues[i] = 0;
        }

        // Start the training
        for (int i = 0; i < numberOfThreads; i++) {
            rnns[i].setCurrentInputs(inputBatch);
            trainThreads[i] = new Thread(rnns[i]);
            trainThreads[i].setDaemon(true);
            trainThreads[i].start();
        }

        // Wait for each thread to finish
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                trainThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return errorValues[bestIndex];
    }

    public Matrix[] use (Matrix[] inputs) throws MatrixDimensionMismatchException {
        return rnns[bestIndex].use(inputs);
    }
}
