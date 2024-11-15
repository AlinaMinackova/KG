package com.cgvsu;

import com.cgvsu.math.AffineTransformations;
import com.cgvsu.math.TranslationModel;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.9F; //шаг перемещения камеры

    public ColorPicker choiceBaseColor;
    public CheckBox transformSave;

    //для модели
    public AnchorPane modelPane;
    public TextField sx;
    public TextField sy;
    public TextField sz;
    public TextField tx;
    public TextField ty;
    public TextField tz;
    public TextField rx;
    public TextField ry;
    public TextField rz;
    public Button convert;

    Alert messageWarning = new Alert(Alert.AlertType.WARNING);
    Alert messageError = new Alert(Alert.AlertType.ERROR);
    Alert messageInformation = new Alert(Alert.AlertType.INFORMATION);

    @FXML
    public Button open;
    @FXML
    public TextField text;
    @FXML
    public Button save;
    @FXML
    public AnchorPane gadgetPane;
    @FXML
    public AnchorPane addCameraPane;
    public TextField eyeX;
    public TextField targetX;
    public TextField eyeY;
    public TextField targetY;
    public TextField eyeZ;
    public TextField targetZ;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private List<Model> meshes = new ArrayList<>();

    //кнопки  камер
    private List<Button> addedButtonsCamera = new ArrayList<>();
    //кнопки удаления камер
    private List<Button> deletedButtonsCamera = new ArrayList<>();

    //кнопки моделей
    private List<Button> addedButtonsModel = new ArrayList<>();
    //кнопки удаления моделей
    private List<Button> deletedButtonsModel = new ArrayList<>();
    private List<CheckBox> checkBoxesTexture = new ArrayList<>();
    private List<CheckBox> checkBoxesLighting = new ArrayList<>();
    private List<CheckBox> checkBoxesGrid = new ArrayList<>();
    private List<RadioButton> choiceModelRadioButtons = new ArrayList<>();

    private List<Camera> cameras = new ArrayList<>();


    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        gadgetPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        gadgetPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);


        // Возможность выбора цвета модели

