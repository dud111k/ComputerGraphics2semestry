import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {

    public static void write(Model model, String fileName) throws IOException {
        // Устанавливаем английскую локаль для точек в числах
        Locale.setDefault(Locale.US);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        // Записываем вершины с ТОЧКОЙ в качестве разделителя
        for (Vector3f vertex : model.getVertices()) {
            writer.write(String.format(Locale.US, "v %.6f %.6f %.6f\n",
                    vertex.x, vertex.y, vertex.z));
        }

        // Записываем текстурные координаты
        if (!model.getTextureVertices().isEmpty()) {
            writer.write("\n");
            for (Vector2f texVertex : model.getTextureVertices()) {
                writer.write(String.format(Locale.US, "vt %.6f %.6f\n",
                        texVertex.x, texVertex.y));
            }
        }

        // Записываем нормали
        if (!model.getNormals().isEmpty()) {
            writer.write("\n");
            for (Vector3f normal : model.getNormals()) {
                writer.write(String.format(Locale.US, "vn %.6f %.6f %.6f\n",
                        normal.x, normal.y, normal.z));
            }
        }

        // Записываем полигоны
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