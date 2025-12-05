package finalProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphics Window.
 * FIXED: Implements Double Buffering to completely eliminate flickering.
 * Uses 'pendingShapes' for building the frame and 'currentShapes' for displaying it.
 */
public class GraphicsFrame extends JFrame {
    
    private interface ShapeCommand {
        void draw(Graphics2D g2d);
    }

    // Two lists: One for the screen (current), one for the logic (pending)
    private List<ShapeCommand> currentShapes = new ArrayList<>();
    private List<ShapeCommand> pendingShapes = new ArrayList<>();
    
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
                g2d.setStroke(new BasicStroke(2)); 
                
                // THREAD SAFETY: Only read the stable 'currentShapes' list
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

    /**
     * Clears the pending buffer but DOES NOT wipe the screen yet.
     * This prevents the "White Flash" between frames.
     */
    public void clear() {
        synchronized(this) {
            pendingShapes.clear();
        }
        currentColor = Color.BLACK;
    }
    
    /**
     * Updates the screen with the latest drawing commands.
     * Swaps the pending buffer to the current buffer.
     */
    public void refresh() {
        synchronized(this) {
            // Atomic Swap: pending -> current
            currentShapes = new ArrayList<>(pendingShapes);
        }
        canvas.repaint();
    }

    public void setCurrentColor(int r, int g, int b) {
        this.currentColor = new Color(r, g, b);
    }

    // --- Drawing Commands ---
    // NOTE: These now only add to the buffer. 
    // The screen will not update until refresh() (triggered by sleep) is called.

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