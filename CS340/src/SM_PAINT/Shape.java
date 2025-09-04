// Shape.java (updated)
package SM_PAINT;

import java.awt.*;

// Shape class to represent drawn elements
class Shape {
    private String type;
    private int startX, startY, endX, endY;
    private Color color;
    private int thickness;
    
    public Shape(String type, int startX, int startY, int endX, int endY, Color color, int thickness) {
        this.type = type;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
        this.thickness = thickness;
    }
    
    public void setEnd(int endX, int endY) {
        this.endX = endX;
        this.endY = endY;
    }
    
    public void adjustForCrop(int cropX, int cropY) {
        startX -= cropX;
        startY -= cropY;
        endX -= cropX;
        endY -= cropY;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        
        switch (type) {
            case "Line":
                g2d.drawLine(startX, startY, endX, endY);
                break;
            case "Rectangle":
                g2d.drawRect(Math.min(startX, endX), Math.min(startY, endY), 
                            Math.abs(endX - startX), Math.abs(endY - startY));
                break;
            case "Oval":
                g2d.drawOval(Math.min(startX, endX), Math.min(startY, endY), 
                            Math.abs(endX - startX), Math.abs(endY - startY));
                break;
        }
    }
    
    public boolean contains(int pointX, int pointY) {
        switch (type) {
            case "Line":
                // Simple line hit detection - check if point is near the line
                return isPointNearLine(pointX, pointY, startX, startY, endX, endY, thickness + 5);
            case "Rectangle":
                int rectX = Math.min(startX, endX);
                int rectY = Math.min(startY, endY);
                int rectWidth = Math.abs(endX - startX);
                int rectHeight = Math.abs(endY - startY);
                return pointX >= rectX && pointX <= rectX + rectWidth &&
                       pointY >= rectY && pointY <= rectY + rectHeight;
            case "Oval":
                int ovalX = Math.min(startX, endX);
                int ovalY = Math.min(startY, endY);
                int ovalWidth = Math.abs(endX - startX);
                int ovalHeight = Math.abs(endY - startY);
                // Check if point is inside the oval
                double centerX = ovalX + ovalWidth / 2.0;
                double centerY = ovalY + ovalHeight / 2.0;
                double xRadius = ovalWidth / 2.0;
                double yRadius = ovalHeight / 2.0;
                return Math.pow((pointX - centerX) / xRadius, 2) + 
                       Math.pow((pointY - centerY) / yRadius, 2) <= 1;
            default:
                return false;
        }
    }
    
    private boolean isPointNearLine(int px, int py, int x1, int y1, int x2, int y2, int tolerance) {
        // Calculate the distance from the point to the line
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        if (lineLength == 0) return false; // Line is a point
        
        double distance = Math.abs((x2 - x1) * (y1 - py) - (x1 - px) * (y2 - y1)) / lineLength;
        return distance <= tolerance;
    }
    
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public String getType() { return type; }
    public Color getColor() { return color; }
    public int getThickness() { return thickness; }
}