package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class Diagnostics {

    public static void printInputBatch (Matrix[][] batch) {
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

    public static void printInputBatchWithOutputs (Matrix[][] batch, Matrix[] outputs, boolean showTargets) {
        for (int col = 0; col < batch[0][0].getWidth() - 1; col++) {
            System.out.print("INPUT " + (col + 1) + ": ");
            for (int row = batch[0].length - 1; row > 0; row--) {
                System.out.print((int) batch[0][row].get(0, col) + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.print("OUTPUT : ");
        for (int row = batch[0].length - 1; row > 0; row--) {
            System.out.print((int) outputs[row].get(0, 0) + " ");
        }
        if (showTargets) {
            System.out.println();
            System.out.print(" - - - - ");
            for (int row = batch[0].length - 1; row > 0; row--) {
                System.out.print("- ");
            }
            System.out.println();
            System.out.print("TARGET : ");
            for (int row = batch[0].length - 1; row > 0; row--) {
                System.out.print((int) batch[1][row].get(0, 0) + " ");
            }
        }
        System.out.println();
        System.out.println();
    }

    public static void printMatrix (Matrix matrix, String name) {
        System.out.println();
        System.out.print(name + " = ");
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

    public static void printMatrixArray (Matrix[] matrices, String name) {
        for (int count = 0; count < matrices.length; count++) {
            printMatrix(matrices[count], name + " " + count);
        }
    }
}
