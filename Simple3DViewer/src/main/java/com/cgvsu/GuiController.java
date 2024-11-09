package com.cgvsu;

import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
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
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        gadgetPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        gadgetPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (height / width)); // задаем AspectRatio

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height); //создаем отрисовку модели
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    public void moveModel(KeyEvent keyEvent) {
        if (Objects.equals(keyEvent.getText(), "w")) {
            camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "s")) {
            camera.movePosition(new Vector3f(0, 0, TRANSLATION));
        }
        if (Objects.equals(keyEvent.getText(), "a")) {
            camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "d")) {
            camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        }
        if (Objects.equals(keyEvent.getText(), "r")) {
            camera.movePosition(new Vector3f(0, TRANSLATION, 0));
        }
        if (Objects.equals(keyEvent.getText(), "f")) {
            camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
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


}