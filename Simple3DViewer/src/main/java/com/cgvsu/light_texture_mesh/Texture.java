package com.cgvsu.light_texture_mesh;

import com.cgvsu.math.vector.Vector2f;
import com.cgvsu.model.Model;

public class Texture {

    public static double[] getGradientCoordinatesTexture(double[] barizentric, Vector2f[] texture) {
        return new double[] {(barizentric[0] * texture[0].x) +  (barizentric[1] * texture[1].x) +  (barizentric[2] * texture[2].x),
                (barizentric[0] * texture[0].y) + (barizentric[1] * texture[1].y) + (barizentric[2] * texture[2].y)};
    }

    public static void texture(double[] barizentric, Vector2f[] textures, Model mesh, int[] rgb){
        double[] texture = getGradientCoordinatesTexture(barizentric, textures);
        int u = (int) Math.round(texture[0] * (mesh.imageToTexture.wight - 1));
        int v = (int) Math.round(texture[1] * (mesh.imageToTexture.height - 1));
        if (u < mesh.imageToTexture.wight && v < mesh.imageToTexture.height) {
            rgb[0] = mesh.imageToTexture.pixelData[u][v][0];
            rgb[1] = mesh.imageToTexture.pixelData[u][v][1];
            rgb[2] = mesh.imageToTexture.pixelData[u][v][2];
        }
    }
}
