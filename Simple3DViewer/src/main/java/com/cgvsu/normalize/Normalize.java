package com.cgvsu.normalize;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Normalize {
    public static List<Vector3f> normale(final ArrayList<Vector3f> vertices, final List<Polygon> polygons) {
        List<Vector3f> result = new ArrayList<>(vertices.size());
        for (Vector3f vertex : vertices) {
            int countP = 0;
            Vector3f normal = new Vector3f(0, 0, 0);
            for (Polygon polygon : polygons) {
                if (vertices.get(polygon.getVertexIndices().get(0)).equals(vertex)) {
                    countP++;
                    normal.add(getV(vertices.get(polygon.getVertexIndices().get(0)),
                            vertices.get(polygon.getVertexIndices().get(1)),
                            vertices.get(polygon.getVertexIndices().get(2))));
                }
                if (vertices.get(polygon.getVertexIndices().get(1)).equals(vertex)) {
                    countP++;
                    normal.add(getV(vertices.get(polygon.getVertexIndices().get(1)),
                            vertices.get(polygon.getVertexIndices().get(0)),
                            vertices.get(polygon.getVertexIndices().get(2))));
                }
                if (vertices.get(polygon.getVertexIndices().get(2)).equals(vertex)) {
                    countP++;
                    normal.add(getV(vertices.get(polygon.getVertexIndices().get(2)),
                            vertices.get(polygon.getVertexIndices().get(0)),
                            vertices.get(polygon.getVertexIndices().get(1))));
                }
            }
            normal.div(countP);
            normal.div(Math.sqrt(normal.x * normal.x + normal.y * normal.y+ normal.z * normal.z));
            result.add(normal);
        }
        return result;
    }

    private static Vector3f getV(Vector3f v, Vector3f v2, Vector3f v3) {
        Vector3f a = new Vector3f(0, 0, 0);
        a.sub(v, v2);
        Vector3f b = new Vector3f(0, 0, 0);
        b.sub(v, v3);
        Vector3f c = new Vector3f(0, 0, 0);
        c.mul(a, b);
        return c;

    }

}
