import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

class DrawingPanel extends JPanel {
    private Shape[] shapes;
    private int cursorX = 400;
    private int cursorY = 400;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        createShapes();
        setupMouseListeners();

        setCursor(getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(),
                "hidden"
        ));
    }

    private void createShapes() {
        shapes = new Shape[]{
                new Circle(0, 0, 20, Color.RED),
                new CircleFrames(0, 0, 100, Color.RED),
                new CircleLittleFrames(0, 0, 50, Color.RED),
                new Rectangle(-140, -5, 110, 10, Color.RED),
                new Rectangle(30, -5, 110, 10, Color.RED),
                new Rectangle(-5, -140, 10, 110, Color.RED),
                new Rectangle(-5, 30, 10, 110, Color.RED)
        };
    }

    private void setupMouseListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                cursorX = e.getX();
                cursorY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // Преобразуем в Graphics2D

        for (Shape shape : shapes) {
            shape.draw(g2d, cursorX, cursorY); // Передаем Graphics2D
        }
    }
}
