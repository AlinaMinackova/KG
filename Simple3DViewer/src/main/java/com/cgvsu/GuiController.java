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

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    //кнопки добавления камер
    private List<Button> addedButtonsCamera = new ArrayList<>();
    //кнопки удаления камер
    private List<Button> deletedButtonsCamera = new ArrayList<>();
    //кнопки выбора камер
    private List<RadioButton> choiceButtonsCamera = new ArrayList<>();

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
        addedButtonsCamera.add(addCamera2);

        // начальная камера
//        Camera camera = new Camera(
//                new Vector3f(0, 0, 100),
//                new Vector3f(0, 0, 0),
//                1.0F, 1, 0.01F, 100);
        // добавляем начальную камеру
        cameras.add(new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100));
        addCamera();

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

    //проверяем какая камера сейчас активна
    private Camera choiceCamera() {
        //проверять radiobutton какой включён, находить его камеру и возвращать её
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
    public void addCamera() {
        //кнопка добавления камеры
        Button addButton = new Button("Камера " + addedButtonsCamera.size());
        addButton.setLayoutY(addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutY() + 40);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                function(addButton.getText());
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
                functionDelete(addButton.getText());
            }
        });
        deletedButtonsCamera.add(deleteButton);
        //radio button выбора камеры
        for (RadioButton r : choiceButtonsCamera) { // обнуляю предыдущую камеру
            r.setSelected(false);
        }
        RadioButton radioButton = new RadioButton();
        radioButton.setLayoutY(deletedButtonsCamera.get(deletedButtonsCamera.size() - 1).getLayoutY() + 3);
        radioButton.setLayoutX(deletedButtonsCamera.get(deletedButtonsCamera.size() - 1).getLayoutX() + 80);
        radioButton.setText(String.valueOf(choiceButtonsCamera.size()));
        radioButton.setSelected(true);
        radioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                functionChoice(radioButton.getText(), radioButton);
            }
        });
        choiceButtonsCamera.add(radioButton);
        addCameraPane.getChildren().add(addButton);
        addCameraPane.getChildren().add(deleteButton);
        addCameraPane.getChildren().add(radioButton);
    }


    // чтоб можно было вызвать из меня
    public void addCamera(MouseEvent mouseEvent) {
        addCamera();
    }

    public void function(String text) {
        System.out.println(text);
    }

    public void functionDelete(String text) {
        System.out.println(text + "delete");
    }

    public void functionChoice(String text, RadioButton radioButton) {
        for (RadioButton r : choiceButtonsCamera) { // обнуляю предыдущую камеру
            r.setSelected(false);
        }
        radioButton.setSelected(true);
        System.out.println(text + "choice");
    }

}