package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class RNN implements Runnable {

    // The master controlling this RNN thread
    private RNNMaster master;
    private int threadID;

    // THE NUMBER OF NODES IN EACH LAYER, EXCLUDING BIAS NODES
    private RNNParameters rnnParameters;
    private RNNTrainingParameters tP;

    // WEIGHT MATRICES
    private Matrix weightsIH; // FROM INPUT TO HIDDEN LAYER
    private Matrix weightsHH; // FROM HIDDEN LAYER TO ITSELF
    private Matrix weightsHO; // FROM HIDDEN LAYER TO OUTPUT

    // INITIAL HIDDEN ACTIVATIONS
    private Matrix initialHiddenActs;

    // Current inputs
    private Matrix[][] inputTargetPairs;

    // SINGLETON SO ALL REFERENCES TO THE RNN ARE THE SAME
    public RNN (RNNMaster master, RNNParameters rnnParameters, int threadID) {
        this.master             = master;
        this.rnnParameters      = rnnParameters;
        this.tP                 = rnnParameters.trainingParameters;
        this.threadID           = threadID;

        // ASSUMING BIAS EXCLUDED IN inputNodesNo
        this.weightsIH = new Matrix(rnnParameters.inputNodesNo + 1, rnnParameters.hiddenNeuronNo);
        // ASSUMING BIAS EXCLUDED IN hiddenNeuronNo
        this.weightsHH = new Matrix(rnnParameters.hiddenNeuronNo, rnnParameters.hiddenNeuronNo);
        this.weightsHO = new Matrix(rnnParameters.hiddenNeuronNo + 1, rnnParameters.outputNeuronNo);

        this.initialHiddenActs = new Matrix(1, rnnParameters.hiddenNeuronNo);

        initWeightsAndActs();
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
    public void initWeightsAndActs () {
        fillRandom(weightsIH, rnnParameters.inputNodesNo + 1);
        fillRandom(weightsHH, rnnParameters.hiddenNeuronNo);
        fillRandom(weightsHO, rnnParameters.hiddenNeuronNo);

        fillRandom(initialHiddenActs, rnnParameters.hiddenNeuronNo);
    }

    // TRAIN THE RNN
    public double train () throws MatrixDimensionMismatchException {
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

        // CREATE REQUIRED MATRICES FILLED WITH ONES
        Matrix outputOnes = Matrix.onesMatrix(noOfTrainingSequences, rnnParameters.outputNeuronNo);
        Matrix hiddenOnes = Matrix.onesMatrix(noOfTrainingSequences, rnnParameters.hiddenNeuronNo);

        // INITIALISE DELTA Os
        deltaO[0] = new Matrix(new double[noOfTrainingSequences][rnnParameters.outputNeuronNo]);

        // INITIALISE GRADIENT MATRICES
        Matrix dWho = new Matrix(new double[weightsHO.getHeight()][weightsHO.getWidth()]);
        Matrix dWhh = new Matrix(new double[weightsHH.getHeight()][weightsHH.getWidth()]);
        Matrix dWih = new Matrix(new double[weightsIH.getHeight()][weightsIH.getWidth()]);

        Matrix dWhoPrev;
        Matrix dWhhPrev;
        Matrix dWihPrev;

        Matrix dEdWho;
        Matrix dEdWhh;
        Matrix dEdWih;

        for (int iteration = 0; iteration < tP.iterationNo + 1; iteration++) {

            // PERFORM A FORWARD PASS THROUGH THE RNN
            // STORE ALL ACTIVATIONS ALONG THE WAY
            hiddenActivations = inputToHidden(inputs);
            outputActivations = hiddenToOutput(hiddenActivations);

            // COMPUTE DELTA Os AND OUTPUT ERROR
            error = 0;

            for (int t = 1; t < inputs.length; t++) {

                // COMPUTE OUTPUT ERROR
                for (int tS = 0; tS < noOfTrainingSequences; tS++) {
                    for (int k = 0; k < rnnParameters.outputNeuronNo; k++) {
                        error += Math.pow(targets[t].get(tS, k) - outputActivations[t].get(tS, k), 2);
                    }
                }

                // COMPUTE DELTA O^T
                deltaO[t] = targets[t].subtract(outputActivations[t]).multiplyEach(outputActivations[t]).multiplyEach(outputOnes.subtract(outputActivations[t])).scalarMultiply(-1.0);
            }

            // OCCASIONALLY DISPLAY ERROR
            if (iteration % 100 == 0) {
                master.updateError(error, iteration / (double) tP.iterationNo, threadID);
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
            dWhoPrev = (Matrix) dWho.clone();
            dWhhPrev = (Matrix) dWhh.clone();
            dWihPrev = (Matrix) dWih.clone();

            dEdWho = new Matrix(new double[weightsHO.getHeight()][weightsHO.getWidth()]);
            dEdWhh = new Matrix(new double[weightsHH.getHeight()][weightsHH.getWidth()]);
            dEdWih = new Matrix(new double[weightsIH.getHeight()][weightsIH.getWidth()]);

            for (int t = 1; t < inputs.length; t++) {
                dEdWho = dEdWho.add(hiddenActivations[t].addBiasColumn().transpose().multiply(deltaO[t]));
                dEdWhh = dEdWhh.add(hiddenActivations[t-1].transpose().multiply(deltaH[t]));
                dEdWih = dEdWih.add(inputs[t].transpose().multiply(deltaH[t]));
            }
            Matrix dEdH0 = deltaH[1].multiply(weightsHH.transpose()).sumColumns();

            // APPLY LEARNING RATE AND MOMENTUM
            dWho = dWhoPrev.scalarMultiply(tP.momentum).subtract(dEdWho.scalarMultiply(tP.lR));
            dWhh = dWhhPrev.scalarMultiply(tP.momentum).subtract(dEdWhh.scalarMultiply(tP.lR));
            dWih = dWihPrev.scalarMultiply(tP.momentum).subtract(dEdWih.scalarMultiply(tP.lR));

            // UPDATE WEIGHTS FROM HIDDEN TO OUTPUT
            weightsHO = weightsHO.add(dWho);
            weightsHH = weightsHH.add(dWhh);
            weightsIH = weightsIH.add(dWih);

            // UPDATE INITIAL HIDDEN ACTIVATIONS
            initialHiddenActs = initialHiddenActs.subtract(dEdH0.scalarMultiply(tP.lR));
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
        hiddenActivations[0] = new Matrix(noOfTrainingSequences, rnnParameters.hiddenNeuronNo);
        for (int tS = 0; tS < noOfTrainingSequences; tS++) {
            for (int col = 0; col < rnnParameters.hiddenNeuronNo; col++) {
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
        outputActivations[0] = new Matrix(new double[noOfTrainingSequences][rnnParameters.outputNeuronNo]);

        for (int time = 1; time < hiddenActivations.length; time++) {
            outputActivations[time] = hiddenActivations[time].addBiasColumn().multiply(weightsHO);
            outputActivations[time].applyLogisticActivation();
        }

        return outputActivations;
    }

    public void setCurrentInputs (Matrix[][] inputTargetPairs) {
        this.inputTargetPairs = inputTargetPairs;
    }

    @Override
    public void run() {
        try {
            train();
        } catch (MatrixDimensionMismatchException e) {
            e.printStackTrace();
        }
    }
}
