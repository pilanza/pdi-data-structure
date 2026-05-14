package com.pdi.image_processing.filters;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class MedianFilter extends WritableImage {

    public MedianFilter(Image image, int windowSize) {
        super((int) image.getWidth(), (int) image.getHeight());
        int width  = (int) image.getWidth();
        int height = (int) image.getHeight();
        int half   = windowSize / 2;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[] neighbors = collectNeighbors(image, x, y, half, width, height);
                quicksort(neighbors, 0, neighbors.length - 1);
                double median = neighbors[neighbors.length / 2];
                getPixelWriter().setColor(x, y, Color.gray(median));
            }
        }
    }

    private double[] collectNeighbors(Image image, int cx, int cy, int half, int width, int height) {
        int count = 0;
        // conta quantos vizinhos existem dentro dos limites
        for (int dx = -half; dx <= half; dx++)
            for (int dy = -half; dy <= half; dy++) {
                int nx = cx + dx, ny = cy + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) count++;
            }

        double[] values = new double[count];
        int i = 0;
        for (int dx = -half; dx <= half; dx++)
            for (int dy = -half; dy <= half; dy++) {
                int nx = cx + dx, ny = cy + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height)
                    values[i++] = image.getPixelReader().getColor(nx, ny).getRed(); // cinza: R=G=B
            }
        return values;
    }

    // ── Quicksort ────────────────────────────────────────────────────────────
    private void quicksort(double[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quicksort(arr, low, pi - 1);
            quicksort(arr, pi + 1, high);
        }
    }

    private int partition(double[] arr, int low, int high) {
        double pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                double tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
            }
        }
        double tmp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = tmp;
        return i + 1;
    }
}