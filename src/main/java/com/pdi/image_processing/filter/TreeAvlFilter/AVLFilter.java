package com.pdi.image_processing.filter.TreeAvlFilter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AVLFilter extends WritableImage {

    public AVLFilter(Image image, int windowSize) {
        super((int) image.getWidth(), (int) image.getHeight());

        int width  = (int) image.getWidth();
        int height = (int) image.getHeight();
        int half   = windowSize / 2;

        // Cria imagem com borda duplicada (padding)
        int paddedWidth  = width  + 2 * half;
        int paddedHeight = height + 2 * half;
        int[][] padded   = new int[paddedHeight][paddedWidth];

        // Preenche o interior com os valores originais (0-255 escala de cinza)
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                padded[y + half][x + half] = toGray(image, x, y);

        // Duplica bordas horizontais
        for (int y = 0; y < height; y++) {
            for (int p = 0; p < half; p++) {
                padded[y + half][p]                    = padded[y + half][half];           // borda esquerda
                padded[y + half][paddedWidth - 1 - p]  = padded[y + half][paddedWidth - 1 - half]; // borda direita
            }
        }

        // Duplica bordas verticais (incluindo cantos)
        for (int x = 0; x < paddedWidth; x++) {
            for (int p = 0; p < half; p++) {
                padded[p][x]                     = padded[half][x];                // borda topo
                padded[paddedHeight - 1 - p][x]  = padded[paddedHeight - 1 - half][x]; // borda baixo
            }
        }

        int[][] resultado = new int[height][width];

        // Janela deslizante por linha — igual ao seu código C
        for (int y = 0; y < height; y++) {
            ArvoreAVL arvore = new ArvoreAVL();

            // Monta a primeira janela (coluna 0)
            for (int ky = y; ky < y + windowSize; ky++)
                for (int kx = 0; kx < windowSize; kx++)
                    arvore.adicionar(padded[ky][kx]);

            resultado[y][0] = arvore.mediana();

            // Desliza horizontalmente
            for (int x = 1; x < width; x++) {
                // Remove coluna que saiu da janela
                for (int ky = y; ky < y + windowSize; ky++)
                    arvore.remover(padded[ky][x - 1]);

                // Adiciona nova coluna que entrou
                for (int ky = y; ky < y + windowSize; ky++)
                    arvore.adicionar(padded[ky][x + windowSize - 1]);

                resultado[y][x] = arvore.mediana();
            }
        }

        // Escreve o resultado na WritableImage
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                getPixelWriter().setColor(x, y, Color.gray(resultado[y][x] / 255.0));
    }

    private int toGray(Image image, int x, int y) {
        return (int)(image.getPixelReader().getColor(x, y).getRed() * 255);
    }
}