package com.cgvsu.light_texture_mesh;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

import java.util.List;

public class Lighting {

    final static double k = 0.5;

    public static void calculateLight(int[] rgb, List<List<Double>> light, Vector3f normal){
        double l = -(light.get(0).get(0) * normal.x + light.get(0).get(1) * normal.y + light.get(0).get(2) * normal.z);
        if(l < 0){
            l = 0;
        }
        double l2 = -(light.get(1).get(0) * normal.x + light.get(1).get(1) * normal.y + light.get(1).get(2) * normal.z);
        if(l2 < 0){
            l2 = 0;
        }
        double l3 = -(light.get(2).get(0) * normal.x + light.get(2).get(1) * normal.y + light.get(2).get(2) * normal.z);
        if(l3 < 0){
            l3 = 0;
        }
        rgb[0] = Math.min(255, (int) ((rgb[0] * (1 - k) + rgb[0] * k * l) + (255 * l2) + (0 * l3)));
        rgb[1] = Math.min(255, (int) ((rgb[1] * (1 - k) + rgb[1] * k * l) + (0 * l2) + (0 * l3)));
        rgb[2] = Math.min(255, (int) ((rgb[2] * (1 - k) + rgb[2] * k * l) + (255 * l2) + (255 * l3)));
    }

    public static int[] getGradientCoordinatesRGB(final double[] baristicCoords, final Color[] color) {
        int r = Math.min(255, (int) Math.abs(color[0].getRed() * 255 * baristicCoords[0] + color[1].getRed()
                * 255 * baristicCoords[1] + color[2].getRed() * 255 * baristicCoords[2]));
        int g = Math.min(255, (int) Math.abs(color[0].getGreen() * 255 * baristicCoords[0] + color[1].getGreen()
                * 255 * baristicCoords[1] + color[2].getGreen() * 255 * baristicCoords[2]));
        int b = Math.min(255, (int) Math.abs(color[0].getBlue() * 255 * baristicCoords[0] + color[1].getBlue()
                * 255 * baristicCoords[1] + color[2].getBlue() * 255 * baristicCoords[2]));

        return new int[]{r, g, b};
    }

    public static Vector3f smoothingNormal(final double[] baristicCoords, final Vector3f[] normals) {
        return new Vector3f((float) (baristicCoords[0] * normals[0].x + baristicCoords[1] * normals[1].x + baristicCoords[2] * normals[2].x),
                (float) (baristicCoords[0] * normals[0].y + baristicCoords[1] * normals[1].y + baristicCoords[2] * normals[2].y),
                (float) (baristicCoords[0] * normals[0].z + baristicCoords[1] * normals[1].z + baristicCoords[2] * normals[2].z));
    }

    public static void light(final double[] barizentric, final Vector3f[] normals, List<List<Double>> light, int[] rgb){
        Vector3f smooth = smoothingNormal(barizentric, normals);
        calculateLight(rgb, light, smooth);
    }
}
