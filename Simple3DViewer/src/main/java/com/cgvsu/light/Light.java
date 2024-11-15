package com.cgvsu.light;

import com.cgvsu.math.Vector3f;

public class Light {
    final static double k = 0.25;

    public static void calculateLight(int[] rgb, double[] light, Vector3f normal){
        double l = -(light[0] * normal.x + light[1] * normal.y + light[2] * normal.z) < 0 ? 0 :
                -(light[0] * normal.x + light[1] * normal.y + light[2] * normal.z);
        rgb[0] = (int) (rgb[0] * (1 - k) + rgb[0] * k * l);
        rgb[1] = (int) (rgb[1] * (1 - k) + rgb[1] * k * l);
        rgb[2] = (int) (rgb[2] * (1 - k) + rgb[2] * k * l);
    }
}
