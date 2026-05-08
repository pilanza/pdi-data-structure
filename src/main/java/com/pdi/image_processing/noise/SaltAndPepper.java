package com.pdi.image_processing.noise;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SaltAndPepper extends WritableImage {
    public SaltAndPepper(Image image, double probability) {
        super((int) image.getWidth(), (int) image.getHeight());
        double random;

        for(int x=0; x < image.getWidth(); x++) {
            for(int y=0; y < image.getHeight(); y++) {
                random = Math.random();
                if(random<=probability/2) getPixelWriter().setColor(x, y, Color.BLACK);
                else if (random <= probability) getPixelWriter().setColor(x, y, Color.WHITE);
                else getPixelWriter().setColor(x, y, image.getPixelReader().getColor(x, y));
            }
        }
    }
}
