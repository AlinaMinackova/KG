package com.cgvsu.math;

import com.cgvsu.math.vector.Vector3f;
import com.cgvsu.math.vector.Vector4f;
import com.cgvsu.model.Model;

import javax.vecmath.Matrix4f;

public class TranslationModel {
    public static void move(Matrix4f transposeMatrix, Model model) {
        //TODO:
        // 1. ИЗМЕНЕНИЯ КЛАДЁШЬ В МАССИВ verticesTransform
        // 2. НЕ ЗАБЫВАЙ ПРОВЕРЯТЬ, ЧТОБЫ ТРАНСФОРМАЦИЯ ПРОВОДИЛАСЬ ТОЛЬКО ДЛЯ ТЕХ ВЕРШИН,
        // ИНДЕКСОВ КОТОРЫХ НЕТ В deletedVertex
        int transformIndexVertex = 0;
        for (int i = 0; i < model.vertices.size(); i++) { //кручу координаты исходных вершин для трансформации
            if (!model.deletedVertexes.contains(i)) {
                Vector3f newVertex = mul(model.vertices.get(i), transposeMatrix);
                model.verticesTransform.get(transformIndexVertex).x = newVertex.x; // и заношу их в измененные
                model.verticesTransform.get(transformIndexVertex).y = newVertex.y;
                model.verticesTransform.get(transformIndexVertex).z = newVertex.z;
                transformIndexVertex++;
            }
        }
        model.normalize();
    }

    public static Vector3f mul(Vector3f vector3f, Matrix4f modelMatrix) {
        Vector4f vector4f = new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1);
        //почему-то x уходит влево при положительных значениях?!?!
        return new Vector3f(vector4f.x * modelMatrix.m00 + vector4f.y * modelMatrix.m01 + vector4f.z * modelMatrix.m02 + vector4f.w * modelMatrix.m03,
                vector4f.x * modelMatrix.m10 + vector3f.y * modelMatrix.m11 + vector3f.z * modelMatrix.m12 + vector4f.w * modelMatrix.m13,
                vector4f.x * modelMatrix.m20 + vector3f.y * modelMatrix.m21 + vector3f.z * modelMatrix.m22 + vector4f.w * modelMatrix.m23);
    }
}
