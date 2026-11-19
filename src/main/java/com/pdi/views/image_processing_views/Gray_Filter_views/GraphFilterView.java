package com.pdi.views.image_processing_views.Gray_Filter_views;

import java.io.File;
import java.util.function.Consumer;

import com.pdi.image_processing.filter.Gray_Filter.graph_filter.GraphFilter;
import com.pdi.views.image_processing_views.FilterViewUtils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GraphFilterView {

    public static void applyOnImage(Image image, Consumer<Image> onResult) {
        if (image == null) { FilterViewUtils.showWarning("Nenhuma imagem selecionada!"); return; }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (!Stage.getWindows().isEmpty()) stage.initOwner(Stage.getWindows().get(0));
        stage.setTitle("Filtro da Mediana — Grafo");
        stage.setResizable(false);

        VBox header = FilterViewUtils.buildHeader("Filtro da Mediana", "Grafo (BFS)");

        Label depthLabel = FilterViewUtils.styledLabel("Profundidade BFS");
        Spinner<Integer> spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2, 1));
        spinner.setEditable(true);
        spinner.setPrefWidth(Double.MAX_VALUE);
        Label hint = new Label("Maior profundidade = janela maior (mais lento)");
        hint.setFont(Font.font("Segoe UI", 11));
        hint.setTextFill(Color.web("#868e96"));
        VBox depthRow = new VBox(6, depthLabel, spinner, hint);

        Button cancelBtn = FilterViewUtils.buildCancelButton();
        Button applyBtn  = FilterViewUtils.buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            spinner.commitValue();
            int depth = spinner.getValue();
            stage.close();
            try {
                FilterViewUtils.applyTimedFilter(() -> new GraphFilter(image, depth), onResult);
            } catch (Throwable ex) {
                FilterViewUtils.showWarning("Erro ao aplicar filtro Grafo: " + ex.getClass().getSimpleName() + " — " + ex.getMessage());
            }
        });

        HBox buttons = FilterViewUtils.buildButtonBar(cancelBtn, applyBtn);
        VBox body = new VBox(18, depthRow);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 260));
        stage.show();
    }

    public static void applyOnBatch() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (!Stage.getWindows().isEmpty()) stage.initOwner(Stage.getWindows().get(0));
        stage.setTitle("Filtro da Mediana Grafo — Lote");
        stage.setResizable(false);

        VBox header = FilterViewUtils.buildHeader("Aplicar Filtro em Lote", "Mediana por Grafo (BFS)");

        final File[] inputDir  = {null};
        final File[] outputDir = {null};

        Label inputLabel = FilterViewUtils.styledLabel("Pasta de entrada");
        Label inputPath  = FilterViewUtils.pathLabel("Nenhuma pasta selecionada");
        Button inputBtn  = FilterViewUtils.browseButton("Selecionar pasta…");
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

        Label depthLabel = FilterViewUtils.styledLabel("Profundidade BFS");
        Spinner<Integer> spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2, 1));
        spinner.setEditable(true);
        spinner.setPrefWidth(Double.MAX_VALUE);
        VBox depthRow = new VBox(6, depthLabel, spinner);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: #4361ee;");

        Label progressLabel = new Label("");
        progressLabel.setFont(Font.font("Segoe UI", 11));
        progressLabel.setTextFill(Color.web("#6c757d"));

        Button cancelBtn = FilterViewUtils.buildCancelButton();
        Button applyBtn  = FilterViewUtils.buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            if (inputDir[0] == null)  { FilterViewUtils.showWarning("Selecione a pasta de entrada.");  return; }
            if (outputDir[0] == null) { FilterViewUtils.showWarning("Selecione a pasta de destino."); return; }
            spinner.commitValue();
            int depth = spinner.getValue();

            FilterViewUtils.runBatch(
                inputDir[0], outputDir[0], "_graph",
                img -> new GraphFilter(img, depth),
                progressBar, progressLabel, applyBtn, cancelBtn, stage
            );
        });

        HBox buttons = FilterViewUtils.buildButtonBar(cancelBtn, applyBtn);
        VBox body = new VBox(18, inputRow, outputRow, depthRow, progressBar, progressLabel);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 460));
        stage.show();
    }
}
