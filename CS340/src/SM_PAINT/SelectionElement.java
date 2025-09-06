// SelectionElement.java
package SM_PAINT;

import java.awt.*;
import java.awt.image.BufferedImage;

// New class to represent a selected area that can be moved
class SelectionElement {
    private BufferedImage image;
    private int x, y;
    private int width, height;
    
    public SelectionElement(BufferedImage image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }
    
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public void adjustForCrop(int cropX, int cropY) {
        x -= cropX;
        y -= cropY;
    }
    
    public void draw(Graphics g) {
        // Draw the selection image
        g.drawImage(image, x, y, width, height, null);
        
        // Draw a dashed border to indicate it's selected
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                      0, new float[]{5}, 0)); // Dashed line
        g2d.drawRect(x, y, width, height);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    // Helper method to get the bounds of the selection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
}