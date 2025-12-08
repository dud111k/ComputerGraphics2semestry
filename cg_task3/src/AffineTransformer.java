import javax.vecmath.*;

public class AffineTransformer {

    // Конвертация между Vector3f и vecmath типами
    private static Point3d toPoint3d(Vector3f v) {
        return new Point3d(v.x, v.y, v.z);
    }

    private static Vector3f toVector3f(Point3d p) {
        return new Vector3f((float)p.x, (float)p.y, (float)p.z);
    }

    private static Vector3d toVector3d(Vector3f v) {
        return new Vector3d(v.x, v.y, v.z);
    }

    private static Vector3f toVector3f(Vector3d v) {
        return new Vector3f((float)v.x, (float)v.y, (float)v.z);
    }

    public static void transform(Model model, Matrix4d transform) {
        transformVertices(model, transform);
        transformNormals(model, transform);
    }

    private static void transformVertices(Model model, Matrix4d transform) {
        for (int i = 0; i < model.getVertices().size(); i++) {
            Vector3f vertex = model.getVertices().get(i);
            Point3d point = toPoint3d(vertex);
            transform.transform(point);
            model.getVertices().set(i, toVector3f(point));
        }
    }

    private static void transformNormals(Model model, Matrix4d transform) {
        if (model.getNormals().isEmpty()) return;

        Matrix3d normalMatrix = new Matrix3d();
        transform.getRotationScale(normalMatrix);
        normalMatrix.invert();
        normalMatrix.transpose();

        for (int i = 0; i < model.getNormals().size(); i++) {
            Vector3f normal = model.getNormals().get(i);
            Vector3d normalVec = toVector3d(normal);
            normalMatrix.transform(normalVec);
            normalVec.normalize();
            model.getNormals().set(i, toVector3f(normalVec));
        }
    }


    public static Matrix4d createTranslation(double dx, double dy, double dz) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        matrix.setTranslation(new Vector3d(dx, dy, dz));
        return matrix;
    }

    public static Matrix4d createRotationX(double angleDegrees) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        matrix.rotX(Math.toRadians(angleDegrees));
        return matrix;
    }

    public static Matrix4d createRotationY(double angleDegrees) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        matrix.rotY(Math.toRadians(angleDegrees));
        return matrix;
    }

    public static Matrix4d createRotationZ(double angleDegrees) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        matrix.rotZ(Math.toRadians(angleDegrees));
        return matrix;
    }

    public static Matrix4d createScale(double sx, double sy, double sz) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        matrix.m00 = sx;
        matrix.m11 = sy;
        matrix.m22 = sz;
        return matrix;
    }

    public static Matrix4d combine(Matrix4d... matrices) {
        Matrix4d result = new Matrix4d();
        result.setIdentity();

        for (Matrix4d matrix : matrices) {
            result.mul(matrix);
        }

        return result;
    }
}