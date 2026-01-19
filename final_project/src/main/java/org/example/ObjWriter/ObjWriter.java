package org.example.ObjWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

import org.example.model.Model;
import org.example.model.Polygon;
import org.example.math.vector.Vector3;
import org.example.math.vector.Vector2;

public class ObjWriter {

    // Старый метод для совместимости (без трансформаций)
    public static void write(Model model, String fileName) throws IOException {
        write(model, fileName, false);
    }

    // Новый метод с поддержкой трансформаций
    public static void write(Model model, String fileName, boolean applyTransformations) throws IOException {
        // Устанавливаем английскую локаль для точек в числах
        Locale.setDefault(Locale.US);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        // --- ЗАПИСЬ ВЕРШИН (с трансформациями или без) ---
        for (int i = 0; i < model.getVertices().size(); i++) {
            Vector3 vertex;

            if (applyTransformations) {
                // Получаем трансформированную вершину
                vertex = model.getTransformedVertex(i);
            } else {
                // Берем оригинальную вершину
                vertex = model.getVertices().get(i);
            }

            writer.write(String.format(Locale.US, "v %.6f %.6f %.6f\n",
                    vertex.getX(), vertex.getY(), vertex.getZ()));
        }

        // --- ЗАПИСЬ ТЕКСТУРНЫХ КООРДИНАТ ---
        if (!model.getTextureVertices().isEmpty()) {
            writer.write("\n");
            for (Vector2 texVertex : model.getTextureVertices()) {
                writer.write(String.format(Locale.US, "vt %.6f %.6f\n",
                        texVertex.getX(), texVertex.getY()));
            }
        }

        // --- ЗАПИСЬ НОРМАЛЕЙ ---
        if (!model.getNormals().isEmpty()) {
            writer.write("\n");
            for (Vector3 normal : model.getNormals()) {
                writer.write(String.format(Locale.US, "vn %.6f %.6f %.6f\n",
                        normal.getX(), normal.getY(), normal.getZ()));
            }
        }

        // --- ЗАПИСЬ ПОЛИГОНОВ ---
        if (!model.getPolygons().isEmpty()) {
            writer.write("\n");
            for (Polygon polygon : model.getPolygons()) {
                writer.write(faceToString(polygon) + "\n");
            }
        }

        writer.close();
    }

    private static String faceToString(Polygon polygon) {
        StringBuilder sb = new StringBuilder("f");
        ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
        ArrayList<Integer> texIndices = polygon.getTextureVertexIndices();
        ArrayList<Integer> normalIndices = polygon.getNormalIndices();

        boolean hasTex = !texIndices.isEmpty();
        boolean hasNormals = !normalIndices.isEmpty();

        for (int i = 0; i < vertexIndices.size(); i++) {
            sb.append(" ");
            sb.append(vertexIndices.get(i) + 1);

            if (hasTex || hasNormals) {
                sb.append("/");
                if (hasTex && i < texIndices.size()) {
                    sb.append(texIndices.get(i) + 1);
                }

                if (hasNormals) {
                    sb.append("/");
                    if (i < normalIndices.size()) {
                        sb.append(normalIndices.get(i) + 1);
                    }
                }
            }
        }

        return sb.toString();
    }
}