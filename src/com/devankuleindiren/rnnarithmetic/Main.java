package com.devankuleindiren.rnnarithmetic;

import java.util.regex.Pattern;

/**
 * Created by Devan Kuleindiren on 28/08/15.
 */
public class Main {

    public static void main (String[] args) {
//        String num1 = args[0];
//        String num2 = args[1];
//
//        String regex = "[01]*";
//        boolean isValid = true;
//
//        // CHECK STRINGS ARE BINARY STRINGS
//        if (!Pattern.matches(regex, num1)) {
//            System.out.println(num1 + " is not a valid binary number.");
//            isValid = false;
//        }
//        if (!Pattern.matches(regex, num2)) {
//            System.out.println(num2 + " is not a valid binary number.");
//            isValid = false;
//        }
//
//        if (isValid) {
//            // PAD STRINGS TO EQUAL LENGTH
//            if (num1.length() < num2.length()) {
//                for (int len = num1.length(); len < num2.length(); len++) {
//                    num1 += "0";
//                }
//            } else if (num2.length() < num1.length()) {
//                for (int len = num2.length(); len < num1.length(); len++) {
//                    num2 += "0";
//                }
//            }
//
//            // GET RNN TO PERFORM ADDITION
//
//        }

        RNN rnn = RNN.getInstance(2, 3, 1);
        Matrix[][] inputBatch = InputGenerator.generateInput(1000);
        try {
            double error = rnn.train(inputBatch, 0.01, 100);
        } catch (MatrixDimensionMismatchException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
