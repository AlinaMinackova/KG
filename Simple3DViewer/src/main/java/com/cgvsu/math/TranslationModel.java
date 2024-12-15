package com.cgvsu.math;

import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.math.vector.Vector3f;
import com.cgvsu.math.vector.Vector4f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.GraphicConveyor;


public class TranslationModel {
    public static void move(Matrix4f transposeMatrix, Model model) {

        int transformIndexVertex = 0;
        for (int i = 0; i < model.vertices.size(); i++) { //кручу координаты исходных вершин для трансформации
            if (!model.deletedVertexes.contains(i)) {
                Vector3f newVertex = GraphicConveyor.multMatrix4OnVector3f(transposeMatrix, model.vertices.get(i));
                model.verticesTransform.get(transformIndexVertex).x = newVertex.x; // и заношу их в измененные
                model.verticesTransform.get(transformIndexVertex).y = newVertex.y;
                model.verticesTransform.get(transformIndexVertex).z = newVertex.z;
                transformIndexVertex++;
            }
        }
        model.normalize();
    }


}
