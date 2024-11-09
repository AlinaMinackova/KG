package com.cgvsu.render_engine;

import java.util.ArrayList;

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
            final Model mesh,
            final int width,
            final int height)
    {
        Matrix4f modelMatrix = rotateScaleTranslate(); //матрица модели (пока единичная)
        Matrix4f viewMatrix = camera.getViewMatrix(); //видовая матрица
        Matrix4f projectionMatrix = camera.getProjectionMatrix(); //проекционная матрица

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        final int nPolygons = mesh.polygons.size(); //количество полигонов
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size(); //количество вершин в полигоне

            ArrayList<Point2f> resultPoints = new ArrayList<>(); //список преобразованных вершин полигона в системе XY
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) { //идем по вершинам в полигоне
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd)); //получаю координату вершины

                javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z); //делаем вектор строку

                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height); //преобразуем координаты в систему координат монитора
                resultPoints.add(resultPoint);
            }

            TriangleRasterization.draw(graphicsContext,
                    new int[]{(int) resultPoints.get(0).x, (int) resultPoints.get(1).x, (int) resultPoints.get(2).x},
                    new int[]{(int) resultPoints.get(0).y, (int) resultPoints.get(1).y, (int) resultPoints.get(2).y},
                    new Color[]{Color.GREEN, Color.RED, Color.BLUE});

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine( //соединяем линией две точки полигона
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0) //дорисовать соединение между последней и первой точкой
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);
        }
    }
}