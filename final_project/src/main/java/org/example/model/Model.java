package org.example.model;

import java.util.ArrayList;
import org.example.math.vector.Vector2;
import org.example.math.vector.Vector3;
import org.example.math.vector.Vector4;
import org.example.math.matrix.Matrix4;

public class Model {
    private ArrayList<Vector3> vertices;
    private ArrayList<Vector2> textureVertices;
    private ArrayList<Vector3> normals;
    private ArrayList<Polygon> polygons;
    private Matrix4 transformationMatrix = Matrix4.identity();

    public Model() {
        vertices = new ArrayList<>();
        textureVertices = new ArrayList<>();
        normals = new ArrayList<>();
        polygons = new ArrayList<>();
    }

    public ArrayList<Vector3> getVertices() {
        return vertices;
    }

    public ArrayList<Vector2> getTextureVertices() {
        return textureVertices;
    }

    public ArrayList<Vector3> getNormals() {
        return normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public Matrix4 getTransformationMatrix() {
        return transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4 matrix) {
        this.transformationMatrix = matrix;
    }

    // Метод для получения трансформированной вершины
    public Vector3 getTransformedVertex(int index) {
        Vector3 original = vertices.get(index);

        // Преобразуем Vector3 в Vector4 (w=1 для точки)
        Vector4 point = new Vector4(
                original.getX(), original.getY(), original.getZ(), 1.0
        );

        // Умножаем матрицу на вектор
        Vector4 transformed = transformationMatrix.mul(point);

        return new Vector3(
                (float)transformed.getX(),
                (float)transformed.getY(),
                (float)transformed.getZ()
        );
    }
}