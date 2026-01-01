import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Bullet {
    private int x, y;           
    private int width = 30;
    private int height = 0;
    private final int speed = 10; 
    
    private Image bulletImg;

    public Bullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;

        bulletImg = Toolkit.getDefaultToolkit().getImage("assets/bullet.png");
    }

    public void move() {
        y -= speed;
        if(height<350) {
        	height += 10; //yukarı doğru uzayan bullet image
        }
    }
    
    public boolean isOffScreen() {
        return y + height < 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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
    public Image getImage() {
    	return bulletImg;
    }

}
