package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 02/09/15.
 */
public class Testing {

    public static void test (int inputLength) throws MatrixDimensionMismatchException {

        // GENERATE INPUTS, TARGETS AND OUTPUTS
        Matrix[][] inputBatch = InputGenerator.generateInput(inputLength);
        RNN rnn = RNN.getInstance(1, 3, 2);
        Matrix[] outputs = rnn.use(inputBatch[0]);

        for (Matrix o : outputs) {
            o.rectify();
        }

        // PRINT THEM
        Diagnostics.printInputBatchWithOutputs(inputBatch, outputs, true);

        // CALCULATE AND PRINT PERCENTAGE CORRECT
        double correct = 0;
        for (int i = 0; i < inputLength; i++) {
            if (outputs[i].get(0, 0) == inputBatch[1][i].get(0, 0)) {
                correct++;
            }
        }
        System.out.println("The RNN got " + (int) correct + "/" + inputLength + " bits correct. (" + ((correct / inputLength) * 100) + "%)");
    }

    public static void test (String input1, String input2) throws MatrixDimensionMismatchException {
        if (input1.length() == input2.length()) {

            Matrix[][] inputBatch = InputGenerator.generateInput(input1, input2);

            RNN rnn = RNN.getInstance(2, 3, 1);
            Matrix[] outputs = rnn.use(inputBatch[0]);

            for (Matrix o : outputs) {
                o.rectify();
            }

            // PRINT RESULT
            System.out.println();
            Diagnostics.printInputBatchWithOutputs(inputBatch, outputs, false);

        } else {
            System.out.println("Test failed.");
        }
    }
}
