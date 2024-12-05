package com.cgvsu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Simple3DViewer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // чтобы была возможность добавить больше виджетов нужно сделать AchorPane и gadgetPane одного размера
        // чтоб в приложении вниз прокрутить
        // чтоб в реале вниз крутить, нужно сделать их размеры больше чем экрана, например 2400)
        AnchorPane viewport = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/gui2.fxml")));

        Scene scene = new Scene(viewport);
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Simple3DViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}