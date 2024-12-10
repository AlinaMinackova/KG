package com.cgvsu.light_texture_mesh;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

import java.util.List;

public class Lighting {

    final static double k = 0.5;

    public static void calculateLight(int[] rgb, List<Light> light, Vector3f normal){
        double l = -(light.get(0).x * normal.x + light.get(0).y * normal.y + light.get(0).z * normal.z);
        if(l < 0){
            l = 0; // фоновый свет
        }
        double mixingLightRed = 0;
        double mixingLightGreen = 0;
        double mixingLightBlue = 0;
        for (Light light1: light) {
            if (light1.color != null){
                double l2 = -(light1.x * normal.x + light1.y * normal.y + light1.z * normal.z);
                if(l2 < 0){
                    l2 = 0;
                }
                mixingLightRed += light1.color.getRed() * 255 * l2;
                mixingLightGreen += light1.color.getGreen() * 255 * l2;
                mixingLightBlue += light1.color.getBlue() * 255 * l2;
            }
        }
        rgb[0] = Math.min(255, (int) ((rgb[0] * (1 - k) + rgb[0] * k * l) + mixingLightRed));
        rgb[1] = Math.min(255, (int) ((rgb[1] * (1 - k) + rgb[1] * k * l) + mixingLightGreen));
        rgb[2] = Math.min(255, (int) ((rgb[2] * (1 - k) + rgb[2] * k * l) + mixingLightBlue));
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

    public static void light(final double[] barizentric, final Vector3f[] normals, List<Light> light, int[] rgb){
        Vector3f smooth = smoothingNormal(barizentric, normals);
        calculateLight(rgb, light, smooth);
    }
}
