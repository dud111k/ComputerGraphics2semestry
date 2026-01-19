package org.example.ObjReader;


import java.io.*;
import java.util.*;
import org.example.model.Model;
import org.example.model.Polygon;
import org.example.math.vector.Vector3;
import org.example.math.vector.Vector2;


public class ObjReader {
    public static Model read(String filename) throws IOException {
        Model model = new Model();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int lineInd = 0;

        while ((line = reader.readLine()) != null) {
            lineInd++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] wordsInLine = line.split("\\s+");
            if (wordsInLine.length == 0) continue;

            try {
                switch (wordsInLine[0]) {
                    case "v":
                        parseVertex(wordsInLine, model);
                        break;
                    case "vt":
                        parseTextureVertex(wordsInLine, model);
                        break;
                    case "vn":
                        parseNormal(wordsInLine, model);
                        break;
                    case "f":
                        parseFace(wordsInLine, model);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error at line " + lineInd + ": " + e.getMessage());
            }
        }

        reader.close();
        return model;
    }

    private static void parseVertex(String[] words, Model model) {
        if (words.length >= 4) {
            float x = Float.parseFloat(words[1]);
            float y = Float.parseFloat(words[2]);
            float z = Float.parseFloat(words[3]);
            model.getVertices().add(new Vector3(x, y, z));
        }
    }

    private static void parseTextureVertex(String[] words, Model model) {
        if (words.length >= 3) {
            float u = Float.parseFloat(words[1]);
            float v = Float.parseFloat(words[2]);
            model.getTextureVertices().add(new Vector2(u, v));
        }
    }

    private static void parseNormal(String[] words, Model model) {
        if (words.length >= 4) {
            float x = Float.parseFloat(words[1]);
            float y = Float.parseFloat(words[2]);
            float z = Float.parseFloat(words[3]);
            model.getNormals().add(new Vector3(x, y, z));
        }
    }

    private static void parseFace(String[] words, Model model) {
        Polygon polygon = new Polygon();
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> textureIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();

        for (int i = 1; i < words.length; i++) {
            String[] indices = words[i].split("/");

            if (indices[0].length() > 0) {
                vertexIndices.add(Integer.parseInt(indices[0]) - 1);
            }

            if (indices.length > 1 && indices[1].length() > 0) {
                textureIndices.add(Integer.parseInt(indices[1]) - 1);
            }

            if (indices.length > 2 && indices[2].length() > 0) {
                normalIndices.add(Integer.parseInt(indices[2]) - 1);
            }
        }

        polygon.setVertexIndices(vertexIndices);
        if (!textureIndices.isEmpty()) {
            polygon.setTextureVertexIndices(textureIndices);
        }
        if (!normalIndices.isEmpty()) {
            polygon.setNormalIndices(normalIndices);
        }

        model.getPolygons().add(polygon);
    }
}