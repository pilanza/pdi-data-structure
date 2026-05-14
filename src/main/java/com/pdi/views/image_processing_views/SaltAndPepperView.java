package com.pdi.views.image_processing_views;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import com.pdi.image_processing.noise.SaltAndPepper;

import java.awt.image.BufferedImage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaltAndPepperView {

    // ── Aplicar na imagem ativa ──────────────────────────────────────────────
    public static void applyOnImage(Image image, String imageName,
                                    String imageExtension, Consumer<Image> onResult) {
        if (image == null) { showWarning("Nenhuma imagem selecionada!"); return; }

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

        // ── Cabeçalho ──
        Label title = new Label("Aplicar Ruído em Lote");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Sal e Pimenta");
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web("#6c757d"));

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(20, 24, 12, 24));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        // ── Campos ──
        final File[] inputDir  = {null};
        final File[] outputDir = {null};

        // Pasta de entrada
        Label inputLabel = styledLabel("Pasta de entrada");
        Label inputPath  = pathLabel("Nenhuma pasta selecionada");
        Button inputBtn  = browseButton("Selecionar pasta…");
        inputBtn.setOnAction(e -> {
            File dir = chooseDir(stage, "Selecionar pasta de entrada");
            if (dir != null) { inputDir[0] = dir; inputPath.setText(dir.getAbsolutePath()); }
        });
        VBox inputRow = fieldRow(inputLabel, inputPath, inputBtn);

        // Pasta de destino
        Label outputLabel = styledLabel("Pasta de destino");
        Label outputPath  = pathLabel("Nenhuma pasta selecionada");
        Button outputBtn  = browseButton("Selecionar pasta…");
        outputBtn.setOnAction(e -> {
            File dir = chooseDir(stage, "Selecionar pasta de destino");
            if (dir != null) { outputDir[0] = dir; outputPath.setText(dir.getAbsolutePath()); }
        });
        VBox outputRow = fieldRow(outputLabel, outputPath, outputBtn);

        // Probabilidade
        Label probLabel = styledLabel("Probabilidade (0.0 – 1.0)");
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
        sliderRow.setMaxWidth(Double.MAX_VALUE); // <- adiciona essa linha
        sliderRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(probSlider, Priority.ALWAYS);

        VBox probRow = new VBox(6, probLabel, sliderRow);

        // ── Barra de progresso (oculta até processar) ──
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: #4361ee;");

        Label progressLabel = new Label("");
        progressLabel.setFont(Font.font("Segoe UI", 11));
        progressLabel.setTextFill(Color.web("#6c757d"));

        // ── Botões de ação ──
        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefWidth(100);
        cancelBtn.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #ced4da;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-family: 'Segoe UI';
            -fx-font-size: 13;
            -fx-cursor: hand;
        """);
        cancelBtn.setOnAction(e -> stage.close());

        Button applyBtn = new Button("Aplicar");
        applyBtn.setPrefWidth(100);
        applyBtn.setStyle("""
            -fx-background-color: #4361ee;
            -fx-text-fill: white;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-family: 'Segoe UI';
            -fx-font-weight: bold;
            -fx-font-size: 13;
            -fx-cursor: hand;
        """);

        applyBtn.setOnAction(e -> {
            if (inputDir[0] == null)  { showWarning("Selecione a pasta de entrada.");  return; }
            if (outputDir[0] == null) { showWarning("Selecione a pasta de destino."); return; }

            File[] bmps = inputDir[0].listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".bmp")
            );
            if (bmps == null || bmps.length == 0) {
                showWarning("Nenhum arquivo .bmp encontrado na pasta de entrada.");
                return;
            }

            double prob = probSlider.getValue();
            progressBar.setVisible(true);
            applyBtn.setDisable(true);
            cancelBtn.setDisable(true);

            int[] success = {0}, fail = {0};
            for (int i = 0; i < bmps.length; i++) {
                File file = bmps[i];
                try {
                    Image original  = new Image(file.toURI().toString());
                    WritableImage noisy = new SaltAndPepper(original, prob);
                    String baseName = file.getName().replaceFirst("\\.[^.]+$", "");
                    File outFile    = new File(outputDir[0], baseName + "_salt_and_pepper.bmp");
                    ImageIO.write(toBufferedImage(noisy), "bmp", outFile);
                    success[0]++;
                } catch (IOException ex) {
                    fail[0]++;
                }
                double prog = (double)(i + 1) / bmps.length;
                progressBar.setProgress(prog);
                progressLabel.setText("Processando " + (i + 1) + " de " + bmps.length + "…");
            }

            progressLabel.setText("Concluído!");
            applyBtn.setDisable(false);
            cancelBtn.setDisable(false);

            Alert done = new Alert(AlertType.INFORMATION);
            done.initOwner(stage);
            done.setTitle("Lote concluído");
            done.setHeaderText(null);
            done.setContentText(
                "✔  Salvas com sucesso: " + success[0] + "\n" +
                (fail[0] > 0 ? "✘  Falhas: " + fail[0] : "")
            );
            done.showAndWait();
            stage.close();
        });

        HBox buttons = new HBox(10, cancelBtn, applyBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 24, 20, 24));
        buttons.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");

        // ── Montagem ──
        VBox body = new VBox(18, inputRow, outputRow, probRow, progressBar, progressLabel);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");

        stage.setScene(new Scene(root, 480, 430));
        stage.show();
    }

    // ── Utilitários de UI ────────────────────────────────────────────────────
    private static Label styledLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#495057"));
        return l;
    }

    private static Label pathLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", 12));
        l.setTextFill(Color.web("#868e96"));
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle("""
            -fx-background-color: #f1f3f5;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-padding: 6 10 6 10;
        """);
        HBox.setHgrow(l, Priority.ALWAYS);
        return l;
    }

    private static Button browseButton(String text) {
        Button b = new Button(text);
        b.setStyle("""
            -fx-background-color: #e9ecef;
            -fx-border-color: #ced4da;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-font-family: 'Segoe UI';
            -fx-font-size: 12;
            -fx-cursor: hand;
        """);
        return b;
    }

    private static VBox fieldRow(Label label, Label path, Button btn) {
        HBox selector = new HBox(8, path, btn);
        selector.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(path, Priority.ALWAYS);
        return new VBox(6, label, selector);
    }

    private static File chooseDir(Stage owner, String title) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(title);
        return dc.showDialog(owner);
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
            if (val < 0 || val > 1) { showWarning("Probabilidade deve estar entre 0 e 1."); return Optional.empty(); }
            return Optional.of(val);
        } catch (NumberFormatException e) {
            showWarning("Valor inválido. Digite um número entre 0 e 1.");
            return Optional.empty();
        }
    }

    // ── Conversão de imagem ──────────────────────────────────────────────────
    public static BufferedImage toBufferedImage(Image image) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                javafx.scene.paint.Color fx = image.getPixelReader().getColor(x, y);
                int r = (int)(fx.getRed()   * 255);
                int g = (int)(fx.getGreen() * 255);
                int b = (int)(fx.getBlue()  * 255);
                buf.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return buf;
    }

    private static void showWarning(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}