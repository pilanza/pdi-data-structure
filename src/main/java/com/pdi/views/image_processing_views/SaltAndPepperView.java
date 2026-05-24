package com.pdi.views.image_processing_views;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import com.pdi.image_processing.noise.SaltAndPepper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaltAndPepperView {

    // ── Aplicar na imagem ativa ──────────────────────────────────────────────
    public static void applyOnImage(Image image, String imageName, String imageExtension, Consumer<Image> onResult) {
        if (image == null) { FilterViewUtils.showWarning("Nenhuma imagem selecionada!"); return; }

        Optional<Double> prob = askProbability();
        if (prob.isEmpty()) return;

        onResult.accept(new SaltAndPepper(image, prob.get()));
    }

    // ── Aplicar em lote (janela dedicada) ───────────────────────────────────
    public static void applyOnBatch() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Sal e Pimenta — Lote");
        stage.setResizable(false);

        VBox header = FilterViewUtils.buildHeader("Aplicar Ruído em Lote", "Sal e Pimenta");

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

        Label probLabel = FilterViewUtils.styledLabel("Probabilidade (0.0 – 1.0)");
        Slider probSlider = new Slider(0, 1, 0.1);
        probSlider.setShowTickLabels(true);
        probSlider.setShowTickMarks(true);
        probSlider.setMajorTickUnit(0.25);
        probSlider.setMinorTickCount(4);
        probSlider.setSnapToTicks(false);
        probSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(probSlider, Priority.ALWAYS);
        probSlider.setStyle("-fx-accent: #4361ee;");

        Label probValue = new Label("0.10");
        probValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        probValue.setTextFill(Color.web("#4361ee"));
        probValue.setMinWidth(36);
        probValue.setAlignment(Pos.CENTER_RIGHT);
        probSlider.valueProperty().addListener((obs, o, n) ->
            probValue.setText(String.format("%.2f", n.doubleValue())));

        HBox sliderRow = new HBox(10, probSlider, probValue);
        sliderRow.setMaxWidth(Double.MAX_VALUE);
        sliderRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(probSlider, Priority.ALWAYS);

        VBox probRow = new VBox(6, probLabel, sliderRow);

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

            double prob = probSlider.getValue();
            FilterViewUtils.runBatch(
                inputDir[0], outputDir[0], "_salt_and_pepper",
                img -> new SaltAndPepper(img, prob),
                progressBar, progressLabel, applyBtn, cancelBtn, stage
            );
        });

        HBox buttons = FilterViewUtils.buildButtonBar(cancelBtn, applyBtn);

        VBox body = new VBox(18, inputRow, outputRow, probRow, progressBar, progressLabel);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 430));
        stage.show();
    }

    // ── Diálogo de probabilidade (para "aplicar na imagem") ─────────────────
    private static Optional<Double> askProbability() {
        TextInputDialog dialog = new TextInputDialog("0.1");
        dialog.setTitle("Probabilidade");
        dialog.setHeaderText("");
        dialog.setContentText("Digite a probabilidade (0-1): ");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return Optional.empty();
        try {
            double val = Double.parseDouble(result.get());
            if (val < 0 || val > 1) { FilterViewUtils.showWarning("Probabilidade deve estar entre 0 e 1."); return Optional.empty(); }
            return Optional.of(val);
        } catch (NumberFormatException e) {
            FilterViewUtils.showWarning("Valor inválido. Digite um número entre 0 e 1.");
            return Optional.empty();
        }
    }
}
