package com.pdi.image_processing.filter.Color_Filter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class YcbcrConverter {

    // ── RGB → YCbCr ──────────────────────────────────────────────────────────
    // Retorna 3 WritableImages: [0]=Y, [1]=Cb, [2]=Cr  (valores 0-255 mapeados em 0.0-1.0)
    public static WritableImage[] toYCbCr(Image image) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();

        WritableImage channelY  = new WritableImage(w, h);
        WritableImage channelCb = new WritableImage(w, h);
        WritableImage channelCr = new WritableImage(w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = image.getPixelReader().getColor(x, y);
                double r = c.getRed()   * 255;
                double g = c.getGreen() * 255;
                double b = c.getBlue()  * 255;

                double Y  =  0.299  * r + 0.587  * g + 0.114  * b;
                double Cb = -0.1687 * r - 0.3313 * g + 0.5    * b + 128;
                double Cr =  0.5    * r - 0.4187 * g - 0.0813 * b + 128;

                channelY.getPixelWriter().setColor(x, y,
                    Color.gray(clamp(Y)  / 255.0));
                channelCb.getPixelWriter().setColor(x, y,
                    Color.gray(clamp(Cb) / 255.0));
                channelCr.getPixelWriter().setColor(x, y,
                    Color.gray(clamp(Cr) / 255.0));
            }
        }

        return new WritableImage[]{ channelY, channelCb, channelCr };
    }

    // ── YCbCr → RGB ──────────────────────────────────────────────────────────
    // Recebe Y, Cb, Cr como WritableImages (valores 0.0-1.0 → 0-255 internamente)
    public static WritableImage toRGB(Image channelY, Image channelCb, Image channelCr) {
        int w = (int) channelY.getWidth();
        int h = (int) channelY.getHeight();
        WritableImage result = new WritableImage(w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double Y  = channelY.getPixelReader().getColor(x, y).getRed()  * 255;
                double Cb = channelCb.getPixelReader().getColor(x, y).getRed() * 255 - 128;
                double Cr = channelCr.getPixelReader().getColor(x, y).getRed() * 255 - 128;

                double r = Y + 1.402   * Cr;
                double g = Y - 0.34414 * Cb - 0.71414 * Cr;
                double b = Y + 1.772   * Cb;

                result.getPixelWriter().setColor(x, y, Color.color(
                    clamp(r) / 255.0,
                    clamp(g) / 255.0,
                    clamp(b) / 255.0
                ));
            }
        }

        return result;
    }

    private static double clamp(double val) {
        return Math.max(0, Math.min(255, val));
    }
}