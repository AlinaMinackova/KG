package com.cgvsu.checkbox;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Greed {

    public static void drawLine(int x1, int y1, int x2, int y2, GraphicsContext graphicsContext){
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        int y = y1;
        int dirY = y2 - y1;

        double error = 0;
        double deltaerr = (double) (deltaY + 1) / (double) (deltaX + 1);

        if (dirY > 0) {dirY = 1;}
        if (dirY < 0) {dirY = -1;}

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++){
            pixelWriter.setColor(x, y, Color.BLACK);
            error += deltaerr;
            if(error >= 1.0){
                y += dirY;
                error -= 1.0;

            }
        }
    }
}
