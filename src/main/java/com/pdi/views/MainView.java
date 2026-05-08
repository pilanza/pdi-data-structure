package com.pdi.views;

import java.io.File;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

public class MainView extends BorderPane {
    private Pane imagesPane = new Pane();

    public MainView() {
        MenuBar topMenu = new MenuBar();

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

        //Cria o menu de sobre
        Menu about = new Menu("Sobre");

        //Configura o topMenu
        topMenu.getMenus().addAll(archive, gray_filter, about);
        this.setTop(topMenu);

        this.setCenter(imagesPane);
    }

    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagem", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (file == null) return;
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        
        InternalWindow imageInternalWindow = new InternalWindow(file.getName(), imageView);
        imagesPane.getChildren().add(imageInternalWindow);
    }
}