//        Label l1 = new Label("no selected color ");
//        // create a color picker
//        ColorPicker cp = new ColorPicker();
//        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e)
//            {
//                // color
//                Color c = cp.getValue();
//
//                // set text of the label to RGB value of color
//                l1.setText("Red = " + c.getRed() + ", Green = " + c.getGreen()
//                        + ", Blue = " + c.getBlue());
//            }
//        };
//        // set listener
//        cp.setOnAction(event2);
//        gadgetPane.getChildren().add(cp);

        cameras.add(new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.001F, 1000, true));
        addCameraButtons();


        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            for (Camera c : cameras) {
                c.setAspectRatio((float) (width / height)); // задаем AspectRatio
            }

            if (meshes.size() != 0) {
                RenderEngine.render(canvas.getGraphicsContext2D(), choiceCamera(), meshes, (int) width, (int) height); //создаем отрисовку модели

            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private void showMessage(String headText, String messageText, Alert alert) {
        alert.setHeaderText(headText);
        alert.setContentText(messageText);
        alert.showAndWait();
    }

    //проверяем какая камера сейчас активна
    private Camera choiceCamera() {
        for (Camera camera : cameras) {
            if (camera.isActive()) {
                return camera;
            }
        }
        showMessage("Информация", "Нет активной камеры. Переключаю на: Камера 1", messageInformation);
        return cameras.get(0);
    }

    @FXML
    public void moveCamera(KeyEvent keyEvent) {
        if (Objects.equals(keyEvent.getText(), "w")) {
            choiceCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "s")) {
            choiceCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "a")) {
            choiceCamera().movePosition(new Vector3f(TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "d")) {
            choiceCamera().movePosition(new Vector3f(-TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "r")) {
            choiceCamera().movePosition(new Vector3f(0, TRANSLATION, 0));
        }
        if (Objects.equals(keyEvent.getText(), "f")) {
            choiceCamera().movePosition(new Vector3f(0, -TRANSLATION, 0));
        }
    }

    @FXML
    void open(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model mesh = ObjReader.read(fileContent);
            mesh.triangulate();
            mesh.normalize();
            meshes.add(mesh);
            addModelButtons();
            // добавить функцию, которая будет создавать кнопки:
            // модель (для выбора), удалить, (добавить текстуру, включить сетку, освещение - checkbox)
        } catch (IOException exception) {
            showMessage("Ошибка", "Неудалось найти файл!", messageError);
        }
    }

    @FXML
    void save(MouseEvent event) {
        if (meshes.size() != 0) {
            if (!text.getText().equals("") && text.getText().substring(text.getText().length() - 4).equals(".obj")) {
                File f = new File(text.getText());
                if (f.exists()) {
                    showMessage("Предупреждение", "Файл с таким именем уже существует!", messageWarning);
                } else {
                    if (transformSave.isSelected()) { //сохранить модель с изменениями?
                        //model.transform();
                        // когда будут приходить значения для трансформации,
                        // изменяй не сами вершины в модели, а создай доп поле transformationVertices которое
                        // будет хранить изменённые вершины. Также создай метод transform(), при вызове которого
                        // будешь менять местами згначения полей vertices и transformationVertices, чтобы
                        // я смогла сохранить модель с изменёнными параметрами
                    }
                    ObjWriter.write(meshes.get(checkMesh()), text.getText());

                    showMessage("Информация", "Модель " + (checkMesh()+1) + " успешно сохранёна!", messageInformation);
                }
            } else {
                showMessage("Предупреждение", "Введите имя файла в формате .obj", messageWarning);
            }
        } else {
            showMessage("Предупреждение", "Откройте модель для сохранения!", messageWarning);
        }
    }

    // обработка кнопок для добавления, удаления и выбора камер
    public void addCameraButtons() {
        //кнопка добавления камеры
        Button addButton = new Button("Камера " + (addedButtonsCamera.size() + 1));
        addButton.setLayoutY((addedButtonsCamera.size() > 0) ?
                addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutY() + 40 :
                185);
        addButton.setLayoutX(33);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showCamera(addButton.getText());
            }
        });
        addedButtonsCamera.add(addButton);
        //кнопка удаления камеры
        Button deleteButton = new Button("Удалить");
        deleteButton.setLayoutY(addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutY());
        deleteButton.setLayoutX(addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutX() + 85);
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteCamera(addButton.getText());
            }
        });
        deletedButtonsCamera.add(deleteButton);

        addCameraPane.getChildren().add(addButton);
        addCameraPane.getChildren().add(deleteButton);
    }

    public void addModelButtons() {
        //кнопка добавления камеры
        Button addButton = new Button("Модель " + (addedButtonsModel.size() + 1));
        addButton.setLayoutY((addedButtonsModel.size() > 0) ?
                addedButtonsModel.get(addedButtonsModel.size() - 1).getLayoutY() + 40 :
                240);
        addButton.setLayoutX(45);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showModel(addButton.getText());
            }
        });
        addedButtonsModel.add(addButton);
        //кнопка удаления камеры
        Button deleteButton = new Button("Удалить");
        deleteButton.setLayoutY(addedButtonsModel.get(addedButtonsModel.size() - 1).getLayoutY());
        deleteButton.setLayoutX(addedButtonsModel.get(addedButtonsModel.size() - 1).getLayoutX() + 85);
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteModel(addButton.getText());
            }
        });
        deletedButtonsModel.add(deleteButton);

        RadioButton radioButton = new RadioButton();
        radioButton.setLayoutY(deletedButtonsModel.get(deletedButtonsModel.size() - 1).getLayoutY() + 4);
        radioButton.setLayoutX(deletedButtonsModel.get(deletedButtonsModel.size() - 1).getLayoutX() + 75);
        radioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showModel(addButton.getText());
            }
        });
        choiceModelRadioButtons.add(radioButton);

        showModel(addButton.getText());

        modelPane.getChildren().add(addButton);
        modelPane.getChildren().add(deleteButton);
        modelPane.getChildren().add(radioButton);
    }

    @FXML
    private void createCamera() {
        if (!Objects.equals(eyeX.getText(), "") && !Objects.equals(eyeY.getText(), "") && !Objects.equals(eyeZ.getText(), "")
                && !Objects.equals(targetX.getText(), "") && !Objects.equals(targetY.getText(), "") && !Objects.equals(targetZ.getText(), "")) {
            for (Camera camera : cameras) {
                camera.setActive(false);
            }
            cameras.add(new Camera(
                    new Vector3f(Float.parseFloat(eyeX.getText()), Float.parseFloat(eyeY.getText()), Float.parseFloat(eyeZ.getText())),
                    new Vector3f(Float.parseFloat(targetX.getText()), Float.parseFloat(targetY.getText()), Float.parseFloat(targetZ.getText())),
                    1.0F, 1, 0.01F, 100, true));
            addCameraButtons();
        } else {
            showMessage("Предупреждение", "Введите необходимые данные!", messageWarning);
        }
    }

    //
    public void showCamera(String text) {
        int numOfCamera = Integer.parseInt(text.substring(text.length() - 1));
        for (int i = 0; i < cameras.size(); i++) {
            if (cameras.get(i).isActive()) {
                cameras.get(i).setActive(false);
            }
            if (i + 1 == numOfCamera) {
                cameras.get(i).setActive(true);
            }
        }
    }

    public void deleteCamera(String text) {
        int numOfCamera = Integer.parseInt(text.substring(text.length() - 1));
        for (int i = 0; i < addedButtonsCamera.size(); i++) {
            if (i + 1 == numOfCamera) {
                if (cameras.get(i).isActive()) {
                    cameras.get(0).setActive(true);
                }
                delete(i);
                break;
            }
        }
    }

    public void delete(int cameraID) {
        if (cameras.size() == 1) {
            showMessage("Ошибка", "Нельзя удалить единственную камеру!", messageError);
        } else {
            cameras.remove(cameraID);
            addCameraPane.getChildren().remove(addedButtonsCamera.get(cameraID));
            addCameraPane.getChildren().remove(deletedButtonsCamera.get(cameraID));
            //переименовываем кнопки
            for (int i = 0; i < addedButtonsCamera.size(); i++) {
                if (i + 1 > cameraID) {
                    addedButtonsCamera.get(i).setText("Камера " + i);
                }
            }
            //смещаем координаты
            for (int i = addedButtonsCamera.size() - 1; i >= 1; i--) {
                if (i + 1 > cameraID) {
                    addedButtonsCamera.get(i).setLayoutY(addedButtonsCamera.get(i - 1).getLayoutY());
                    deletedButtonsCamera.get(i).setLayoutY(deletedButtonsCamera.get(i - 1).getLayoutY());
                }
            }
            addedButtonsCamera.remove(cameraID);
            deletedButtonsCamera.remove(cameraID);
        }
    }

    // при нажатии на кнопку преобразовать - проверить какая модель мейчас активна
    // и светануть окошко тип, выбрана модель :... или как-то так
    public void convert(MouseEvent mouseEvent) {
        //реализовываю только для смещения

        // проверить, что все поля заполенны
        Matrix4f transposeMatrix = AffineTransformations.translationMatrix(
                Integer.parseInt(tx.getText()), Integer.parseInt(ty.getText()), Integer.parseInt(tz.getText()));
        TranslationModel.move(transposeMatrix, meshes.get(checkMesh()));
    }

    private Integer checkMesh() {
        for (int i = 0; i < meshes.size(); i++){
            if (meshes.get(i).isActive) {
                return i;
            }
        }
        //оповестить
        return 0;
    }

    public void showModel(String text) {
        int numOfModel = Integer.parseInt(text.substring(text.length() - 1));
        for (int i = 0; i < meshes.size(); i++) {
            if (meshes.get(i).isActive) {
                meshes.get(i).isActive = false;
                choiceModelRadioButtons.get(i).setSelected(false);
            }
            if (i + 1 == numOfModel) {
                meshes.get(i).isActive = true;
                choiceModelRadioButtons.get(i).setSelected(true);
            }
        }
        System.out.println();
    }

    public void deleteModel(String text) {
        int numOfModel = Integer.parseInt(text.substring(text.length() - 1));
        meshes.remove(numOfModel - 1);
        modelPane.getChildren().remove(addedButtonsModel.get(numOfModel - 1));
        modelPane.getChildren().remove(deletedButtonsModel.get(numOfModel - 1));
        modelPane.getChildren().remove(choiceModelRadioButtons.get(numOfModel - 1));
        //переименовываем кнопки
        for (int i = 0; i < addedButtonsModel.size(); i++) {
            if (i + 1 > numOfModel) {
                addedButtonsModel.get(i).setText("Модель " + i);
            }
        }
        //смещаем координаты
        for (int i = addedButtonsModel.size() - 1; i >= 1; i--) {
            if (i + 1 > numOfModel) {
                addedButtonsModel.get(i).setLayoutY(addedButtonsModel.get(i - 1).getLayoutY());
                deletedButtonsModel.get(i).setLayoutY(deletedButtonsModel.get(i - 1).getLayoutY());
                choiceModelRadioButtons.get(i).setLayoutY(choiceModelRadioButtons.get(i - 1).getLayoutY());
            }
        }
        addedButtonsModel.remove(numOfModel - 1);
        deletedButtonsModel.remove(numOfModel - 1);
        choiceModelRadioButtons.remove(numOfModel - 1);
    }
}