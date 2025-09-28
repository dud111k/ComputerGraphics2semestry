import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Drawing Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        DrawingPanel panel = new DrawingPanel();
        frame.add(panel);
        frame.setVisible(true);
    }
}