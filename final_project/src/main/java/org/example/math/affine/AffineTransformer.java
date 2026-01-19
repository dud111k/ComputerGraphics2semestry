package org.example.math.affine;


import org.example.math.matrix.*;
import org.example.math.vector.*;
import org.example.model.Model;
import org.example.math.*;

public class AffineTransformer {

    // Преобразование модели с учетом векторов-столбцов
    public static void transform(Model model, Matrix4 transform) {
        transformVertices(model, transform);
        transformNormals(model, transform);
    }

    private static void transformVertices(Model model, Matrix4 transform) {
        for (int i = 0; i < model.getVertices().size(); i++) {
            Vector3 vertex = model.getVertices().get(i);

            // Преобразуем Vector3 в Vector4 (добавляем w=1 для точки)
            Vector4 point = new Vector4(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0);

            // Умножаем матрицу на вектор-столбец: v' = M * v
            Vector4 transformed = transform.mul(point);

            // Делим на w (для перспективных преобразований) и сохраняем
            double w = transformed.getW();
            if (Math.abs(w - 1.0) > Config.EPSILON) {
                transformed = transformed.div(w);
            }

            model.getVertices().set(i,
                    new Vector3((float)transformed.getX(),
                            (float)transformed.getY(),
                            (float)transformed.getZ()));
        }
    }

    private static void transformNormals(Model model, Matrix4 transform) {
        if (model.getNormals().isEmpty()) return;

        // Для нормалей используем обратную транспонированную матрицу 3x3
        // Извлекаем верхнюю левую подматрицу 3x3
        double[][] m3 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m3[i][j] = transform.get(i, j);
            }
        }

        Matrix3 normalMatrix = new Matrix3(m3);
        normalMatrix = normalMatrix.inverse().transpose();

        for (int i = 0; i < model.getNormals().size(); i++) {
            Vector3 normal = model.getNormals().get(i);
            Vector3 transformed = normalMatrix.mul(normal);
            transformed = transformed.normalize();
            model.getNormals().set(i, transformed);
        }
    }

    // Создание матрицы перемещения (векторы-столбцы)
    public static Matrix4 createTranslation(double dx, double dy, double dz) {
        Matrix4 matrix = Matrix4.identity();
        matrix.set(0, 3, dx); // m14
        matrix.set(1, 3, dy); // m24
        matrix.set(2, 3, dz); // m34
        return matrix;
    }

    // Создание матрицы поворота вокруг X (в радианах)
    public static Matrix4 createRotationX(double angleRadians) {
        Matrix4 matrix = Matrix4.identity();
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        matrix.set(1, 1, cos);
        matrix.set(1, 2, -sin);
        matrix.set(2, 1, sin);
        matrix.set(2, 2, cos);

        return matrix;
    }

    // Создание матрицы поворота вокруг Y (в радианах)
    public static Matrix4 createRotationY(double angleRadians) {
        Matrix4 matrix = Matrix4.identity();
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        matrix.set(0, 0, cos);
        matrix.set(0, 2, sin);
        matrix.set(2, 0, -sin);
        matrix.set(2, 2, cos);

        return matrix;
    }

    // Создание матрицы поворота вокруг Z (в радианах)
    public static Matrix4 createRotationZ(double angleRadians) {
        Matrix4 matrix = Matrix4.identity();
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        matrix.set(0, 0, cos);
        matrix.set(0, 1, -sin);
        matrix.set(1, 0, sin);
        matrix.set(1, 1, cos);

        return matrix;
    }

    // Создание матрицы масштабирования
    public static Matrix4 createScale(double sx, double sy, double sz) {
        Matrix4 matrix = Matrix4.identity();
        matrix.set(0, 0, sx);
        matrix.set(1, 1, sy);
        matrix.set(2, 2, sz);
        return matrix;
    }

    // Комбинирование матриц (для векторов-столбцов порядок обратный)
    // Если хотим сначала T, потом R, потом S, то: M = S * R * T
    public static Matrix4 combine(Matrix4... matrices) {
        if (matrices.length == 0) {
            return Matrix4.identity();
        }

        Matrix4 result = new Matrix4(matrices[0]);

        for (int i = 1; i < matrices.length; i++) {
            // Для векторов-столбцов: result = matrices[i] * result
            result = matrices[i].mul(result);
        }

        return result;
    }

    // Вспомогательный метод для создания матрицы поворота по углу в градусах
    public static Matrix4 createRotationXDeg(double angleDegrees) {
        return createRotationX(Math.toRadians(angleDegrees));
    }

    public static Matrix4 createRotationYDeg(double angleDegrees) {
        return createRotationY(Math.toRadians(angleDegrees));
    }

    public static Matrix4 createRotationZDeg(double angleDegrees) {
        return createRotationZ(Math.toRadians(angleDegrees));
    }
}