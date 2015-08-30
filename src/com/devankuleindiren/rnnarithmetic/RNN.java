package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class RNN {

    // THE NUMBER OF NODES IN EACH LAYER
    private static int inputNodesNo;
    private static int hiddenNeuronNo;
    private static int outputNeuronNo;

    // WEIGHT MATRICES
    private static Matrix weightsIH; // FROM INPUT TO HIDDEN LAYER
    private static Matrix weightsHH; // FROM HIDDEN LAYER TO ITSELF
    private static Matrix weightsHO; // FROM HIDDEN LAYER TO OUTPUT

    // SINGLETON SO ALL REFERENCES TO THE RNN ARE THE SAME
    private static RNN instance = null;

    private RNN () {}

    public static RNN getInstance (int inputNodesNo, int hiddenNeuronNo, int outputNeuronNo) {
        if (instance == null) {
            instance = new RNN();

            instance.inputNodesNo = inputNodesNo;
            instance.hiddenNeuronNo = hiddenNeuronNo;
            instance.outputNeuronNo = outputNeuronNo;

            weightsIH = new Matrix(inputNodesNo, hiddenNeuronNo); // ASSUMING BIAS INCLUDED IN inputNodesNo
            weightsHH = new Matrix(hiddenNeuronNo, hiddenNeuronNo);
            weightsHO = new Matrix(hiddenNeuronNo + 1, outputNeuronNo);

            initWeights();
        }
        return instance;
    }

    // INITIALISE EACH WEIGHT TO A RANDOM VALUE, X, IN THE RANGE -N^(-0.5) < X < N^(-0.5)
    // WHERE N IS THE NUMBER OF NODES IN THE LAYER BEFORE THE WEIGHTS
    private static void fillRandom (Matrix matrix, int noOfInputs) {
        for (int row = 0; row < matrix.getHeight(); row++) {
            for (int col = 0; col < matrix.getWidth(); col++) {
                matrix.set(row, col, (Math.random() * (2*(1/Math.pow(noOfInputs, 0.5)))) - (1/Math.pow(noOfInputs, 0.5)));
            }
        }
    }

    // INITIALISE THE WEIGHT ARRAYS
    public static void initWeights() {
        fillRandom(weightsIH, inputNodesNo);
        fillRandom(weightsHH, hiddenNeuronNo);
        fillRandom(weightsHO, hiddenNeuronNo);
    }

    // TRAIN THE RNN
    public double train (Matrix inputStream, Matrix targetStream, double lR, int iterationNo) {

        return 0;
    }

    public Matrix[] use (Matrix[][] inputBatch) throws MatrixDimensionMismatchException {

        // SEPARATE THE INPUTS & TARGETS
        Matrix[] inputs = inputBatch[0];
        Matrix[] targets = inputBatch[1];

        // PERFORM A FORWARD PASS THROUGH THE RNN
        // STORE ALL ACTIVATIONS ALONG THE WAY
        Matrix[] hiddenActivations = inputToHidden(inputs);
        Matrix[] outputActivations = hiddenToOutput(hiddenActivations);

        return outputActivations;
    }

    private Matrix[] inputToHidden (Matrix[] inputs) throws MatrixDimensionMismatchException {

        Matrix[] hiddenActivations = new Matrix[inputs.length];
        hiddenActivations[0] = new Matrix(new double[1][hiddenNeuronNo]);

        for (int time = 1; time < inputs.length; time++) {
            hiddenActivations[time] = inputs[time].multiply(weightsIH).add(hiddenActivations[time - 1].multiply(weightsHH));
            hiddenActivations[time].applyLogisticActivation();
        }

        return hiddenActivations;
    }

    private Matrix[] hiddenToOutput (Matrix[] hiddenActivations) throws MatrixDimensionMismatchException {

        Matrix[] outputActivations = new Matrix[hiddenActivations.length];
        outputActivations[0] = new Matrix(new double[1][1]);

        for (int time = 1; time < hiddenActivations.length; time++) {
            hiddenActivations[time] = hiddenActivations[time].addBiasColumn();
            outputActivations[time] = hiddenActivations[time].multiply(weightsHO);
            outputActivations[time].applyLogisticActivation();
        }

        return outputActivations;
    }
}
