import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Rectangle;

public class Player {
    private int x, y;
    private int width, height;
    private int dx;
    private Image currentImg;

    private Image imgDefault;
    private Image imgRunRight;
    private Image imgRunLeft;
    private Image imgShooting;

    private final int SPEED = 7;

    public enum State {
        DEFAULT,
        RUN_RIGHT,
        RUN_LEFT,
        SHOOT
    }

    private State state = State.DEFAULT;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;

        imgDefault = Toolkit.getDefaultToolkit().getImage("assets/player.png");
        imgRunRight = Toolkit.getDefaultToolkit().getImage("assets/runningRight.png");
        imgRunLeft = Toolkit.getDefaultToolkit().getImage("assets/runningLeft.png");
        imgShooting = Toolkit.getDefaultToolkit().getImage("assets/shooting.png");

        width = 64;
        height = 64;

        currentImg = imgDefault;
    }

    public void move() {
        x += dx;
        if (x <= 0) {
            x = 0;
        }
        if (x + width > GamePanel.PANEL_WIDTH) {
            x = GamePanel.PANEL_WIDTH - width;
        }
    }

    public void keyPressed(int keyCode) {
        if (keyCode == java.awt.event.KeyEvent.VK_LEFT
                || keyCode == java.awt.event.KeyEvent.VK_A) {
            dx = -SPEED;
            setState(State.RUN_LEFT);
        } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT ||
                keyCode == java.awt.event.KeyEvent.VK_D) {
            dx = SPEED;
            setState(State.RUN_RIGHT);
        }
    }

    public void keyReleased(int keyCode) {
        if (keyCode == java.awt.event.KeyEvent.VK_UP) {
            setState(State.DEFAULT);
        }
        if (keyCode == java.awt.event.KeyEvent.VK_RIGHT ||
                keyCode == java.awt.event.KeyEvent.VK_LEFT) {
            dx = 0;
            setState(State.DEFAULT);
        }

        if (keyCode == java.awt.event.KeyEvent.VK_D ||
                keyCode == java.awt.event.KeyEvent.VK_A) {
            dx = 0;
            setState(State.DEFAULT);
        }
        if (keyCode == java.awt.event.KeyEvent.VK_W) {
            setState(State.DEFAULT);
        }

    }

    public void shoot() {
        setState(State.SHOOT);
    }

    public void setState(State newState) {
        this.state = newState;
        switch (state) {
            case DEFAULT:
                currentImg = imgDefault;
                break;
            case RUN_RIGHT:
                currentImg = imgRunRight;
                break;
            case RUN_LEFT:
                currentImg = imgRunLeft;
                break;
            case SHOOT:
                currentImg = imgShooting;
                break;
            default:
                currentImg = imgDefault;
                break;
        }
    }

    public Image getImage() {
        return currentImg;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public State getState() {
        return this.state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
