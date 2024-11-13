package com.cgvsu.model;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.normalize.Normalize;
import com.cgvsu.triangulation.Triangulation;


import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    public ArrayList<Polygon> polygonsWithoutTriangulation = new ArrayList<Polygon>();
    public boolean isActive = true;
    public boolean isActiveGrid = false;
    public boolean isActiveTexture = false;
    public boolean isActiveLighting = false;


    public void triangulate(){
        polygonsWithoutTriangulation = polygons;
        polygons = (ArrayList<Polygon>) Triangulation.triangulate(polygons);
    }

    public void normalize(){
        normals = (ArrayList<Vector3f>) Normalize.normale(vertices, polygons);
    }
}
