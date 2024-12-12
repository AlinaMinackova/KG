package com.cgvsu;

import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.scene_tools.SceneTools;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.util.Duration;

import java.io.IOException;
import java.io.File;
import java.util.*;
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
    @FXML
    public TextField deleteVertexField;
    @FXML
    public Button deleteVertexButton;
    @FXML
    public ListView<String> listLights;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TextField eyeXLight;
    @FXML
    public TextField eyeYLight;
    @FXML
    public TextField eyeZLight;
    @FXML
    public Button createLightButton;
    @FXML
    public Button deleteLightButton;
    @FXML
    public Button hideLightButton;
    @FXML
    public ColorPicker chooseLightColor;
    @FXML
    public ProgressBack progressBack;


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
        listLights.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listModels.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        progressBar.setStyle("-fx-accent: green;");

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

        progressBack = new ProgressBack(progressBar);

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            for (Camera c : SceneTools.cameras) {
                c.setAspectRatio((float) (width / height)); // задаем AspectRatio
            }

            if (SceneTools.meshes.size() != 0) {
                RenderEngine.prepareToRender(canvas.getGraphicsContext2D(), SceneTools.activeCamera(), SceneTools.drawMeshes(), (int) width, (int) height, SceneTools.activeLights()); //создаем отрисовку модели
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
            texture.setSelected(false);
            light.setSelected(false);
            grid.setSelected(false);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
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
            SceneTools.createCamera(eyeX, eyeY, eyeZ, targetX, targetY, targetZ);
            listCameras.getItems().add("Камера " + SceneTools.cameras.size());
            listCameras.getSelectionModel().select(listCameras.getItems().size() - 1);
            if (listLights.getItems().size() == 0) {
                listLights.getItems().add("Свет камеры");
                listLights.getSelectionModel().select(listLights.getItems().size() - 1);
            }
        } else {
            showMessage("Предупреждение", "Введите необходимые данные!");
        }
    }

    @FXML
    public void choiceCamera(MouseEvent mouseEvent) {
        String[] cameraId = listCameras.getSelectionModel().getSelectedItem().split(" ");
        SceneTools.choiceCamera(Integer.parseInt(cameraId[cameraId.length - 1]) - 1);
        listCameras.getSelectionModel().select(Integer.parseInt(cameraId[cameraId.length - 1]) - 1);
    }

    @FXML
    public void deleteCamera(MouseEvent mouseEvent) {
        int cameraId = SceneTools.indexActiveCamera;
        SceneTools.deleteCamera();
        listCameras.getItems().remove(cameraId);
        //нахлжу свет удаляемой камеры и удаляю его
        List<Integer> indexesLightCamera = new ArrayList<>();
        for (int i = 0; i < listLights.getItems().size(); i++) {
            String[] lightId = listLights.getItems().get(i).split(" ");
            if (lightId.length == 3 && Integer.parseInt(lightId[lightId.length - 1]) == cameraId) {
                listLights.getItems().remove(cameraId);
            }
        }
        for (int i = 0; i < listLights.getItems().size(); i++) {
            String[] lightId = listLights.getItems().get(i).split(" ");
            if (lightId.length == 3) {
                listLights.getItems().remove(cameraId);
            }
        }
        for (int i = 0; i < listCameras.getItems().size(); i++) {
            listCameras.getItems().set(i, "Камера " + (i + 1));
        }
        listCameras.getSelectionModel().select(0);
    }

    @FXML
    public void choiceModel(MouseEvent mouseEvent) {
        SceneTools.choiceModels(listModels.getSelectionModel().getSelectedIndices());
        baseModelColor.setValue(SceneTools.activeModels().get(0).color);
        //при выборе моделей, в панельке Вид модели менять чекбоксы
        if (SceneTools.activeModels().size() == 1) {
            texture.setSelected(SceneTools.activeModels().get(0).isActiveTexture);
            light.setSelected(SceneTools.activeModels().get(0).isActiveLighting);
            grid.setSelected(SceneTools.activeModels().get(0).isActiveGrid);
        } else {
            boolean gridChoice = true;
            boolean lightChoice = true;
            texture.setSelected(false);
            for (Model model : SceneTools.activeModels()) {
                if (!model.isActiveLighting) {
                    lightChoice = false;
                }
                if (!model.isActiveGrid) {
                    gridChoice = false;
                }
            }
            if (SceneTools.activeModels().size() == 0) {
                light.setSelected(false);
                grid.setSelected(false);
            } else {
                light.setSelected(lightChoice);
                grid.setSelected(gridChoice);
            }
        }
    }

    @FXML
    public void deleteModel(MouseEvent mouseEvent) {
        if (SceneTools.activeMeshes.size() != 0) {
            List<Integer> modelsId = SceneTools.activeMeshes;
            Collections.sort(modelsId);
            for (int i = modelsId.size() - 1; i >= 0; i--) {
                listModels.getItems().remove((int) modelsId.get(i));
            }
            SceneTools.deleteModel();
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
                        int index = getIndex();
                        // Меняем цвет текста для определенного элемента
                        if (SceneTools.hideMeshes.contains(index)) {
                            setTextFill(Color.GRAY);  // Изменить цвет текста
                        } else {
                            setTextFill(Color.BLACK);  // Для остальных элементов
                        }
                    }
                }
            });
            //при выборе моделей, в панельке Вид модели менять чекбоксы
            if (SceneTools.activeModels().size() == 1) {
                texture.setSelected(SceneTools.activeModels().get(0).isActiveTexture);
                light.setSelected(SceneTools.activeModels().get(0).isActiveLighting);
                grid.setSelected(SceneTools.activeModels().get(0).isActiveGrid);
            } else {
                boolean gridChoice = true;
                boolean lightChoice = true;
                texture.setSelected(false);
                for (Model model : SceneTools.activeModels()) {
                    if (!model.isActiveLighting) {
                        lightChoice = false;
                    }
                    if (!model.isActiveGrid) {
                        gridChoice = false;
                    }
                }
                if (SceneTools.activeModels().size() == 0) {
                    light.setSelected(false);
                    grid.setSelected(false);
                } else {
                    light.setSelected(lightChoice);
                    grid.setSelected(gridChoice);
                }
            }
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
            texture.setSelected(false);
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
        if (Objects.equals(tx.getText(), "") || Objects.equals(ty.getText(), "") || Objects.equals(tz.getText(), "")
                || Objects.equals(sx.getText(), "") || Objects.equals(sy.getText(), "") || Objects.equals(sz.getText(), "")
                || Objects.equals(rx.getText(), "") || Objects.equals(ry.getText(), "") || Objects.equals(rz.getText(), "")) {
            showMessage("Ошибка", "Введите необходимые данные!");
        } else {
            //TODO: ЗДЕСЬ ИСПОЛЬЗУЕТСЯ МОЙ МЕТОД ПРИМЕНЕНИЯ АФФИННЫХ ПРЕОБРАЗОВАНИЙ, КОГДА БУДЕШЬ ДЕЛАТЬ, ЗАМЕНИ НА СВОЙ
            SceneTools.convert(sx, sy, sz, rx, ry, rz, tx, ty, tz);
            //обновляю поля пребразований
            sx.setText("1");
            sy.setText("1");
            sz.setText("1");
            rx.setText("0");
            ry.setText("0");
            rz.setText("0");
            tx.setText("0");
            ty.setText("0");
            tz.setText("0");
        }
    }

    @FXML
    public void deleteVertexAction(MouseEvent mouseEvent) {
        if (SceneTools.activeModels().size() == 1) {
            List<Integer> indexes = parseVertex();
            SceneTools.deleteVertexes(indexes);
        } else {
            showMessage("Ошибка", "Веберите одну модель для удаления вершин");
        }
    }

    public List<Integer> parseVertex() {
        Set<Integer> vertexIndexes = new HashSet<>();
        for (String index : deleteVertexField.getText().split(", ")) {
            vertexIndexes.add(Integer.valueOf(index));
        }
        return new ArrayList<Integer>(vertexIndexes);
    }

    @FXML
    public void createLight(MouseEvent mouseEvent) {
        if (!Objects.equals(eyeXLight.getText(), "") && !Objects.equals(eyeYLight.getText(), "") && !Objects.equals(eyeZLight.getText(), "")) {
            SceneTools.createLight(eyeXLight, eyeYLight, eyeZLight, chooseLightColor.getValue());
            listLights.getItems().add(getColorHex(chooseLightColor.getValue()));
            listLights.getSelectionModel().select(listLights.getItems().size() - 1);
        } else {
            showMessage("Предупреждение", "Введите необходимые данные!");
        }
    }

    public String getColorHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b).toLowerCase(Locale.ROOT);
    }

    @FXML
    public void deleteLight(MouseEvent mouseEvent) {
        if (SceneTools.indexActiveLight != -1) {
            if (listLights.getSelectionModel().getSelectedItem().split(" ").length == 2) {
                showMessage("Ошибка", "Нельзя удалить свет камеры");
            } else {
                listLights.getItems().remove(SceneTools.indexActiveLight);
                SceneTools.deleteLight();
            }
        } else {
            showMessage("Ошибка", "Нет света для удаления");
        }

    }

    @FXML
    public void hideLight(MouseEvent mouseEvent) {
        if (SceneTools.indexActiveLight != -1) {
            if (SceneTools.lights.size() > 1) {
                if ((SceneTools.lights.size() - SceneTools.hideLights.size()) > 1 || SceneTools.hideLights.contains(SceneTools.indexActiveLight)) {
                    SceneTools.hideLights();

                    // Устанавливаем CellFactory для кастомизации отображения элементов
                    listLights.setCellFactory(list -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {

                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                int index = getIndex();
                                // Меняем цвет текста для определенного элемента
                                if (SceneTools.hideLights.contains(index)) {
                                    setTextFill(Color.GRAY);  // Изменить цвет текста
                                } else {
                                    setTextFill(Color.BLACK);  // Для остальных элементов
                                }
                            }
                        }
                    });
                } else {
                    showMessage("Ошибка", "Нельзя скрыть единственный источник света");
                }
            } else {
                showMessage("Ошибка", "Нельзя скрыть единственный источник света");
            }
        } else {
            showMessage("Ошибка", "Нет света для скрытия");
        }
    }

    @FXML
    public void chooseLights(MouseEvent mouseEvent) {
        int lightId = listLights.getSelectionModel().getSelectedIndex();
        SceneTools.chooseLight(lightId);
        listLights.getSelectionModel().select(lightId);
    }
}