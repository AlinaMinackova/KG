package com.cgvsu.model;
import com.cgvsu.math.vector.Vector2f;
import com.cgvsu.math.vector.Vector3f;
import com.cgvsu.normalize.Normalize;
import com.cgvsu.texture.ImageToTexture;
import com.cgvsu.triangulation.Triangulation;


import javafx.scene.paint.Color;
import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> verticesTransform = new ArrayList<Vector3f>();
    public ArrayList<Integer> deletedVertexes = new ArrayList<Integer>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    public ArrayList<Polygon> polygonsBase = new ArrayList<Polygon>();
    public ArrayList<Polygon> polygonsWithoutTriangulation = new ArrayList<Polygon>();
    public boolean isActiveGrid = false;
    public boolean isActiveTexture = false;
    public String pathTexture = null;
    public boolean isActiveLighting = false;
    public Color color = Color.GRAY;
    public ImageToTexture imageToTexture = null;

    public void triangulate(){
        for (Vector3f vertex : vertices){
            verticesTransform.add(new Vector3f(vertex.x, vertex.y, vertex.z));
        }
        polygonsWithoutTriangulation = polygons;
        polygons = (ArrayList<Polygon>) Triangulation.triangulate(polygons);
        for (Polygon polygon : polygons){
            polygonsBase.add(polygon.clone());
        }
    }

    public void normalize(){
        normals = (ArrayList<Vector3f>) Normalize.normale(vertices, polygons);
    }
}
