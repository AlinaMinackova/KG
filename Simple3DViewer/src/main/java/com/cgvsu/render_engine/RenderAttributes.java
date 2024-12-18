package com.cgvsu.render_engine;

import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.light_texture_mesh.Light;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;


import java.util.List;

public class RenderAttributes {
    private final Matrix4f modelViewProjectionMatrix;
    public List<Model> models;
    public GraphicsContext graphicsContext;
    public int width;
    public int height;
    public double[][] ZBuffer;
    public Matrix4f viewMatrix;
    public Matrix4f modelViewProjectionMatrix;
    public List<Light> lights;

    public RenderAttributes(List<Model> models, GraphicsContext graphicsContext, int width, int height, double[][] ZBuffer, Matrix4f viewMatrix, Matrix4f modelViewProjectionMatrix, List<Light> lights) {
        this.models = models;
        this.graphicsContext = graphicsContext;
        this.width = width;
        this.height = height;
        this.ZBuffer = ZBuffer;
        this.viewMatrix = viewMatrix;
        this.modelViewProjectionMatrix = modelViewProjectionMatrix;
        this.lights = lights;
    }

    public Matrix4f getModelViewProjectionMatrix() {
        return modelViewProjectionMatrix;
    }

    public List<Model> getModels() {
        return models;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double[][] getZBuffer() {
        return ZBuffer;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
