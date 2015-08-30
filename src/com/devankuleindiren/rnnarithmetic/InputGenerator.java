package com.devankuleindiren.rnnarithmetic;

import java.util.Random;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class InputGenerator {

    public static Matrix[][] generateInput (int length) {

        Matrix[][] inputsAndTargets = new Matrix[2][length + 1];

        // NOTE: THIS INPUT-TARGET PAIR IS NEVER USED IN THE TRAINING OF THE RNN
        // THE ONLY THING DEFINED AT t = 0 IS THE HIDDEN ACTIVATIONS
        inputsAndTargets[0][0] = new Matrix(new double[1][3]);
        inputsAndTargets[1][0] = new Matrix(new double[1][1]);

        Random random = new Random();

        int carry = 0;
        int bit1 = random.nextInt(2);
        int bit2 = random.nextInt(2);

        for (int count = 1; count <= length; count++) {

            inputsAndTargets[0][count] = new Matrix(1, 3);
            inputsAndTargets[0][count].set(0, 0, bit1);
            inputsAndTargets[0][count].set(0, 1, bit2);
            inputsAndTargets[0][count].set(0, 2, -1);

            inputsAndTargets[1][count] = new Matrix(1, 1);
            if ((bit1 == 1 && bit2 == 0 && carry == 0) ||
                    (bit1 == 0 && bit2 == 1 && carry == 0) ||
                    (bit1 == 0 && bit2 == 0 && carry == 1) ||
                    (bit1 == 1 && bit2 == 1 && carry == 1)) {
                inputsAndTargets[1][count].set(0, 0, 1);
            } else {
                inputsAndTargets[1][count].set(0, 0, 0);
            }

            if ((bit1 == 1 && bit2 == 1) ||
                    (bit1 == 1 && carry == 1) ||
                    (bit2 == 1 && carry == 1)) {
                carry = 1;
            } else {
                carry = 0;
            }

            bit1 = random.nextInt(2);
            bit2 = random.nextInt(2);
        }

        return inputsAndTargets;
    }
}
