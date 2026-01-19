package org.example.math.affine;

import java.util.Scanner;
import org.example.ObjWriter.ObjWriter;
import org.example.ObjReader.ObjReader;
import org.example.model.Model;
import org.example.math.matrix.Matrix4;
import org.example.math.affine.AffineTransformer;

public class Main {
    private static Model model;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== 3D Model Transformer ===\n");

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": loadModel(); break;
                case "2": translate(); break;
                case "3": rotate(); break;
                case "4": scale(); break;
                case "5": saveModel(); break;
                case "6": showInfo(); break;
                case "7": applyCombined(); break;
                case "0":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. Load OBJ model");
        System.out.println("2. Translate model");
        System.out.println("3. Rotate model");
        System.out.println("4. Scale model");
        System.out.println("5. Save model");
        System.out.println("6. Show model info");
        System.out.println("7. Apply combined transformation");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void loadModel() {
        System.out.print("Enter path to OBJ file: ");
        String path = scanner.nextLine();

        try {
            model = ObjReader.read(path);
            System.out.println("✓ Model loaded successfully!");
            System.out.println("  Vertices: " + model.getVertices().size());
            System.out.println("  Texture vertices: " + model.getTextureVertices().size());
            System.out.println("  Normals: " + model.getNormals().size());
            System.out.println("  Polygons: " + model.getPolygons().size());
        } catch (Exception e) {
            System.out.println("✗ Error loading model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void translate() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }
        System.out.println("\n=== TRANSLATION ===");
        System.out.print("Enter translation X: ");
        double dx = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter translation Y: ");
        double dy = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter translation Z: ");
        double dz = Double.parseDouble(scanner.nextLine());

        Matrix4 currentMatrix = model.getTransformationMatrix();
        Matrix4 translationMatrix = AffineTransformer.createTranslation(dx, dy, dz);
        Matrix4 newMatrix = currentMatrix.mul(translationMatrix);

        model.setTransformationMatrix(newMatrix);
        System.out.println("✓ Translation applied successfully");
        System.out.println("  Total displacement: (" + dx + ", " + dy + ", " + dz + ")");
    }

    private static void rotate() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.println("\n=== ROTATION ===");
        System.out.println("Select rotation axis:");
        System.out.println("X - Rotate around X axis");
        System.out.println("Y - Rotate around Y axis");
        System.out.println("Z - Rotate around Z axis");
        System.out.print("Enter axis (X/Y/Z): ");
        String axis = scanner.nextLine().toUpperCase();

        System.out.print("Enter angle in degrees: ");
        double angle = Double.parseDouble(scanner.nextLine());

        // Получаем текущую матрицу трансформации
        Matrix4 currentMatrix = model.getTransformationMatrix();

        // Создаем матрицу поворота
        Matrix4 rotationMatrix;
        switch (axis) {
            case "X":
                rotationMatrix = AffineTransformer.createRotationXDeg(angle);
                System.out.println("✓ Rotation around X axis applied");
                break;
            case "Y":
                rotationMatrix = AffineTransformer.createRotationYDeg(angle);
                System.out.println("✓ Rotation around Y axis applied");
                break;
            case "Z":
                rotationMatrix = AffineTransformer.createRotationZDeg(angle);
                System.out.println("✓ Rotation around Z axis applied");
                break;
            default:
                System.out.println("✗ Invalid axis. Use X, Y or Z.");
                return;
        }

        // Накопление трансформации: new = rotation * current
        Matrix4 newMatrix = currentMatrix.mul(rotationMatrix);

        // Устанавливаем новую матрицу в модель
        model.setTransformationMatrix(newMatrix);

        System.out.println("  Rotation angle: " + angle + " degrees around " + axis + " axis");
    }

    private static void scale() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.println("\n=== SCALING ===");
        System.out.print("Enter scale factor for X axis: ");
        double sx = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter scale factor for Y axis: ");
        double sy = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter scale factor for Z axis: ");
        double sz = Double.parseDouble(scanner.nextLine());

        // Получаем текущую матрицу трансформации
        Matrix4 currentMatrix = model.getTransformationMatrix();

        // Создаем матрицу масштабирования
        Matrix4 scaleMatrix = AffineTransformer.createScale(sx, sy, sz);

        // Накопление трансформации: new = scale * current
        Matrix4 newMatrix = currentMatrix.mul(scaleMatrix);

        // Устанавливаем новую матрицу в модель
        model.setTransformationMatrix(newMatrix);

        System.out.println("✓ Scaling applied successfully");
        System.out.println("  Scale factors: (" + sx + ", " + sy + ", " + sz + ")");
    }

