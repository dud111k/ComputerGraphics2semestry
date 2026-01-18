// test/AffineTransformerTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.vecmath.*;

public class AffineTransformerTest {

    @Test
    public void testCreateTranslation() {
        Matrix4d matrix = AffineTransformer.createTranslation(5.0, 3.0, 1.0);

        // Матрица перемещения в vecmath:
        // [1 0 0 dx]
        // [0 1 0 dy]
        // [0 0 1 dz]
        // [0 0 0 1 ]
        assertEquals(1.0, matrix.m00, 0.0001); // [0,0] = 1
        assertEquals(1.0, matrix.m11, 0.0001); // [1,1] = 1
        assertEquals(1.0, matrix.m22, 0.0001); // [2,2] = 1
        assertEquals(5.0, matrix.m03, 0.0001); // [0,3] = dx (перемещение X)
        assertEquals(3.0, matrix.m13, 0.0001); // [1,3] = dy (перемещение Y)
        assertEquals(1.0, matrix.m23, 0.0001); // [2,3] = dz (перемещение Z)
        assertEquals(1.0, matrix.m33, 0.0001); // [3,3] = 1
    }

    @Test
    public void testCreateRotationX() {
        // 90 градусов вокруг X
        Matrix4d matrix = AffineTransformer.createRotationX(90.0);

        // Матрица вращения вокруг X в vecmath:
        // [1    0    0    0]
        // [0   cos  -sin  0]
        // [0   sin   cos  0]
        // [0    0    0    1]

        // cos(90°) = 0, sin(90°) = 1
        assertEquals(1.0, matrix.m00, 0.0001); // [0,0] = 1

        // Диагональные элементы cos
        assertEquals(0.0, matrix.m11, 0.0001, "cos(90°) в [1,1]");
        assertEquals(0.0, matrix.m22, 0.0001, "cos(90°) в [2,2]");

        // sin элементы
        assertEquals(-1.0, matrix.m12, 0.0001, "-sin(90°) в [1,2]"); // МИНУС sin
        assertEquals(1.0, matrix.m21, 0.0001, "sin(90°) в [2,1]");  // ПЛЮС sin
    }

    @Test
    public void testCreateRotationY() {
        // 90 градусов вокруг Y
        Matrix4d matrix = AffineTransformer.createRotationY(90.0);

        // Матрица вращения вокруг Y:
        // [ cos  0   sin  0]
        // [  0   1    0   0]
        // [-sin  0   cos  0]
        // [  0   0    0   1]

        assertEquals(0.0, matrix.m00, 0.0001, "cos(90°) в [0,0]");  // cos
        assertEquals(1.0, matrix.m11, 0.0001, "1 в [1,1]");         // 1
        assertEquals(0.0, matrix.m22, 0.0001, "cos(90°) в [2,2]");  // cos

        assertEquals(1.0, matrix.m02, 0.0001, "sin(90°) в [0,2]");  // +sin
        assertEquals(-1.0, matrix.m20, 0.0001, "-sin(90°) в [2,0]"); // -sin
    }

    @Test
    public void testCreateScale() {
        Matrix4d matrix = AffineTransformer.createScale(2.0, 3.0, 4.0);

        // Матрица масштабирования:
        // [sx  0   0   0]
        // [ 0  sy  0   0]
        // [ 0  0   sz  0]
        // [ 0  0   0   1]

        assertEquals(2.0, matrix.m00, 0.0001); // sx
        assertEquals(3.0, matrix.m11, 0.0001); // sy
        assertEquals(4.0, matrix.m22, 0.0001); // sz
        assertEquals(1.0, matrix.m33, 0.0001); // 1
    }

    @Test
    public void testSimpleTransform() {
        // Создаем модель с одной вершиной
        Model model = new Model();
        model.getVertices().add(new Vector3f(1.0f, 2.0f, 3.0f));

        // Перемещаем на (4,5,6)
        Matrix4d transform = AffineTransformer.createTranslation(4.0, 5.0, 6.0);
        AffineTransformer.transform(model, transform);

        // Проверяем
        Vector3f result = model.getVertices().get(0);
        assertEquals(5.0f, result.x, 0.0001f); // 1 + 4 = 5
        assertEquals(7.0f, result.y, 0.0001f); // 2 + 5 = 7
        assertEquals(9.0f, result.z, 0.0001f); // 3 + 6 = 9
    }
    @Test
    public void testTransformNormals() {
        // Тест 1: Нормали нормализуются
        Model model = new Model();
        model.getNormals().add(new Vector3f(3.0f, 4.0f, 0.0f)); // Длина 5

        Matrix4d transform = AffineTransformer.createScale(1.0, 1.0, 1.0); // Без изменений
        AffineTransformer.transform(model, transform);

        Vector3f result = model.getNormals().get(0);
        float length = (float)Math.sqrt(result.x*result.x + result.y*result.y + result.z*result.z);

        // После transform() нормаль должна иметь длину 1
        assertEquals(1.0f, length, 0.0001f,
                "Нормаль должна быть нормализована, даже без реальных изменений");

        // Проверяем что (3,4,0) → (0.6, 0.8, 0)
        assertEquals(0.6f, result.x, 0.0001f); // 3/5 = 0.6
        assertEquals(0.8f, result.y, 0.0001f); // 4/5 = 0.8
        assertEquals(0.0f, result.z, 0.0001f);
    }

    @Test
    public void testTransformNormalsWithScale() {
        // Тест 2: Неравномерное масштабирование меняет нормали
        Model model = new Model();
        model.getNormals().add(new Vector3f(0.0f, 1.0f, 0.0f)); // Вверх

        // Масштабируем ТОЛЬКО по X в 2 раза
        Matrix4d transform = AffineTransformer.createScale(2.0, 1.0, 1.0);
        AffineTransformer.transform(model, transform);

        Vector3f result = model.getNormals().get(0);

        // Длина должна быть 1
        float length = (float)Math.sqrt(result.x*result.x + result.y*result.y + result.z*result.z);
        assertEquals(1.0f, length, 0.0001f);

        // Y должен остаться положительным
        assertTrue(result.y > 0.5f, "Нормаль должна смотреть вверх");
    }
}