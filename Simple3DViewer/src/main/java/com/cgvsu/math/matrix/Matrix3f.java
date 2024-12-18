package com.cgvsu.math.matrix;

import com.cgvsu.math.vector.Vector3f;

import static com.cgvsu.math.Global.EPS;

public class Matrix3f {
    public Matrix3f(final float[][] mat){
        if (mat.length != SIZE || mat[0].length != SIZE) {
            throw new IllegalArgumentException("Matrix must be 3x3");
        }
        this.mat = mat;
    }

    public Matrix3f() {
        this.mat = new float[SIZE][SIZE];
    }
    public Matrix3f(float num) {
        this.mat = new float[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            this.mat[i][i] = num;
        }
    }
    private float[][] mat;
    static final private int SIZE = 3;

    public static Matrix3f add(final Matrix3f m1, final Matrix3f m2){  //сложение
        Matrix3f res = new Matrix3f(new float[SIZE][SIZE]);
        for(int row = 0; row < SIZE; row++){
            for(int col = 0; col < SIZE; col++){
                res.mat[row][col] = m1.mat[row][col] + m2.mat[row][col];
            }
        }
        return res;
    }

    public static Matrix3f sub(final Matrix3f m1, final Matrix3f m2){ //вычитание
        Matrix3f res = new Matrix3f(new float[SIZE][SIZE]);
        for(int row = 0; row<SIZE; row++){
            for(int col = 0; col<SIZE; col++){
                res.mat[row][col] = m1.mat[row][col] - m2.mat[row][col];
            }
        }
        return res;
    }

    public static Matrix3f multiply(final Matrix3f m1, final Matrix3f m2){ //умножение
        Matrix3f res = new Matrix3f(new float[SIZE][SIZE]);
        for (int arr1row = 0; arr1row<SIZE; arr1row++){
            for (int arr2col = 0; arr2col<SIZE; arr2col++){
                float a = 0;
                for (int i = 0; i<SIZE; i++){
                    a+=m1.mat[arr1row][i] * m2.mat[i][arr2col];
                }
                res.mat[arr1row][arr2col] = a;
            }
        }
        return res;
    }

    public Vector3f mulVector(final Vector3f v) { //умнжожение на вектор
        float[] arr = new float[]{v.x, v.y, v.z};
        float[] res = new float[SIZE];
        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col<SIZE; col++){
                res[row] += this.mat[row][col]*arr[col];
            }
        }
        return new Vector3f(res[0], res[1], res[2]);
    }

    public void transpose() {
        for(int row = 0; row < SIZE; row++) {
            for (int col = row + 1; col < SIZE; col++) {
                float a = this.mat[row][col];
                this.mat[row][col] = this.mat[col][row];
                this.mat[col][row] = a;
            }
        }
    }

    public float determinant() {
        return mat[0][0] * (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1])
                - mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0])
                + mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
    }

    public boolean equals(final Matrix3f other) {
        for(int row = 0; row<SIZE; row++){
            for(int col = 0; col<SIZE; col++){
                if (Math.abs(this.mat[row][col] - other.mat[row][col]) >= EPS){
                    return false;
                }
            }
        }
        return true;
    }

}
