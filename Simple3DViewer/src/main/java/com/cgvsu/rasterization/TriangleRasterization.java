package com.cgvsu.rasterization;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TriangleRasterization {
    public static void draw(final GraphicsContext graphicsContext,
                            final int[] coordX, final int[] coordY, final Color[] color,
                            final double[][] zBuff, final double[] deepZ, Vector3f[] normals) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        sort(coordX, coordY, deepZ, normals, color);

        for (int y = coordY[0]; y <= coordY[1]; y++) {
            //находим x
            int xl = (coordY[1] - coordY[0] == 0) ? coordX[0] :
                    (y - coordY[0]) * (coordX[1] - coordX[0]) / (coordY[1] - coordY[0]) + coordX[0];
            int xr = (coordY[0] - coordY[2] == 0) ? coordX[2] :
                    (y - coordY[2]) * (coordX[0] - coordX[2]) / (coordY[0] - coordY[2]) + coordX[2];
            if (xl > xr) { // проверяем границы
                int tempX = xl;
                xl = xr;
                xr = tempX;
            }
            for (int x = xl; x <= xr; x++) {
                if (x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length) {
                    double[] barizentric = barizentricCoordinates(x, y, coordX, coordY);
                    if (!Double.isNaN(barizentric[0]) && !Double.isNaN(barizentric[1]) && !Double.isNaN(barizentric[2])) {
                        double xy = interpolateCoordinatesZBuffer(barizentric, deepZ);
                        if (zBuff[x][y] <= xy) {
                            continue;
                        }
                        int[] rgb = getGradientCoordinatesRGB(barizentric, color);
//                        Vector3f smooth = smoothingNormal(barizentric, normals);
//                        Light.calculateLight(rgb, light, smooth);
                        zBuff[x][y] = xy;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }

        for (int y = coordY[1]; y <= coordY[2]; y++) {
            //находим x
            int xl = (coordY[2] - coordY[1] == 0) ? coordX[1] :
                    (y - coordY[1]) * (coordX[2] - coordX[1]) / (coordY[2] - coordY[1]) + coordX[1];
            int xr = (coordY[0] - coordY[2] == 0) ? coordX[2] :
                    (y - coordY[2]) * (coordX[0] - coordX[2]) / (coordY[0] - coordY[2]) + coordX[2];
            if (xl > xr) {
                int tempX = xl;
                xl = xr;
                xr = tempX;
            }
            for (int x = xl; x <= xr; x++) {
                if (x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length) {
                    double[] barizentric = barizentricCoordinates(x, y, coordX, coordY);
                    if (!Double.isNaN(barizentric[0]) && !Double.isNaN(barizentric[1]) && !Double.isNaN(barizentric[2])) {
                        double xy = interpolateCoordinatesZBuffer(barizentric, deepZ);
                        if (zBuff[x][y] <= xy) {
                            continue;
                        }
                        int[] rgb = getGradientCoordinatesRGB(barizentric, color);
//                        Vector3f smooth = smoothingNormal(barizentric, normals);
//                        Light.calculateLight(rgb, light, smooth);
                        zBuff[x][y] = xy;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }
    }

    private static double determinator(int[][] arr) {
        return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] +
                arr[0][1] * arr[1][2] * arr[2][0] - arr[0][2] * arr[1][1] * arr[2][0] -
                arr[0][0] * arr[1][2] * arr[2][1] - arr[0][1] * arr[1][0] * arr[2][2];
    }

    private static double[] barizentricCoordinates(int x, int y, int[] arrX, int[] arrY){
        final double generalDeterminant = determinator(new int[][]{arrX, arrY, new int[]{1, 1, 1}});
        final double alfa = Math.abs(determinator(
                new int[][]{new int[]{x, arrX[1], arrX[2]}, new int[]{y, arrY[1], arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final double betta = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], x, arrX[2]}, new int[]{arrY[0], y, arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final double gamma = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], arrX[1], x}, new int[]{arrY[0], arrY[1], y}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        return new double[]{alfa, betta, gamma};
    }

    private static void sort(int[] coordX, int[] coordY, double[] deepZ, Vector3f[] normals,  Color[] color) {
        //сортируем вершины
        if (coordY[0] > coordY[1]) {
            reverse(0, 1, coordX, coordY, deepZ, normals, color);
        }
        if (coordY[0] > coordY[2]) {
            reverse(0, 2, coordX, coordY, deepZ, normals, color);
        }
        if (coordY[1] > coordY[2]) {
            reverse(1, 2, coordX, coordY, deepZ, normals, color);
        }
    }

    private static void reverse(int i, int j, int[] coordX, int[] coordY, double[] deepZ, Vector3f[] normals, Color[] color) {
        int termY = coordY[i];
        coordY[i] = coordY[j];
        coordY[j] = termY;
        int termX = coordX[i];
        coordX[i] = coordX[j];
        coordX[j] = termX;
        Color colorTerm = color[i];
        color[i] = color[j];
        color[j] = colorTerm;
        double zBuf = deepZ[i];
        deepZ[i] = deepZ[j];
        deepZ[j] = zBuf;
        Vector3f normal = normals[i];
        normals[i] = normals[j];
        normals[j] = normal;
    }


    public static Vector3f smoothingNormal(final double[] baristicCoords, final Vector3f[] normals) {
        return new Vector3f((float) (baristicCoords[0] * normals[0].x + baristicCoords[1] * normals[1].x + baristicCoords[2] * normals[2].x),
                (float) (baristicCoords[0] * normals[0].y + baristicCoords[1] * normals[1].y + baristicCoords[2] * normals[2].y),
                (float) (baristicCoords[0] * normals[0].z + baristicCoords[1] * normals[1].z + baristicCoords[2] * normals[2].z));
    }


    public static double interpolateCoordinatesZBuffer(final double[] baristicCoords, final double[] deepZ) {
        return baristicCoords[0] * deepZ[0] + baristicCoords[1] * deepZ[1] + baristicCoords[2] * deepZ[2];
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

}
