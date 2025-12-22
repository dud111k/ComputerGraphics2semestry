package math.engine;

import math.engine.matrix.*;
import math.engine.vector.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class LinearAlgebraTest {

    private static final float FLOAT_PRECISION = 0.001f;

    // ==================== ТЕСТЫ ВЕКТОРОВ ====================

    @Test
    public void testVector2DOperations() {
        Vector2D v1 = new Vector2D(3, 4);
        Vector2D v2 = new Vector2D(1, 2);

        assertEquals(2, v1.getDimensions());
        assertEquals(5.0f, v1.length(), FLOAT_PRECISION);

        Vector2D sum = v1.add(v2);
        assertEquals(4.0f, sum.getX(), FLOAT_PRECISION);
        assertEquals(6.0f, sum.getY(), FLOAT_PRECISION);

        Vector2D normalized = v1.normalize();
        assertEquals(1.0f, normalized.length(), FLOAT_PRECISION);

        float cross = v1.cross(v2);
        assertEquals(2.0f, cross, FLOAT_PRECISION);

        float distance = v1.distance(v2);
        assertEquals(2.828f, distance, FLOAT_PRECISION);
    }

    @Test
    public void testVector3DOperations() {
        Vector3D v1 = new Vector3D(1, 2, 2);
        Vector3D v2 = new Vector3D(4, 5, 6);

        // Базовые операции
        assertEquals(3, v1.getDimensions());
        assertEquals(3.0f, v1.length(), FLOAT_PRECISION);

        Vector3D sum = v1.add(v2);
        assertEquals(5.0f, sum.getX(), FLOAT_PRECISION);
        assertEquals(7.0f, sum.getY(), FLOAT_PRECISION);
        assertEquals(8.0f, sum.getZ(), FLOAT_PRECISION);

        float dot = v1.dot(v2);
        assertEquals(26.0f, dot, FLOAT_PRECISION);  // ИСПРАВЛЕНО: 26 вместо 32

        Vector3D cross = v1.cross(v2);
        assertEquals(new Vector3D(2, 2, -3), cross);

        Vector3D normalized = v1.normalize();
        assertEquals(1.0f, normalized.length(), FLOAT_PRECISION);
    }

    @Test
    public void testVector4DOperations() {
        Vector4D v1 = new Vector4D(1, 2, 3, 4);
        Vector4D v2 = new Vector4D(2, 4, 6, 2);

        assertEquals(4, v1.getDimensions());

        Vector4D sum = v1.add(v2);
        assertEquals(3.0f, sum.getX(), FLOAT_PRECISION);
        assertEquals(6.0f, sum.getY(), FLOAT_PRECISION);
        assertEquals(9.0f, sum.getZ(), FLOAT_PRECISION);
        assertEquals(6.0f, sum.getW(), FLOAT_PRECISION);

        Vector3D converted = v2.toVector3D();
        assertEquals(new Vector3D(1, 2, 3), converted);

        assertEquals(5.477f, v1.length(), FLOAT_PRECISION);
    }

    @Test
    public void testAbstractVectorInheritance() {
        Vector2D v2d = new Vector2D(1, 2);
        Vector3D v3d = new Vector3D(1, 2, 3);
        Vector4D v4d = new Vector4D(1, 2, 3, 4);

        assertTrue(v2d instanceof AbstractVector);
        assertTrue(v3d instanceof AbstractVector);
        assertTrue(v4d instanceof AbstractVector);

        assertEquals(2.236f, v2d.length(), FLOAT_PRECISION);
        assertEquals(3.741f, v3d.length(), FLOAT_PRECISION);
        assertEquals(5.477f, v4d.length(), FLOAT_PRECISION);
    }

    // ==================== ТЕСТЫ МАТРИЦ ====================

    @Test
    public void testMatrix3x3Operations() {
        Matrix3x3 matrix = new Matrix3x3(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Matrix3x3 identity = Matrix3x3.identity();

        assertEquals(3, matrix.getRows());
        assertEquals(3, matrix.getCols());

        Matrix3x3 result = matrix.multiply(identity);
        assertTrue(matrix.equals(result));

        Matrix3x3 transposed = matrix.transpose();
        assertEquals(1.0f, transposed.get(0, 0), FLOAT_PRECISION);
        assertEquals(4.0f, transposed.get(0, 1), FLOAT_PRECISION);
        assertEquals(7.0f, transposed.get(0, 2), FLOAT_PRECISION);

        // Определитель
        float det = matrix.determinant();
        assertEquals(0.0f, det, FLOAT_PRECISION);
    }

    @Test
    public void testMatrix4x4Operations() {
        Matrix4x4 translation = Matrix4x4.translation(2, 3, 4);
        Vector3D point = new Vector3D(1, 1, 1);

        // Умножение матрицы на вектор
        Vector3D result = translation.multiply(point);
        assertEquals(new Vector3D(3, 4, 5), result);

        // Умножение матриц
        Matrix4x4 identity = Matrix4x4.identity();
        Matrix4x4 product = translation.multiply(identity);
        assertTrue(translation.equals(product));

        // Определитель единичной матрицы
        assertEquals(1.0f, identity.determinant(), FLOAT_PRECISION);
    }

    @Test
    public void testMatrixInverse() {
        Matrix3x3 matrix = new Matrix3x3(new float[][]{
                {4, 7, 2},
                {3, 5, 1},
                {2, 3, 4}
        });

        // Обратная матрица
        Matrix3x3 inverse = matrix.inverse();
        Matrix3x3 product = matrix.multiply(inverse);
        Matrix3x3 identity = Matrix3x3.identity();

        assertTrue(product.equals(identity));
    }

    @Test
    public void testAbstractMatrixInheritance() {
        // Проверяем что все матрицы наследуют от AbstractMatrix
        Matrix3x3 m3x3 = Matrix3x3.identity();
        Matrix4x4 m4x4 = Matrix4x4.identity();
        CustomMatrix custom = new CustomMatrix(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });

        assertTrue(m3x3 instanceof AbstractMatrix);
        assertTrue(m4x4 instanceof AbstractMatrix);
        assertTrue(custom instanceof AbstractMatrix);

        // Проверяем общие методы
        assertEquals(1.0f, m3x3.determinant(), FLOAT_PRECISION);
        assertEquals(1.0f, m4x4.determinant(), FLOAT_PRECISION);
        assertEquals(1.0f, custom.determinant(), FLOAT_PRECISION);
    }

    // ==================== ТЕСТЫ КАСТОМНОЙ МАТРИЦЫ ====================

    @Test
    public void testCustomMatrixImplementation() {
        CustomMatrix custom = new CustomMatrix(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });

        // Проверяем базовые операции (наследуемые)
        assertEquals(1.0f, custom.determinant(), FLOAT_PRECISION);

        // ИСПРАВЛЕНО: transpose() возвращает CustomMatrix, а не Matrix3x3
        CustomMatrix transposed = custom.transpose();
        assertTrue(custom.equals(transposed));

        // Проверяем кастомные операции
        CustomMatrix customOp = custom.customOperation();
        assertEquals(2.0f, customOp.get(0, 0), FLOAT_PRECISION);
        assertEquals(2.0f, customOp.get(1, 1), FLOAT_PRECISION);
        assertEquals(2.0f, customOp.get(2, 2), FLOAT_PRECISION);

        // Проверяем что реализует интерфейс
        assertTrue(custom instanceof Matrix);
    }

    // ==================== ТЕСТЫ РЕШЕНИЯ СИСТЕМ ====================

    @Test
    public void testLinearSystemSolving() {
        Matrix3x3 A = new Matrix3x3(new float[][]{
                {2, 1, -1},
                {-3, -1, 2},
                {-2, 1, 2}
        });

        Vector3D b = new Vector3D(8, -11, -3);
        Vector3D x = A.solveLinearSystem(b);

        // Проверяем решение
        Vector3D expected = new Vector3D(2, 3, -1);
        assertTrue(x.equals(expected));

        // Проверяем что A*x = b
        Vector3D Ax = A.multiply(x);
        assertTrue(Ax.equals(b));
    }

    @Test
    public void testMatrixVectorMultiplication() {
        Matrix3x3 matrix = new Matrix3x3(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Vector3D vector = new Vector3D(1, 2, 3);
        Vector3D result = matrix.multiply(vector);

        assertEquals(new Vector3D(14, 32, 50), result);
    }

    // ==================== ТЕСТЫ ПРЕОБРАЗОВАНИЙ ====================

    @Test
    public void testTransformationMatrices() {
        // Тестирование переноса
        Matrix4x4 translation = LinearAlgebraEngine.createTranslationMatrix(1, 2, 3);
        Vector3D point = new Vector3D(0, 0, 0);
        Vector3D translated = translation.multiply(point);
        assertEquals(new Vector3D(1, 2, 3), translated);

        // Тестирование масштабирования
        Matrix4x4 scale = LinearAlgebraEngine.createScaleMatrix(2, 3, 4);
        Vector3D scaled = scale.multiply(new Vector3D(1, 1, 1));
        assertEquals(new Vector3D(2, 3, 4), scaled);

        // Тестирование поворота
        Matrix4x4 rotation = LinearAlgebraEngine.createRotationMatrixZ((float) Math.PI / 2);
        Vector3D rotated = rotation.multiply(new Vector3D(1, 0, 0));
        assertEquals(0.0f, rotated.getX(), FLOAT_PRECISION);
        assertEquals(1.0f, rotated.getY(), FLOAT_PRECISION);
        assertEquals(0.0f, rotated.getZ(), FLOAT_PRECISION);
    }

    @Test
    public void testLinearAlgebraEngine() {
        // Тестирование фасадных методов
        Vector3D v1 = LinearAlgebraEngine.createVector3D(1, 0, 0);
        Vector3D v2 = LinearAlgebraEngine.createVector3D(0, 1, 0);

        float angle = LinearAlgebraEngine.computeAngleBetweenVectors(v1, v2);
        assertEquals(Math.PI / 2, angle, FLOAT_PRECISION);

        Matrix4x4 identity = LinearAlgebraEngine.createIdentityMatrix4x4();
        assertEquals(1.0f, identity.determinant(), FLOAT_PRECISION);
    }

    // ==================== ТЕСТЫ ОБРАБОТКИ ОШИБОК ====================

    @Test
    public void testVectorErrorHandling() {
        // Деление на ноль
        assertThrows(ArithmeticException.class, () -> {
            Vector3D v = new Vector3D(1, 2, 3);
            v.divide(0.0f);
        });

        // Нормализация нулевого вектора
        assertThrows(ArithmeticException.class, () -> {
            Vector3D zero = new Vector3D(0, 0, 0);
            zero.normalize();
        });
    }

    @Test
    public void testMatrixErrorHandling() {
        // Обращение вырожденной матрицы
        assertThrows(ArithmeticException.class, () -> {
            Matrix3x3 singular = new Matrix3x3(new float[][]{
                    {1, 2, 3},
                    {4, 5, 6},
                    {7, 8, 9}
            });
            singular.inverse();
        });

        // Неверная размерность матрицы
        assertThrows(IllegalArgumentException.class, () -> {
            new Matrix3x3(new float[][]{
                    {1, 2},
                    {3, 4}
            });
        });

        // Неверные индексы
        assertThrows(IllegalArgumentException.class, () -> {
            Matrix3x3 matrix = Matrix3x3.identity();
            matrix.get(5, 5);
        });
    }

    @Test
    public void testVector4DErrorHandling() {
        // Преобразование Vector4D с W=0
        assertThrows(ArithmeticException.class, () -> {
            Vector4D v = new Vector4D(1, 2, 3, 0);
            v.toVector3D();
        });
    }

    // ==================== ТЕСТЫ ИНТЕРФЕЙСОВ ====================

    @Test
    public void testInterfaceImplementation() {
        // Проверяем что все матрицы реализуют интерфейс Matrix
        Matrix3x3 m3x3 = Matrix3x3.identity();
        Matrix4x4 m4x4 = Matrix4x4.identity();
        CustomMatrix custom = new CustomMatrix(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });

        assertTrue(m3x3 instanceof Matrix);
        assertTrue(m4x4 instanceof Matrix);
        assertTrue(custom instanceof Matrix);

        // Проверяем что все векторы реализуют интерфейс Vector
        Vector2D v2d = new Vector2D(1, 2);
        Vector3D v3d = new Vector3D(1, 2, 3);
        Vector4D v4d = new Vector4D(1, 2, 3, 4);

        assertTrue(v2d instanceof Vector);
        assertTrue(v3d instanceof Vector);
        assertTrue(v4d instanceof Vector);
    }

    @Test
    public void testEdgeCases() {
        // Нулевые векторы
        Vector3D zero = new Vector3D(0, 0, 0);
        assertEquals(0.0f, zero.length(), FLOAT_PRECISION);

        // Единичные векторы
        Vector3D unit = new Vector3D(1, 0, 0);
        assertEquals(1.0f, unit.length(), FLOAT_PRECISION);

        // Большие числа
        Vector3D large = new Vector3D(1e6f, 2e6f, 3e6f);
        assertTrue(large.length() > 0);

        // Матрица с нулевым определителем
        Matrix3x3 zeroDet = new Matrix3x3(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });
        assertEquals(0.0f, zeroDet.determinant(), FLOAT_PRECISION);
    }

    // ==================== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ====================

    @Test
    public void testMatrixAdditionSubtraction() {
        Matrix3x3 m1 = new Matrix3x3(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Matrix3x3 m2 = new Matrix3x3(new float[][]{
                {9, 8, 7},
                {6, 5, 4},
                {3, 2, 1}
        });

        Matrix3x3 sum = m1.add(m2);
        Matrix3x3 expectedSum = new Matrix3x3(new float[][]{
                {10, 10, 10},
                {10, 10, 10},
                {10, 10, 10}
        });

        assertTrue(sum.equals(expectedSum));

        Matrix3x3 diff = m1.subtract(m2);
        Matrix3x3 expectedDiff = new Matrix3x3(new float[][]{
                {-8, -6, -4},
                {-2, 0, 2},
                {4, 6, 8}
        });

        assertTrue(diff.equals(expectedDiff));
    }

    @Test
    public void testVectorScalarOperations() {
        Vector3D v = new Vector3D(1, 2, 3);

        Vector3D multiplied = v.multiply(2);
        assertEquals(new Vector3D(2, 4, 6), multiplied);

        Vector3D divided = v.divide(2);
        assertEquals(new Vector3D(0.5f, 1, 1.5f), divided);
    }
}