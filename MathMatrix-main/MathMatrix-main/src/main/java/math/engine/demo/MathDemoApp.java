package math.engine.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import math.engine.*;
import math.engine.matrix.*;
import math.engine.vector.Vector2D;
import math.engine.vector.Vector3D;
import math.engine.vector.Vector4D;

public class MathDemoApp extends Application {

    private TextArea outputArea;
    private TextField inputField1, inputField2, inputField3, inputField4;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Math Matrix Engine - Новая архитектура");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titleLabel = new Label("Math Matrix Engine - Обобщенная архитектура");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane inputPanel = createInputPanel();
        HBox operationPanel = createOperationPanel();

        outputArea = new TextArea();
        outputArea.setPrefHeight(400);
        outputArea.setEditable(false);
        outputArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");

        root.getChildren().addAll(titleLabel, inputPanel, operationPanel, outputArea);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        showWelcomeMessage();
    }

    private GridPane createInputPanel() {
        GridPane panel = new GridPane();
        panel.setHgap(10);
        panel.setVgap(5);

        Label inputLabel1 = new Label("Вектор 1 (2,3,4 числа через запятую):");
        inputField1 = new TextField();
        inputField1.setPromptText("1, 2, 3");
        inputField1.setPrefWidth(200);

        Label inputLabel2 = new Label("Вектор 2 (для операций):");
        inputField2 = new TextField();
        inputField2.setPromptText("4, 5, 6");
        inputField2.setPrefWidth(200);

        Label matrixLabel = new Label("Матрица 3x3 (9 чисел):");
        inputField3 = new TextField();
        inputField3.setPromptText("1,2,3,4,5,6,7,8,9");
        inputField3.setPrefWidth(300);

        Label vectorLabel = new Label("Вектор для системы (3 числа):");
        inputField4 = new TextField();
        inputField4.setPromptText("8, -11, -3");
        inputField4.setPrefWidth(200);

        panel.add(inputLabel1, 0, 0);
        panel.add(inputField1, 1, 0);
        panel.add(inputLabel2, 0, 1);
        panel.add(inputField2, 1, 1);
        panel.add(matrixLabel, 0, 2);
        panel.add(inputField3, 1, 2);
        panel.add(vectorLabel, 0, 3);
        panel.add(inputField4, 1, 3);

        return panel;
    }

    private HBox createOperationPanel() {
        HBox panel = new HBox(10);

        Button vectorOpsBtn = new Button("Векторные операции");
        Button matrixOpsBtn = new Button("Матричные операции");
        Button systemBtn = new Button("Решить систему");
        Button transformBtn = new Button("Преобразования");
        Button customBtn = new Button("Кастомная матрица");
        Button interfaceBtn = new Button("Работа через интерфейсы");
        Button clearBtn = new Button("Очистить");

        vectorOpsBtn.setOnAction(e -> performVectorOperations());
        matrixOpsBtn.setOnAction(e -> performMatrixOperations());
        systemBtn.setOnAction(e -> solveLinearSystem());
        transformBtn.setOnAction(e -> performTransformations());
        customBtn.setOnAction(e -> demonstrateCustomMatrix());
        interfaceBtn.setOnAction(e -> demonstrateInterfaceUsage());
        clearBtn.setOnAction(e -> clearAll());

        panel.getChildren().addAll(vectorOpsBtn, matrixOpsBtn, systemBtn,
                transformBtn, customBtn, interfaceBtn, clearBtn);

        return panel;
    }

    private void showWelcomeMessage() {
        outputArea.appendText("=== MATH MATRIX ENGINE - ОБОБЩЕННАЯ АРХИТЕКТУРА ===\n");
        outputArea.appendText("Новая архитектура с интерфейсами и базовыми классами\n\n");
        outputArea.appendText("Основные компоненты:\n");
        outputArea.appendText("- Matrix и Vector интерфейсы\n");
        outputArea.appendText("- AbstractMatrix и AbstractVector базовые классы\n");
        outputArea.appendText("- Легкое расширение кастомными реализациями\n\n");
    }

    private float[] parseInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new float[0];
        }

        String[] parts = input.replace(",", " ").split("\\s+");
        float[] result = new float[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Float.parseFloat(parts[i].trim());
            } catch (NumberFormatException e) {
                outputArea.appendText("Ошибка: неверный формат числа: " + parts[i] + "\n");
                return new float[0];
            }
        }

        return result;
    }

    private void performVectorOperations() {
        outputArea.appendText("\n=== ВЕКТОРНЫЕ ОПЕРАЦИИ ===\n");

        float[] values1 = parseInput(inputField1.getText());
        float[] values2 = parseInput(inputField2.getText());

        if (values1.length < 2) {
            outputArea.appendText("Ошибка: введите минимум 2 числа для вектора\n");
            return;
        }

        try {
            if (values1.length == 2) {
                Vector2D v1 = new Vector2D(values1[0], values1[1]);
                outputArea.appendText("Vector2D: " + v1 + "\n");
                outputArea.appendText("Размерность: " + v1.getDimensions() + "\n");
                outputArea.appendText("Длина: " + v1.length() + "\n");
                outputArea.appendText("Нормализованный: " + v1.normalize() + "\n");

                if (values2.length == 2) {
                    Vector2D v2 = new Vector2D(values2[0], values2[1]);
                    outputArea.appendText("Второй вектор: " + v2 + "\n");
                    outputArea.appendText("Сумма: " + v1.add(v2) + "\n");
                    outputArea.appendText("Скалярное произведение: " + v1.dot(v2) + "\n");
                    outputArea.appendText("Векторное произведение (2D): " + v1.cross(v2) + "\n");
                }

            } else if (values1.length == 3) {
                Vector3D v1 = new Vector3D(values1[0], values1[1], values1[2]);
                outputArea.appendText("Vector3D: " + v1 + "\n");
                outputArea.appendText("Размерность: " + v1.getDimensions() + "\n");
                outputArea.appendText("Длина: " + v1.length() + "\n");
                outputArea.appendText("Нормализованный: " + v1.normalize() + "\n");

                if (values2.length == 3) {
                    Vector3D v2 = new Vector3D(values2[0], values2[1], values2[2]);
                    outputArea.appendText("Второй вектор: " + v2 + "\n");
                    outputArea.appendText("Сумма: " + v1.add(v2) + "\n");
                    outputArea.appendText("Скалярное произведение: " + v1.dot(v2) + "\n");
                    outputArea.appendText("Векторное произведение (3D): " + v1.cross(v2) + "\n");
                }

            } else if (values1.length == 4) {
                Vector4D v1 = new Vector4D(values1[0], values1[1], values1[2], values1[3]);
                outputArea.appendText("Vector4D: " + v1 + "\n");
                outputArea.appendText("Размерность: " + v1.getDimensions() + "\n");
                outputArea.appendText("Длина: " + v1.length() + "\n");

                if (Math.abs(v1.getW()) > 1e-6) {
                    outputArea.appendText("Преобразование в 3D: " + v1.toVector3D() + "\n");
                }
            }

        } catch (Exception e) {
            outputArea.appendText("Ошибка: " + e.getMessage() + "\n");
        }
    }

    private void performMatrixOperations() {
        outputArea.appendText("\n=== МАТРИЧНЫЕ ОПЕРАЦИИ ===\n");

        float[] matrixValues = parseInput(inputField3.getText());

        if (matrixValues.length == 9) {
            float[][] data3x3 = {
                    {matrixValues[0], matrixValues[1], matrixValues[2]},
                    {matrixValues[3], matrixValues[4], matrixValues[5]},
                    {matrixValues[6], matrixValues[7], matrixValues[8]}
            };

            try {
                Matrix3x3 matrix = new Matrix3x3(data3x3);
                outputArea.appendText("Матрица 3x3:\n" + matrix);
                outputArea.appendText("Строк: " + matrix.getRows() + ", Столбцов: " + matrix.getCols() + "\n");
                outputArea.appendText("Определитель: " + matrix.determinant() + "\n");
                outputArea.appendText("Транспонированная:\n" + matrix.transpose());

                if (Math.abs(matrix.determinant()) > 1e-6) {
                    outputArea.appendText("Обратная матрица:\n" + matrix.inverse());
                }

            } catch (Exception e) {
                outputArea.appendText("Ошибка: " + e.getMessage() + "\n");
            }
        } else {
            outputArea.appendText("Для матрицы 3x3 нужно 9 чисел\n");
        }
    }

    private void solveLinearSystem() {
        outputArea.appendText("\n=== РЕШЕНИЕ СИСТЕМЫ ===\n");

        float[] matrixValues = parseInput(inputField3.getText());
        float[] vectorValues = parseInput(inputField4.getText());

        if (matrixValues.length != 9) {
            outputArea.appendText("Ошибка: для матрицы нужно 9 чисел\n");
            return;
        }

        if (vectorValues.length != 3) {
            outputArea.appendText("Ошибка: для вектора нужно 3 числа\n");
            return;
        }

        try {
            float[][] data = {
                    {matrixValues[0], matrixValues[1], matrixValues[2]},
                    {matrixValues[3], matrixValues[4], matrixValues[5]},
                    {matrixValues[6], matrixValues[7], matrixValues[8]}
            };

            Matrix3x3 A = new Matrix3x3(data);
            Vector3D b = new Vector3D(vectorValues[0], vectorValues[1], vectorValues[2]);

            outputArea.appendText("Матрица A:\n" + A);
            outputArea.appendText("Вектор b: " + b + "\n");

            Vector3D solution = A.solveLinearSystem(b);
            outputArea.appendText("Решение x: " + solution + "\n");

            Vector3D verification = A.multiply(solution);
            outputArea.appendText("Проверка A*x: " + verification + "\n");
            outputArea.appendText("Решение " + (verification.equals(b) ? "ВЕРНО" : "ОШИБКА") + "\n");

        } catch (Exception e) {
            outputArea.appendText("Ошибка решения: " + e.getMessage() + "\n");
        }
    }

    private void performTransformations() {
        outputArea.appendText("\n=== АФФИННЫЕ ПРЕОБРАЗОВАНИЯ ===\n");

        float[] pointValues = parseInput(inputField1.getText());

        if (pointValues.length < 3) {
            outputArea.appendText("Введите точку (3 числа) для преобразований\n");
            return;
        }

        Vector3D point = new Vector3D(pointValues[0], pointValues[1], pointValues[2]);
        outputArea.appendText("Исходная точка: " + point + "\n");

        Matrix4x4 translation = LinearAlgebraEngine.createTranslationMatrix(2, 3, 4);
        Vector3D translated = translation.multiply(point);
        outputArea.appendText("После переноса на (2,3,4): " + translated + "\n");

        Matrix4x4 rotation = LinearAlgebraEngine.createRotationMatrixZ((float) Math.PI / 4);
        Vector3D rotated = rotation.multiply(point);
        outputArea.appendText("После поворота на 45° вокруг Z: " + rotated + "\n");

        Matrix4x4 scale = LinearAlgebraEngine.createScaleMatrix(2, 2, 2);
        Vector3D scaled = scale.multiply(point);
        outputArea.appendText("После масштабирования в 2 раза: " + scaled + "\n");
    }

    private void demonstrateCustomMatrix() {
        outputArea.appendText("\n=== КАСТОМНАЯ МАТРИЦА (НАСЛЕДОВАНИЕ) ===\n");

        float[] matrixValues = parseInput(inputField3.getText());

        if (matrixValues.length == 9) {
            float[][] data = {
                    {matrixValues[0], matrixValues[1], matrixValues[2]},
                    {matrixValues[3], matrixValues[4], matrixValues[5]},
                    {matrixValues[6], matrixValues[7], matrixValues[8]}
            };

            try {
                CustomMatrix custom = new CustomMatrix(data);
                outputArea.appendText("Кастомная матрица:\n" + custom);
                outputArea.appendText("Определитель: " + custom.determinant() + "\n");
                outputArea.appendText("Транспонированная:\n" + custom.transpose());

                CustomMatrix customOp = custom.customOperation();
                outputArea.appendText("После кастомной операции (x2):\n" + customOp);

                outputArea.appendText("Наследует все методы AbstractMatrix: " +
                        (custom instanceof AbstractMatrix) + "\n");
                outputArea.appendText("Реализует интерфейс Matrix: " +
                        (custom instanceof Matrix) + "\n");

            } catch (Exception e) {
                outputArea.appendText("Ошибка: " + e.getMessage() + "\n");
            }
        } else {
            outputArea.appendText("Для матрицы нужно 9 чисел\n");
        }
    }

    private void demonstrateInterfaceUsage() {
        outputArea.appendText("\n=== РАБОТА ЧЕРЕЗ ИНТЕРФЕЙСЫ ===\n");

        outputArea.appendText("Демонстрация полиморфизма:\n");

        Matrix3x3 matrix1 = new Matrix3x3(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Matrix4x4 matrix2 = Matrix4x4.identity();

        CustomMatrix matrix3 = new CustomMatrix(new float[][]{
                {2, 0, 0},
                {0, 2, 0},
                {0, 0, 2}
        });

        outputArea.appendText("Матрица 3x3:\n" + matrix1);
        outputArea.appendText("Матрица 4x4:\n" + matrix2);
        outputArea.appendText("Кастомная матрица:\n" + matrix3);

        outputArea.appendText("\nВсе матрицы поддерживают общие операции:\n");
        outputArea.appendText("- Matrix3x3: определитель = " + matrix1.determinant() + "\n");
        outputArea.appendText("- Matrix4x4: определитель = " + matrix2.determinant() + "\n");
        outputArea.appendText("- CustomMatrix: определитель = " + matrix3.determinant() + "\n");

        outputArea.appendText("\nДемонстрация работы с векторами:\n");

        Vector2D vec2d = new Vector2D(3, 4);
        Vector3D vec3d = new Vector3D(1, 2, 2);
        Vector4D vec4d = new Vector4D(2, 4, 6, 2);

        outputArea.appendText("Vector2D длина: " + vec2d.length() + "\n");
        outputArea.appendText("Vector3D длина: " + vec3d.length() + "\n");
        outputArea.appendText("Vector4D длина: " + vec4d.length() + "\n");

        outputArea.appendText("Vector2D нормализованный: " + vec2d.normalize() + "\n");
        outputArea.appendText("Vector3D нормализованный: " + vec3d.normalize() + "\n");
    }

    private void clearAll() {
        inputField1.clear();
        inputField2.clear();
        inputField3.clear();
        inputField4.clear();
        outputArea.clear();
        showWelcomeMessage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}