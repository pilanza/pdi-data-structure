package com.pdi;

import com.pdi.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Filtragem Utilizando Estrutura de Dados");

        MainView mainView = new MainView();
        stage.setScene(new Scene(mainView, 800, 600));
        stage.setMaximized(true);
        stage.show();
    }
}