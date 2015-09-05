package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class RNN {

    // THE NUMBER OF NODES IN EACH LAYER, EXCLUDING BIAS NODES
    private static int inputNodesNo;
    private static int hiddenNeuronNo;
    private static int outputNeuronNo;

    // WEIGHT MATRICES
    private static Matrix weightsIH; // FROM INPUT TO HIDDEN LAYER
    private static Matrix weightsHH; // FROM HIDDEN LAYER TO ITSELF
    private static Matrix weightsHO; // FROM HIDDEN LAYER TO OUTPUT

    // INITIAL HIDDEN ACTIVATIONS
    private static Matrix initialHiddenActs;

    // SINGLETON SO ALL REFERENCES TO THE RNN ARE THE SAME
    private static RNN instance = null;

    private RNN () {}

    public static RNN getInstance (int inputNodesNo, int hiddenNeuronNo, int outputNeuronNo) {
        if (instance == null) {
            instance = new RNN();

            instance.inputNodesNo = inputNodesNo;
            instance.hiddenNeuronNo = hiddenNeuronNo;
            instance.outputNeuronNo = outputNeuronNo;

            weightsIH = new Matrix(inputNodesNo + 1, hiddenNeuronNo); // ASSUMING BIAS EXCLUDED IN inputNodesNo
            weightsHH = new Matrix(hiddenNeuronNo, hiddenNeuronNo);   // ASSUMING BIAS EXCLUDED IN hiddenNeuronNo
            weightsHO = new Matrix(hiddenNeuronNo + 1, outputNeuronNo);

            initialHiddenActs = new Matrix(1, hiddenNeuronNo);

            initWeightsAndActs();
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
    public static void initWeightsAndActs () {
        fillRandom(weightsIH, inputNodesNo + 1);
        fillRandom(weightsHH, hiddenNeuronNo);
        fillRandom(weightsHO, hiddenNeuronNo);

        fillRandom(initialHiddenActs, hiddenNeuronNo);
    }

    // TRAIN THE RNN
    public double train (Matrix[][] inputTargetPairs, double lR, int iterationNo) throws MatrixDimensionMismatchException {

        // SEPARATE INPUTS & TARGETS
        Matrix[] inputs = inputTargetPairs[0];
        Matrix[] targets = inputTargetPairs[1];

        Matrix[] hiddenActivations;
        Matrix[] outputActivations;

        // DELTA O
        Matrix deltaO[] = new Matrix[inputs.length];
        // DELTA H
        Matrix deltaH[] = new Matrix[inputs.length];

        // ERROR MEASURE
        double error = 0;

        for (int iteration = 0; iteration < iterationNo; iteration++) {

            // PERFORM A FORWARD PASS THROUGH THE RNN
            // STORE ALL ACTIVATIONS ALONG THE WAY
            hiddenActivations = inputToHidden(inputs);
            outputActivations = hiddenToOutput(hiddenActivations);

            // CREATE REQUIRED MATRICES FILLED WITH ONES
            Matrix outputOnes = Matrix.onesMatrix(1, outputNeuronNo);
            Matrix hiddenOnes = Matrix.onesMatrix(1, hiddenNeuronNo);

            // COMPUTE DELTA Os AND OUTPUT ERROR
            deltaO[0] = new Matrix(new double[1][outputNeuronNo]);

            error = 0;

            for (int t = 1; t < inputs.length; t++) {

                // COMPUTE OUTPUT ERROR
                if (iteration % 10 == 0) {
                    for (int k = 0; k < outputNeuronNo; k++) {
                        error += Math.pow(targets[t].get(0, k) - outputActivations[t].get(0, k), 2);
                    }
                }

                // COMPUTE DELTA O^T
                deltaO[t] = targets[t].subtract(outputActivations[t]).multiplyEach(outputActivations[t]).multiplyEach(outputOnes.subtract(outputActivations[t])).scalarMultiply(-1.0);
            }

            // OCCASIONALLY DISPLAY ERROR
            if (iteration % 10 == 0) {
                System.out.println("Error measure: " + error);
            }

            // COMPUTE DELTA Hs
            int T = inputs.length - 1;
            Matrix weightsHOWithoutBias = weightsHO.removeBiasRow();
            deltaH[T] = hiddenActivations[T].multiplyEach(hiddenOnes.subtract(hiddenActivations[T])).multiplyEach(deltaO[T].multiply(weightsHOWithoutBias.transpose()));
            for (int t = inputs.length - 2; t > 0; t--) {
                // COMPUTE DELTA H^T
                deltaH[t] = hiddenActivations[t].multiplyEach(hiddenOnes.subtract(hiddenActivations[t])).multiplyEach(deltaO[t].multiply(weightsHOWithoutBias.transpose()).add(deltaH[t+1].multiply(weightsHH.transpose())));
            }

            // COMPUTE ERROR GRADIENTS
            Matrix dEdWho = new Matrix(new double[weightsHO.getHeight()][weightsHO.getWidth()]);
            Matrix dEdWhh = new Matrix(new double[weightsHH.getHeight()][weightsHH.getWidth()]);
            Matrix dEdWih = new Matrix(new double[weightsIH.getHeight()][weightsIH.getWidth()]);
            for (int t = 1; t < inputs.length; t++) {
                dEdWho = dEdWho.add(hiddenActivations[t].addBiasColumn().transpose().multiply(deltaO[t]));
                dEdWhh = dEdWhh.add(hiddenActivations[t-1].transpose().multiply(deltaH[t]));
                dEdWih = dEdWih.add(inputs[t].transpose().multiply(deltaH[t]));
            }
            Matrix dEdH0 = deltaH[1].multiply(weightsHH.transpose());

            // UPDATE WEIGHTS FROM HIDDEN TO OUTPUT
            weightsHO = weightsHO.subtract(dEdWho.scalarMultiply(lR));
            weightsHH = weightsHH.subtract(dEdWhh.scalarMultiply(lR));
            weightsIH = weightsIH.subtract(dEdWih.scalarMultiply(lR));

            // UPDATE INITIAL HIDDEN ACTIVATIONS
            initialHiddenActs = initialHiddenActs.subtract(dEdH0.scalarMultiply(lR));
        }

        return error;
    }

    public Matrix[] use (Matrix[] inputs) throws MatrixDimensionMismatchException {

        // PERFORM A FORWARD PASS THROUGH THE RNN
        // STORE ALL ACTIVATIONS ALONG THE WAY
        Matrix[] hiddenActivations = inputToHidden(inputs);
        Matrix[] outputActivations = hiddenToOutput(hiddenActivations);

        return outputActivations;
    }

    private Matrix[] inputToHidden (Matrix[] inputs) throws MatrixDimensionMismatchException {

        Matrix[] hiddenActivations = new Matrix[inputs.length];
        hiddenActivations[0] = initialHiddenActs;

        for (int time = 1; time < inputs.length; time++) {
            hiddenActivations[time] = inputs[time].multiply(weightsIH).add(hiddenActivations[time - 1].multiply(weightsHH));
            hiddenActivations[time].applyLogisticActivation();
        }

        return hiddenActivations;
    }

    private Matrix[] hiddenToOutput (Matrix[] hiddenActivations) throws MatrixDimensionMismatchException {

        Matrix[] outputActivations = new Matrix[hiddenActivations.length];
        outputActivations[0] = new Matrix(new double[1][outputNeuronNo]);

        for (int time = 1; time < hiddenActivations.length; time++) {
            outputActivations[time] = hiddenActivations[time].addBiasColumn().multiply(weightsHO);
            outputActivations[time].applyLogisticActivation();
        }

        return outputActivations;
    }
}
