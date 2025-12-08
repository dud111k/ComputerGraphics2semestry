import javax.vecmath.Matrix4d;
import java.util.Scanner;

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

        Matrix4d matrix = AffineTransformer.createTranslation(dx, dy, dz);
        AffineTransformer.transform(model, matrix);
        System.out.println("✓ Translation applied successfully");
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

        Matrix4d matrix;
        switch (axis) {
            case "X":
                matrix = AffineTransformer.createRotationX(angle);
                System.out.println("✓ Rotation around X axis applied");
                break;
            case "Y":
                matrix = AffineTransformer.createRotationY(angle);
                System.out.println("✓ Rotation around Y axis applied");
                break;
            case "Z":
                matrix = AffineTransformer.createRotationZ(angle);
                System.out.println("✓ Rotation around Z axis applied");
                break;
            default:
                System.out.println("✗ Invalid axis. Use X, Y or Z.");
                return;
        }

        AffineTransformer.transform(model, matrix);
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

        Matrix4d matrix = AffineTransformer.createScale(sx, sy, sz);
        AffineTransformer.transform(model, matrix);
        System.out.println("✓ Scaling applied successfully");
    }

    private static void saveModel() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.print("\nEnter output file name (e.g., output.obj): ");
        String fileName = scanner.nextLine();

        try {
            ObjWriter.write(model, fileName);
            System.out.println("✓ Model saved to: " + fileName);
        } catch (Exception e) {
            System.out.println("✗ Error saving model: " + e.getMessage());
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
            Vector3f firstVertex = model.getVertices().get(0);
            System.out.println("First vertex coordinates: (" +
                    String.format("%.3f, %.3f, %.3f",
                            firstVertex.x, firstVertex.y, firstVertex.z) + ")");
        }
    }

    private static void applyCombined() {
        if (model == null) {
            System.out.println("✗ Please load a model first!");
            return;
        }

        System.out.println("\n=== COMBINED TRANSFORMATION ===");
        System.out.println("Example: translate 1 0 0, then rotate Y 45, then scale 2 1 1");

        // Translation
        System.out.println("\n--- Translation ---");
        System.out.print("Enter translation X Y Z (separated by spaces): ");
        String[] trans = scanner.nextLine().split(" ");
        double tx = Double.parseDouble(trans[0]);
        double ty = Double.parseDouble(trans[1]);
        double tz = Double.parseDouble(trans[2]);
        Matrix4d translate = AffineTransformer.createTranslation(tx, ty, tz);

        // Rotation
        System.out.println("\n--- Rotation ---");
        System.out.print("Enter rotation axis (X/Y/Z): ");
        String axis = scanner.nextLine().toUpperCase();
        System.out.print("Enter rotation angle (degrees): ");
        double angle = Double.parseDouble(scanner.nextLine());

        Matrix4d rotate;
        switch (axis) {
            case "X": rotate = AffineTransformer.createRotationX(angle); break;
            case "Y": rotate = AffineTransformer.createRotationY(angle); break;
            case "Z": rotate = AffineTransformer.createRotationZ(angle); break;
            default:
                System.out.println("✗ Invalid axis. Using Y as default.");
                rotate = AffineTransformer.createRotationY(angle);
        }

        // Scaling
        System.out.println("\n--- Scaling ---");
        System.out.print("Enter scale X Y Z (separated by spaces): ");
        String[] scale = scanner.nextLine().split(" ");
        double sx = Double.parseDouble(scale[0]);
        double sy = Double.parseDouble(scale[1]);
        double sz = Double.parseDouble(scale[2]);
        Matrix4d scaleMat = AffineTransformer.createScale(sx, sy, sz);

        // Combine matrices: T * R * S
        Matrix4d combined = new Matrix4d();
        combined.setIdentity();
        combined.mul(translate);
        combined.mul(rotate);
        combined.mul(scaleMat);

        AffineTransformer.transform(model, combined);
        System.out.println("✓ Combined transformation applied successfully!");
        System.out.println("  Translation: (" + tx + ", " + ty + ", " + tz + ")");
        System.out.println("  Rotation: " + angle + " degrees around " + axis + " axis");
        System.out.println("  Scaling: (" + sx + ", " + sy + ", " + sz + ")");
    }
}