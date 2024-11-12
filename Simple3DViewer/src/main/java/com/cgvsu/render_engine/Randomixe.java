package com.cgvsu.render_engine;

import javafx.scene.paint.Color;

import java.util.Random;

public class Randomixe {

    static Color[] mas = new Color[1000];

    static {
        for (int i = 0; i < 1000; i++) {
            Random rand = new Random();
            int randomNum = rand.nextInt((255) + 1);
            int randomNum2 = rand.nextInt((255) + 1);
            int randomNum3 = rand.nextInt((255) + 1);

            mas[i] = Color.rgb(randomNum, randomNum2, randomNum3);
        }
    }
}
