package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class Diagnostics {

    public static void printBinaryInputBatch (Matrix[][] batch) {
        for (int col = 0; col < batch[0][0].getWidth() - 1; col++) {
            for (int row = batch[0].length - 1; row >= 0; row--) {
                System.out.print((int) batch[0][row].get(0, col) + " ");
            }
            System.out.println();
        }
        System.out.println();
        for (int row = batch[0].length - 1; row >= 0; row--) {
            System.out.print((int) batch[1][row].get(0, 0) + " ");
        }
        System.out.println();
        System.out.println();
    }
}