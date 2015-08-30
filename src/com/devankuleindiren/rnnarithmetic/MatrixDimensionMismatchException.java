package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class MatrixDimensionMismatchException extends Exception {

    public MatrixDimensionMismatchException (String operation) {
        super ("The matrix dimensions were invalid for " + operation + ".");
    }

}
