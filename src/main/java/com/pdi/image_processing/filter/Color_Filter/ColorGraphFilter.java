package com.pdi.image_processing.filter.Color_Filter;

import com.pdi.image_processing.filter.Gray_Filter.graph_filter.GraphFilter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ColorGraphFilter extends WritableImage {

    public ColorGraphFilter(Image image, int depth) {
        super((int) image.getWidth(), (int) image.getHeight());

        WritableImage[] channels = YcbcrConverter.toYCbCr(image);
        WritableImage Y  = channels[0];
        WritableImage Cb = channels[1];
        WritableImage Cr = channels[2];

        WritableImage filteredY = new GraphFilter(Y, depth);

        WritableImage result = YcbcrConverter.toRGB(filteredY, Cb, Cr);

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                getPixelWriter().setColor(x, y, result.getPixelReader().getColor(x, y));
    }
}
