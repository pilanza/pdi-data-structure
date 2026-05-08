package com.pdi.views.image_processing_views;

import java.util.Optional;
import java.util.function.Consumer;

import com.pdi.image_processing.noise.SaltAndPepper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.TextInputDialog;

public class SaltAndPepperView {
    public SaltAndPepperView(Image image, String name, Consumer<Image> onResult) {
        double salt_and_pepper_propability = 0.1;

        if (image == null) {
            Alert no_active_image_alert = new Alert(AlertType.WARNING);
            no_active_image_alert.setHeaderText("Nenhuma imagem selecionada!");
            no_active_image_alert.showAndWait();
            return;
        }

        TextInputDialog salt_and_pepper_dialog = new TextInputDialog(String.valueOf(salt_and_pepper_propability));
        salt_and_pepper_dialog.setTitle("Probabilidade");
        salt_and_pepper_dialog.setHeaderText("");
        salt_and_pepper_dialog.setContentText("Digite a probabilidade (0-1): ");

        Optional<String> result = salt_and_pepper_dialog.showAndWait();
        if(result.isPresent()) salt_and_pepper_propability = Double.parseDouble(result.get());

        Image salt_and_pepper_image = new SaltAndPepper(image, salt_and_pepper_propability);
        onResult.accept(salt_and_pepper_image);
    }
}
