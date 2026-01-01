import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicLabelUI;

public class Bubble {
    private int x, y;
    private int width, height;
    private double dx, dy;
    private double VelMultiplier = 2;
    private Image img;
    private String type;

    private final double GRAVITY = 0.5;
    private final int BOUNCE_SPEED = -15;

    public Bubble(int startX, int startY, String type, double VelMultiplier) {
        this.x = startX;
        this.y = startY;
        this.type = type;
        this.VelMultiplier = VelMultiplier;
        initProperties(type);
    }

    private void initProperties(String type) {
        switch (type) {
            case "veryLarge":
                img = Toolkit.getDefaultToolkit().getImage("assets/bubble_veryLarge.png");
                width = 96;
                height = 96;
                dx = 1*VelMultiplier;
                dy = 0;
                break;
            case "large":
                img = Toolkit.getDefaultToolkit().getImage("assets/bubble_large.png");
                width = 64;
                height = 64;
                dx = 2*VelMultiplier;
                dy = 0;
                break;
            case "medium":
                img = Toolkit.getDefaultToolkit().getImage("assets/bubble_medium.png");
                width = 48;
                height = 48;
                dx = 3*VelMultiplier;
                dy = 0;
                break;
            case "small":
                img = Toolkit.getDefaultToolkit().getImage("assets/bubble_small.png");
                width = 32;
                height = 32;
                dx = 4*VelMultiplier;
                dy = 0;
                break;
            default:
                img = Toolkit.getDefaultToolkit().getImage("assets/bubble_large.png");
                width = 64;
                height = 64;
                dx = 4;
                dy = 0;
                this.type = "large";
                break;
        }
    }

    public void move() {
        dy += GRAVITY;
        x += dx;
        y += dy;

        if (x < 0) {
            x = 0;
            dx = -dx;
        }
        if (x + width > GamePanel.PANEL_WIDTH) {
            x = GamePanel.PANEL_WIDTH - width;
            dx = -dx;
        }

        if (y + height >= 384) {
            y = 384 - height;  
            dy = BOUNCE_SPEED; 
        }
    }

    public ArrayList<Bubble> split() {
        ArrayList<Bubble> pieces = new ArrayList<>();
        String nextType = null;

        switch (type) {
            case "veryLarge": nextType = "large"; break;
            case "large":     nextType = "medium"; break;
            case "medium":    nextType = "small"; break;
            case "small":
                return pieces;
        }

        Bubble b1 = new Bubble(x, y, nextType,VelMultiplier);
        Bubble b2 = new Bubble(x, y, nextType,VelMultiplier);

        b1.setDx(Math.abs(b1.getDx()));
        b2.setDx(-Math.abs(b2.getDx()));
        b1.setDy(BOUNCE_SPEED / 2);
        b2.setDy(BOUNCE_SPEED / 2);

        pieces.add(b1);
        pieces.add(b2);
        return pieces;
    }

    public double getDx() {
        return dx;
    }
    public void setDx(double dx) {
        this.dx = dx;
    }
    public double getDy() {
        return dy;
    }
    public void setDy(double dy) {
        this.dy = dy;
    }

    public int getX() {
        return x;
    }
    public void setX(int newX) {
        this.x = newX;
    }
    public int getY() {
        return y;
    }
    public void setY(int newY) {
        this.y = newY;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Image getImage() {
        return img;
    }
    public String getType() {
        return type;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