    private static void saveModel() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.println("\n=== SAVE MODEL ===");
        System.out.println("1. Save original model (without transformations)");
        System.out.println("2. Save transformed model");
        System.out.print("Choose option (1/2): ");
        String option = scanner.nextLine();

        boolean applyTransformations = option.equals("2");

        System.out.print("Enter output file name: ");
        String fileName = scanner.nextLine();

        try {
            ObjWriter.write(model, fileName, applyTransformations);
            System.out.println("✓ Model saved!");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void showInfo() {
        if (model == null) {
            System.out.println("✗ No model loaded");
            return;
        }

        System.out.println("\n=== MODEL INFORMATION ===");
        System.out.println("Number of vertices: " + model.getVertices().size());
        System.out.println("Number of texture vertices: " + model.getTextureVertices().size());
        System.out.println("Number of normals: " + model.getNormals().size());
        System.out.println("Number of polygons: " + model.getPolygons().size());

        if (!model.getVertices().isEmpty()) {
            org.example.math.vector.Vector3 firstVertex = model.getVertices().get(0);
            System.out.println("First vertex coordinates: (" +
                    String.format("%.3f, %.3f, %.3f",
                            firstVertex.getX(), firstVertex.getY(), firstVertex.getZ()) + ")");
        }
    }

    private static void resetTransformations() {
        if (model == null) {
            System.out.println("✗ No model loaded!");
            return;
        }

        // Сбрасываем матрицу трансформации к единичной
        model.setTransformationMatrix(Matrix4.identity());
        System.out.println("✓ All transformations reset to identity");
    }

    private static void showCurrentTransform() {
        if (model == null) {
            System.out.println("✗ No model loaded");
            return;
        }

        Matrix4 transform = model.getTransformationMatrix();
        System.out.println("\n=== CURRENT TRANSFORMATION MATRIX ===");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.printf("%10.4f ", transform.get(i, j));
            }
            System.out.println();
        }
    }

    private static void applyCombined() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.println("\n=== COMBINED TRANSFORMATION ===");
        System.out.println("Note: Transformations are applied in order: Scale * Rotation * Translation");
        System.out.println("(For vectors as columns: v' = S * R * T * v)");

        // Translation
        System.out.println("\n--- Translation ---");
        System.out.print("Enter translation X Y Z (separated by spaces): ");
        String[] trans = scanner.nextLine().split(" ");
        double tx = Double.parseDouble(trans[0]);
        double ty = Double.parseDouble(trans[1]);
        double tz = Double.parseDouble(trans[2]);
        Matrix4 translate = AffineTransformer.createTranslation(tx, ty, tz);

        // Rotation
        System.out.println("\n--- Rotation ---");
        System.out.print("Enter rotation axis (X/Y/Z): ");
        String axis = scanner.nextLine().toUpperCase();
        System.out.print("Enter rotation angle (degrees): ");
        double angle = Double.parseDouble(scanner.nextLine());

        Matrix4 rotate;
        switch (axis) {
            case "X": rotate = AffineTransformer.createRotationXDeg(angle); break;
            case "Y": rotate = AffineTransformer.createRotationYDeg(angle); break;
            case "Z": rotate = AffineTransformer.createRotationZDeg(angle); break;
            default:
                System.out.println("✗ Invalid axis. Using Y as default.");
                rotate = AffineTransformer.createRotationYDeg(angle);
        }

        // Scaling
        System.out.println("\n--- Scaling ---");
        System.out.print("Enter scale X Y Z (separated by spaces): ");
        String[] scale = scanner.nextLine().split(" ");
        double sx = Double.parseDouble(scale[0]);
        double sy = Double.parseDouble(scale[1]);
        double sz = Double.parseDouble(scale[2]);
        Matrix4 scaleMat = AffineTransformer.createScale(sx, sy, sz);

        // Combine matrices: S * R * T (для векторов-столбцов)
        Matrix4 combined = AffineTransformer.combine(scaleMat, rotate, translate);

        AffineTransformer.transform(model, combined);
        System.out.println("✓ Combined transformation applied successfully!");
        System.out.println("  Order: Scale * Rotation * Translation");
        System.out.println("  Translation: (" + tx + ", " + ty + ", " + tz + ")");
        System.out.println("  Rotation: " + angle + " degrees around " + axis + " axis");
        System.out.println("  Scaling: (" + sx + ", " + sy + ", " + sz + ")");
    }
}