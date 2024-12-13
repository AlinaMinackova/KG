package com.cgvsu.light_texture_mesh;

import javafx.scene.paint.Color;

public class Light {

    public final Double x;
    public final Double y;
    public final Double z;
    public final Color color;


    public Light(Double x, Double y, Double z, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }
}
