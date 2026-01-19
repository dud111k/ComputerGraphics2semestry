package org.example.render_engine;

import org.example.math.matrix.Matrix4;
import org.example.math.vector.Vector3;
import org.example.render_engine.GraphicConveyor;

public class Camera {
    private Vector3 position;
    private Vector3 target;
    private double fov;
    private double aspectRatio;
    private double nearPlane;
    private double farPlane;

    // Параметры скорости
    private double moveSpeed = 50.0;
    private double rotationSpeed = 0.5; // градусы на пиксель

    // Углы вращения (для FPS-камеры)
    private double yaw = 0.0;   // Горизонтальный угол (в градусах)
    private double pitch = 0.0; // Вертикальный угол (в градусах)

    private final Vector3 WORLD_UP = new Vector3(0, 1, 0);

    public Camera(Vector3 position, Vector3 target, double fovRad,
                  double aspectRatio, double nearPlane, double farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fovRad;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        updateYawPitchFromTarget();
    }

    private void updateYawPitchFromTarget() {
        // Вычисляем yaw и pitch из начального направления
        Vector3 direction = target.sub(position);
        double distance = direction.length();

        // Нормализуем
        direction = direction.normalize();

        // Вычисляем углы
        yaw = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
        pitch = Math.toDegrees(Math.asin(direction.getY()));
    }

    private void updateTargetFromAngles() {
        // Конвертируем углы в радианы
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // Вычисляем направление
        double x = Math.cos(pitchRad) * Math.sin(yawRad);
        double y = Math.sin(pitchRad);
        double z = Math.cos(pitchRad) * Math.cos(yawRad);

        Vector3 direction = new Vector3(x, y, z).normalize();

        // Вычисляем расстояние до старой цели
        double distance = target.sub(position).length();

        // Устанавливаем новую цель на том же расстоянии
        target = position.add(direction.mul(distance));
    }

    private Vector3 getForwardVector() {
        return target.sub(position).normalize();
    }

    private Vector3 getRightVector() {
        Vector3 forward = getForwardVector();
        return forward.cross(WORLD_UP).normalize();
    }

    private Vector3 getUpVector() {
        Vector3 forward = getForwardVector();
        Vector3 right = getRightVector();
        return right.cross(forward).normalize();
    }

    // Движение ВПЕРЕД/НАЗАД - по направлению взгляда
    public void moveForward(double multiplier) {
        Vector3 forward = getForwardVector();
        double amount = moveSpeed * multiplier;

        // Исключаем вертикальную составляющую для движения по горизонтали
        forward = new Vector3(forward.getX(), 0, forward.getZ()).normalize();

        position = position.add(forward.mul(amount));
        target = target.add(forward.mul(amount));

        System.out.println("Вперед на " + amount + ". Позиция: " + position);
    }

    public void moveBackward(double multiplier) {
        Vector3 forward = getForwardVector();
        double amount = moveSpeed * multiplier;

        // Исключаем вертикальную составляющую для движения по горизонтали
        forward = new Vector3(forward.getX(), 0, forward.getZ()).normalize();

        position = position.sub(forward.mul(amount));
        target = target.sub(forward.mul(amount));

        System.out.println("Назад на " + amount + ". Позиция: " + position);
    }

    // Движение ВЛЕВО/ВПРАВО - перпендикулярно направлению взгляда
    public void moveLeft(double multiplier) {
        Vector3 right = getRightVector();
        double amount = moveSpeed * multiplier;

        position = position.sub(right.mul(amount));
        target = target.sub(right.mul(amount));

        System.out.println("Влево на " + amount + ". Позиция: " + position);
    }

    public void moveRight(double multiplier) {
        Vector3 right = getRightVector();
        double amount = moveSpeed * multiplier;

        position = position.add(right.mul(amount));
        target = target.add(right.mul(amount));

        System.out.println("Вправо на " + amount + ". Позиция: " + position);
    }

