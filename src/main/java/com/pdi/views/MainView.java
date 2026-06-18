package com.pdi.views;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pdi.image_processing.filter.Color_Filter.ColorQSFilterView;
import com.pdi.views.image_processing_views.FilterViewUtils;
import com.pdi.views.image_processing_views.Gray_Filter_views.AVLFilterView;
import com.pdi.views.image_processing_views.Gray_Filter_views.GraphFilterView;
import com.pdi.views.image_processing_views.Gray_Filter_views.HistogramFilterView;
import com.pdi.views.image_processing_views.Gray_Filter_views.MedianFilterView;
import com.pdi.views.image_processing_views.NoiseView.ConfettiView;
import com.pdi.views.image_processing_views.NoiseView.SaltAndPepperView;
import com.pdi.views.image_processing_views.ColoredFilterView.ColorAVLFilterView;
import com.pdi.views.image_processing_views.ColoredFilterView.ColorHistogramFilterView;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;    

public class MainView extends BorderPane {
    private Pane images_pane = new Pane();
    private Image active_image;
    private String active_image_name;
    private String active_image_extension;
    private InternalWindow activeWindow;
    private File lastDirectory;

    public MainView() {
        MenuBar top_menu = new MenuBar();

        // Menu Arquivo
        Menu archive = new Menu("Arquivo");
        MenuItem load_image = new MenuItem("Carregar Imagem");
            load_image.setOnAction(e -> loadImage());
        MenuItem save = new MenuItem("Salvar");
            save.setOnAction(e -> saveImage());
        MenuItem exit = new MenuItem("Sair");
            exit.setOnAction(e -> System.exit(0));
        archive.getItems().addAll(load_image, save, exit);

        // ------------------------------------------------------------------------------
        // |                                   Cinza                                    |
        // ------------------------------------------------------------------------------

        Menu gray_filter = new Menu("Filtragem (Cinza)");

        // Quick Sort -------------------------------------------------------------------
        Menu quicksort_gray_filtering = new Menu("Filtragem com Ordenação (QS)");

        MenuItem qs_apply = new MenuItem("Aplicar na imagem");
        qs_apply.setOnAction(e -> MedianFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_median" + active_image_extension)
        ));
        MenuItem qs_batch = new MenuItem("Aplicar em lote");
        qs_batch.setOnAction(e -> MedianFilterView.applyOnBatch());
        quicksort_gray_filtering.getItems().addAll(qs_apply, qs_batch);
        // ------------------------------------------------------------------------------


        // Arvore AVL -------------------------------------------------------------------
        Menu tree_gray_filtering = new Menu("Filtragem com Árvore");

