package com.devankuleindiren.rnnarithmetic;

public class RNNParameters {

    public final int inputNodesNo;
    public final int hiddenNeuronNo;
    public final int outputNeuronNo;
    public final RNNTrainingParameters trainingParameters;

    public RNNParameters (int inputNodesNo,
                          int hiddenNeuronNo,
                          int outputNeuronNo,
                          RNNTrainingParameters trainingParameters) {
        this.inputNodesNo           = inputNodesNo;
        this.hiddenNeuronNo         = hiddenNeuronNo;
        this.outputNeuronNo         = outputNeuronNo;
        this.trainingParameters     = trainingParameters;
    }
}
