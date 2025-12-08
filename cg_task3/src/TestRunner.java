import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=== ПРОСТЫЕ ТЕСТЫ БЕЗ MATRIX4D ===\n");

        testModel();
        testPolygon();
        testObjWriter();
        testFileOperations();

        System.out.println("\n=== Все тесты завершены ===");
    }

    private static void testModel() {
        System.out.print("Тест 1: Создание модели... ");
        Model model = new Model();

        if (model.getVertices() == null) {
            System.out.println("FAIL: vertices is null");
            return;
        }

        if (model.getPolygons() == null) {
            System.out.println("FAIL: polygons is null");
            return;
        }

        model.getVertices().add(new Vector3f(1, 2, 3));
        if (model.getVertices().size() != 1) {
            System.out.println("FAIL: не добавилась вершина");
            return;
        }

        System.out.println("OK");
    }

    private static void testPolygon() {
        System.out.print("Тест 2: Создание полигона... ");
        Polygon poly = new Polygon();

        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(0);
        indices.add(1);
        indices.add(2);
        poly.setVertexIndices(indices);

        if (poly.getVertexIndices().size() != 3) {
            System.out.println("FAIL: неверное количество индексов");
            return;
        }

        if (poly.getVertexIndices().get(0) != 0) {
            System.out.println("FAIL: неверный первый индекс");
            return;
        }

        System.out.println("OK");
    }

    private static void testObjWriter() {
        System.out.print("Тест 3: Запись OBJ... ");

        try {
            // Создаем простую модель
            Model model = new Model();
            model.getVertices().add(new Vector3f(1.0f, 2.0f, 3.0f));
            model.getVertices().add(new Vector3f(4.0f, 5.0f, 6.0f));

            Polygon poly = new Polygon();
            ArrayList<Integer> indices = new ArrayList<>();
            indices.add(0);
            indices.add(1);
            poly.setVertexIndices(indices);
            model.getPolygons().add(poly);

            // Пытаемся записать - ВОТ ИСПРАВЛЕНИЕ!
            String filename = "test_output.obj";
            ObjWriter.write(model, filename);  // ДВА аргумента!

            // Проверяем, что файл создан
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("FAIL: файл не создан");
                return;
            }

            // Читаем и проверяем содержимое
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("\nСодержимое файла:");
            System.out.println(content);

            // Проверяем ключевые элементы
            boolean hasVertex1 = content.contains("1.000000") || content.contains("1.0");
            boolean hasVertex2 = content.contains("4.000000") || content.contains("4.0");
            boolean hasFace = content.contains("f") || content.contains("1 2");

            if (!hasVertex1 || !hasVertex2 || !hasFace) {
                System.out.println("FAIL: неверное содержимое");
                System.out.println("  Ищется '1.000000' или '1.0': " + hasVertex1);
                System.out.println("  Ищется '4.000000' или '4.0': " + hasVertex2);
                System.out.println("  Ищется 'f' или '1 2': " + hasFace);
                return;
            }

            // Удаляем временный файл
            boolean deleted = file.delete();
            if (!deleted) {
                System.out.println("Предупреждение: не удалось удалить файл");
            }

            System.out.println("OK");

        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testFileOperations() {
        System.out.print("Тест 4: Операции с файлами... ");

        try {
            // Создаем тестовый OBJ файл
            String testContent =
                    "v 1.0 2.0 3.0\n" +
                            "v 4.0 5.0 6.0\n" +
                            "f 1 2\n";

            Files.write(new File("test_input.obj").toPath(), testContent.getBytes());

            // Читаем через ObjReader
            Model model = ObjReader.read("test_input.obj");

            if (model.getVertices().size() != 2) {
                System.out.println("FAIL: неверное количество вершин: " + model.getVertices().size());
                return;
            }

            if (model.getPolygons().size() != 1) {
                System.out.println("FAIL: неверное количество полигонов: " + model.getPolygons().size());
                return;
            }

            // Проверяем значения вершин
            Vector3f v1 = model.getVertices().get(0);
            if (Math.abs(v1.x - 1.0) > 0.001 ||
                    Math.abs(v1.y - 2.0) > 0.001 ||
                    Math.abs(v1.z - 3.0) > 0.001) {
                System.out.println("FAIL: неверные координаты первой вершины");
                return;
            }

            // Удаляем тестовые файлы
            new File("test_input.obj").delete();

            System.out.println("OK");

        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}