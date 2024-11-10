package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import javax.vecmath.Point2f;
import java.util.ArrayList;
import java.util.List;

public class TriangleRasterization {
    public static void draw(final GraphicsContext graphicsContext,
                                 final int[] coordX, final int[] coordY, final Color[] color,
                                 final float[][] zBuff, final float[] deepZ) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        //сортируем вершины
        if (coordY[0] > coordY[1]) {
            int termY = coordY[0];
            coordY[0] = coordY[1];
            coordY[1] = termY;
            int termX = coordX[0];
            coordX[0] = coordX[1];
            coordX[1] = termX;
            Color colorTerm = color[0];
            color[0] = color[1];
            color[1] = colorTerm;
            float zBuf = deepZ[0];
            deepZ[0] = deepZ[1];
            deepZ[1] = zBuf;
        }
        if (coordY[0] > coordY[2]) {
            int term = coordY[0];
            coordY[0] = coordY[2];
            coordY[2] = term;
            int termX = coordX[0];
            coordX[0] = coordX[2];
            coordX[2] = termX;
            Color colorTerm = color[0];
            color[0] = color[2];
            color[2] = colorTerm;
            float zBuf = deepZ[0];
            deepZ[0] = deepZ[2];
            deepZ[2] = zBuf;
        }
        if (coordY[1] > coordY[2]) {
            int term = coordY[1];
            coordY[1] = coordY[2];
            coordY[2] = term;
            int termX = coordX[1];
            coordX[1] = coordX[2];
            coordX[2] = termX;
            Color colorTerm = color[1];
            color[1] = color[2];
            color[2] = colorTerm;
            float zBuf = deepZ[1];
            deepZ[1] = deepZ[2];
            deepZ[2] = zBuf;
        }

        if (coordY[0] == coordY[1] && coordY[1] == coordY[2]) {
            for (int x = coordX[0]; x <= coordX[2]; x++) {
                int[] rgb = getGradientCoordinatesRGB(coordX, coordY, x, coordY[0], color);
                pixelWriter.setColor(x, coordY[0], Color.rgb(rgb[0], rgb[1], rgb[2]));
            }
        }
        if (coordY[0] != coordY[1]) {
            for (int y = coordY[0]; y <= coordY[1]; y++) {
                //находим x
                int xl = ((coordX[1] - coordX[0]) * (y - coordY[0]) / (coordY[1] - coordY[0])) + coordX[0];
                int xr = ((coordX[2] - coordX[0]) * (y - coordY[0]) / (coordY[2] - coordY[0])) + coordX[0];
                if (xl > xr) { // проверяем границы
                    int tempX = xl;
                    xl = xr;
                    xr = tempX;
                }
                for (int x = xl; x <= xr; x++) {
                    if (x >= 0 && y >= 0  && x < zBuff.length && y < zBuff[0].length) {
                        float xy = interpolateCoordinatesZBuffer(coordX, coordY, x, y, deepZ);
                        if(zBuff[x][y] <= xy) {
                            continue;
                        }
                        int[] rgb = getGradientCoordinatesRGB(coordX, coordY, x, y, color);
                        zBuff[x][y] = (long) xy;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }
        if (coordY[1] != coordY[2]) {
            for (int y = coordY[1]; y <= coordY[2]; y++) {
                //находим x
                int xl = ((coordX[1] - coordX[2]) * (y - coordY[2]) / (coordY[1] - coordY[2])) + coordX[2];
                int xr = ((coordX[0] - coordX[2]) * (y - coordY[2]) / (coordY[0] - coordY[2])) + coordX[2];
                if (xl > xr) {
                    int tempX = xl;
                    xl = xr;
                    xr = tempX;
                }
                for (int x = xl; x <= xr; x++) {
                    if (x >= 0 && y >= 0  && x < zBuff.length && y < zBuff[0].length) {
                        float xy = interpolateCoordinatesZBuffer(coordX, coordY, x, y, deepZ);
                        if(zBuff[x][y] <= xy) {
                            continue;
                        }
                        int[] rgb = getGradientCoordinatesRGB(coordX, coordY, x, y, color);
                        zBuff[x][y] = (long) xy;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }
    }


    public static float interpolateCoordinatesZBuffer(final int[] coordX, final int[] coordY, final int x, final int y, final float[] deepZ) {
        final float[] baristicCoords = barycentricCoordinates(coordX, coordY, x, y);
        return baristicCoords[0] * deepZ[0] + baristicCoords[1] * deepZ[1] + baristicCoords[2] * deepZ[2];
    }


    public static int[] getGradientCoordinatesRGB(final int[] coordX, final int[] coordY, final int x, final int y, final Color[] color) {
        final float[] baristicCoords = barycentricCoordinates(coordX, coordY, x, y);
        int r = Math.min(255, (int) Math.abs(color[0].getRed() * 255 * baristicCoords[0] + color[1].getRed()
                * 255 * baristicCoords[1] + color[2].getRed() * 255 * baristicCoords[2]));
        int g = Math.min(255, (int) Math.abs(color[0].getGreen() * 255 * baristicCoords[0] + color[1].getGreen()
                * 255 * baristicCoords[1] + color[2].getGreen() * 255 * baristicCoords[2]));
        int b = Math.min(255, (int) Math.abs(color[0].getBlue() * 255 * baristicCoords[0] + color[1].getBlue()
                * 255 * baristicCoords[1] + color[2].getBlue() * 255 * baristicCoords[2]));
        return new int[]{r, g, b};
    }

    public static float[] barycentricCoordinates(final int[] coordX, final int[] coordY, final int x, final int y) {

        float alfa = (float) ((coordY[1] - coordY[2]) * (x - coordX[2]) + (coordX[2] - coordX[1]) * (y - coordY[2])) /
                (float) ((coordY[1] - coordY[2]) * (coordX[0] - coordX[2]) + (coordX[2] - coordX[1]) * (coordY[0] - coordY[2]));

        float betta = (float) ((coordY[2] - coordY[0]) * (x - coordX[2]) + (coordX[0] - coordX[2]) * (y - coordY[2])) /
                (float) ((coordY[1] - coordY[2]) * (coordX[0] - coordX[2]) + (coordX[2] - coordX[1]) * (coordY[0] - coordY[2]));

        float gamma = 1 - alfa - betta;

        return new float[]{alfa, betta, gamma};
    }

}
