package com.devankuleindiren.rnnarithmetic;

import java.util.Random;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class InputGenerator {

    public static Matrix[][] generateInput (int length) {
        return generateInput(length, 1);
    }

    public static Matrix[][] generateInput (int length, int noOfTrainingSequences) {

        Matrix[][] inputsAndTargets = new Matrix[2][length + 1];

        // NOTE: THIS INPUT-TARGET PAIR IS NEVER USED IN THE TRAINING OF THE RNN
        // THE ONLY THING DEFINED AT t = 0 IS THE HIDDEN ACTIVATIONS
        inputsAndTargets[0][0] = new Matrix(new double[noOfTrainingSequences][3]);
        inputsAndTargets[1][0] = new Matrix(new double[noOfTrainingSequences][1]);

        Random random = new Random();

        int[] carries = new int[noOfTrainingSequences];
        int bit1, bit2;

        for (int count = 1; count <= length; count++) {

            inputsAndTargets[0][count] = new Matrix(noOfTrainingSequences, 3);
            inputsAndTargets[1][count] = new Matrix(noOfTrainingSequences, 1);

            for (int tS = 0; tS < noOfTrainingSequences; tS++) {

                bit1 = random.nextInt(2);
                bit2 = random.nextInt(2);

                inputsAndTargets[0][count].set(tS, 0, bit1);
                inputsAndTargets[0][count].set(tS, 1, bit2);
                inputsAndTargets[0][count].set(tS, 2, -1);

                if ((bit1 == 1 && bit2 == 0 && carries[tS] == 0) ||
                        (bit1 == 0 && bit2 == 1 && carries[tS] == 0) ||
                        (bit1 == 0 && bit2 == 0 && carries[tS] == 1) ||
                        (bit1 == 1 && bit2 == 1 && carries[tS] == 1)) {
                    inputsAndTargets[1][count].set(tS, 0, 1);
                } else {
                    inputsAndTargets[1][count].set(tS, 0, 0);
                }

                if ((bit1 == 1 && bit2 == 1) ||
                        (bit1 == 1 && carries[tS] == 1) ||
                        (bit2 == 1 && carries[tS] == 1)) {
                    carries[tS] = 1;
                } else {
                    carries[tS] = 0;
                }
            }
        }

        return inputsAndTargets;
    }

    public static Matrix[][] generateInput (String input1, String input2) {

        Matrix[][] inputsAndTargets = new Matrix[2][input1.length() + 1];

        // NOTE: THIS INPUT-TARGET PAIR IS NEVER USED IN THE TRAINING OF THE RNN
        // THE ONLY THING DEFINED AT t = 0 IS THE HIDDEN ACTIVATIONS
        inputsAndTargets[0][0] = new Matrix(new double[1][3]);
        inputsAndTargets[1][0] = new Matrix(new double[1][1]);

        char[] inputs1 = input1.toCharArray();
        char[] inputs2 = input2.toCharArray();

        int bit1, bit2;
        int carry = 0;

        for (int count = 1; count <= input1.length(); count++) {

            int charIndex = input1.length() - count;

            if (inputs1[charIndex] == '1') {
                bit1 = 1;
            } else {
                bit1 = 0;
            }

            if (inputs2[charIndex] == '1') {
                bit2 = 1;
            } else {
                bit2 = 0;
            }

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
        }

        return inputsAndTargets;
    }
}
