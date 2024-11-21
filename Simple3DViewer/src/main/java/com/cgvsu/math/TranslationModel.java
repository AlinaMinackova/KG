package com.cgvsu.math;

import com.cgvsu.model.Model;

import javax.vecmath.Matrix4f;

public class TranslationModel {
    public static void move(Matrix4f transposeMatrix, Model model) {
        for (Vector3f vertex : model.vertices) {
            Vector3f newVertex = mul(vertex, transposeMatrix);
            vertex.x = newVertex.x;
            vertex.y = newVertex.y;
            vertex.z = newVertex.z;
        }
    }

    public static Vector3f mul(Vector3f vector3f, Matrix4f modelMatrix) {
        Vector4f vector4f = new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1);
        //почему-то x уходит влево при положительных значениях?!?!
        return new Vector3f(vector4f.x * modelMatrix.m00 + vector4f.y * modelMatrix.m01 + vector4f.z * modelMatrix.m02 + vector4f.w * modelMatrix.m03,
                vector4f.x * modelMatrix.m10 + vector3f.y * modelMatrix.m11 + vector3f.z * modelMatrix.m12 + vector4f.w * modelMatrix.m13,
                vector4f.x * modelMatrix.m20 + vector3f.y * modelMatrix.m21 + vector3f.z * modelMatrix.m22 + vector4f.w * modelMatrix.m23);
    }
}
