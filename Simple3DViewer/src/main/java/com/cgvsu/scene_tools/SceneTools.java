package com.cgvsu.scene_tools;

import com.cgvsu.math.AffineTransformations;
import com.cgvsu.math.TranslationModel;
import com.cgvsu.model.DeleteVertices;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SceneTools {

    //список камер
    public static List<Camera> cameras = new ArrayList<>();
    public static int indexActiveCamera = 0;
    //список моделей
    public static List<Model> meshes = new ArrayList<>();
    public static List<Integer> activeMeshes = new ArrayList<>();
    public static List<Integer> hideMeshes = new ArrayList<>();

    public static void open(Canvas canvas) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            throw new RuntimeException("не открыли файл!");
        }

        Path fileName = Path.of(file.getAbsolutePath());

        String fileContent = Files.readString(fileName);
        Model mesh = ObjReader.read(fileContent);
        mesh.triangulate();
        mesh.normalize();
        meshes.add(mesh);
        activeMeshes.add(meshes.size() - 1);
    }

    //TODO: СДЕЛАТЬ СОХРАНЕНИЕ НЕСКОЛЬКИХ МОДЕЛЕЙ
    public static String save(Canvas canvas, Boolean transform) {
        if (meshes.size() != 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Model");

            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            if (file == null) {
                return "Ошибка сохранения! Файл не найден.";
            }
            String fileName = String.valueOf(Path.of(file.getAbsolutePath()));
            int index = 1;
            for (Model model : activeModels()) {
                ObjWriter.write(model, fileName + index + ".obj", transform);
                index++;
            }
            return "Модель успешно сохранёна!";
        } else {
            return "Откройте модель для сохранения!";
        }
    }

    public static void createCamera(TextField eyeX, TextField eyeY, TextField eyeZ, TextField tx, TextField ty, TextField tz) {
        cameras.add(new Camera(
                new Vector3f(Float.parseFloat(eyeX.getText()), Float.parseFloat(eyeY.getText()), Float.parseFloat(eyeZ.getText())),
                new Vector3f(Float.parseFloat(tx.getText()), Float.parseFloat(ty.getText()), Float.parseFloat(tz.getText())),
                1.0F, 1, 0.01F, 100));
        indexActiveCamera = cameras.size() - 1;
    }

    public static void choiceCamera(int cameraId) {
        indexActiveCamera = cameraId;
    }

    public static void deleteCamera() {
        cameras.remove(indexActiveCamera);
        indexActiveCamera = 0;
    }

    public static Camera activeCamera() {
        return cameras.get(indexActiveCamera);
    }

    public static void choiceModels(List<Integer> selectedModels) {
        activeMeshes.clear();
        activeMeshes.addAll(selectedModels);
    }

    public static void deleteModel() {
        List<Integer> modelsId = activeMeshes;
        Collections.sort(modelsId);
        for (int i = modelsId.size() - 1; i >= 0; i--) {
            meshes.remove((int) modelsId.get(i));
            hideMeshes.remove(modelsId.get(i));

        }
        activeMeshes.clear();

    }

    public static List<Model> activeModels() {
        List<Model> activeModels = new ArrayList<>();
        for (int i = 0; i < meshes.size(); i++) {
            if (!hideMeshes.contains(i) && activeMeshes.contains(i)) {
                activeModels.add(meshes.get(i));
            }
        }
        return activeModels;
    }


    public static void hideModels() {
        Set<Integer> hideModels = new HashSet<>(hideMeshes);
        Set<Integer> selectedHideModels = new HashSet<>(activeMeshes);

        boolean isSubset = true;
        for (Integer element : selectedHideModels) {
            if (!hideModels.contains(element)) {
                isSubset = false;
                break;
            }
        }
        //если выбранные модели уже неактивны, сделать активными
        if (isSubset) {
            Set<Integer> intersection = new HashSet<>(hideModels);
            intersection.retainAll(selectedHideModels);
            hideMeshes.removeAll(intersection);
            activeMeshes.addAll(intersection); //если модели больше не спрятаны, то становятся активными
        }
        //если хотя бы 1 неактивна, сделать все неактивными
        else {
            hideModels.addAll(selectedHideModels);
            hideMeshes = new ArrayList<>(hideModels);
            activeMeshes.removeAll(selectedHideModels);//если модели спрятаны, то становятся не активными
        }
    }

    public static boolean showTexture(Canvas canvas) {
        if (meshes.get(activeMeshes.get(0)).pathTexture == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texture (*.png, *.jpg)", "*.png", "*.jpg"));
            fileChooser.setTitle("Load Texture");

            File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
            if (file == null) {
                meshes.get(activeMeshes.get(0)).isActiveTexture = false;
                return false;
            }

            Path fileName = Path.of(file.getAbsolutePath());
            meshes.get(activeMeshes.get(0)).pathTexture = String.valueOf(fileName);
            meshes.get(activeMeshes.get(0)).isActiveTexture = true;
            return true;
        }
        meshes.get(activeMeshes.get(0)).isActiveTexture = !meshes.get(activeMeshes.get(0)).isActiveTexture;
        return meshes.get(activeMeshes.get(0)).isActiveTexture;
    }

    public static boolean showLight() {
        Set<Integer> activeModels = new HashSet<>(activeMeshes);
        Set<Integer> lightModels = new HashSet<>();
        for (int i = 0; i < meshes.size(); i++) {
            if (meshes.get(i).isActiveLighting) {
                lightModels.add(i);
            }
        }
        boolean isSubset = true;
        for (Integer element : activeModels) {
            if (!lightModels.contains(element)) {
                isSubset = false;
                break;
            }
        }
        if (isSubset) {
            Set<Integer> intersection = new HashSet<>(lightModels);
            intersection.retainAll(activeModels);
            for (int i = 0; i < meshes.size(); i++) {
                if (intersection.contains(i)) {
                    meshes.get(i).isActiveLighting = false;
                }
            }
            return false;
        }
        //если хотя бы 1 без света, сделать все со светом
        else {
            lightModels.addAll(activeModels);
            for (int i = 0; i < meshes.size(); i++) {
                if (lightModels.contains(i)) {
                    meshes.get(i).isActiveLighting = true;
                }
            }
            return true;
        }
    }

    public static boolean showGrid() {
        Set<Integer> activeModels = new HashSet<>(activeMeshes);
        Set<Integer> gridModels = new HashSet<>();
        for (int i = 0; i < meshes.size(); i++) {
            if (meshes.get(i).isActiveGrid) {
                gridModels.add(i);
            }
        }
        boolean isSubset = true;
        for (Integer element : activeModels) {
            if (!gridModels.contains(element)) {
                isSubset = false;
                break;
            }
        }
        if (isSubset) {
            Set<Integer> intersection = new HashSet<>(gridModels);
            intersection.retainAll(activeModels);
            for (int i = 0; i < meshes.size(); i++) {
                if (intersection.contains(i)) {
                    meshes.get(i).isActiveGrid = false;
                }
            }
            return false;
        }
        //если хотя бы 1 без сетки, сделать все с сетками
        else {
            gridModels.addAll(activeModels);
            for (int i = 0; i < meshes.size(); i++) {
                if (gridModels.contains(i)) {
                    meshes.get(i).isActiveGrid = true;
                }
            }
            return true;
        }
    }

    public static void convert(TextField sx, TextField sy, TextField sz, TextField rx, TextField ry, TextField rz, TextField tx, TextField ty, TextField tz) {
        for (Model model : activeModels()) {
            Matrix4f transposeMatrix = AffineTransformations.modelMatrix(
                    Integer.parseInt(tx.getText()), Integer.parseInt(ty.getText()), Integer.parseInt(tz.getText()),
                    Float.parseFloat(rx.getText()), Float.parseFloat(ry.getText()), Float.parseFloat(rz.getText()),
                    Integer.parseInt(sx.getText()), Integer.parseInt(sy.getText()), Integer.parseInt(sz.getText()));
            //TODO: ЭТО МОЙ МЕТОД ПРИМЕНЕНИЯ АФФИННЫХ ПРЕОБРАЗОВАНИЙ, КОГДА БУДЕШЬ ДЕЛАТЬ, ЗАМЕНИ НА СВОЙ
            TranslationModel.move(transposeMatrix, model);
        }
    }

    public static void deleteVertexes(List<Integer> indexes) {
        activeModels().get(0).deletedVertexes.addAll(indexes);
        DeleteVertices.deleteVerticesFromModel(activeModels().get(0), indexes);
    }

    public static List<Model> drawMeshes() {
        List<Model> activeModels = new ArrayList<>();
        for (int i = 0; i < meshes.size(); i++) {
            if (!hideMeshes.contains(i)) {
                activeModels.add(meshes.get(i));
            }
        }
        return activeModels;

    }
}
