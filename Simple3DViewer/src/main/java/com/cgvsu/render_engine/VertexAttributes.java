package com.cgvsu.render_engine;

import com.cgvsu.math.vector.Vector3f;

import javax.vecmath.Point2f;

public class VertexAttributes {

    Vector3f coorVertex;
    Vector3f normal; //список нормалей полигона
    double z;
    Point2f resultPoint; //координаты монитора

    public void setResultPoint(Point2f resultPoint) {
        this.resultPoint = resultPoint;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setCoorVertex(Vector3f coorVertex) {
        this.coorVertex = coorVertex;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }
}
