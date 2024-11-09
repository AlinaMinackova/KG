package com.cgvsu.objreader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;
import com.cgvsu.objwriter.ObjWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ObjWriterTest {

    @Test
    @DisplayName("записывает вершины")
    public void testWriteVertices() {
        Vector3f vector3f = new Vector3f(1.01f, 1.02f, 1.03f);
        final String result = ObjWriter.writeVertices(vector3f);
        final String expectedResult = "v 1.01 1.02 1.03" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает текстуры")
    public void testWriteTextureVertices() {
        Vector2f vector2f = new Vector2f(-1, 1);
        final String result = ObjWriter.writeTextureVertices(vector2f);
        final String expectedResult = "vt -1.0 1.0" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает нормали")
    public void testWriteNormals() {
        Vector3f vector3f = new Vector3f(-1, 0, 0);
        final String result = ObjWriter.writeNormals(vector3f);
        final String expectedResult = "vn -1.0 0.0 0.0" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает полигоны с вершинами, текстурами и нормалями")
    public void testWritePolygonWithVTN() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 6, 8)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6)));
        polygon.setNormalIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 4, 6)));

        final String result = ObjWriter.writePolygons(polygon);
        final String expectedResult = "f 2/2/2 3/3/3 4/4/4 6/5/6 7/6/5 9/7/7" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает полигоны с вершинами и текстурами")
    public void testWritePolygonWithVT() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));

        final String result = ObjWriter.writePolygons(polygon);
        final String expectedResult = "f 2/2 3/3 4/4" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает полигоны с вершинами и нормалями")
    public void testWritePolygonWithVN() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
        polygon.setNormalIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));

        final String result = ObjWriter.writePolygons(polygon);
        final String expectedResult = "f 2//2 3//3 4//4" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("записывает полигоны только с вершинами")
    public void testWritePolygonWithV() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));

        final String result = ObjWriter.writePolygons(polygon);
        final String expectedResult = "f 2 3 4" + "\n";
        Assertions.assertEquals(expectedResult, result);
    }
}
