package com.cgvsu;

import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.9F; //шаг перемещения камеры
    @FXML
    public Text fileAlreadyExist;
    @FXML
    public Text successSaveText;
    @FXML
    public Text wrongSaveText;
    @FXML
    public Button open;
    @FXML
    public TextField text;
    @FXML
    public Button save;
    @FXML
    public AnchorPane gadgetPane;
    @FXML
    public Button addCamera2;
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

    private Model mesh = null;

    //кнопки добавления камер
    private List<Button> addedButtonsCamera = new ArrayList<>();
    //кнопки удаления камер
    private List<Button> deletedButtonsCamera = new ArrayList<>();

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

        //кнока "Добавить камеру"
        //addedButtonsCamera.add(addCamera2);

        // начальная камера
//        Camera camera = new Camera(
//                new Vector3f(0, 0, 100),
//                new Vector3f(0, 0, 0),
//                1.0F, 1, 0.01F, 100);
        // добавляем начальную камеру
        cameras.add(new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100, true));
        addCameraWithoutParams();

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            for (Camera c : cameras) {
                c.setAspectRatio((float) (height / width)); // задаем AspectRatio
            }

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), choiceCamera(), mesh, (int) width, (int) height); //создаем отрисовку модели
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    //проверяем какая камера сейчас активна /*** обработчик выключенной камеры ***/
    private Camera choiceCamera() {
        for (Camera camera : cameras) {
            if (camera.isActive()) {
                return camera;
            }
        }
        System.out.println("камера не выбрана");
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
        successSaveText.setVisible(false);
        wrongSaveText.setVisible(false);
        fileAlreadyExist.setVisible(false);
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
            mesh = ObjReader.read(fileContent);
            mesh.triangulate();
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    void save(MouseEvent event) {
        if (mesh != null) {
            if (!text.getText().equals("") && text.getText().substring(text.getText().length() - 4).equals(".obj")) {
                File f = new File(text.getText());
                if (f.exists()) {
                    fileAlreadyExist.setVisible(true);
                    successSaveText.setVisible(false);
                    wrongSaveText.setVisible(false);
                } else {
                    ObjWriter.write(mesh, text.getText());
                    successSaveText.setVisible(true);
                    wrongSaveText.setVisible(false);
                    fileAlreadyExist.setVisible(false);
                }
            } else {
                successSaveText.setVisible(false);
                wrongSaveText.setVisible(true);
                fileAlreadyExist.setVisible(false);
            }
        }
    }

    // обработка кнопок для добавления, удаления и выбора камер
    public void addCameraWithoutParams() {
        createCamera();
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

    //проверить, что таких координат камеры ещё нет
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
        }
        else {
            System.out.println("введите нужные значения");
        }
        System.out.println();
    }

    // чтоб можно было вызвать из меня
    public void addCamera(MouseEvent mouseEvent) {
        addCameraWithoutParams();
    }

    //
    public void showCamera(String text) {
        int numOfCamera = Integer.parseInt(text.substring(text.length()-1));
        for (int i = 0; i < cameras.size(); i++){
            if (cameras.get(i).isActive()){
                cameras.get(i).setActive(false);
            }
            if (i+1 == numOfCamera){
                cameras.get(i).setActive(true);
            }
        }
        System.out.println(text);
    }

    public void deleteCamera(String text) {
        System.out.println(text + "delete");
    }

}