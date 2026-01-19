package AffineTransformerTests;

import org.junit.jupiter.api.Test;
import org.example.math.matrix.Matrix4;
import org.example.math.vector.Vector3;
import org.example.math.affine.AffineTransformer;
import org.example.model.Model;

import static org.junit.jupiter.api.Assertions.*;

public class AffineTransformerTest {

    private static final double EPSILON = 1e-10;

    @Test
    public void testCreateTranslation() {
        Matrix4 matrix = AffineTransformer.createTranslation(5.0, 3.0, 1.0);
        // Матрица перемещения для векторов-столбцов:
        // [1 0 0 dx]
        // [0 1 0 dy]
        // [0 0 1 dz]
        // [0 0 0 1 ]
        assertEquals(1.0, matrix.get(0, 0), 0.0001); // [0,0] = 1
        assertEquals(1.0, matrix.get(1, 1), 0.0001); // [1,1] = 1
        assertEquals(1.0, matrix.get(2, 2), 0.0001); // [2,2] = 1
        assertEquals(5.0, matrix.get(0, 3), 0.0001); // [0,3] = dx
        assertEquals(3.0, matrix.get(1, 3), 0.0001); // [1,3] = dy
        assertEquals(1.0, matrix.get(2, 3), 0.0001); // [2,3] = dz
        assertEquals(1.0, matrix.get(3, 3), 0.0001); // [3,3] = 1
    }

    @Test
    public void testCreateRotationX() {
        Matrix4 matrix = AffineTransformer.createRotationX(Math.PI / 2); // 90 градусов в радианах
        // Матрица вращения вокруг X:
        // [1    0    0    0]
        // [0   cos  -sin  0]
        // [0   sin   cos  0]
        // [0    0    0    1]
        // cos(90°) = 0, sin(90°) = 1
        assertEquals(1.0, matrix.get(0, 0), 0.0001); // [0,0] = 1
        assertEquals(0.0, matrix.get(1, 1), 0.0001, "cos(90°) в [1,1]");
        assertEquals(0.0, matrix.get(2, 2), 0.0001, "cos(90°) в [2,2]");
        assertEquals(-1.0, matrix.get(1, 2), 0.0001, "-sin(90°) в [1,2]"); // -sin
        assertEquals(1.0, matrix.get(2, 1), 0.0001, "sin(90°) в [2,1]");  // +sin
    }

    @Test
    public void testCreateRotationY() {
        Matrix4 matrix = AffineTransformer.createRotationY(Math.PI / 2); // 90 градусов в радианах
        // Матрица вращения вокруг Y:
        // [ cos  0   sin  0]
        // [  0   1    0   0]
        // [-sin  0   cos  0]
        // [  0   0    0   1]
        assertEquals(0.0, matrix.get(0, 0), 0.0001, "cos(90°) в [0,0]");  // cos
        assertEquals(1.0, matrix.get(1, 1), 0.0001, "1 в [1,1]");         // 1
        assertEquals(0.0, matrix.get(2, 2), 0.0001, "cos(90°) в [2,2]");  // cos
        assertEquals(1.0, matrix.get(0, 2), 0.0001, "sin(90°) в [0,2]");  // +sin
        assertEquals(-1.0, matrix.get(2, 0), 0.0001, "-sin(90°) в [2,0]"); // -sin
    }

    @Test
    public void testCreateRotationZ() {
        Matrix4 matrix = AffineTransformer.createRotationZ(Math.PI / 2); // 90 градусов в радианах
        // Матрица вращения вокруг Z:
        // [cos  -sin  0   0]
        // [sin   cos  0   0]
        // [ 0     0   1   0]
        // [ 0     0   0   1]
        assertEquals(0.0, matrix.get(0, 0), 0.0001, "cos(90°) в [0,0]");  // cos
        assertEquals(0.0, matrix.get(1, 1), 0.0001, "cos(90°) в [1,1]");  // cos
        assertEquals(1.0, matrix.get(2, 2), 0.0001, "1 в [2,2]");         // 1
        assertEquals(-1.0, matrix.get(0, 1), 0.0001, "-sin(90°) в [0,1]"); // -sin
        assertEquals(1.0, matrix.get(1, 0), 0.0001, "sin(90°) в [1,0]");  // +sin
    }

    @Test
    public void testCreateScale() {
        Matrix4 matrix = AffineTransformer.createScale(2.0, 3.0, 4.0);
        // Матрица масштабирования:
        // [sx  0   0   0]
        // [ 0  sy  0   0]
        // [ 0  0   sz  0]
        // [ 0  0   0   1]
        assertEquals(2.0, matrix.get(0, 0), 0.0001); // sx
        assertEquals(3.0, matrix.get(1, 1), 0.0001); // sy
        assertEquals(4.0, matrix.get(2, 2), 0.0001); // sz
        assertEquals(1.0, matrix.get(3, 3), 0.0001); // 1
    }

