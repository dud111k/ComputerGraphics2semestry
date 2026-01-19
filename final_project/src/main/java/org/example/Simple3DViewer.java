package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.math.vector.Vector3;
import org.example.math.matrix.Matrix4;
import org.example.model.Model;
import org.example.render_engine.Camera;
import org.example.render_engine.RenderEngine;
import org.example.math.affine.AffineTransformer;
import org.example.ObjWriter.ObjWriter;
import org.example.ObjReader.ObjReader;
import javafx.application.Platform;

import java.io.File;

public class Simple3DViewer extends Application {

    private Camera camera;
    private Model currentModel;
    private Canvas canvas;
    private GraphicsContext gc;

    // UI элементы
    private Label statusLabel;
    private TextField scaleXField, scaleYField, scaleZField;
    private TextField rotateXField, rotateYField, rotateZField;
    private TextField translateXField, translateYField, translateZField;

    // Для управления камерой
    private double mouseX, mouseY;
    private boolean mousePressed = false;

    @Override
    public void start(Stage primaryStage) {
        // 1. Создаем основную компоновку
        BorderPane root = new BorderPane();

        // 2. Создаем холст для рисования
        canvas = new Canvas(1200, 800);
        gc = canvas.getGraphicsContext2D();

        // Сразу после создания canvas:
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        // Привязка события при фокусе окна
        primaryStage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(() -> {
                    canvas.requestFocus();
                    System.out.println("Окно активно - фокус на canvas!");
                });
            }
        });

        // Камера
        camera = new Camera(
                new Vector3(100, 100, 300),
                new Vector3(0, 0, 0),
                Math.toRadians(60.0),
                canvas.getWidth() / canvas.getHeight(),
                0.1,
                1000.0
        );

        // 4. Создаем панель управления
        VBox controlPanel = createControlPanel();

        // 5. Располагаем элементы
        root.setCenter(canvas);
        root.setRight(controlPanel);

        // 6. Создаем сцену
        Scene scene = new Scene(root, 1600, 900);

        // 7. Настраиваем управление
        setupControls(scene);

        // 8. Настраиваем окно
        primaryStage.setTitle("3D Model Transformer");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // 9. Рисуем начальный кадр
        drawFrame();

        primaryStage.show();
    }

    private TextField createNumberField(String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPrefWidth(60);
        field.setStyle("-fx-font-size: 12px;");
        return field;
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
        panel.setPrefWidth(300);

        // Заголовок
        Label title = new Label("Управление моделью");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label hotkeysTitle = new Label("🔥 ГОРЯЧИЕ КЛАВИШИ:");
        hotkeysTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox hotkeysBox = new VBox(3);
        hotkeysBox.setStyle("-fx-background-color: #e8f4f8; -fx-padding: 8; -fx-border-radius: 5;");

        hotkeysBox.getChildren().addAll(
                new Label("W/S - Движение вперед/назад"),
                new Label("A/D - Движение влево/вправо"),
                new Label("Q/E - Движение вверх/вниз"),
                new Label("+/- - Приближение/отдаление"),
                new Label("R - Сброс камеры"),
                new Label("Z - Сброс трансформаций"),
                new Label("1/2/3 - Виды: спереди, сбоку, сверху"),
                new Label("Стрелки - Вращение камеры"),
                new Label("Колесико - Зум"),
                new Label("ЛКМ+движение - Вращение камеры")
        );

        // Кнопка загрузки
        Button loadButton = new Button("Загрузить OBJ");
        loadButton.setOnAction(e -> loadModel());

        // Статус
        statusLabel = new Label("Модель не загружена");

        // Масштабирование
        Label scaleLabel = new Label("Масштабирование:");
        HBox scaleBox = new HBox(5);
        scaleXField = createNumberField("1.0");
        scaleYField = createNumberField("1.0");
        scaleZField = createNumberField("1.0");
        scaleBox.getChildren().addAll(
                new Label("X:"), scaleXField,
                new Label("Y:"), scaleYField,
                new Label("Z:"), scaleZField
        );
        Button scaleButton = new Button("Применить масштаб");
        scaleButton.setOnAction(e -> applyScale());

        // Поворот
        Label rotateLabel = new Label("Поворот (градусы):");
        HBox rotateBox = new HBox(5);
        rotateXField = createNumberField("0");
        rotateYField = createNumberField("0");
        rotateZField = createNumberField("0");
        rotateBox.getChildren().addAll(
                new Label("X:"), rotateXField,
                new Label("Y:"), rotateYField,
                new Label("Z:"), rotateZField
        );
        Button rotateButton = new Button("Применить поворот");
        rotateButton.setOnAction(e -> applyRotation());

        // Перемещение
        Label translateLabel = new Label("Перемещение:");
        HBox translateBox = new HBox(5);
        translateXField = createNumberField("0");
        translateYField = createNumberField("0");
        translateZField = createNumberField("0");
        translateBox.getChildren().addAll(
                new Label("X:"), translateXField,
                new Label("Y:"), translateYField,
                new Label("Z:"), translateZField
        );
        Button translateButton = new Button("Применить перемещение");
        translateButton.setOnAction(e -> applyTranslation());

        // Кнопки управления
        Button resetButton = new Button("Сбросить трансформации");
        resetButton.setOnAction(e -> resetTransformations());

        Button saveOriginalButton = new Button("Сохранить оригинал");
        saveOriginalButton.setOnAction(e -> saveModel(false));

        Button saveTransformedButton = new Button("Сохранить с трансформациями");
        saveTransformedButton.setOnAction(e -> saveModel(true));

        // Добавляем все элементы на панель
        panel.getChildren().addAll(
                title, loadButton, statusLabel,
                new Separator(),
                hotkeysTitle, hotkeysBox,
                new Separator(),
                scaleLabel, scaleBox, scaleButton,
                new Separator(),
                rotateLabel, rotateBox, rotateButton,
                new Separator(),
                translateLabel, translateBox, translateButton,
                new Separator(),
                resetButton, saveOriginalButton, saveTransformedButton
        );

        return panel;
    }

    private void setupControls(Scene scene) {
        canvas.setOnKeyPressed(event -> {
            double moveSpeed = 50.0; // Скорость движения

            switch (event.getCode()) {
                case W:
                    camera.moveForward(1.0);
                    System.out.println("W - ВПЕРЕД");
                    break;
                case S:
                    camera.moveBackward(1.0);
                    System.out.println("S - НАЗАД");
                    break;
                case A:
                    camera.moveLeft(1.0);
                    System.out.println("A - ВЛЕВО");
                    break;
                case D:
                    camera.moveRight(1.0);
                    System.out.println("D - ВПРАВО");
                    break;
                case Q:
                    camera.moveDown(1.0); // ВНИЗ
                    System.out.println("Q - ВНИЗ");
                    break;
                case E:
                    camera.moveUp(1.0); // ВВЕРХ
                    System.out.println("E - ВВЕРХ");
                    break;

                // Зум
                case ADD:
                case EQUALS:
                    camera.zoomIn();
                    System.out.println("+ - ПРИБЛИЖЕНИЕ");
                    break;
                case SUBTRACT:
                case MINUS:
                    camera.zoomOut();
                    System.out.println("- - ОТДАЛЕНИЕ");
                    break;

                // Вращение стрелками
                case UP:
                    camera.rotateVertical(-5.0);
                    break;
                case DOWN:
                    camera.rotateVertical(5.0);
                    break;
                case LEFT:
                    camera.rotateHorizontal(-5.0);
                    break;
                case RIGHT:
                    camera.rotateHorizontal(5.0);
                    break;

                case R:
                    camera.reset();
                    System.out.println("R - СБРОС КАМЕРЫ");
                    break;

                case DIGIT1:
                    camera.setFrontView();
                    System.out.println("1 - ВИД СПЕРЕДИ");
                    break;
                case DIGIT2:
                    camera.setSideView();
                    System.out.println("2 - ВИД СБОКУ");
                    break;
                case DIGIT3:
                    camera.setTopView();
                    System.out.println("3 - ВИД СВЕРХУ");
                    break;

                case Z:
                    resetTransformations();
                    System.out.println("Z - СБРОС ТРАНСФОРМАЦИЙ");
                    break;
            }

            drawFrame();
            event.consume();
        });

        // Мышь
        scene.setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
            mousePressed = true;
        });

        scene.setOnMouseReleased(e -> mousePressed = false);

        scene.setOnMouseDragged(e -> {
            if (mousePressed) {
                double dx = e.getSceneX() - mouseX;
                double dy = e.getSceneY() - mouseY;
                camera.rotate(dx, dy);
                mouseX = e.getSceneX();
                mouseY = e.getSceneY();
                drawFrame();
            }
        });

        // Зум колесиком
        scene.setOnScroll(e -> {
            double delta = e.getDeltaY();
            if (delta > 0) {
                camera.zoomIn();
            } else {
                camera.zoomOut();
            }
            drawFrame();
        });
    }

    // МЕТОД ЗАГРУЗКИ МОДЕЛИ (ДОБАВЬ ЭТО!)
    private void loadModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите OBJ файл");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                currentModel = ObjReader.read(file.getAbsolutePath());
                statusLabel.setText("Загружено: " + file.getName() +
                        " (" + currentModel.getVertices().size() + " вершин)");

                // Центрируем модель
                centerModel();

                // Сбрасываем трансформации при загрузке новой модели
                resetTransformations();

                drawFrame();
            } catch (Exception e) {
                statusLabel.setText("Ошибка загрузки: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void centerModel() {
        if (currentModel == null || currentModel.getVertices().isEmpty()) return;

        // Вычисляем центр модели
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (Vector3 vertex : currentModel.getVertices()) {
            minX = Math.min(minX, vertex.getX());
            maxX = Math.max(maxX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            maxY = Math.max(maxY, vertex.getY());
            minZ = Math.min(minZ, vertex.getZ());
            maxZ = Math.max(maxZ, vertex.getZ());
        }

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;
        double centerZ = (minZ + maxZ) / 2;

        // Сдвигаем модель в центр
        Matrix4 translateMatrix = AffineTransformer.createTranslation(-centerX, -centerY, -centerZ);
        currentModel.setTransformationMatrix(translateMatrix);
    }

    private void applyScale() {
        if (currentModel == null) {
            statusLabel.setText("Сначала загрузите модель!");
            return;
        }

        try {
            double sx = Double.parseDouble(scaleXField.getText());
            double sy = Double.parseDouble(scaleYField.getText());
            double sz = Double.parseDouble(scaleZField.getText());

            // Применяем масштабирование
            Matrix4 scaleMatrix = AffineTransformer.createScale(sx, sy, sz);
            Matrix4 currentMatrix = currentModel.getTransformationMatrix();
            Matrix4 newMatrix = currentMatrix.mul(scaleMatrix);
            currentModel.setTransformationMatrix(newMatrix);

            statusLabel.setText("Масштаб применен: (" + sx + ", " + sy + ", " + sz + ")");
            drawFrame();
        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка: введите числа!");
        }
    }

    private void applyRotation() {
        if (currentModel == null) {
            statusLabel.setText("Сначала загрузите модель!");
            return;
        }

        try {
            double rx = Double.parseDouble(rotateXField.getText());
            double ry = Double.parseDouble(rotateYField.getText());
            double rz = Double.parseDouble(rotateZField.getText());

            // Применяем повороты по осям
            Matrix4 currentMatrix = currentModel.getTransformationMatrix();

            if (rx != 0) {
                Matrix4 rotX = AffineTransformer.createRotationXDeg(rx);
                currentMatrix = currentMatrix.mul(rotX);
            }
            if (ry != 0) {
                Matrix4 rotY = AffineTransformer.createRotationYDeg(ry);
                currentMatrix = currentMatrix.mul(rotY);
            }
            if (rz != 0) {
                Matrix4 rotZ = AffineTransformer.createRotationZDeg(rz);
                currentMatrix = currentMatrix.mul(rotZ);
            }

            currentModel.setTransformationMatrix(currentMatrix);
            statusLabel.setText("Поворот применен");
            drawFrame();
        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка: введите числа!");
        }
    }

    private void applyTranslation() {
        if (currentModel == null) {
            statusLabel.setText("Сначала загрузите модель!");
            return;
        }

        try {
            double tx = Double.parseDouble(translateXField.getText());
            double ty = Double.parseDouble(translateYField.getText());
            double tz = Double.parseDouble(translateZField.getText());

            Matrix4 translateMatrix = AffineTransformer.createTranslation(tx, ty, tz);
            Matrix4 currentMatrix = currentModel.getTransformationMatrix();
            Matrix4 newMatrix = currentMatrix.mul(translateMatrix);
            currentModel.setTransformationMatrix(newMatrix);

            statusLabel.setText("Перемещение применено");
            drawFrame();
        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка: введите числа!");
        }
    }

    private void resetTransformations() {
        if (currentModel != null) {
            currentModel.setTransformationMatrix(Matrix4.identity());

            // Сбрасываем поля ввода
            scaleXField.setText("1.0"); scaleYField.setText("1.0"); scaleZField.setText("1.0");
            rotateXField.setText("0"); rotateYField.setText("0"); rotateZField.setText("0");
            translateXField.setText("0"); translateYField.setText("0"); translateZField.setText("0");

            statusLabel.setText("Трансформации сброшены");
            drawFrame();
        }
    }

    private void saveModel(boolean applyTransformations) {
        if (currentModel == null) {
            statusLabel.setText("Нет модели для сохранения!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить модель");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj")
        );
        fileChooser.setInitialFileName(
                applyTransformations ? "model_transformed.obj" : "model_original.obj"
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ObjWriter.write(currentModel, file.getAbsolutePath(), applyTransformations);
                statusLabel.setText("Сохранено: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Ошибка сохранения: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void drawFrame() {
        // Очищаем холст
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Рисуем фон
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentModel != null) {
            // Устанавливаем цвет для рисования модели
            gc.setStroke(javafx.scene.paint.Color.BLACK);
            gc.setLineWidth(1.0);

            // Рисуем модель
            RenderEngine.render(gc, camera, currentModel,
                    (int)canvas.getWidth(), (int)canvas.getHeight());
        } else {
            // Рисуем инструкцию
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(20));
            gc.fillText("Нажмите 'Загрузить OBJ' для начала",
                    canvas.getWidth() / 2 - 150, canvas.getHeight() / 2);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}