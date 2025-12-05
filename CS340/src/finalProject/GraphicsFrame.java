package finalProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicsFrame extends JFrame {
    
    private interface ShapeCommand {
        void draw(Graphics2D g2d);
    }

    private List<ShapeCommand> shapes = new ArrayList<>();
    private JPanel canvas;
    private Color currentColor = Color.BLACK;

    public GraphicsFrame() {
        setTitle("Graphics Output Canvas");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
        
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setStroke(new BasicStroke(2)); // Thicker lines
                
                for (ShapeCommand cmd : shapes) {
                    cmd.draw(g2d);
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        add(canvas);
    }

    public void clear() {
        shapes.clear();
        currentColor = Color.BLACK;
        canvas.repaint();
    }

    public void setCurrentColor(int r, int g, int b) {
        this.currentColor = new Color(r, g, b);
    }

    public void addCircle(int x, int y, int radius) {
        Color c = currentColor;
        shapes.add(g -> {
            g.setColor(c);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        });
        canvas.repaint();
    }

    public void addRect(int x, int y, int w, int h) {
        Color c = currentColor;
        shapes.add(g -> {
            g.setColor(c);
            g.fillRect(x, y, w, h);
        });
        canvas.repaint();
    }

    public void addLine(int x1, int y1, int x2, int y2) {
        Color c = currentColor;
        shapes.add(g -> {
            g.setColor(c);
            g.drawLine(x1, y1, x2, y2);
        });
        canvas.repaint();
    }
}