    @Test
    public void testTransform() {
        // Создаю модель с одной вершиной
        Model model = new Model();
        model.getVertices().add(new Vector3(1.0, 2.0, 3.0));

        // Перемещаю на (4,5,6) - векторы-столбцы
        Matrix4 transform = AffineTransformer.createTranslation(4.0, 5.0, 6.0);
        AffineTransformer.transform(model, transform);

        Vector3 result = model.getVertices().get(0);
        assertEquals(5.0, result.getX(), 0.0001); // 1 + 4 = 5
        assertEquals(7.0, result.getY(), 0.0001); // 2 + 5 = 7
        assertEquals(9.0, result.getZ(), 0.0001); // 3 + 6 = 9
    }

    @Test
    public void testTransformNormals() {
        // Проверка нормализации нормалей
        Model model = new Model();
        model.getNormals().add(new Vector3(3.0, 4.0, 0.0)); // Длина 5

        // Масштаб 1:1:1 (без изменений)
        Matrix4 transform = AffineTransformer.createScale(1.0, 1.0, 1.0);
        AffineTransformer.transform(model, transform);

        Vector3 result = model.getNormals().get(0);
        double length = Math.sqrt(
                result.getX() * result.getX() +
                        result.getY() * result.getY() +
                        result.getZ() * result.getZ()
        );

        // После transform() нормаль должна иметь длину 1
        assertEquals(1.0, length, 0.0001,
                "Нормаль должна быть нормализована, даже без реальных изменений");
        // Проверяем что (3,4,0) → (0.6, 0.8, 0)
        assertEquals(0.6, result.getX(), 0.0001); // 3/5 = 0.6
        assertEquals(0.8, result.getY(), 0.0001); // 4/5 = 0.8
        assertEquals(0.0, result.getZ(), 0.0001);
    }

    @Test
    public void testTransformNormalsWithScale() {
        // Неравномерное масштабирование меняет нормали
        Model model = new Model();
        model.getNormals().add(new Vector3(0.0, 1.0, 0.0)); // Вверх
        Matrix4 transform = AffineTransformer.createScale(2.0, 1.0, 1.0);
        AffineTransformer.transform(model, transform);
        Vector3 result = model.getNormals().get(0);
        // Длина должна быть 1
        double length = Math.sqrt(
                result.getX() * result.getX() +
                        result.getY() * result.getY() +
                        result.getZ() * result.getZ()
        );
        assertEquals(1.0, length, 0.0001);
        // Y должен остаться положительным
        assertTrue(result.getY() > 0.5, "Нормаль должна смотреть вверх");
    }

    @Test
    public void testCombineTransformationsSimple() {
        // Простой тест: только перемещение и масштаб
        Matrix4 translation = AffineTransformer.createTranslation(10.0, 20.0, 30.0);
        Matrix4 scale = AffineTransformer.createScale(2.0, 3.0, 4.0);
        // Для векторов-столбцов: S * T
        Matrix4 combined = scale.mul(translation);
        assertEquals(2.0, combined.get(0, 0), 0.0001); // sx
        assertEquals(3.0, combined.get(1, 1), 0.0001); // sy
        assertEquals(4.0, combined.get(2, 2), 0.0001); // sz

        // Перемещение должно быть умножено на масштаб
        assertEquals(20.0, combined.get(0, 3), 0.0001); // 10 * 2 = 20
        assertEquals(60.0, combined.get(1, 3), 0.0001); // 20 * 3 = 60
        assertEquals(120.0, combined.get(2, 3), 0.0001); // 30 * 4 = 120
    }

