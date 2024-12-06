package com.cgvsu;

import com.cgvsu.math.AffineTransformations;
import com.cgvsu.math.TranslationModel;
import com.cgvsu.model.DeleteVertices;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.scene_tools.SceneTools;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;

public class GuiController {

    final private float TRANSLATION = 0.9F; //шаг перемещения камеры

    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Canvas canvas;
    @FXML
    public AnchorPane gadgetPane;
    @FXML
    public ColorPicker baseModelColor = new ColorPicker();
    @FXML
    public ListView<String> listModels;
    @FXML
    public TextField sx;
    @FXML
    public TextField sy;
    @FXML
    public TextField sz;
    @FXML
    public TextField ry;
    @FXML
    public TextField rx;
    @FXML
    public TextField rz;
    @FXML
    public TextField tz;
    @FXML
    public TextField ty;
    @FXML
    public TextField tx;
    @FXML
    public Button convertButton;
    @FXML
    public CheckBox texture;
    @FXML
    public CheckBox light;
    @FXML
    public CheckBox grid;
    @FXML
    public Button deleteModelButton;
    @FXML
    public Button hideModelButton;
    @FXML
    public ListView<String> listCameras;
    @FXML
    public TextField targetZ;
    @FXML
    public TextField targetY;
    @FXML
    public TextField targetX;
    @FXML
    public TextField eyeX;
    @FXML
    public TextField eyeY;
    @FXML
    public TextField eyeZ;
    @FXML
    public Button createCameraButton;
    @FXML
    public Button deleteCameraButton;

    private Timeline timeline;

