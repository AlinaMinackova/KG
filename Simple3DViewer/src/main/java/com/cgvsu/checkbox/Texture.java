package com.cgvsu.checkbox;

import com.cgvsu.math.Vector2f;

public class Texture {

    public static double[] getGradientCoordinatesTexture(double[] barizentric, Vector2f[] texture) {
        return new double[] {(barizentric[0] * texture[0].x) +  (barizentric[1] * texture[1].x) +  (barizentric[2] * texture[2].x),
                (barizentric[0] * texture[0].y) + (barizentric[1] * texture[1].y) + (barizentric[2] * texture[2].y)};
    }
}
