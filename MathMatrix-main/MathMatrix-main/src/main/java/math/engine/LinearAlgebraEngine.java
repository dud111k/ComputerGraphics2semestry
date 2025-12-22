package math.engine;


import math.engine.matrix.Matrix3x3;
import math.engine.matrix.Matrix4x4;
import math.engine.vector.Vector2D;
import math.engine.vector.Vector3D;
import math.engine.vector.Vector4D;

public final class LinearAlgebraEngine {

    private LinearAlgebraEngine() {}

    public static Vector2D createVector2D(float x, float y) {
        return new Vector2D(x, y);
    }

    public static Vector3D createVector3D(float x, float y, float z) {
        return new Vector3D(x, y, z);
    }

    public static Vector4D createVector4D(float x, float y, float z, float w) {
        return new Vector4D(x, y, z, w);
    }

    public static Vector4D createVector4DFrom3D(Vector3D vector, float w) {
        return new Vector4D(vector, w);
    }

    public static Matrix3x3 createIdentityMatrix3x3() {
        return Matrix3x3.identity();
    }

    public static Matrix3x3 createZeroMatrix3x3() {
        return Matrix3x3.zero();
    }

    public static Matrix4x4 createIdentityMatrix4x4() {
        return Matrix4x4.identity();
    }

    public static Matrix4x4 createZeroMatrix4x4() {
        return Matrix4x4.zero();
    }

    public static Matrix4x4 createTranslationMatrix(float x, float y, float z) {
        return Matrix4x4.translation(x, y, z);
    }

    public static Matrix4x4 createRotationMatrixX(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        return new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, cos, -sin, 0},
                {0, sin, cos, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 createRotationMatrixY(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        return new Matrix4x4(new float[][]{
                {cos, 0, sin, 0},
                {0, 1, 0, 0},
                {-sin, 0, cos, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 createRotationMatrixZ(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        return new Matrix4x4(new float[][]{
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 createScaleMatrix(float scaleX, float scaleY, float scaleZ) {
        return new Matrix4x4(new float[][]{
                {scaleX, 0, 0, 0},
                {0, scaleY, 0, 0},
                {0, 0, scaleZ, 0},
                {0, 0, 0, 1}
        });
    }

    public static float computeAngleBetweenVectors(Vector3D v1, Vector3D v2) {
        float dotProduct = v1.dot(v2);
        float lengths = v1.length() * v2.length();

        if (Math.abs(lengths) < 1e-12f) {
            throw new ArithmeticException("Cannot compute angle for zero vectors");
        }

        float cosAngle = dotProduct / lengths;
        cosAngle = Math.max(-1.0f, Math.min(1.0f, cosAngle));

        return (float) Math.acos(cosAngle);
    }
}