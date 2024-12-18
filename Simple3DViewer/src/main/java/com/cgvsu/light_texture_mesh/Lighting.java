package com.cgvsu.light_texture_mesh;

import com.cgvsu.math.vector.Vector3f;
import javafx.scene.paint.Color;

import java.util.List;

public class Lighting {

    final static double k = 0.5;

    public static void calculateLight(int[] rgb,  List<Light> lights, Vector3f normal){
        double l = -(lights.get(0).x * normal.x + lights.get(0).y * normal.y + lights.get(0).z * normal.z);
        if(l < 0){
            l = 0; // фоновый свет
        }
        rgb[0] = Math.min(255, (int) ((rgb[0] * (1 - k) + rgb[0] * k * l)));
        rgb[1] = Math.min(255, (int) ((rgb[1] * (1 - k) + rgb[1] * k * l)));
        rgb[2] = Math.min(255, (int) ((rgb[2] * (1 - k) + rgb[2] * k * l)));
        calculateAdditionLight(lights, normal, rgb);
    }

    private static void calculateAdditionLight(List<Light> lightsColor, Vector3f normal, int[] rgb) {
        double mixingLightRed = 0;
        double mixingLightGreen = 0;
        double mixingLightBlue = 0;
        for (Light light1: lightsColor) {
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
        rgb[0] = (int) Math.min(255, rgb[0] + mixingLightRed);
        rgb[1] = (int) Math.min(255, rgb[1] + mixingLightGreen);
        rgb[2] = (int) Math.min(255, rgb[2] + mixingLightBlue);
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

    public static void light(final double[] barizentric, final Vector3f[] normals, List<Light> lights, int[] rgb){
        Vector3f smooth = smoothingNormal(barizentric, normals);
        calculateLight(rgb, lights, smooth);
    }
}
