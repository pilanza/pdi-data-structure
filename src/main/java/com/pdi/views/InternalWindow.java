package com.pdi.views;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class InternalWindow extends VBox {
    private double dragOffsetX;
    private double dragOffsetY;

    public InternalWindow(String label, ImageView image_view, Runnable change_active_window) {
        Label titleLabel = new Label(label);
        Button closeButton = new Button("x");
        HBox titleBox = new HBox(titleLabel, closeButton);
        getChildren().addAll(titleBox, image_view);

        //Fecha a janela
        closeButton.setOnAction(e -> ((Pane) this.getParent()).getChildren().remove(this));

        //Seleciona a janela ativa
        titleBox.setOnMouseClicked(e -> change_active_window.run());

        //Drag 'n Drop
        titleBox.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX() - this.getLayoutX(); 
            dragOffsetY = e.getSceneY() - this.getLayoutY();
        });

        titleBox.setOnMouseDragged(e-> {
            this.setLayoutX(e.getSceneX() - dragOffsetX);
            this.setLayoutY(Math.max(0, (e.getSceneY() - dragOffsetY)));
        });

        //Estilos
        String close_button_general_style = "-fx-font-family: 'Segoe UI'; -fx-font-size: 12; -fx-padding: 2 8 2 8; -fx-background-radius: 0";
        closeButton.setStyle("-fx-background-color: transparent; " + close_button_general_style);
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #c42b1c; -fx-text-fill: white; -fx-cursor: hand;" + close_button_general_style));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " + close_button_general_style));

        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleBox.setStyle("-fx-background-color: white");

        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setStyle("-fx-padding: 2 0 2 8");

        this.setStyle("-fx-border-color: black; -fx-border-width: 0.5");
    }
}
