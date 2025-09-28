import java.awt.*;

class gunSight {
    protected int x, y;
    protected Color color;

    public gunSight(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void draw(Graphics2D g, int centerX, int centerY) {
        g.setColor(color);
    }
}

class Rectangle extends gunSight {
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

class Circle extends gunSight {
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

class CircleFrames extends gunSight {
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

class CircleLittleFrames extends gunSight {
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
