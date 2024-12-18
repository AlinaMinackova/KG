package com.cgvsu.model;

import com.cgvsu.math.vector.Vector3f;

import java.util.*;

public class DeleteVertices {

    public static Model deleteVerticesFromModel(Model model, List<Integer> vertexIndices) {
        // Список вершин на удаление отсортированный по возрастанию
        List<Integer> vertexIndicesToDelete = new ArrayList<>(vertexIndices).stream().sorted(Comparator.reverseOrder()).toList();

        // Удаление вершин
        deleteVertices(model.verticesTransform, vertexIndicesToDelete);

        // Удаление полигонов, часть вершин которых исчезла
        deleteDanglingPolygons(model.polygons, vertexIndices);

        // Смещение вершинных индексов внутри полигона
        shiftIndicesInPolygons(model.polygons, vertexIndices, model);

        return model;
    }


    private static void deleteVertices(List<Vector3f> modelVertices, List<Integer> vertexIndicesToDelete) {
        for (Integer i : vertexIndicesToDelete) {
            modelVertices.remove(i.intValue());
        }
    }

    private static void deleteDanglingPolygons(List<Polygon> modelPolygons, List<Integer> vertexIndicesToDelete) {
        for (int i = modelPolygons.size() - 1; i >= 0; i--) {
            Polygon polygon = modelPolygons.get(i);
            boolean areVertexIndicesToDeletePresentInPolygon = polygon.getVertexIndices().stream()
                    .anyMatch(vertexIndicesToDelete::contains);
            if (areVertexIndicesToDeletePresentInPolygon) {
                modelPolygons.remove(i);
            }
        }
    }


    private static void shiftIndicesInPolygons(List<Polygon> modelPolygons, List<Integer> vertexIndicesToDelete, Model model) {
        for (Integer i : vertexIndicesToDelete) {
            model.normals.remove((int)i);
        }
        vertexIndicesToDelete.sort(Collections.reverseOrder());
        for (Integer vertex: vertexIndicesToDelete) {
            for (Polygon polygon : modelPolygons) {
                for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                    if (polygon.getVertexIndices().get(i) > vertex) {
                        polygon.getVertexIndices().set(i, polygon.getVertexIndices().get(i) - 1);
                    }
                }
            }
        }
    }
}