    @FXML
    private void initialize() throws IOException {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        gadgetPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        gadgetPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        listCameras.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listModels.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ThemeSwitch buttonStyle = new ThemeSwitch();
        buttonStyle.setLayoutY(20);
        buttonStyle.setLayoutX(350);
        SimpleBooleanProperty isOn = buttonStyle.switchOnProperty();
        //АБСОЛЮТНЫЙ ПУТЬ ДО ПРОЕКТА
        File directory = new File(".");
        String path = "file:/" + directory.getCanonicalPath().replace("\\", "/") + "/Simple3DViewer/target/classes/style.css";
        isOn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                buttonStyle.getScene().getRoot().getStylesheets().add(path);
            } else {
                buttonStyle.getScene().getRoot().getStylesheets().remove(path);
            }
        });
        gadgetPane.getChildren().add(buttonStyle);

        createCamera();

        baseModelColor.setValue(Color.GRAY);

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            for (Camera c : SceneTools.cameras) {
                c.setAspectRatio((float) (width / height)); // задаем AspectRatio
            }

            if (SceneTools.meshes.size() != 0) {
                RenderEngine.render(canvas.getGraphicsContext2D(), SceneTools.activeCamera(), SceneTools.activeModels(), (int) width, (int) height); //создаем отрисовку модели
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    public void open(ActionEvent actionEvent) {
        try {
            SceneTools.open(canvas);
            listModels.getItems().add("Модель " + SceneTools.meshes.size());
            listModels.getSelectionModel().select(listModels.getItems().size() - 1);

        } catch (IOException e) {
            showMessage("Ошибка", "Неудалось найти файл!");
        }
    }

    @FXML
    public void saveWithTransform(ActionEvent actionEvent) {
        save(true);
    }

    @FXML
    public void saveWithoutTransform(ActionEvent actionEvent) {
        save(false);
    }

    void save(Boolean transform) {
        String info = SceneTools.save(canvas, transform);
        showMessage("Информация", info);
    }

    private void showMessage(String headText, String messageText) {
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText(headText);
        message.setContentText(messageText);
        message.showAndWait();
    }

    @FXML
    public void moveCamera(KeyEvent keyEvent) {
        if (Objects.equals(keyEvent.getText(), "w")) {
            SceneTools.activeCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "s")) {
            SceneTools.activeCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "a")) {
            SceneTools.activeCamera().movePosition(new Vector3f(TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "d")) {
            SceneTools.activeCamera().movePosition(new Vector3f(-TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "r")) {
            SceneTools.activeCamera().movePosition(new Vector3f(0, TRANSLATION, 0));
        }
        if (Objects.equals(keyEvent.getText(), "f")) {
            SceneTools.activeCamera().movePosition(new Vector3f(0, -TRANSLATION, 0));
        }
    }

    @FXML
    private void createCamera() {
        if (!Objects.equals(eyeX.getText(), "") && !Objects.equals(eyeY.getText(), "") && !Objects.equals(eyeZ.getText(), "")
                && !Objects.equals(targetX.getText(), "") && !Objects.equals(targetY.getText(), "") && !Objects.equals(targetZ.getText(), "")) {
            SceneTools.createCamera(eyeX, eyeY, eyeZ, tx, ty, tz);
            listCameras.getItems().add("Камера " + SceneTools.cameras.size());
            listCameras.getSelectionModel().select(listCameras.getItems().size() - 1);
        } else {
            showMessage("Предупреждение", "Введите необходимые данные!");
        }
    }

    @FXML
    public void choiceCamera(MouseEvent mouseEvent) {
        //TODO: СДЕЛАТЬ ЧТОБЫ МОДЕЛЬ 10 ТОЖЕ ОБРАБАТЫВАЛАСЬ
        int cameraId = Integer.parseInt(listCameras.getSelectionModel().getSelectedItem().substring(listCameras.getSelectionModel().getSelectedItem().length() - 1)) - 1;
        SceneTools.choiceCamera(cameraId);
        listCameras.getSelectionModel().select(cameraId);
    }

    @FXML
    public void deleteCamera(MouseEvent mouseEvent) {
        int cameraId = SceneTools.indexActiveCamera;
        SceneTools.deleteCamera();
        listCameras.getItems().remove(cameraId);
        for (int i = 0; i < listCameras.getItems().size(); i++) {
            listCameras.getItems().set(i, "Камера " + (i + 1));
        }
        //TODO: ПРОВЕРИТЬ ЕСЛИ УДАЛЕНА ВКЛЮЧЕННАЯ КАМЕРА
        listCameras.getSelectionModel().select(0);
    }

    @FXML
    public void choiceModel(MouseEvent mouseEvent) {
        //TODO: ПРОВЕРИТЬ ЕСЛИ КАМЕРА НАЖАТА СНЯТЬ ВЫДЕЛЕНИЯ И УДАЛИТЬ ИЗ АКТИВНЫХ
        List<Integer> selectedModels = new ArrayList<>();
        for (String modelName : listModels.getSelectionModel().getSelectedItems()) {
            int modelId = Integer.parseInt(modelName.substring(modelName.length() - 1)) - 1;
            listModels.getSelectionModel().select(modelId);
            selectedModels.add(modelId);
        }
        SceneTools.choiceModels(selectedModels);
    }

    @FXML
    public void deleteModel(MouseEvent mouseEvent) {
        if (SceneTools.activeMeshes.size() != 0) {
            List<Integer> modelsId = SceneTools.activeMeshes;
            SceneTools.deleteModel();
            for (int i : modelsId) {
                listModels.getItems().remove(i);
            }

            for (int i = 0; i < listModels.getItems().size(); i++) {
                listModels.getItems().set(i, "Модель " + (i + 1));
            }
        } else {
            showMessage("Ошибка", "Нет моделей для удаления");
        }
    }

    @FXML
    public void hideModel(MouseEvent mouseEvent) {
        if (SceneTools.activeMeshes.size() != 0) {
            SceneTools.hideModels();

            // Устанавливаем CellFactory для кастомизации отображения элементов
            listModels.setCellFactory(list -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Меняем цвет текста для определенного элемента
                        if (SceneTools.hideMeshes.contains(Integer.parseInt(item.substring(item.length() - 1)) - 1)) {
                            setTextFill(Color.GRAY);  // Изменить цвет текста
                        } else {
                            setTextFill(Color.BLACK);  // Для остальных элементов
                        }
                    }
                }
            });
        } else {
            showMessage("Ошибка", "Нет моделей для скрытия");
        }
    }

    @FXML
    public void changeDefaultColor(MouseEvent mouseEvent) {
        baseModelColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Color c = baseModelColor.getValue();
                List<Model> models = SceneTools.activeModels();
                for (Model model : models) {
                    model.color = c;
                }
            }
        });
    }

    @FXML
    public void showTexture(MouseEvent mouseEvent) {
        if (SceneTools.activeMeshes.size() > 1) {
            showMessage("Предупреждение", "Выберите одну модель");
        } else {
            boolean action = SceneTools.showTexture(canvas);
            texture.setSelected(action);
        }
    }

    @FXML
    public void showLight(MouseEvent mouseEvent) {
        boolean action = SceneTools.showLight();
        light.setSelected(action);
    }

    @FXML
    public void showGrid(MouseEvent mouseEvent) {
        boolean action = SceneTools.showGrid();
        grid.setSelected(action);
    }

    @FXML
    public void convert(MouseEvent mouseEvent) {

    }

