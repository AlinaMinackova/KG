package com.cgvsu.math;

import com.cgvsu.model.Model;

import javax.vecmath.Matrix4f;

public class TranslationModel {
    public static void move(Matrix4f modelMatrix, Model model) {
        for (Vector3f vertex : model.vertices) {
            Vector3f newVertex = mul(vertex, modelMatrix);
            vertex.x = newVertex.x;
            vertex.y = newVertex.y;
            vertex.z = newVertex.z;
        }
    }

    public static Vector3f mul(Vector3f vector3f, Matrix4f modelMatrix) {
        //почему-то x уходит влево при положительных значениях?!?!
        return new Vector3f(vector3f.x + modelMatrix.m03,
                vector3f.y + modelMatrix.m13,
                vector3f.z + modelMatrix.m23);
    }
}
