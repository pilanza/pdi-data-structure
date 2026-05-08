package com.pdi.views;

import java.io.File;

import com.pdi.views.image_processing_views.SaltAndPepperView;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

public class MainView extends BorderPane {
    private Pane images_pane = new Pane();
    private Image active_image;
    private String active_image_name;

    public MainView() {
        MenuBar top_menu = new MenuBar();

        //Cria o menu de arquivos
        Menu archive = new Menu("Arquivo");
        MenuItem load_image = new MenuItem("Carregar Imagem");
        load_image.setOnAction(e->loadImage());
        MenuItem save = new MenuItem("Salvar");
        MenuItem exit = new MenuItem("Sair");
        archive.getItems().addAll(load_image, save, exit);

        //Cria o menu de filtragem em níveis de cinza
        Menu gray_filter = new Menu("Filtragem (Cinza)");
        Menu tree_gray_filtering = new Menu("Filtragem com Árvore");
        tree_gray_filtering.getItems().addAll(
            new MenuItem("Aplicar na imagem"),
            new MenuItem("Aplicar em lote")
        );
        Menu graph_gray_filtering = new Menu("Filtragem com Grafo");
        graph_gray_filtering.getItems().addAll(
            new MenuItem("Aplicar na imagem"),
            new MenuItem("Aplicar em lote")
        );
        gray_filter.getItems().addAll(tree_gray_filtering, graph_gray_filtering);

        //Cria o menu de ruído
        Menu noise = new Menu("Ruído");
        MenuItem salt_and_pepper = new MenuItem("Sal e Pimenta");
        salt_and_pepper.setOnAction(e-> new SaltAndPepperView(active_image, active_image_name, result -> openImage(result, active_image_name + "salt_and_pepper")));
        noise.getItems().addAll(salt_and_pepper);

        //Cria o menu de sobre
        Menu about = new Menu("Sobre");

        //Configura o top_menu
        top_menu.getMenus().addAll(archive, gray_filter, noise, about);
        this.setTop(top_menu);

        this.setCenter(images_pane);
    }

    private void loadImage() {
        FileChooser file_chooser = new FileChooser();
        file_chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagem", "*.bmp")
        );

        File file = file_chooser.showOpenDialog(this.getScene().getWindow());

        if (file == null) return;
        Image image = new Image(file.toURI().toString());

        active_image = image;
        active_image_name = file.getName();

        openImage(image, active_image_name);
    }

    private void openImage(Image image, String name) {
        ImageView image_view = new ImageView(image);
        
        InternalWindow image_internal_window = new InternalWindow(name, image_view, () -> active_image = image);
        images_pane.getChildren().add(image_internal_window);
    }
}