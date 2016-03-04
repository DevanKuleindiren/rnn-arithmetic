package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 02/09/15.
 */
public class Testing {
    public static void test (RNNMaster rnnMaster, String input1, String input2) throws MatrixDimensionMismatchException {
        if (input1.length() == input2.length()) {

            Matrix[][] inputBatch = InputGenerator.generateInput(input1, input2);

            Matrix[] outputs = rnnMaster.use(inputBatch[0]);

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
