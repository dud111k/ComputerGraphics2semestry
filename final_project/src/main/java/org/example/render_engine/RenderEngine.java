package org.example.render_engine;

import javafx.scene.canvas.GraphicsContext;
import org.example.math.matrix.Matrix4;
import org.example.math.vector.Vector2;
import org.example.math.vector.Vector3;
import org.example.model.Model;
import org.example.model.Polygon;
import java.util.ArrayList;
import org.example.render_engine.Camera;
import org.example.render_engine.GraphicConveyor;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {

        // 1. Получаем матрицы преобразований
        // УБЕРИ modelMatrix из меша - используй только камеру!
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        // 2. Комбинируем матрицы: MVP = Projection * View
        // БЕЗ Model матрицы!
        Matrix4 viewProjectionMatrix = projectionMatrix.mul(viewMatrix);

        // 3. Рендерим каждый полигон
        for (Polygon polygon : mesh.getPolygons()) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            int nVertices = vertexIndices.size();

            if (nVertices < 2) continue;

            // 4. Преобразуем вершины полигона в экранные координаты
            ArrayList<Vector2> screenPoints = new ArrayList<>();

            for (int i = 0; i < nVertices; i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < mesh.getVertices().size()) {
                    Vector3 vertex = mesh.getVertices().get(vertexIndex);

                    // Применяем ТОЛЬКО View * Projection
                    Vector3 transformedVertex = GraphicConveyor.multiplyMatrix4ByVector3(
                            viewProjectionMatrix, vertex);

                    // Конвертируем в экранные координаты
                    Vector2 screenPoint = GraphicConveyor.vertexToPoint(
                            transformedVertex, width, height);

                    screenPoints.add(screenPoint);
                }
            }

            // 5. Рисуем полигон (линии между вершинами)
            if (screenPoints.size() > 1) {
                // Рисуем линии между последовательными вершинами
                for (int i = 1; i < screenPoints.size(); i++) {
                    Vector2 p1 = screenPoints.get(i - 1);
                    Vector2 p2 = screenPoints.get(i);

                    graphicsContext.strokeLine(
                            p1.getX(), p1.getY(),
                            p2.getX(), p2.getY());
                }

                // Замыкаем полигон (последняя вершина → первая)
                if (screenPoints.size() > 2) {
                    Vector2 first = screenPoints.get(0);
                    Vector2 last = screenPoints.get(screenPoints.size() - 1);

                    graphicsContext.strokeLine(
                            last.getX(), last.getY(),
                            first.getX(), first.getY());
                }
            }
        }
    }

    // Альтернативная версия с использованием трансформированных вершин из модели
    public static void renderWithTransformations(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final boolean applyModelTransformations) {

        // 1. Получаем матрицы
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();
        Matrix4 viewProjectionMatrix = projectionMatrix.mul(viewMatrix);

        // 2. Рендерим полигоны
        for (Polygon polygon : mesh.getPolygons()) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            int nVertices = vertexIndices.size();

            if (nVertices < 2) continue;

            ArrayList<Vector2> screenPoints = new ArrayList<>();

            for (int i = 0; i < nVertices; i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < mesh.getVertices().size()) {
                    Vector3 vertex;

                    if (applyModelTransformations) {
                        // Используем трансформированные вершины
                        vertex = mesh.getTransformedVertex(vertexIndex);
                    } else {
                        // Используем оригинальные вершины
                        vertex = mesh.getVertices().get(vertexIndex);
                    }

                    // Применяем только View * Projection (модельные трансформации уже учтены)
                    Vector3 transformedVertex = GraphicConveyor.multiplyMatrix4ByVector3(
                            viewProjectionMatrix, vertex);

                    Vector2 screenPoint = GraphicConveyor.vertexToPoint(
                            transformedVertex, width, height);

                    screenPoints.add(screenPoint);
                }
            }

            // Рисуем полигон
            drawPolygon(graphicsContext, screenPoints);
        }
    }

    // Вспомогательный метод для рисования полигона
    private static void drawPolygon(GraphicsContext gc, ArrayList<Vector2> points) {
        if (points.size() < 2) return;

        for (int i = 1; i < points.size(); i++) {
            Vector2 p1 = points.get(i - 1);
            Vector2 p2 = points.get(i);
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }

        // Замыкаем полигон если нужно
        if (points.size() > 2) {
            Vector2 first = points.get(0);
            Vector2 last = points.get(points.size() - 1);
            gc.strokeLine(last.getX(), last.getY(), first.getX(), first.getY());
        }
    }

    // Простая версия для отладки (без камеры, только модельные трансформации)
    public static void renderSimple(
            final GraphicsContext graphicsContext,
            final Model mesh,
            final int width,
            final int height) {

        // Просто применяем модельные трансформации и рисуем
        for (Polygon polygon : mesh.getPolygons()) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            int nVertices = vertexIndices.size();

            if (nVertices < 2) continue;

            ArrayList<Vector2> screenPoints = new ArrayList<>();

            for (int i = 0; i < nVertices; i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < mesh.getVertices().size()) {
                    // Используем трансформированные вершины
                    Vector3 vertex = mesh.getTransformedVertex(vertexIndex);

                    // Простая ортографическая проекция (игнорируем Z)
                    double screenX = vertex.getX() * 100 + width / 2.0;
                    double screenY = -vertex.getY() * 100 + height / 2.0;

                    screenPoints.add(new Vector2(screenX, screenY));
                }
            }

            drawPolygon(graphicsContext, screenPoints);
        }
    }
}