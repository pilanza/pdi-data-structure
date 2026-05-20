package com.pdi.views.image_processing_views;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import com.pdi.image_processing.filter.TreeAvlFilter.AVLFilter;

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

public class AVLFilterView {

    // ── Aplicar na imagem ativa ──────────────────────────────────────────────
    public static void applyOnImage(Image image, Consumer<Image> onResult) {
        if (image == null) { showWarning("Nenhuma imagem selecionada!"); return; }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Filtro da Mediana — Árvore AVL");
        stage.setResizable(false);

        VBox header = buildHeader("Filtro da Mediana", "Árvore AVL");

        Label windowLabel = styledLabel("Tamanho da janela");
        Spinner<Integer> spinner = buildSpinner();
        Label hint = buildHint();
        VBox windowRow = new VBox(6, windowLabel, spinner, hint);

        Button cancelBtn = buildCancelButton();
        Button applyBtn  = buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            int size = getSpinnerValue(spinner);
            if (size < 0) return;
            stage.close();
            onResult.accept(new AVLFilter(image, size));
        });

        HBox buttons = buildButtonBar(cancelBtn, applyBtn);
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
        stage.setTitle("Filtro da Mediana AVL — Lote");
        stage.setResizable(false);

        VBox header = buildHeader("Aplicar Filtro em Lote", "Mediana por Árvore AVL");

        final File[] inputDir  = {null};
        final File[] outputDir = {null};

        Label inputLabel = styledLabel("Pasta de entrada");
        Label inputPath  = pathLabel("Nenhuma pasta selecionada");
        Button inputBtn  = browseButton("Selecionar pasta…");
        inputBtn.setOnAction(e -> {
            File dir = chooseDir(stage, "Selecionar pasta de entrada");
            if (dir != null) { inputDir[0] = dir; inputPath.setText(dir.getAbsolutePath()); }
        });
        VBox inputRow = fieldRow(inputLabel, inputPath, inputBtn);

        Label outputLabel = styledLabel("Pasta de destino");
        Label outputPath  = pathLabel("Nenhuma pasta selecionada");
        Button outputBtn  = browseButton("Selecionar pasta…");
        outputBtn.setOnAction(e -> {
            File dir = chooseDir(stage, "Selecionar pasta de destino");
            if (dir != null) { outputDir[0] = dir; outputPath.setText(dir.getAbsolutePath()); }
        });
        VBox outputRow = fieldRow(outputLabel, outputPath, outputBtn);

        Label windowLabel = styledLabel("Tamanho da janela");
        Spinner<Integer> spinner = buildSpinner();
        Label hint = buildHint();
        VBox windowRow = new VBox(6, windowLabel, spinner, hint);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: #4361ee;");

        Label progressLabel = new Label("");
        progressLabel.setFont(Font.font("Segoe UI", 11));
        progressLabel.setTextFill(Color.web("#6c757d"));

        Button cancelBtn = buildCancelButton();
        Button applyBtn  = buildApplyButton();

        cancelBtn.setOnAction(e -> stage.close());
        applyBtn.setOnAction(e -> {
            if (inputDir[0] == null)  { showWarning("Selecione a pasta de entrada.");  return; }
            if (outputDir[0] == null) { showWarning("Selecione a pasta de destino."); return; }

            int size = getSpinnerValue(spinner);
            if (size < 0) return;

            File[] bmps = inputDir[0].listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".bmp")
            );
            if (bmps == null || bmps.length == 0) {
                showWarning("Nenhum arquivo .bmp encontrado na pasta de entrada.");
                return;
            }

            progressBar.setVisible(true);
            applyBtn.setDisable(true);
            cancelBtn.setDisable(true);

            int[] success = {0}, fail = {0};
            for (int i = 0; i < bmps.length; i++) {
                File file = bmps[i];
                try {
                    Image original = new Image(file.toURI().toString());
                    WritableImage filtered = new AVLFilter(original, size);
                    String baseName = file.getName().replaceFirst("\\.[^.]+$", "");
                    File outFile = new File(outputDir[0], baseName + "_avl.bmp");
                    ImageIO.write(toBufferedImage(filtered), "bmp", outFile);
                    success[0]++;
                } catch (IOException ex) {
                    fail[0]++;
                }
                progressBar.setProgress((double)(i + 1) / bmps.length);
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

        HBox buttons = buildButtonBar(cancelBtn, applyBtn);

        VBox body = new VBox(18, inputRow, outputRow, windowRow, progressBar, progressLabel);
        body.setPadding(new Insets(20, 24, 8, 24));

        VBox root = new VBox(header, body, buttons);
        root.setStyle("-fx-background-color: white;");
        stage.setScene(new Scene(root, 480, 460));
        stage.show();
    }

    // ── Utilitários de UI ────────────────────────────────────────────────────

    private static VBox buildHeader(String titleText, String subtitleText) {
        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label(subtitleText);
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web("#6c757d"));

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(20, 24, 12, 24));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private static Spinner<Integer> buildSpinner() {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 3, 2));
        spinner.setEditable(true);
        spinner.setPrefWidth(Double.MAX_VALUE);
        spinner.setStyle("""
            -fx-background-color: #f1f3f5;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-font-family: 'Segoe UI';
            -fx-font-size: 13;
        """);
        return spinner;
    }

    private static Label buildHint() {
        Label hint = new Label("Use valores ímpares: 3, 5, 7, 9…");
        hint.setFont(Font.font("Segoe UI", 11));
        hint.setTextFill(Color.web("#868e96"));
        return hint;
    }

    private static int getSpinnerValue(Spinner<Integer> spinner) {
        try {
            spinner.commitValue();
            int size = spinner.getValue();
            if (size < 1) { showWarning("O tamanho deve ser pelo menos 1."); return -1; }
            if (size % 2 == 0) { showWarning("O tamanho da janela deve ser ímpar (ex: 3, 5, 7)."); return -1; }
            return size;
        } catch (NumberFormatException ex) {
            showWarning("Valor inválido. Digite um número inteiro ímpar.");
            return -1;
        }
    }

    private static Button buildCancelButton() {
        Button b = new Button("Cancelar");
        b.setPrefWidth(100);
        b.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #ced4da;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-family: 'Segoe UI';
            -fx-font-size: 13;
            -fx-cursor: hand;
        """);
        return b;
    }

    private static Button buildApplyButton() {
        Button b = new Button("Aplicar");
        b.setPrefWidth(100);
        b.setStyle("""
            -fx-background-color: #4361ee;
            -fx-text-fill: white;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-family: 'Segoe UI';
            -fx-font-weight: bold;
            -fx-font-size: 13;
            -fx-cursor: hand;
        """);
        return b;
    }

    private static HBox buildButtonBar(Button cancel, Button apply) {
        HBox bar = new HBox(10, cancel, apply);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(8, 24, 20, 24));
        bar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        return bar;
    }

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