package com.cgvsu.texture;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;

//public class ImageToText {
//
//    static public int[][] color;
//   static {
//       BufferedImage image = null;
//       try {
//           image = ImageIO.read(new File("3DModels/CaracalCube/Abyssian_cat_texture.png"));
//           color = new int[image.getWidth()][image.getHeight()];
//
//           for (int i = 0; i < image.getWidth(); i++) {
//               for (int j = 0; j < image.getHeight(); j++) {
//                   color[i][j] = image.getRGB(i, j);
//               }
//           }
//       } catch (IOException e) {
//           e.printStackTrace();
//       }
//    }

public class ImageToText {

    static public int[][][] pixelData;
    public static int wight;
    public static int height;

    static { // main(String[] args)

        BufferedImage img;

        try {
            img = ImageIO.read(new File("3DModels/CaracalCube/cat.png")); //caracal_texture.png

            pixelData = new int[img.getWidth()][img.getHeight()][3];
            wight = img.getWidth();
            height = img.getHeight();
            int[] rgb;

//            for(int i = 0; i < img.getWidth(); i++){  //для лица и куба
//                for(int j = 0; j < img.getHeight(); j++){
//                    rgb = getPixelData(img, i, j);
//                    pixelData[img.getWidth() - 1 - i][img.getHeight() - 1 - j][0] = rgb[0];
//                    pixelData[img.getWidth() - 1 - i][img.getHeight() - 1 - j][1] = rgb[1];
//                    pixelData[img.getWidth() - 1 - i][img.getHeight() - 1 - j][2] = rgb[2];
//                }
//            }
//            for(int i = 0; i < img.getWidth(); i++){
//                for(int j = 0; j < img.getHeight(); j++){
//                    rgb = getPixelData(img, i, j);
//                    pixelData[i][j][0] = rgb[0];
//                    pixelData[i][j][1] = rgb[1];
//                    pixelData[i][j][2] = rgb[2];
//                }
//            }
//            for(int i = 0; i < img.getWidth(); i++){          //для кота pops
//                for(int j = 0; j < img.getHeight(); j++){
//                    rgb = getPixelData(img, i, j);
//                    pixelData[i][img.getHeight() - 1 - j][0] = rgb[0];
//                    pixelData[i][img.getHeight() - 1 - j][1] = rgb[1];
//                    pixelData[i][img.getHeight() - 1 - j][2] = rgb[2];
//                }
//            }

//            for(int i = 0; i < img.getWidth(); i++){
//                for(int j = 0; j < img.getHeight(); j++){
//                    rgb = getPixelData(img, i, j);
//                    pixelData[img.getWidth() - 1 - i][j][0] = rgb[0];
//                    pixelData[img.getWidth() - 1 - i][j][1] = rgb[1];
//                    pixelData[img.getWidth() - 1 - i][j][2] = rgb[2];
//                }
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);

        return new int[] {
                (argb >> 16) & 0xff, //red
                (argb >>  8) & 0xff, //green
                (argb      ) & 0xff  //blue
        };
    }

}