    // Движение ВВЕРХ/ВНИЗ - по вертикали
    public void moveUp(double multiplier) {
        double amount = moveSpeed * multiplier;

        position = new Vector3(
                position.getX(),
                position.getY() + amount,
                position.getZ()
        );
        target = new Vector3(
                target.getX(),
                target.getY() + amount,
                target.getZ()
        );

        System.out.println("Вверх на " + amount + ". Позиция: " + position);
    }

    public void moveDown(double multiplier) {
        double amount = moveSpeed * multiplier;

        position = new Vector3(
                position.getX(),
                position.getY() - amount,
                position.getZ()
        );
        target = new Vector3(
                target.getX(),
                target.getY() - amount,
                target.getZ()
        );

        System.out.println("Вниз на " + amount + ". Позиция: " + position);
    }

    // Вращение камеры мышью
    public void rotate(double deltaX, double deltaY) {
        // Обновляем углы
        yaw += deltaX * rotationSpeed;
        pitch += deltaY * rotationSpeed;

        // Ограничиваем вертикальный угол (не даем камере перевернуться)
        pitch = Math.max(-89.0, Math.min(89.0, pitch));

        // Обновляем цель на основе новых углов
        updateTargetFromAngles();

        System.out.println("Вращение: yaw=" + yaw + "°, pitch=" + pitch + "°");
    }

    // Вращение стрелками
    public void rotateHorizontal(double angleDegrees) {
        yaw += angleDegrees;
        updateTargetFromAngles();
    }

    public void rotateVertical(double angleDegrees) {
        pitch += angleDegrees;
        pitch = Math.max(-89.0, Math.min(89.0, pitch));
        updateTargetFromAngles();
    }

    // Зум через изменение FOV
    public void zoomIn() {
        fov = Math.max(Math.toRadians(10.0), fov - Math.toRadians(5.0));
        System.out.println("Зум IN. Новый FOV: " + Math.toDegrees(fov) + "°");
    }

    public void zoomOut() {
        fov = Math.min(Math.toRadians(120.0), fov + Math.toRadians(5.0));
        System.out.println("Зум OUT. Новый FOV: " + Math.toDegrees(fov) + "°");
    }

    // Движение к цели/от цели (альтернативный зум)
    public void zoomTowardTarget() {
        Vector3 direction = target.sub(position).normalize();
        double distance = target.sub(position).length();
        double amount = Math.min(distance * 0.1, moveSpeed);

        position = position.add(direction.mul(amount));
        System.out.println("Движение к цели на " + amount + ". Позиция: " + position);
    }

    public void zoomAwayFromTarget() {
        Vector3 direction = target.sub(position).normalize();
        double amount = moveSpeed;

        position = position.sub(direction.mul(amount));
        target = target.sub(direction.mul(amount));
        System.out.println("Движение от цели на " + amount + ". Позиция: " + position);
    }

    // Сброс камеры
    public void reset() {
        position = new Vector3(100, 100, 300);
        target = new Vector3(0, 0, 0);
        fov = Math.toRadians(60.0);
        yaw = 0.0;
        pitch = 0.0;
        updateYawPitchFromTarget();
        System.out.println("Камера сброшена!");
    }

    // Режимы просмотра
    public void setFrontView() {
        position = new Vector3(0, 0, 500);
        target = new Vector3(0, 0, 0);
        yaw = 0.0;
        pitch = 0.0;
        System.out.println("Вид спереди");
    }

    public void setSideView() {
        position = new Vector3(500, 0, 0);
        target = new Vector3(0, 0, 0);
        yaw = -90.0;
        pitch = 0.0;
        System.out.println("Вид сбоку");
    }

    public void setTopView() {
        position = new Vector3(0, 500, 0);
        target = new Vector3(0, 0, 0);
        yaw = 0.0;
        pitch = -90.0;
        System.out.println("Вид сверху");
    }

    // Геттеры
    public Vector3 getPosition() { return position; }
    public Vector3 getTarget() { return target; }
    public double getFov() { return Math.toDegrees(fov); }
    public double getYaw() { return yaw; }
    public double getPitch() { return pitch; }

    public Matrix4 getViewMatrix() {
        return GraphicConveyor.lookAt(position, target, WORLD_UP);
    }

    public Matrix4 getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}