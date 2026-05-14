package com.pdi.views.image_processing_views;

import java.util.function.Consumer;

import com.pdi.image_processing.filters.MedianFilter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MedianFilterView {

    public static void applyOnImage(Image image, Consumer<Image> onResult) {
        if (image == null) {
            showWarning("Nenhuma imagem selecionada!");
            return;
        }

        // Diálogo para escolher o tamanho da janela
        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("Filtro da Mediana");
        dialog.setHeaderText("Tamanho da janela");
        dialog.setContentText("Digite um número ímpar (ex: 3, 5, 7...): ");

        dialog.showAndWait().ifPresent(input -> {
            int size;
            try {
                size = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                showWarning("Valor inválido. Digite um número inteiro ímpar.");
                return;
            }

            if (size < 1) {
                showWarning("O tamanho deve ser pelo menos 1.");
                return;
            }
            if (size % 2 == 0) {
                showWarning("O tamanho da janela deve ser ímpar (ex: 3, 5, 7).");
                return;
            }

            WritableImage result = new MedianFilter(image, size);
            onResult.accept(result);
        });
    }

    private static void showWarning(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}