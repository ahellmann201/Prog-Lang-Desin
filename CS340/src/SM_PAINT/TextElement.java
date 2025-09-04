// TextElement.java
package SM_PAINT;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;

public class TextElement {
    private String text;
    private int x, y;
    private Color color;
    private Font font;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private int fontSize;
    private String fontName;
    private Rectangle bounds;
    
    public TextElement(String text, int x, int y, Color color, int fontSize, 
                      String fontName, boolean bold, boolean italic, boolean underline) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        updateFont();
        calculateBounds();
    }
    
    private void updateFont() {
        int style = Font.PLAIN;
        if (bold) style |= Font.BOLD;
        if (italic) style |= Font.ITALIC;
        
        this.font = new Font(fontName, style, fontSize);
    }
    
    private void calculateBounds() {
        if (text == null || text.isEmpty()) {
            bounds = new Rectangle(x, y - fontSize, 0, fontSize);
            return;
        }
        
        // Create a temporary graphics context to calculate text bounds
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        FontMetrics metrics = g2d.getFontMetrics(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        bounds = new Rectangle(x, y - metrics.getAscent(), width, height);
        g2d.dispose();
    }
    
    public void adjustForCrop(int cropX, int cropY) {
        x -= cropX;
        y -= cropY;
        bounds.setLocation(x, y - bounds.height);
    }
    
    public void draw(Graphics g) {
        if (text == null || text.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Create attributed string for underline support
        AttributedString attributedText = new AttributedString(text);
        attributedText.addAttribute(TextAttribute.FONT, font);
        attributedText.addAttribute(TextAttribute.FOREGROUND, color);
        
        if (underline) {
            attributedText.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        
        g2d.drawString(attributedText.getIterator(), x, y);
    }
    
    public boolean contains(int pointX, int pointY) {
        return bounds.contains(pointX, pointY);
    }
    
    public void setPosition(int newX, int newY) {
        int dx = newX - x;
        int dy = newY - y;
        x = newX;
        y = newY;
        bounds.translate(dx, dy);
    }
    
    public void setText(String text) {
        this.text = text;
        calculateBounds();
    }
    
    public void setFontSize(int size) {
        this.fontSize = size;
        updateFont();
        calculateBounds();
    }
    
    public void setBold(boolean bold) {
        this.bold = bold;
        updateFont();
        calculateBounds();
    }
    
    public void setItalic(boolean italic) {
        this.italic = italic;
        updateFont();
        calculateBounds();
    }
    
    public void setUnderline(boolean underline) {
        this.underline = underline;
    }
    
    public void setFontName(String fontName) {
        this.fontName = fontName;
        updateFont();
        calculateBounds();
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public String getText() { return text; }
    public Color getColor() { return color; }
    public int getFontSize() { return fontSize; }
    public String getFontName() { return fontName; }
    public boolean isBold() { return bold; }
    public boolean isItalic() { return italic; }
    public boolean isUnderline() { return underline; }
    public Rectangle getBounds() { return bounds; }
}