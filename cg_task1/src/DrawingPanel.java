import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

class DrawingPanel extends JPanel implements ActionListener {
    private Duck duck;
    private gunSight[] gunSight;
    private Cloud[] clouds;
    private int cursorX = 400;
    private int cursorY = 400;
    private Random random = new Random();
    private final Timer timer = new Timer(16, this);
    private final Timer hitTimer = new Timer(2000, timerEvent -> resetHit());
    private int ticksFromStart = 0;
    private boolean isHit = false;
    private int score = 0;
    private Font scoreFont = new Font("Arial", Font.BOLD, 36);
    private boolean showDebug = true;
    private int currentDuckX = -50;
    private int currentDuckY = 200;

    public DrawingPanel() {
        timer.start();
        hitTimer.setRepeats(false);
        setBackground(new Color(135, 206, 235));
        createGunSight();
        createClouds();
        setupMouseListeners();
        updateDuck();
        setCursor(getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(),
                "hidden"
        ));
    }

    private void createClouds() {
        clouds = new Cloud[]{
                new Cloud(random.nextInt(1800), random.nextInt(400)),
                new Cloud(random.nextInt(1800), random.nextInt(400)),
                new Cloud(random.nextInt(1800), random.nextInt(400)),
                new Cloud(random.nextInt(1800), random.nextInt(400)),
                new Cloud(random.nextInt(1800), random.nextInt(400))
        };
    }

    private void updateDuck() {
        if (currentDuckX >= 1800) {
            currentDuckX = -50;
            currentDuckY = 100 + random.nextInt(500);
        }
        currentDuckX += 25;
        duck = new Duck(currentDuckX, currentDuckY);
    }

    private void createGunSight() {
        gunSight = new gunSight[]{
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
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    checkHit(e.getX(), e.getY());
                }
            }
        });
        setFocusable(true);
    }

    private void checkHit(int mouseX, int mouseY) {
        int checkDuckX = currentDuckX;
        int checkDuckY = currentDuckY;
        System.out.println("=== ПРОВЕРКА ПОПАДАНИЯ ===");
        System.out.println("Утка: " + checkDuckX + ", " + checkDuckY);
        System.out.println("Курсор: " + mouseX + ", " + mouseY);
        boolean hit = false;
        // Проверка попадания в тело
        if (mouseX >= (checkDuckX - 45) && mouseX <= (checkDuckX + 90) &&
                mouseY >= (checkDuckY - 30) && mouseY <= (checkDuckY + 30)) {
            hit = true;
            System.out.println("Попадание в тело!");
        }
        // Проверка попадания в голову
        if (!hit && mouseX >= (checkDuckX + 20) && mouseX <= (checkDuckX + 65) &&
                mouseY >= (checkDuckY - 30) && mouseY <= (checkDuckY + 15)) {
            hit = true;
            System.out.println("Попадание в голову!");
        }
        if (hit) {
            isHit = true;
            score++;
            hitTimer.restart();
            System.out.println("*** ПОПАЛ! Счет: " + score + " ***");
        } else {
            System.out.println("Промах");
        }
        repaint();
    }

    private void resetHit() {
        isHit = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (Cloud cloud : clouds) {
            cloud.draw(g2d);
        }
        duck.draw(g2d);
        Color sightColor = isHit ? Color.GREEN : Color.RED;
        for (gunSight gunSight : gunSight) {
            Color originalColor = gunSight.color;
            gunSight.color = sightColor;
            gunSight.draw(g2d, cursorX, cursorY);
            gunSight.color = originalColor;
        }
        drawScore(g2d);

//        // ОТЛАДКА
//        if (showDebug) {
//            g2d.setColor(Color.RED);
//            g2d.setStroke(new BasicStroke(2));
//
//            // Основной хитбокс (тело + голова)
//            g2d.drawRect(currentDuckX - 45, currentDuckY - 30, 135, 60);
//
//            // Отдельно тело
//            g2d.setColor(Color.BLUE);
//            g2d.drawRect(currentDuckX - 35, currentDuckY - 20, 70, 40);
//
//            // Отдельно голова
//            g2d.setColor(Color.GREEN);
//            g2d.drawRect(currentDuckX + 25, currentDuckY - 25, 35, 35);
//
//            g2d.setColor(Color.BLACK);
//            g2d.setFont(new Font("Arial", Font.BOLD, 16));
//            g2d.drawString("Утка: " + currentDuckX + ", " + currentDuckY, 10, 20);
//            g2d.drawString("Стреляй в КРАСНУЮ рамку!", 10, 40);
//        }
    }

    private void drawScore(Graphics2D g) {
        g.setFont(scoreFont);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(getWidth() - 220, 15, 190, 70, 15, 15);
        g.setColor(Color.WHITE);
        g.drawString("Очки: " + score, getWidth() - 180, 55);
        if (isHit) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("ПОПАЛ!", getWidth() - 180, 85);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            updateDuck();
            repaint();
            ticksFromStart++;
        }
    }
}