//    public void convert(MouseEvent mouseEvent) {
//        //реализовываю только для смещения
//        if (Objects.equals(tx.getText(), "") || Objects.equals(ty.getText(), "") || Objects.equals(tz.getText(), "")
//                || Objects.equals(sx.getText(), "") || Objects.equals(sy.getText(), "") || Objects.equals(sz.getText(), "")
//                || Objects.equals(rx.getText(), "") || Objects.equals(ry.getText(), "") || Objects.equals(rz.getText(), "")) {
//            showMessage("Ошибка", "Введите необходимые данные!");
//        } else {
//            Matrix4f transposeMatrix = AffineTransformations.modelMatrix(
//                    Integer.parseInt(tx.getText()), Integer.parseInt(ty.getText()), Integer.parseInt(tz.getText()),
//                    Float.parseFloat(rx.getText()), Float.parseFloat(ry.getText()), Float.parseFloat(rz.getText()),
//                    Integer.parseInt(sx.getText()), Integer.parseInt(sy.getText()), Integer.parseInt(sz.getText()));
//            TranslationModel.move(transposeMatrix, activeModel());
//        }
//    }

//    public void deleteVertexAction(MouseEvent mouseEvent) {
//        List<Integer> vectorIndex = parseVertex(deleteVertex.getText());
//        if (vectorIndex.size() == 0) {
//            showMessage("Ошибка", "нет такой вершины у активной модели", messageError);
//        } else {
//            Model model = activeModel();
//            model = DeleteVertices.deleteVerticesFromModel(activeModel(), vectorIndex);
//            model.normalize();
//        }
//    }
//
//    public List<Integer> parseVertex(String vertex) {
//        List<Float> coords = new ArrayList<>();
//        StringBuilder prom = new StringBuilder();
//        for (int i = 0; i < vertex.length(); i++) {
//            if (vertex.charAt(i) == ' ') {
//                coords.add(Float.parseFloat(String.valueOf(prom)));
//                prom = new StringBuilder();
//            } else {
//                prom.append(vertex.charAt(i));
//            }
//        }
//        coords.add(Float.parseFloat(String.valueOf(prom)));
//        com.cgvsu.math.Vector3f resultVector = new com.cgvsu.math.Vector3f(coords.get(0), coords.get(1), coords.get(2));
//        Model model = activeModel();
//        for (int i = 0; i < model.vertices.size(); i++) {
//            if (model.vertices.get(i).equals(resultVector)) {
//                return new ArrayList<>(List.of(i));
//            }
//        }
//        return new ArrayList<>();
//    }
}