        MenuItem avl_apply = new MenuItem("Aplicar na imagem");
        avl_apply.setOnAction(e -> AVLFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_avl" + active_image_extension)
        ));

        MenuItem avl_batch = new MenuItem("Aplicar em lote");
        avl_batch.setOnAction(e -> AVLFilterView.applyOnBatch());

        tree_gray_filtering.getItems().addAll(avl_apply, avl_batch);
        // ------------------------------------------------------------------------------


        // Grafo ------------------------------------------------------------------------
        Menu graph_gray_filtering = new Menu("Filtragem com Grafo");

        MenuItem graph_apply = new MenuItem("Aplicar na imagem");
        graph_apply.setOnAction(e -> GraphFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_graph" + active_image_extension)
        ));
        MenuItem graph_batch = new MenuItem("Aplicar em lote");
        graph_batch.setOnAction(e -> GraphFilterView.applyOnBatch());
        graph_gray_filtering.getItems().addAll(graph_apply, graph_batch);
        // ------------------------------------------------------------------------------

        
        // Historiograma ----------------------------------------------------------------
        Menu histo_gray_filtering = new Menu("Filtragem com Histograma");
        MenuItem histo_apply = new MenuItem("Aplicar na imagem");
        histo_apply.setOnAction(e -> HistogramFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_histogram" + active_image_extension)
        ));

        MenuItem histo_batch = new MenuItem("Aplicar em lote");
        histo_batch.setOnAction(e -> HistogramFilterView.applyOnBatch());

        histo_gray_filtering.getItems().addAll(histo_apply, histo_batch);
        // ------------------------------------------------------------------------------


        gray_filter.getItems().addAll(quicksort_gray_filtering, tree_gray_filtering, graph_gray_filtering, histo_gray_filtering);

        // ------------------------------------------------------------------------------
        // |                                  Colorido                                  |
        // ------------------------------------------------------------------------------

        Menu colored_filter = new Menu("Filtragem (colorida)");

        // Quick Sort -------------------------------------------------------------------
        Menu colored_qs = new Menu("Filtragem com Ordenação");

        MenuItem colored_qs_apply = new MenuItem("Aplicar na imagem");
        colored_qs_apply.setOnAction(e -> ColorQSFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_color_qs" + active_image_extension)
        ));

        MenuItem colored_qs_batch = new MenuItem("Aplicar em lote");
        colored_qs_batch.setOnAction(e -> ColorQSFilterView.applyOnBatch());

        colored_qs.getItems().addAll(colored_qs_apply, colored_qs_batch);
        colored_filter.getItems().addAll(colored_qs);
        // ------------------------------------------------------------------------------


        // Arvore AVL -------------------------------------------------------------------
        Menu color_avl = new Menu("Filtragem com Árvore AVL");

        MenuItem color_avl_apply = new MenuItem("Aplicar na imagem");
        color_avl_apply.setOnAction(e -> ColorAVLFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_color_avl" + active_image_extension)
        ));

        MenuItem color_avl_batch = new MenuItem("Aplicar em lote");
        color_avl_batch.setOnAction(e -> ColorAVLFilterView.applyOnBatch());

        color_avl.getItems().addAll(color_avl_apply, color_avl_batch);
        // ------------------------------------------------------------------------------


        // Grafo ------------------------------------------------------------------------
        Menu graph_colored_filtering = new Menu("Filtragem com Grafo");
        graph_colored_filtering.getItems().addAll(
            new MenuItem("Aplicar na imagem"),
            new MenuItem("Aplicar em lote")
        );
        // ------------------------------------------------------------------------------

        
        // Historiograma ----------------------------------------------------------------
        Menu color_histogram = new Menu("Filtragem com Histograma");

        MenuItem color_histogram_apply = new MenuItem("Aplicar na imagem");
        color_histogram_apply.setOnAction(e -> ColorHistogramFilterView.applyOnImage(
            active_image,
            result -> openImageAsActive(result, active_image_name + "_color_histogram" + active_image_extension)
        ));

        MenuItem color_histogram_batch = new MenuItem("Aplicar em lote");
        color_histogram_batch.setOnAction(e -> ColorHistogramFilterView.applyOnBatch());

        color_histogram.getItems().addAll(color_histogram_apply, color_histogram_batch);
        // ------------------------------------------------------------------------------

        colored_filter.getItems().addAll(colored_qs, color_avl, graph_colored_filtering, color_histogram);
        
        // ------------------------------------------------------------------------------



        // Menu Ruído
        Menu noise = new Menu("Inserir ruído");

        // ------------------------------------------------------------------------------
        Menu salt_and_pepper = new Menu("Sal e Pimenta");

        MenuItem sp_apply = new MenuItem("Aplicar na imagem");
        sp_apply.setOnAction(e -> SaltAndPepperView.applyOnImage(
            active_image,
            active_image_name,
            active_image_extension,
            result -> openImageAsActive(result, active_image_name + "_salt_and_pepper" + active_image_extension)
        ));

        MenuItem sp_batch = new MenuItem("Aplicar em lote");
        sp_batch.setOnAction(e -> SaltAndPepperView.applyOnBatch());

        salt_and_pepper.getItems().addAll(sp_apply, sp_batch);
        // ------------------------------------------------------------------------------

        
        // ------------------------------------------------------------------------------
        Menu confetti = new Menu("Confete");

        MenuItem confetti_apply = new MenuItem("Aplicar na imagem");
        confetti_apply.setOnAction(e -> ConfettiView.applyOnImage(
            active_image, active_image_name, active_image_extension,
            result -> openImageAsActive(result, active_image_name + "_confetti" + active_image_extension)
        ));

        MenuItem confetti_batch = new MenuItem("Aplicar em lote");
        confetti_batch.setOnAction(e -> ConfettiView.applyOnBatch());

        confetti.getItems().addAll(confetti_apply, confetti_batch);

        
        // ------------------------------------------------------------------------------

        noise.getItems().addAll(salt_and_pepper,confetti);

        // Menu Sobre
        Menu about = new Menu("Sobre");
        MenuItem about_item = new MenuItem("Créditos");
        about_item.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Mensagem");
            alert.setHeaderText(null);
            alert.setContentText(
                "Programa desenvolvido e implementado por\n" +
                "Pietro Lanza Peixoto e Rafael Bitencort Sploradori\n" +
                "em 2026/1 na disciplina de PDI ministrada pelo\n" +
                "Prof. Jacques Facon."
            );
            alert.showAndWait();
        });
        about.getItems().add(about_item);

        top_menu.getMenus().addAll(archive, noise, gray_filter, colored_filter, about);
        this.setTop(top_menu);
        this.setCenter(images_pane);
    }

    private void activateWindow(InternalWindow window) {
        if (activeWindow != null) activeWindow.setActive(false);
        activeWindow = window;
        if (window != null) window.setActive(true);
    }

    private void loadImage() {
        FileChooser file_chooser = new FileChooser();
        file_chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imagem", "*.bmp", "*.png", "*.jpeg", "*.jpg")
        );
        if (lastDirectory != null) file_chooser.setInitialDirectory(lastDirectory);

        File file = file_chooser.showOpenDialog(this.getScene().getWindow());
        if (file == null) return;

        lastDirectory = file.getParentFile();

        Image image = new Image(file.toURI().toString());
        active_image = image;
        String name = file.getName();
        active_image_name = name.substring(0, name.lastIndexOf("."));
        active_image_extension = name.substring(name.lastIndexOf("."));

        openImage(image, name);
    }

    private void openImage(Image image, String name) {
        active_image = image;
        String baseName = name.contains(".") ? name.substring(0, name.lastIndexOf(".")) : name;
        String ext = name.contains(".") ? name.substring(name.lastIndexOf(".")) : "";

        ImageView image_view = new ImageView(image);
        InternalWindow window = new InternalWindow(name, image_view, w -> {
            active_image = image;
            active_image_name = baseName;
            active_image_extension = ext;
            activateWindow(w);
        });
        images_pane.getChildren().add(window);
        activateWindow(window);
    }

    private void openImageAsActive(Image image, String name) {
        active_image = image;
        active_image_name = name.contains(".") ? name.substring(0, name.lastIndexOf(".")) : name;
        active_image_extension = name.contains(".") ? name.substring(name.lastIndexOf(".")) : "";

        String baseName = active_image_name;
        String ext = active_image_extension;

        ImageView image_view = new ImageView(image);
        InternalWindow window = new InternalWindow(name, image_view, w -> {
            active_image = image;
            active_image_name = baseName;
            active_image_extension = ext;
            activateWindow(w);
        });
        images_pane.getChildren().add(window);
        activateWindow(window);
    }

    private void saveImage() {
        if (active_image == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Nenhuma imagem foi carregada.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar imagem");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Bitmap (*.bmp)", "*.bmp")
        );
        fileChooser.setInitialFileName(active_image_name + "_salva.bmp");
        if (lastDirectory != null) fileChooser.setInitialDirectory(lastDirectory);

        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file == null) return;

        if (!file.getName().toLowerCase().endsWith(".bmp")) {
            file = new File(file.getAbsolutePath() + ".bmp");
        }

        try {
            java.awt.image.BufferedImage buffered = FilterViewUtils.toBufferedImage(active_image);
            ImageIO.write(buffered, "bmp", file);
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível salvar a imagem");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
}
