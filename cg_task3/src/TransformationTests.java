import javax.vecmath.Matrix4d;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class TransformationTests {
    private static Scanner scanner = new Scanner(System.in);
    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
        System.out.println("=== ТЕСТЫ ПРЕОБРАЗОВАНИЙ С ВЫВОДОМ В ФАЙЛ ===\n");


        Model cube = createTestCube();
        System.out.println("Создан тестовый куб (8 вершин)\n");

        boolean continueTesting = true;
        int testNumber = 1;

        while (continueTesting) {
            System.out.println("=== ТЕСТ #" + testNumber + " ===");


            System.out.println("Выберите преобразование:");
            System.out.println("1. Перемещение");
            System.out.println("2. Вращение");
            System.out.println("3. Масштабирование");
            System.out.println("4. Комбинированное");
            System.out.println("5. Выход");
            System.out.print("Ваш выбор: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    runTranslationTest(cube, testNumber);
                    break;
                case "2":
                    runRotationTest(cube, testNumber);
                    break;
                case "3":
                    runScaleTest(cube, testNumber);
                    break;
                case "4":
                    runCombinedTest(cube, testNumber);
                    break;
                case "5":
                    continueTesting = false;
                    System.out.println("Тестирование завершено.");
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }

            testNumber++;
            System.out.println();
        }

        scanner.close();
    }

    private static void runTranslationTest(Model model, int testNum) {
        System.out.println("\n--- ТЕСТ ПЕРЕМЕЩЕНИЯ ---");


        System.out.print("Введите перемещение по X: ");
        double tx = Double.parseDouble(scanner.nextLine());

        System.out.print("Введите перемещение по Y: ");
        double ty = Double.parseDouble(scanner.nextLine());

        System.out.print("Введите перемещение по Z: ");
        double tz = Double.parseDouble(scanner.nextLine());


        Model testModel = deepCopyModel(model);

        Matrix4d transform = AffineTransformer.createTranslation(tx, ty, tz);
        AffineTransformer.transform(testModel, transform);


        String filename = String.format("test_translate_%d_%.1f_%.1f_%.1f.obj",
                testNum, tx, ty, tz);

        try {
            ObjWriter.write(testModel, filename);
            System.out.println("\n✓ Преобразование применено!");
            System.out.println("Сохранено в файл: " + filename);
            printComparison(model, testModel, "До перемещения", "После перемещения");

        } catch (Exception e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
        }
    }

    private static void runRotationTest(Model model, int testNum) {
        System.out.println("\n--- ТЕСТ ВРАЩЕНИЯ ---");

        // Выбор оси
        System.out.println("Выберите ось вращения:");
        System.out.println("1. Ось X");
        System.out.println("2. Ось Y");
        System.out.println("3. Ось Z");
        System.out.print("Ваш выбор: ");
        String axisChoice = scanner.nextLine();

        System.out.print("Введите угол вращения (градусы): ");
        double angle = Double.parseDouble(scanner.nextLine());


        Model testModel = deepCopyModel(model);

        Matrix4d transform;
        String axisName;

        switch (axisChoice) {
            case "1":
                transform = AffineTransformer.createRotationX(angle);
                axisName = "X";
                break;
            case "2":
                transform = AffineTransformer.createRotationY(angle);
                axisName = "Y";
                break;
            case "3":
                transform = AffineTransformer.createRotationZ(angle);
                axisName = "Z";
                break;
            default:
                System.out.println("Неверный выбор оси. Использую ось Y.");
                transform = AffineTransformer.createRotationY(angle);
                axisName = "Y";
        }

        AffineTransformer.transform(testModel, transform);


        String filename = String.format("test_rotate_%d_%s_%.1f.obj",
                testNum, axisName, angle);

        try {
            ObjWriter.write(testModel, filename);

            System.out.println("\n✓ Преобразование применено!");
            System.out.println("Сохранено в файл: " + filename);
            printComparison(model, testModel, "До вращения", "После вращения");

        } catch (Exception e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
        }
    }

    private static void runScaleTest(Model model, int testNum) {
        System.out.println("\n--- ТЕСТ МАСШТАБИРОВАНИЯ ---");


        System.out.print("Введите масштаб по X: ");
        double sx = Double.parseDouble(scanner.nextLine());

        System.out.print("Введите масштаб по Y: ");
        double sy = Double.parseDouble(scanner.nextLine());

        System.out.print("Введите масштаб по Z: ");
        double sz = Double.parseDouble(scanner.nextLine());


        if (sx == 0 || sy == 0 || sz == 0) {
            System.out.println("Внимание: масштабирование нулем может привести к делению на ноль!");
        }

        if (sx < 0 || sy < 0 || sz < 0) {
            System.out.println("Внимание: отрицательное масштабирование создает зеркальное отражение!");
        }


        Model testModel = deepCopyModel(model);


        Matrix4d transform = AffineTransformer.createScale(sx, sy, sz);
        AffineTransformer.transform(testModel, transform);


        String filename = String.format("test_scale_%d_%.1f_%.1f_%.1f.obj",
                testNum, sx, sy, sz);

        try {
            ObjWriter.write(testModel, filename);

            System.out.println("\n✓ Преобразование применено!");
            System.out.println("Сохранено в файл: " + filename);
            printComparison(model, testModel, "До масштабирования", "После масштабирования");

        } catch (Exception e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
        }
    }

    private static void runCombinedTest(Model model, int testNum) {
        System.out.println("\n--- ТЕСТ КОМБИНИРОВАННОГО ПРЕОБРАЗОВАНИЯ ---");


        System.out.println("1. Перемещение:");
        System.out.print("  X: ");
        double tx = Double.parseDouble(scanner.nextLine());
        System.out.print("  Y: ");
        double ty = Double.parseDouble(scanner.nextLine());
        System.out.print("  Z: ");
        double tz = Double.parseDouble(scanner.nextLine());


        System.out.println("\n2. Вращение:");
        System.out.print("  Ось (X/Y/Z): ");
        String axis = scanner.nextLine().toUpperCase();
        System.out.print("  Угол (градусы): ");
        double angle = Double.parseDouble(scanner.nextLine());


        System.out.println("\n3. Масштабирование:");
        System.out.print("  X: ");
        double sx = Double.parseDouble(scanner.nextLine());
        System.out.print("  Y: ");
        double sy = Double.parseDouble(scanner.nextLine());
        System.out.print("  Z: ");
        double sz = Double.parseDouble(scanner.nextLine());


        Model testModel = deepCopyModel(model);


        Matrix4d translate = AffineTransformer.createTranslation(tx, ty, tz);
        Matrix4d rotate;

        switch (axis) {
            case "X": rotate = AffineTransformer.createRotationX(angle); break;
            case "Y": rotate = AffineTransformer.createRotationY(angle); break;
            case "Z": rotate = AffineTransformer.createRotationZ(angle); break;
            default: rotate = AffineTransformer.createRotationY(angle);
        }

        Matrix4d scale = AffineTransformer.createScale(sx, sy, sz);

        // Комбинируем: T * R * S
        Matrix4d combined = new Matrix4d();
        combined.setIdentity();
        combined.mul(translate);
        combined.mul(rotate);
        combined.mul(scale);


        AffineTransformer.transform(testModel, combined);


        String filename = String.format("test_combined_%d_T%.1f_%.1f_%.1f_R%s%.1f_S%.1f_%.1f_%.1f.obj",
                testNum, tx, ty, tz, axis, angle, sx, sy, sz);

        try {
            ObjWriter.write(testModel, filename);

            System.out.println("\n✓ Комбинированное преобразование применено!");
            System.out.println("Сохранено в файл: " + filename);
            System.out.println("\nПараметры преобразования:");
            System.out.printf("  Перемещение: (%.1f, %.1f, %.1f)\n", tx, ty, tz);
            System.out.printf("  Вращение: %.1f° вокруг оси %s\n", angle, axis);
            System.out.printf("  Масштаб: (%.1f, %.1f, %.1f)\n", sx, sy, sz);

            printComparison(model, testModel, "До преобразований", "После преобразований");

        } catch (Exception e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
        }
    }

    private static void printComparison(Model original, Model transformed,
                                        String originalLabel, String transformedLabel) {
        System.out.println("\nСравнение вершин:");
        System.out.println("=".repeat(60));
        System.out.printf("%-30s | %-30s\n", originalLabel, transformedLabel);
        System.out.println("-".repeat(60));

        int verticesToShow = Math.min(4, original.getVertices().size());

        for (int i = 0; i < verticesToShow; i++) {
            Vector3f v1 = original.getVertices().get(i);
            Vector3f v2 = transformed.getVertices().get(i);

            String origStr = String.format("(%s, %s, %s)",
                    df.format(v1.x), df.format(v1.y), df.format(v1.z));

            String transStr = String.format("(%s, %s, %s)",
                    df.format(v2.x), df.format(v2.y), df.format(v2.z));

            System.out.printf("Вершина %d: %-30s | %-30s\n",
                    i+1, origStr, transStr);
        }

        if (original.getVertices().size() > verticesToShow) {
            System.out.printf("... и еще %d вершин\n",
                    original.getVertices().size() - verticesToShow);
        }

        System.out.println("=".repeat(60));
    }

    private static Model createTestCube() {
        Model cube = new Model();


        cube.getVertices().add(new Vector3f(-1, -1, -1)); // 0
        cube.getVertices().add(new Vector3f(1, -1, -1));  // 1
        cube.getVertices().add(new Vector3f(1, 1, -1));   // 2
        cube.getVertices().add(new Vector3f(-1, 1, -1));  // 3
        cube.getVertices().add(new Vector3f(-1, -1, 1));  // 4
        cube.getVertices().add(new Vector3f(1, -1, 1));   // 5
        cube.getVertices().add(new Vector3f(1, 1, 1));    // 6
        cube.getVertices().add(new Vector3f(-1, 1, 1));   // 7


        addFace(cube, 0, 1, 2, 3); // задняя
        addFace(cube, 4, 5, 6, 7); // передняя
        addFace(cube, 0, 1, 5, 4); // нижняя
        addFace(cube, 2, 3, 7, 6); // верхняя
        addFace(cube, 0, 3, 7, 4); // левая
        addFace(cube, 1, 2, 6, 5); // правая

        return cube;
    }

    private static void addFace(Model model, int v1, int v2, int v3, int v4) {
        Polygon face = new Polygon();
        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(v1);
        indices.add(v2);
        indices.add(v3);
        indices.add(v4);
        face.setVertexIndices(indices);
        model.getPolygons().add(face);
    }

    private static Model deepCopyModel(Model original) {
        Model copy = new Model();


        for (Vector3f vertex : original.getVertices()) {
            copy.getVertices().add(new Vector3f(vertex.x, vertex.y, vertex.z));
        }


        for (Polygon poly : original.getPolygons()) {
            Polygon polyCopy = new Polygon();
            polyCopy.setVertexIndices(new ArrayList<>(poly.getVertexIndices()));
            if (!poly.getTextureVertexIndices().isEmpty()) {
                polyCopy.setTextureVertexIndices(new ArrayList<>(poly.getTextureVertexIndices()));
            }
            if (!poly.getNormalIndices().isEmpty()) {
                polyCopy.setNormalIndices(new ArrayList<>(poly.getNormalIndices()));
            }
            copy.getPolygons().add(polyCopy);
        }

        return copy;
    }
}