package com.pdi.image_processing.filter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class HistogramFilter extends WritableImage {

    public HistogramFilter(Image image, int windowSize) {
        super((int) image.getWidth(), (int) image.getHeight());
        int width  = (int) image.getWidth();
        int height = (int) image.getHeight();
        int half   = windowSize / 2;

        // Padding com borda duplicada
        int pw = width  + 2 * half;
        int ph = height + 2 * half;
        int[][] padded = new int[ph][pw];

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                padded[y + half][x + half] = toGray(image, x, y);

        // Bordas horizontais
        for (int y = 0; y < height; y++) {
            for (int p = 0; p < half; p++) {
                padded[y + half][p]            = padded[y + half][half];
                padded[y + half][pw - 1 - p]   = padded[y + half][pw - 1 - half];
            }
        }

        // Bordas verticais (incluindo cantos)
        for (int x = 0; x < pw; x++) {
            for (int p = 0; p < half; p++) {
                padded[p][x]            = padded[half][x];
                padded[ph - 1 - p][x]   = padded[ph - 1 - half][x];
            }
        }

        // Janela deslizante por linha
        for (int y = 0; y < height; y++) {
            int[] hist = new int[256];
            int total  = 0;

            // Monta o histograma da primeira janela
            for (int ky = y; ky < y + windowSize; ky++) {
                for (int kx = 0; kx < windowSize; kx++) {
                    hist[padded[ky][kx]]++;
                    total++;
                }
            }

            getPixelWriter().setColor(0, y, Color.gray(mediana(hist, total) / 255.0));

            // Desliza horizontalmente
            for (int x = 1; x < width; x++) {
                // Remove coluna que saiu
                for (int ky = y; ky < y + windowSize; ky++) {
                    hist[padded[ky][x - 1]]--;
                    total--;
                }

                // Adiciona coluna que entrou
                for (int ky = y; ky < y + windowSize; ky++) {
                    hist[padded[ky][x + windowSize - 1]]++;
                    total++;
                }

                getPixelWriter().setColor(x, y, Color.gray(mediana(hist, total) / 255.0));
            }
        }
    }

    private int mediana(int[] hist, int total) {
        int alvo  = (total + 1) / 2;
        int soma  = 0;
        for (int i = 0; i < 256; i++) {
            soma += hist[i];
            if (soma >= alvo) return i;
        }
        return 255;
    }

    private int toGray(Image image, int x, int y) {
        return (int)(image.getPixelReader().getColor(x, y).getRed() * 255);
    }
}