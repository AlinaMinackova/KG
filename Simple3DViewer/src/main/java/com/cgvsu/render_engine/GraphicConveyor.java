package com.cgvsu.render_engine;


import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.math.vector.Vector3f;

import javax.vecmath.Point2f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        //трпнспонированный видовая матрица
        //напомнить поменять координаты на m20, m21, m22
        float[][] matrix = new float[][]{
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        return new Matrix4f(new float[][]{
                {tangentMinusOnDegree / aspectRatio, 0, 0, 0},
                {0, tangentMinusOnDegree, 0, 0},
                {0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 1.0F},
                {0, 0, 2 * (nearPlane * farPlane) / (nearPlane - farPlane), 0}

        });
    }


    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.x * matrix.getMatrix(0, 0)) + (vertex.y * matrix.getMatrix(1, 0)) + (vertex.z * matrix.getMatrix(2, 0)) + matrix.getMatrix(3, 0);
        final float y = (vertex.x * matrix.getMatrix(0, 1)) + (vertex.y * matrix.getMatrix(1, 1)) + (vertex.z * matrix.getMatrix(2, 1)) + matrix.getMatrix(3, 1);
        final float z = (vertex.x * matrix.getMatrix(0, 2)) + (vertex.y * matrix.getMatrix(1, 2)) + (vertex.z * matrix.getMatrix(2, 2)) + matrix.getMatrix(3, 2);
        final float w = (vertex.x * matrix.getMatrix(0, 3)) + (vertex.y * matrix.getMatrix(1, 3)) + (vertex.z * matrix.getMatrix(2, 3)) + matrix.getMatrix(3, 3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Vector3f multMatrix4OnVector3f(Matrix4f matrix, Vector3f vertex) {
        float x = matrix.getMatrix(0, 0) * vertex.getX() + matrix.getMatrix(0, 1) * vertex.getY() + matrix.getMatrix(0, 2) * vertex.getZ() + matrix.getMatrix(0, 3);
        float y = matrix.getMatrix(1, 0) * vertex.getX() + matrix.getMatrix(1, 1) * vertex.getY() + matrix.getMatrix(1, 2) * vertex.getZ() + matrix.getMatrix(1, 3);
        float z = matrix.getMatrix(2, 0) * vertex.getX() + matrix.getMatrix(2, 1) * vertex.getY() + matrix.getMatrix(2, 2) * vertex.getZ() + matrix.getMatrix(2, 3);

        return new Vector3f(x, y, z);
    }

    public static void multiplyMatrix4ByVector(final Matrix4f matrix, final com.cgvsu.math.vector.Vector3f vertex) {
        final float w = (vertex.x * matrix.getMatrix(3, 0)) + (vertex.y * matrix.getMatrix(3, 1)) + (vertex.z * matrix.getMatrix(3, 2)) + matrix.getMatrix(3, 3);
        vertex.x = ((vertex.x * matrix.getMatrix(0, 0)) + (vertex.y * matrix.getMatrix(0, 1)) + (vertex.z * matrix.getMatrix(0, 2)) + matrix.getMatrix(0, 3)) / w;
        vertex.y = ((vertex.x * matrix.getMatrix(1, 0)) + (vertex.y * matrix.getMatrix(1, 1)) + (vertex.z * matrix.getMatrix(1, 2)) + matrix.getMatrix(1, 3)) / w;
        vertex.z = ((vertex.x * matrix.getMatrix(2, 0)) + (vertex.y * matrix.getMatrix(2, 1)) + (vertex.z * matrix.getMatrix(2, 2)) + matrix.getMatrix(3, 2)) / w;
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
