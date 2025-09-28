import java.awt.*;

class Cloud {
    private int x, y;
    public Cloud(int x, int y){
        this.x = x;
        this.y = y;
    }


    void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, 80, 60);
        g.fillOval(x + 20, y - 10, 80, 60);
        g.fillOval(x + 60, y + 15, 60, 50);
}
}
