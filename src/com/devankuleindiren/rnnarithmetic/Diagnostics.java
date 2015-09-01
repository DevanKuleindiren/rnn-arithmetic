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

    public static void printMatrix (Matrix matrix, String name) {
        System.out.println();
        System.out.println(name.toUpperCase());
        for (int row = 0; row < matrix.getHeight(); row++) {
            for (int col = 0; col < matrix.getWidth(); col++) {
                if (row == 0 && col == 0) {
                    System.out.print("[" + matrix.get(row, col));
                } else if (col != 0){
                    System.out.print(", " + matrix.get(row, col));
                } else {
                    System.out.print(matrix.get(row, col));
                }
            }
            if (row == matrix.getHeight() - 1) {
                System.out.print("]");
            } else {
                System.out.println("; ");
            }
        }
        System.out.println();
    }
}
