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

        System.out.println("\nTraining RNN...\n");

        // SEPARATE INPUTS & TARGETS
        Matrix[] inputs = inputTargetPairs[0];
        Matrix[] targets = inputTargetPairs[1];

        // GET NUMBER OF TRAINING SEQUENCES
        int noOfTrainingSequences = inputs[0].getHeight();

        // MATRICES TO STORE THE HIDDEN AND OUTPUT ACTIVATIONS
        Matrix[] hiddenActivations;
        Matrix[] outputActivations;

        // DELTA O
        Matrix deltaO[] = new Matrix[inputs.length];
        // DELTA H
        Matrix deltaH[] = new Matrix[inputs.length];

        // ERROR MEASURE
        double error = 0;

        // SUCCESS BOOLEAN
        boolean trainSuccessful = false;

        while (!trainSuccessful) {

            // CREATE REQUIRED MATRICES FILLED WITH ONES
            Matrix outputOnes = Matrix.onesMatrix(noOfTrainingSequences, outputNeuronNo);
            Matrix hiddenOnes = Matrix.onesMatrix(noOfTrainingSequences, hiddenNeuronNo);

            // INITIALISE DELTA Os
            deltaO[0] = new Matrix(new double[noOfTrainingSequences][outputNeuronNo]);

            for (int iteration = 0; iteration < iterationNo; iteration++) {

                // PERFORM A FORWARD PASS THROUGH THE RNN
                // STORE ALL ACTIVATIONS ALONG THE WAY
                hiddenActivations = inputToHidden(inputs);
                outputActivations = hiddenToOutput(hiddenActivations);

                // COMPUTE DELTA Os AND OUTPUT ERROR
                error = 0;

                for (int t = 1; t < inputs.length; t++) {

                    // COMPUTE OUTPUT ERROR
                    for (int tS = 0; tS < noOfTrainingSequences; tS++) {
                        for (int k = 0; k < outputNeuronNo; k++) {
                            error += Math.pow(targets[t].get(tS, k) - outputActivations[t].get(tS, k), 2);
                        }
                    }

                    // COMPUTE DELTA O^T
                    deltaO[t] = targets[t].subtract(outputActivations[t]).multiplyEach(outputActivations[t]).multiplyEach(outputOnes.subtract(outputActivations[t])).scalarMultiply(-1.0);
                }

                // OCCASIONALLY DISPLAY ERROR
                if (iteration % 1000 == 0) {
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
                Matrix dEdH0 = deltaH[1].multiply(weightsHH.transpose()).sumColumns();

                // UPDATE WEIGHTS FROM HIDDEN TO OUTPUT
                weightsHO = weightsHO.subtract(dEdWho.scalarMultiply(lR));
                weightsHH = weightsHH.subtract(dEdWhh.scalarMultiply(lR));
                weightsIH = weightsIH.subtract(dEdWih.scalarMultiply(lR));

                // UPDATE INITIAL HIDDEN ACTIVATIONS
                initialHiddenActs = initialHiddenActs.subtract(dEdH0.scalarMultiply(lR));
            }
            if (error < 0.01 * noOfTrainingSequences) {
                trainSuccessful = true;
                System.out.println("\nRNN trained.\n");
            } else {
                System.out.println("Re-trying...");
                initWeightsAndActs();
            }
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

        int noOfTrainingSequences = inputs[0].getHeight();
        hiddenActivations[0] = new Matrix(noOfTrainingSequences, hiddenNeuronNo);
        for (int tS = 0; tS < noOfTrainingSequences; tS++) {
            for (int col = 0; col < hiddenNeuronNo; col++) {
                hiddenActivations[0].set(tS, col, initialHiddenActs.get(0, col));
            }
        }

        for (int time = 1; time < inputs.length; time++) {
            hiddenActivations[time] = inputs[time].multiply(weightsIH).add(hiddenActivations[time - 1].multiply(weightsHH));
            hiddenActivations[time].applyLogisticActivation();
        }

        return hiddenActivations;
    }

    private Matrix[] hiddenToOutput (Matrix[] hiddenActivations) throws MatrixDimensionMismatchException {

        Matrix[] outputActivations = new Matrix[hiddenActivations.length];

        int noOfTrainingSequences = hiddenActivations[0].getHeight();
        outputActivations[0] = new Matrix(new double[noOfTrainingSequences][outputNeuronNo]);

        for (int time = 1; time < hiddenActivations.length; time++) {
            outputActivations[time] = hiddenActivations[time].addBiasColumn().multiply(weightsHO);
            outputActivations[time].applyLogisticActivation();
        }

        return outputActivations;
    }
}
