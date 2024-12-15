package com.cgvsu.math;

import com.cgvsu.math.matrix.Matrix4f;

public class AffineTransformations {

    //Параметры масштабирования
    private float Sx = 1;
    private float Sy = 1;
    private float Sz = 1;
    //Параметры поворота
    //УГЛЫ ПОВОРОТА ЗАДАЮТСЯ ПО ЧАСОВОЙ СРЕЛКЕ В ГРАДУСАХ
    private float Rx;
    private float Ry;
    private float Rz;
    //Параметры переноса
    private float Tx;
    private float Ty;
    private float Tz;

    private Matrix4f R = Matrix4f.unitMatrix();
    private Matrix4f S;
    private Matrix4f T;
    private Matrix4f A = Matrix4f.unitMatrix();

    private final Matrix4f U = Matrix4f.unitMatrix();

    public AffineTransformations() {
    }

    public AffineTransformations(float sx, float sy, float sz, float rx, float ry, float rz, float tx, float ty, float tz) {
        Sx = sx;
        Sy = sy;
        Sz = sz;
        Rx = rx;
        Ry = ry;
        Rz = rz;
        Tx = tx;
        Ty = ty;
        Tz = tz;

        calculateA();
    }

    private void calculateA() {
        //Матрица поворота задается единичной
        R = Matrix4f.unitMatrix();

        //Вычисление матрицы переноса
        T = new Matrix4f(new float[][]{{1, 0, 0, Tx},
                {0, 1, 0, Ty},
                {0, 0, 1, Tz},
                {0, 0, 0, 1}});
        //Вычисление матрицы масштабирования
        S = new Matrix4f(new float[][]{{Sx, 0, 0, 0},
                {0, Sy, 0, 0},
                {0, 0, Sz, 0},
                {0, 0, 0, 1}});

        //Вычисление тригонометрических функций
        float sinA = (float) Math.sin(Rx * Math.PI / 180);
        float cosA = (float) Math.cos(Rx * Math.PI / 180);

        float sinB = (float) Math.sin(Ry * Math.PI / 180);
        float cosB = (float) Math.cos(Ry * Math.PI / 180);

        float sinY = (float) Math.sin(Rz * Math.PI / 180);
        float cosY = (float) Math.cos(Rz * Math.PI / 180);

        //Матрицы поворота в каждой из плоскостей
        Matrix4f Z = new Matrix4f(new float[][]{{cosY, sinY, 0, 0},
                {-sinY, cosY, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}});

        Matrix4f Y = new Matrix4f(new float[][]{{cosB, 0, sinB, 0},
                {0, 1, 0, 0},
                {-sinB, 0, cosB, 0},
                {0, 0, 0, 1}});

        Matrix4f X = new Matrix4f(new float[][]{{1, 0, 0, 0},
                {0, cosA, sinA, 0},
                {0, -sinA, cosA, 0},
                {0, 0, 0, 1}});

        //Матрица аффинных преобразований принимается равной единице
        A = new Matrix4f(T.getMatrix());

        //Перемножение матриц поворота согласно их порядку
        R = Matrix4f.multiplication(R, X);
        R = Matrix4f.multiplication(R, Y);
        R = Matrix4f.multiplication(R, Z);


        //Вычисление матрицы аффинных преобразований
        A = Matrix4f.multiplication(A, R);
        A = Matrix4f.multiplication(A, S);
    }

    public Matrix4f getA() {
        return A;
    }

    /*public static Matrix4f modelMatrix(int tx, int ty, int tz,
                                       double alpha, double beta, double gamma,
                                       int sx, int sy, int sz){
        Matrix4f modelMatrix = new Matrix4f();
        Matrix4f transitionMatrix = translationMatrix(tx, ty, tz);
        Matrix4f rotationMatrix = makeMatrix4f(rotationMatrix(alpha,beta,gamma));
        Matrix4f scaleMatrix = makeMatrix4f(scaleMatrix(sx, sy, sz));
        modelMatrix.mul(transitionMatrix, rotationMatrix);
        modelMatrix.mul(scaleMatrix);
        return modelMatrix;
    }

    public static com.cgvsu.math.matrix.Matrix3f scaleMatrix(int sx, int sy, int sz) {
        com.cgvsu.math.matrix.Matrix3f S3 = new com.cgvsu.math.matrix.Matrix3f();
        S3.setElement(0,0,sx);
        S3.setElement(1,1,sy);
        S3.setElement(2,2,sz);
        return S3;
    }

    public static com.cgvsu.math.matrix.Matrix3f rotationAroundAxisMatrix(double alpha, AXIS axis) {
        com.cgvsu.math.matrix.Matrix3f R3 = new com.cgvsu.math.matrix.Matrix3f();
        float[] Ra = new float[]{(float) Math.cos(alpha), (float) Math.sin(alpha), (float) -Math.sin(alpha), (float) Math.cos(alpha)};
        int ind;
        if (axis == AXIS.x) ind = 0;
        else {
            if (axis == AXIS.y) ind = 1;
            else ind = 2;
        }

        int raInd = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == ind || j==ind) {
                    if (j == i) R3.setElement(i, j, 1);
                    else R3.setElement(i, j, 0);
                } else {
                    R3.setElement(i,j, Ra[raInd]);
                    raInd++;
                }
            }
        }
        return R3;
    }
    public static com.cgvsu.math.matrix.Matrix3f rotationMatrix (double alpha, double beta, double gamma){
        *//*Matrix3f Rx = rotationAroundAxisMatrix(alpha, AXIS.x);
        Matrix3f Ry = rotationAroundAxisMatrix(beta, AXIS.y);
        Matrix3f Rz = rotationAroundAxisMatrix(gamma, AXIS.z);
        Matrix3f R = new Matrix3f();
        R.mul(Rz, Ry);
        R.mul(Rx);*//*
        float y1 = (float) cos(beta), y2 = (float) sin(beta), y3 = (float) -sin(beta), y4 = (float) cos(beta);
        float x1 = (float) cos(alpha),x2 = (float) sin(alpha), x3 = (float) -sin(alpha), x4 = (float) cos(alpha);
        float z1 = (float) cos(gamma), z2 = (float) sin(gamma), z3 = (float) -sin(gamma), z4 = (float) cos(gamma);

        float[] floats = new float[]
                {(y1*z1), (x1*z2+y2*x3*z1), (z2*x2+z1*y2*x4),
                        (y1*z3), (x1*z4+y2*z3*x3), (z4*x2+x4*y2*z3),
                        (y3), (y4*x3), (x4*y4)};
        return new com.cgvsu.math.matrix.Matrix3f(floats);
    }

    public static Matrix4f translationMatrix(int tx, int ty, int tz) {
        Matrix4f T4 = new Matrix4f();
        for (int i = 0; i < 3; i++) {
            T4.setElement(i, i, 1);
        }
        T4.setColumn(3, new float[]{tx, ty, tz, 1});
        return T4;
    }

    public static Matrix4f makeMatrix4f(Matrix3f matrix3f) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(matrix3f);
        return matrix4f;
    }

    public enum AXIS {
        x,
        y,
        z
    }*/
}