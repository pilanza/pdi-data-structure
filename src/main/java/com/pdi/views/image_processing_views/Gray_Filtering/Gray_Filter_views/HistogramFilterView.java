package com.pdi.views.image_processing_views.Gray_Filtering.Gray_Filter_views;

import java.io.File;
import java.util.function.Consumer;

import com.pdi.image_processing.filter.Gray_Filter.Histogram_Filter;
import com.pdi.views.image_processing_views.Gray_Filtering.FilterViewUtils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HistogramFilterView {

    // ── Aplicar na imagem ativa ──────────────────────────────────────────────
    public static void applyOnImage(Image image, Consumer<Image> onResult) {
        if (image == null) { FilterViewUtils.showWarning("Nenhuma imagem selecionada!"); return; }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Filtro da Mediana — Histograma");
        stage.setResizable(false);

        VBox header = FilterViewUtils.buildHeader("Filtro da Mediana", "Histograma de Níveis de Cinza");

        Label windowLabel = FilterViewUtils.styledLabel("Tamanho da janela");
        Spinner<Integer> spinner = FilterViewUtils.buildSpinner();
        Label hint = FilterViewUtils.buildHint();
        VBox windowRow = new VBox(6, windowLabel, spinner, hint);

        Button cancelBtn = FilterViewUtils.buildCancelButton();
        Button applyBtn  = FilterViewUtils.buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            int size = FilterViewUtils.getSpinnerValue(spinner);
            if (size < 0) return;
            stage.close();
            onResult.accept(new Histogram_Filter(image, size));
        });

        HBox buttons = FilterViewUtils.buildButtonBar(cancelBtn, applyBtn);
        VBox body = new VBox(18, windowRow);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 260));
        stage.show();
    }

    // ── Aplicar em lote ──────────────────────────────────────────────────────
    public static void applyOnBatch() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Filtro da Mediana Histograma — Lote");
        stage.setResizable(false);

        VBox header = FilterViewUtils.buildHeader("Aplicar Filtro em Lote", "Histograma de Níveis de Cinza");

        final File[] inputDir  = {null};
        final File[] outputDir = {null};

        Label inputLabel  = FilterViewUtils.styledLabel("Pasta de entrada");
        Label inputPath   = FilterViewUtils.pathLabel("Nenhuma pasta selecionada");
        Button inputBtn   = FilterViewUtils.browseButton("Selecionar pasta…");
        inputBtn.setOnAction(e -> {
            File dir = FilterViewUtils.chooseDir(stage, "Selecionar pasta de entrada");
            if (dir != null) { inputDir[0] = dir; inputPath.setText(dir.getAbsolutePath()); }
        });
        VBox inputRow = FilterViewUtils.fieldRow(inputLabel, inputPath, inputBtn);

        Label outputLabel = FilterViewUtils.styledLabel("Pasta de destino");
        Label outputPath  = FilterViewUtils.pathLabel("Nenhuma pasta selecionada");
        Button outputBtn  = FilterViewUtils.browseButton("Selecionar pasta…");
        outputBtn.setOnAction(e -> {
            File dir = FilterViewUtils.chooseDir(stage, "Selecionar pasta de destino");
            if (dir != null) { outputDir[0] = dir; outputPath.setText(dir.getAbsolutePath()); }
        });
        VBox outputRow = FilterViewUtils.fieldRow(outputLabel, outputPath, outputBtn);

        Label windowLabel = FilterViewUtils.styledLabel("Tamanho da janela");
        Spinner<Integer> spinner = FilterViewUtils.buildSpinner();
        Label hint = FilterViewUtils.buildHint();
        VBox windowRow = new VBox(6, windowLabel, spinner, hint);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: #4361ee;");

        Label progressLabel = new Label("");
        progressLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        Button cancelBtn = FilterViewUtils.buildCancelButton();
        Button applyBtn  = FilterViewUtils.buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            if (inputDir[0] == null)  { FilterViewUtils.showWarning("Selecione a pasta de entrada.");  return; }
            if (outputDir[0] == null) { FilterViewUtils.showWarning("Selecione a pasta de destino."); return; }

            int size = FilterViewUtils.getSpinnerValue(spinner);
            if (size < 0) return;

            FilterViewUtils.runBatch(
                inputDir[0], outputDir[0], "_histogram",
                img -> new Histogram_Filter(img, size),
                progressBar, progressLabel,
                applyBtn, cancelBtn, stage
            );
        });

        HBox buttons = FilterViewUtils.buildButtonBar(cancelBtn, applyBtn);

        VBox body = new VBox(18, inputRow, outputRow, windowRow, progressBar, progressLabel);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 460));
        stage.show();
    }
}