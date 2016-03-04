package com.devankuleindiren.rnnarithmetic;

public class RNNTrainingParameters {
    public final double lR;
    public final double momentum;
    public final int iterationNo;

    public RNNTrainingParameters (double lR, double momentum, int iterationNo) {
        this.lR             = lR;
        this.momentum       = momentum;
        this.iterationNo    = iterationNo;
    }
}
