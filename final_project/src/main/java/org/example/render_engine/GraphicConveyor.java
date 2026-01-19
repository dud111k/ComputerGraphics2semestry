package org.example.render_engine;

import org.example.math.matrix.Matrix4;
import org.example.math.vector.Vector2;
import org.example.math.vector.Vector3;
import org.example.math.vector.Vector4;

public class GraphicConveyor {

    // 1. Создание матрицы вида (камера)
    public static Matrix4 lookAt(Vector3 eye, Vector3 target) {
        return lookAt(eye, target, new Vector3(0, 1.0, 0));
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 target, Vector3 up) {
        Vector3 z = target.sub(eye).normalize();
        Vector3 x = up.cross(z).normalize();
        Vector3 y = z.cross(x).normalize();

        double[][] m = {
                {x.getX(), y.getX(), z.getX(), 0},
                {x.getY(), y.getY(), z.getY(), 0},
                {x.getZ(), y.getZ(), z.getZ(), 0},
                {-x.dot(eye), -y.dot(eye), -z.dot(eye), 1}
        };
        return new Matrix4(m);
    }

    // 2. Создание матрицы перспективной проекции
    public static Matrix4 perspective(
            double fov, // в радианах
            double aspectRatio,
            double near,
            double far) {

        double tanHalfFov = 1.0 / Math.tan(fov * 0.5);

        double[][] m = new double[4][4];
        m[0][0] = tanHalfFov / aspectRatio;
        m[1][1] = tanHalfFov;
        m[2][2] = (far + near) / (far - near);
        m[2][3] = 1.0;
        m[3][2] = -2.0 * far * near / (far - near);
        m[3][3] = 0;

        return new Matrix4(m);
    }

    // 3. Умножение матрицы 4x4 на вектор 3
    public static Vector3 multiplyMatrix4ByVector3(Matrix4 m, Vector3 v) {
        // Преобразуем Vector3 в Vector4 с w=1
        double x = m.get(0, 0) * v.getX() + m.get(0, 1) * v.getY() + m.get(0, 2) * v.getZ() + m.get(0, 3);
        double y = m.get(1, 0) * v.getX() + m.get(1, 1) * v.getY() + m.get(1, 2) * v.getZ() + m.get(1, 3);
        double z = m.get(2, 0) * v.getX() + m.get(2, 1) * v.getY() + m.get(2, 2) * v.getZ() + m.get(2, 3);
        double w = m.get(3, 0) * v.getX() + m.get(3, 1) * v.getY() + m.get(3, 2) * v.getZ() + m.get(3, 3);

        if (Math.abs(w) > 1e-10) {
            return new Vector3(x / w, y / w, z / w);
        }
        return new Vector3(x, y, z);
    }

    // 4. Умножение матрицы 4x4 на вектор 4
    public static Vector4 multiplyMatrix4ByVector4(Matrix4 m, Vector4 v) {
        double x = v.getX() * m.get(0, 0) + v.getY() * m.get(1, 0) + v.getZ() * m.get(2, 0) + v.getW() * m.get(3, 0);
        double y = v.getX() * m.get(0, 1) + v.getY() * m.get(1, 1) + v.getZ() * m.get(2, 1) + v.getW() * m.get(3, 1);
        double z = v.getX() * m.get(0, 2) + v.getY() * m.get(1, 2) + v.getZ() * m.get(2, 2) + v.getW() * m.get(3, 2);
        double w = v.getX() * m.get(0, 3) + v.getY() * m.get(1, 3) + v.getZ() * m.get(2, 3) + v.getW() * m.get(3, 3);

        return new Vector4(x, y, z, w);
    }

    // 5. Конвертация из нормализованных координат в экранные
    public static Vector2 vertexToPoint(Vector3 vertex, int width, int height) {
        return new Vector2(
                vertex.getX() * width + width / 2.0,
                -vertex.getY() * height + height / 2.0
        );
    }
}