// ImageElement.java
package SM_PAINT;

import java.awt.*;
import java.awt.image.BufferedImage;

// Class to represent inserted images with resizing and cropping capabilities
public class ImageElement {
    private BufferedImage image;
    private int x, y;
    private int width, height;
    
    public ImageElement(BufferedImage image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public ImageElement(GifImage gif, int x, int y, int width, int height) {
        this.image = gif.getCurrentFrame();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean isGif() {
        return false; // Simplified - no animation
    }
    
    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }
    
    public int getResizeHandleAt(int pointX, int pointY, int handleSize) {
        // Check each corner for resize handle
        if (Math.abs(pointX - x) <= handleSize && Math.abs(pointY - y) <= handleSize) return 0; // Top-left
        if (Math.abs(pointX - (x + width)) <= handleSize && Math.abs(pointY - y) <= handleSize) return 1; // Top-right
        if (Math.abs(pointX - x) <= handleSize && Math.abs(pointY - (y + height)) <= handleSize) return 2; // Bottom-left
        if (Math.abs(pointX - (x + width)) <= handleSize && Math.abs(pointY - (y + height)) <= handleSize) return 3; // Bottom-right
        return -1; // Not on a handle
    }
    
    public boolean advanceFrame() {
        return false; // No animation
    }
    
    public void resize(int handle, int newX, int newY) {
        switch (handle) {
            case 0: // Top-left
                width += x - newX;
                height += y - newY;
                x = newX;
                y = newY;
                break;
            case 1: // Top-right
                width = newX - x;
                height += y - newY;
                y = newY;
                break;
            case 2: // Bottom-left
                width += x - newX;
                height = newY - y;
                x = newX;
                break;
            case 3: // Bottom-right
                width = newX - x;
                height = newY - y;
                break;
        }
        
        // Ensure minimum size
        width = Math.max(width, 10);
        height = Math.max(height, 10);
    }
    
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public void adjustForCrop(int cropX, int cropY) {
        x -= cropX;
        y -= cropY;
    }
    
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }
    
    public void drawSelection(Graphics g, int handleSize) {
        // Draw selection rectangle
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                      0, new float[]{5}, 0)); // Dashed line
        g2d.drawRect(x, y, width, height);
        
        // Draw resize handles
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.fillRect(x - handleSize/2, y - handleSize/2, handleSize, handleSize); // Top-left
        g2d.fillRect(x + width - handleSize/2, y - handleSize/2, handleSize, handleSize); // Top-right
        g2d.fillRect(x - handleSize/2, y + height - handleSize/2, handleSize, handleSize); // Bottom-left
        g2d.fillRect(x + width - handleSize/2, y + height - handleSize/2, handleSize, handleSize); // Bottom-right
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - handleSize/2, y - handleSize/2, handleSize, handleSize); // Top-left
        g2d.drawRect(x + width - handleSize/2, y - handleSize/2, handleSize, handleSize); // Top-right
        g2d.drawRect(x - handleSize/2, y + height - handleSize/2, handleSize, handleSize); // Bottom-left
        g2d.drawRect(x + width - handleSize/2, y + height - handleSize/2, handleSize, handleSize); // Bottom-right
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public BufferedImage getImage() { return image; }
}