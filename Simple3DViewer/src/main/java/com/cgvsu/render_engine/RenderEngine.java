package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.cgvsu.math.Vector3f;
import com.cgvsu.rasterization.TriangleRasterization;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height) {
        double[][] ZBuffer = new double[width][height];
        for (double[] longs : ZBuffer) {
            Arrays.fill(longs, Double.MAX_VALUE);
        }
        Matrix4f modelMatrix = rotateScaleTranslate(); //матрица модели (пока единичная)
        Matrix4f viewMatrix = camera.getViewMatrix(); //видовая матрица
        Matrix4f projectionMatrix = camera.getProjectionMatrix(); //проекционная матрица

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);


        for (Model mesh : meshes) {
            final int nPolygons = mesh.polygons.size(); //количество полигонов
            for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
                final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size(); //количество вершин в полигоне
                javax.vecmath.Vector3f v;
                double[] vz = new double[nVerticesInPolygon];//вектор z
                Vector3f[] normals = new Vector3f[3]; //список нормалей полигона

                ArrayList<Point2f> resultPoints = new ArrayList<>(); //список преобразованных вершин полигона в системе XY
                for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) { //идем по вершинам в полигоне
                    Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd)); //получаю координату вершины
                    normals[vertexInPolygonInd] = (mesh.normals.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd))); //получаю нормаль вершины

                    javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z); //делаем вектор строку
                    v = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                    vz[vertexInPolygonInd] = v.z; // спросить про z штрих
                    Point2f resultPoint = vertexToPoint(v, width, height); //преобразуем координаты в систему координат монитора
                    resultPoints.add(resultPoint);
                }

                TriangleRasterization.draw(
                        graphicsContext,
                        new int[]{(int) resultPoints.get(0).x, (int) resultPoints.get(1).x, (int) resultPoints.get(2).x},
                        new int[]{(int) resultPoints.get(0).y, (int) resultPoints.get(1).y, (int) resultPoints.get(2).y},
                        new Color[]{mesh.color, mesh.color, mesh.color},
                        ZBuffer,
                        vz, normals, new double[]{viewMatrix.m02, viewMatrix.m12, viewMatrix.m22});
//            }

//            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
//                graphicsContext.strokeLine( //соединяем линией две точки полигона
//                        resultPoints.get(vertexInPolygonInd - 1).x,
//                        resultPoints.get(vertexInPolygonInd - 1).y,
//                        resultPoints.get(vertexInPolygonInd).x,
//                        resultPoints.get(vertexInPolygonInd).y);
//            }
//
//            if (nVerticesInPolygon > 0) //дорисовать соединение между последней и первой точкой
//                graphicsContext.strokeLine(
//                        resultPoints.get(nVerticesInPolygon - 1).x,
//                        resultPoints.get(nVerticesInPolygon - 1).y,
//                        resultPoints.get(0).x,
//                        resultPoints.get(0).y);
            }
        }
    }
}