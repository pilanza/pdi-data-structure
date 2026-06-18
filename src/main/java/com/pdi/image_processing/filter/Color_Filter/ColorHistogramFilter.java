package com.pdi.image_processing.filter.Color_Filter;

import com.pdi.image_processing.filter.Gray_Filter.Histogram_Filter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ColorHistogramFilter extends WritableImage {

    public ColorHistogramFilter(Image image, int windowSize) {
        super((int) image.getWidth(), (int) image.getHeight());

        // 1. RGB → YCbCr
        WritableImage[] channels = YcbcrConverter.toYCbCr(image);
        WritableImage Y  = channels[0];
        WritableImage Cb = channels[1];
        WritableImage Cr = channels[2];

        // 2. Aplica Histograma apenas no canal Y
        WritableImage filteredY = new Histogram_Filter(Y, windowSize);

        // 3. YCbCr → RGB
        WritableImage result = YcbcrConverter.toRGB(filteredY, Cb, Cr);

        // 4. Copia para this
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                getPixelWriter().setColor(x, y, result.getPixelReader().getColor(x, y));
    }
}