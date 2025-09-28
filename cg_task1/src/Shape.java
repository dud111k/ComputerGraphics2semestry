//import java.awt.*;
//
//class Shape {
//    protected int relX, relY;
//    protected Color color;
//
//    public Shape(int relX, int relY, Color color) {
//        this.relX = relX;
//        this.relY = relY;
//        this.color = color;
//    }
//
//    public void draw(Graphics2D g, int centerX, int centerY) { // Принимаем Graphics2D
//        g.setColor(color);
//    }
//}
//
//class Circle extends Shape {
//    private int radius;
//
//    public Circle(int relX, int relY, int radius, Color color) {
//        super(relX, relY, color);
//        this.radius = radius;
//    }
//
//    @Override
//    public void draw(Graphics2D g, int centerX, int centerY) {
//        super.draw(g, centerX, centerY);
//        g.fillOval(centerX + relX - radius, centerY + relY - radius, radius * 2, radius * 2);
//    }
//}
//
//class CircleFrames extends Shape {
//    private int radius;
//
//    public CircleFrames(int relX, int relY, int radius, Color color) {
//        super(relX, relY, color);
//        this.radius = radius;
//    }
//
//    @Override
//    public void draw(Graphics2D g, int centerX, int centerY) {
//        super.draw(g, centerX, centerY);
//        g.drawOval(centerX + relX - radius, centerY + relY - radius, radius * 2, radius * 2);
//    }
//}
//
//class CircleLittleFrames extends Shape {
//    private int radius;
//
//    public CircleLittleFrames(int relX, int relY, int radius, Color color) {
//        super(relX, relY, color);
//        this.radius = radius;
//    }
//
//    @Override
//    public void draw(Graphics2D g, int centerX, int centerY) {
//        super.draw(g, centerX, centerY);
//        g.drawOval(centerX + relX - radius, centerY + relY - radius, radius * 2, radius * 2);
//    }
//}
//
//class Rectangle extends Shape {
//    private int width, height;
//
//    public Rectangle(int relX, int relY, int width, int height, Color color) {
//        super(relX, relY, color);
//        this.width = width;
//        this.height = height;
//    }
//
//    @Override
//    public void draw(Graphics2D g, int centerX, int centerY) {
//        super.draw(g, centerX, centerY);
//        g.fillRect(centerX + relX, centerY + relY, width, height);
//    }
//}
import java.awt.*;

// Базовый класс для всех фигур
class Shape {
    protected int x, y;
    protected Color color;

    public Shape(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void draw(Graphics2D g, int centerX, int centerY) {
        g.setColor(color);
    }
}

class Rectangle extends Shape {
    private int width, height;

    public Rectangle(int x, int y, int width, int height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g, int centralX, int centralY) {
        super.draw(g, centralX, centralY);
        g.fillRect(centralX + x, centralY + y, width, height);
    }
}

// Класс круга
class Circle extends Shape {
    private int radius;

    public Circle(int x, int y, int radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g, int centralX, int centralY) {
        super.draw(g, centralX, centralY);
        g.fillOval(centralX + x - radius,centralY + y - radius, radius * 2, radius * 2);
    }
}

class CircleFrames extends Shape {
    private int radius;

    public CircleFrames(int x,int y, int radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g, int centralX, int centralY) {
        super.draw(g, centralX, centralY);
        g.setStroke(new BasicStroke(15));
        g.drawOval(centralX + x - radius,centralY + y - radius, radius * 2, radius * 2);
    }
}

class CircleLittleFrames extends Shape {
    private int radius;
    public CircleLittleFrames(int x, int y, int radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g, int centralX, int centralY){
        super.draw(g, centralX, centralY);
        g.setStroke(new BasicStroke(7));
        g.drawOval(centralX + x - radius,centralY + y - radius, radius * 2, radius * 2);
    }
}

////// Класс треугольника
////class Triangle extends Shape {
////    private int size;
////
////    public Triangle(int x, int y, int size, Color color) {
////        super(x, y, color);
////        this.size = size;
////    }
////
////    @Override
////    public void draw(Graphics g) {
////        super.draw(g);
////        int[] xPoints = {x, x - size, x + size};
////        int[] yPoints = {y, y + size, y + size};
////        g.fillPolygon(xPoints, yPoints, 3);
////    }
////}