package com.pdi.image_processing.noise;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Confetti extends WritableImage {

    private static final Color[] COLORS = {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.PURPLE,
        Color.HOTPINK,
        Color.BLACK,
        Color.WHITE
    };

    public Confetti(Image image, double probability) {
        super((int) image.getWidth(), (int) image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double random = Math.random();
                if (random <= probability) {
                    // Escolhe uma das 8 cores aleatoriamente
                    int index = (int)(Math.random() * COLORS.length);
                    getPixelWriter().setColor(x, y, COLORS[index]);
                } else {
                    getPixelWriter().setColor(x, y, image.getPixelReader().getColor(x, y));
                }
            }
        }
    }
}