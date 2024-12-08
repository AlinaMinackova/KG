package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cgvsu.light_texture_mesh.Mesh;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Polygon;
import com.cgvsu.rasterization.TriangleRasterization;
import com.cgvsu.texture.ImageToTexture;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void prepareToRender(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> models,
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

        RenderAttributes renderAttributes = new RenderAttributes(models, graphicsContext, width, height, ZBuffer, viewMatrix, modelViewProjectionMatrix);
        render(renderAttributes);
    }

    public static void render(final RenderAttributes renderAttributes){
        for (Model model: renderAttributes.models){
            if (model.pathTexture != null && model.imageToTexture == null) {
                model.imageToTexture = new ImageToTexture();
                model.imageToTexture.loadImage(model.pathTexture);
            }

        VertexAttributes[] vertexAttributes = new VertexAttributes[model.verticesTransform.size()]; //массив с данными вершин
        for (int i = 0; i < model.verticesTransform.size(); i++) {
            VertexAttributes vertex = new VertexAttributes();
            vertex.setCoorVertex(model.verticesTransform.get(i));
            vertex.setNormal(model.normals.get(i));
            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.coorVertex.x, vertex.coorVertex.y, vertex.coorVertex.z); //делаем вектор строку
            javax.vecmath.Vector3f v = multiplyMatrix4ByVector3(renderAttributes.modelViewProjectionMatrix, vertexVecmath);
            vertex.setZ(v.z);
            Point2f resultPoint = vertexToPoint(v, renderAttributes.width, renderAttributes.height); //преобразуем координаты в систему координат монитора
            vertex.setResultPoint(resultPoint);

                vertexAttributes[i] = vertex;
            }

            for (Polygon polygon : model.polygons) {
                double[] vz = new double[3];//вектор z
                Vector3f[] normals = new Vector3f[3]; //список нормалей полигона
                Vector2f[] textures = new Vector2f[3]; //список текстур полигона
                for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                    normals[i] = vertexAttributes[polygon.getVertexIndices().get(i)].normal; //получаю нормаль вершины
                    if (model.pathTexture != null) {
                        textures[i] = model.textureVertices.get(polygon.getTextureVertexIndices().get(i)); //получаю текстуру вершины
                    }
                    vz[i] = vertexAttributes[polygon.getVertexIndices().get(i)].z;
                }

                int[] coorX = new int[]{(int) vertexAttributes[polygon.getVertexIndices().get(0)].resultPoint.x,
                        (int) vertexAttributes[polygon.getVertexIndices().get(1)].resultPoint.x,
                        (int) vertexAttributes[polygon.getVertexIndices().get(2)].resultPoint.x};
                int[] coorY = new int[]{(int) vertexAttributes[polygon.getVertexIndices().get(0)].resultPoint.y,
                        (int) vertexAttributes[polygon.getVertexIndices().get(1)].resultPoint.y,
                        (int) vertexAttributes[polygon.getVertexIndices().get(2)].resultPoint.y};

                TriangleRasterization.draw(
                        renderAttributes.graphicsContext,
                        coorX,
                        coorY,
                        new Color[]{model.color, model.color, model.color},
                        renderAttributes.ZBuffer,
                        vz, normals, textures, new double[]{renderAttributes.viewMatrix.m02, renderAttributes.viewMatrix.m12, renderAttributes.viewMatrix.m22}, model);

                if(model.isActiveGrid){
                    Mesh.drawLine(coorX[0], coorY[0], coorX[1], coorY[1], renderAttributes.ZBuffer, vz, coorX, coorY, renderAttributes.graphicsContext);
                    Mesh.drawLine(coorX[0], coorY[0], coorX[2], coorY[2], renderAttributes.ZBuffer, vz, coorX, coorY, renderAttributes.graphicsContext);
                    Mesh.drawLine(coorX[2], coorY[2], coorX[1], coorY[1], renderAttributes.ZBuffer, vz, coorX, coorY, renderAttributes.graphicsContext);
                }
            }
        }
    }

}