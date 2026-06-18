package com.pdi.image_processing.filter.Gray_Filter.graph_filter;

import javafx.scene.image.Image;

public class GraphImage {
    GraphNo[][] nodes;
    int width, height;

    GraphImage(Image image) {
        this.width = (int)image.getWidth();
        this.height = (int)image.getHeight();
        this.nodes = new GraphNo[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new GraphNo((int)(image.getPixelReader().getColor(x, y).getRed() * 255));
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x > 0)          nodes[x][y].neighbors.add(nodes[x-1][y]);
                if (x < width - 1)  nodes[x][y].neighbors.add(nodes[x+1][y]);
                if (y > 0)          nodes[x][y].neighbors.add(nodes[x][y-1]);
                if (y < height - 1) nodes[x][y].neighbors.add(nodes[x][y+1]);
            }
        }
    }
}
