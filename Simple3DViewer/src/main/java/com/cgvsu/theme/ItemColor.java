package com.cgvsu.theme;


import javafx.scene.paint.Color;

public class ItemColor {
    private final String text;
    private final Color color;

    public ItemColor(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