    private void printMatrix(Matrix4 m) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.printf("%8.2f ", m.get(i, j));
            }
            System.out.println();
        }
    }

    @Test
    public void testTransformWithRotation() {
        // Тестируем вращение
        Model model = new Model();
        model.getVertices().add(new Vector3(1.0, 0.0, 0.0)); // Точка на оси X
        // Поворот на 90 градусов вокруг Z
        Matrix4 rotation = AffineTransformer.createRotationZ(Math.PI / 2);
        AffineTransformer.transform(model, rotation);
        Vector3 result = model.getVertices().get(0);
        // (1,0,0) повернуто на 90° вокруг Z должно стать (0,1,0)
        assertEquals(0.0, result.getX(), 0.0001);
        assertEquals(1.0, result.getY(), 0.0001);
        assertEquals(0.0, result.getZ(), 0.0001);
    }

    @Test
    public void testTransformWithScaleAndTranslation() {
        // Комбинированное преобразование: масштаб + перемещение
        Model model = new Model();
        model.getVertices().add(new Vector3(1.0, 2.0, 3.0));
        // Сначала масштаб 2x, потом перемещение (1,1,1)
        // Для векторов-столбцов: M = T * S (если сначала S, потом T)
        Matrix4 scale = AffineTransformer.createScale(2.0, 2.0, 2.0);
        Matrix4 translation = AffineTransformer.createTranslation(1.0, 1.0, 1.0);
        // Важно: порядок для векторов-столбцов обратный!
        Matrix4 combined = translation.mul(scale); // T * S
        AffineTransformer.transform(model, combined);
        Vector3 result = model.getVertices().get(0);
        // (1,2,3) * 2 = (2,4,6) + (1,1,1) = (3,5,7)
        assertEquals(3.0, result.getX(), 0.0001);
        assertEquals(5.0, result.getY(), 0.0001);
        assertEquals(7.0, result.getZ(), 0.0001);
    }

    // 1. Тест: Модель должна хранить матрицу трансформации
    @Test
    public void testModelHasTransformationMatrix() {
        Model model = new Model();
        // Проверяем, что у модели есть матрица трансформации
        assertNotNull(model.getTransformationMatrix(),
                "Модель должна иметь матрицу трансформации");
        // По умолчанию должна быть единичной
        Matrix4 identity = Matrix4.identity();
        Matrix4 modelMatrix = model.getTransformationMatrix();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(identity.get(i, j), modelMatrix.get(i, j), EPSILON,
                        String.format("Матрица по умолчанию должна быть единичной [%d,%d]", i, j));
            }
        }
    }


    @Test
    public void testGetTransformedVertex() {
        Model model = new Model();
        model.getVertices().add(new Vector3(1.0, 2.0, 3.0));
        // Получаем трансформированную вершину (пока без трансформаций)
        Vector3 transformed = model.getTransformedVertex(0);
        // Без трансформаций должна вернуть ту же вершину
        assertEquals(1.0, transformed.getX(), EPSILON);
        assertEquals(2.0, transformed.getY(), EPSILON);
        assertEquals(3.0, transformed.getZ(), EPSILON);
    }

    // кумулятивные трансформации
    @Test
    public void testCumulativeTransformations() {
        Model model = new Model();
        model.getVertices().add(new Vector3(1.0, 1.0, 1.0));
        Matrix4 scale = AffineTransformer.createScale(2.0, 2.0, 2.0);
        Matrix4 translation = AffineTransformer.createTranslation(3.0, 0.0, 0.0);
        // Комбинированная матрица: translation * scale
        Matrix4 combined = translation.mul(scale);
        model.setTransformationMatrix(combined);
        Vector3 transformed = model.getTransformedVertex(0);
        assertEquals(5.0, transformed.getX(), EPSILON);  // 1*2 + 3 = 5
        assertEquals(2.0, transformed.getY(), EPSILON);  // 1*2 = 2
        assertEquals(2.0, transformed.getZ(), EPSILON);  // 1*2 = 2
    }

    @Test
    public void testMatrixCreation() {
        Matrix4 translation = AffineTransformer.createTranslation(1, 2, 3);
        Matrix4 scale = AffineTransformer.createScale(2, 3, 4);
        Matrix4 rotX = AffineTransformer.createRotationXDeg(45);
        Matrix4 rotY = AffineTransformer.createRotationYDeg(45);
        Matrix4 rotZ = AffineTransformer.createRotationZDeg(45);

        assertAll(
                () -> assertNotNull(translation, "Матрица переноса не должна быть null"),
                () -> assertNotNull(scale, "Матрица масштаба не должна быть null"),
                () -> assertNotNull(rotX, "Матрица поворота X не должна быть null"),
                () -> assertNotNull(rotY, "Матрица поворота Y не должна быть null"),
                () -> assertNotNull(rotZ, "Матрица поворота Z не должна быть null")
        );
    }


    @Test
    public void testTransformationOrder() {
        Model model = new Model();
        model.getVertices().add(new Vector3(1, 0, 0));
        Matrix4 T = AffineTransformer.createTranslation(10, 0, 0);
        model.setTransformationMatrix(T);
        Vector3 afterTranslate = model.getTransformedVertex(0);
        System.out.println("После переноса: " + afterTranslate); // Должно быть (11,0,0)
        Matrix4 R = AffineTransformer.createRotationYDeg(90);
        Matrix4 TR = T.mul(R); // T * R
        model.setTransformationMatrix(TR);
        Vector3 afterRotate = model.getTransformedVertex(0);
        System.out.println("После переноса+поворота: " + afterRotate);
        Matrix4 S = AffineTransformer.createScale(2, 1, 1);
        Matrix4 TRS = T.mul(R.mul(S)); // T * R * S
        model.setTransformationMatrix(TRS);
        Vector3 afterScale = model.getTransformedVertex(0);
        System.out.println("После всех: " + afterScale);
    }
}