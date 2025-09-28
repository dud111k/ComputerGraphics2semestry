import java.awt.*;

class Duck {
    private int x, y;
    private Color bodyColor;

    public Duck(int x, int y) {
        this.x = x;
        this.y = y;
        this.bodyColor = new Color(160, 120, 40);
    }

    public void draw(Graphics2D g) {
        // тело
        g.setColor(bodyColor);
        g.fillOval(x - 35, y - 20, 70, 40);

        // голова
        g.setColor(bodyColor);
        g.fillOval(x + 25, y - 25, 35, 35);

        // клюв
        g.setColor(Color.ORANGE);
        int[] beakX = {x + 55, x + 75, x + 55};
        int[] beakY = {y - 7, y, y + 7};
        g.fillPolygon(beakX, beakY, 3);

        // глаз
        g.setColor(Color.WHITE);
        g.fillOval(x + 35, y - 18, 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval(x + 37, y - 16, 8, 8);

        // крыло
        g.setColor(new Color(140, 100, 30));
        g.fillOval(x - 15, y - 15, 40, 25);

        // хвост
        g.setColor(new Color(140, 100, 30));
        int[] tailX = {x - 45, x - 30, x - 30};
        int[] tailY = {y, y - 12, y + 12};
        g.fillPolygon(tailX, tailY, 3);
    }
}