package finalProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphics Window.
 * UPDATED: Tracks 'isOpen' state to allow the Interpreter to stop execution if the user closes the window.
 */
public class GraphicsFrame extends JFrame {
    
    private interface ShapeCommand {
        void draw(Graphics2D g2d);
    }

    private List<ShapeCommand> currentShapes = new ArrayList<>();
    private List<ShapeCommand> pendingShapes = new ArrayList<>();
    
    private JPanel canvas;
    private Color currentColor = Color.BLACK;
    
    // Track if the window is logically "open" to control interpreter execution
    private boolean open = false;

    public GraphicsFrame() {
        setTitle("Graphics Output Canvas");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
        
        // Detect user closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                open = false;
            }
        });
        
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setStroke(new BasicStroke(2)); 
                
                List<ShapeCommand> toDraw;
                synchronized(GraphicsFrame.this) {
                    toDraw = currentShapes;
                }
                
                if (toDraw != null) {
                    for (ShapeCommand cmd : toDraw) {
                        cmd.draw(g2d);
                    }
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        add(canvas);
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) open = true;
    }
    
    public boolean isOpen() {
        return open;
    }

    /**
     * Completely resets the graphics state.
     */
    public void reset() {
        synchronized(this) {
            pendingShapes.clear();
            currentShapes.clear();
        }
        currentColor = Color.BLACK;
        // Do not force visibility here
        canvas.repaint();
    }

    public void clear() {
        synchronized(this) {
            pendingShapes.clear();
        }
        currentColor = Color.BLACK;
    }
    
    public void refresh() {
        synchronized(this) {
            currentShapes = new ArrayList<>(pendingShapes);
        }
        canvas.repaint();
    }

    public void setCurrentColor(int r, int g, int b) {
        this.currentColor = new Color(r, g, b);
    }

    public void addCircle(int x, int y, int radius) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            });
        }
    }

    public void addRect(int x, int y, int w, int h) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.fillRect(x, y, w, h);
            });
        }
    }

    public void addLine(int x1, int y1, int x2, int y2) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.drawLine(x1, y1, x2, y2);
            });
        }
    }

    public void addTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
            });
        }
    }
}