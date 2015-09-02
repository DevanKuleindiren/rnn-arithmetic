package com.devankuleindiren.rnnarithmetic;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class Matrix implements Cloneable {

    private int height;
    private int width;
    double[][] values;

    public Matrix (int height, int width) {
        this.height = height;
        this.width = width;
        values = new double[height][width];
    }

    public Matrix(double[][] values) {
        this.height = values.length;
        this.width = values[0].length;
        this.values = values;
    }

    public int getHeight () {
        return height;
    }

    public int getWidth () {
        return width;
    }

    public double get (int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            return values[row][col];
        }
        else return 0;
    }

    public void set (int row, int col, double newVal) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            values[row][col] = newVal;
        }
    }

    public static Matrix onesMatrix (int height, int width) {
        Matrix result = new Matrix(height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result.set(row, col, 1);
            }
        }
        return result;
    }

    public Matrix add (Matrix toAdd) throws MatrixDimensionMismatchException {
        if (height == toAdd.height && width == toAdd.width) {
            Matrix result = new Matrix(height, width);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    result.values[row][col] = values[row][col] + toAdd.values[row][col];
                }
            }
            return result;
        } else {
            throw new MatrixDimensionMismatchException("addition");
        }
    }

    public Matrix subtract (Matrix toSubtract) throws MatrixDimensionMismatchException {
        return add(toSubtract.scalarMultiply(-1.0));
    }

    public Matrix multiply (Matrix toMultiply) throws MatrixDimensionMismatchException {
        if (width == toMultiply.height) {
            Matrix result = new Matrix(height, toMultiply.width);
            for (int row = 0; row < result.height; row++) {
                for (int col = 0; col < result.width; col++) {
                    double value = 0;
                    for (int count = 0; count < width; count++) {
                        value += values[row][count] * toMultiply.values[count][col];
                    }
                    result.values[row][col] = value;
                }
            }
            return result;
        } else {
            throw new MatrixDimensionMismatchException("multiplication");
        }
    }

    public Matrix multiplyEach (Matrix toMultiply) throws MatrixDimensionMismatchException {
        if (height == toMultiply.height && width == toMultiply.width) {
            Matrix result = new Matrix(height, width);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    result.values[row][col] = values[row][col] * toMultiply.values[row][col];
                }
            }
            return result;
        } else {
            throw new MatrixDimensionMismatchException("multiplication");
        }
    }

    public Matrix transpose () {
        Matrix result = new Matrix(width, height);
        for (int row = 0; row < result.height; row++) {
            for (int col = 0; col < result.width; col++) {
                result.values[row][col] = values[col][row];
            }
        }
        return result;
    }

    public Matrix scalarMultiply (double scalar) {
        Matrix result = new Matrix(height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result.values[row][col] = values[row][col] * scalar;
            }
        }
        return result;
    }

    public void applyLogisticActivation () {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                values[row][col] = 1 / (1 + Math.exp(-values[row][col]));
            }
        }
    }

    public void rectify () {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (values[row][col] >= 0.5) {
                    values[row][col] = 1;
                } else {
                    values[row][col] = 0;
                }
            }
        }
    }

    public Matrix addBiasColumn () {
        Matrix result = new Matrix(height, width + 1);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result.values[row][col] = values[row][col];
            }
        }
        for (int row = 0; row < height; row++) {
            result.values[row][width] = -1;
        }
        return result;
    }

    public Matrix removeBiasRow () {
        Matrix result = new Matrix(height - 1, width);
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                result.values[row][col] = values[row][col];
            }
        }
        return result;
    }

    @Override
    public Object clone () {
        Matrix clone = new Matrix (height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                clone.values[row][col] = values[row][col];
            }
        }
        return clone;
    }
}
