package finalProject;

/*******************************************************************
* Name of program: GraphicsFrame
* PROGRAMMER: Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignment 4 (Graphics)
*
* DESCRIPTION:
* This class creates a separate window for graphics output.
* It implements a Double Buffering strategy to prevent flickering during
* animations. It maintains two lists of shapes (current vs pending)
* and tracks the window's state (open/closed) to control the interpreter.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Assisted by Artificial Intelligence.
*******************************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

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

    /**********************************************************
    * METHOD: GraphicsFrame (Constructor)
    * DESCRIPTION: Sets up the JFrame, the drawing canvas panel,
    * and window listeners.
    * PARAMETERS: None
    * RETURN VALUE: N/A
    **********************************************************/
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
    
    /**********************************************************
    * METHOD: setVisible
    * DESCRIPTION: Overrides standard setVisible to track 'open' state.
    * PARAMETERS: boolean b - True to show, False to hide
    * RETURN VALUE: void
    **********************************************************/
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) open = true;
    }
    
    /**********************************************************
    * METHOD: isOpen
    * DESCRIPTION: Checks if the window is currently considered open.
    * PARAMETERS: None
    * RETURN VALUE: boolean - True if open
    **********************************************************/
    public boolean isOpen() {
        return open;
    }

    /**********************************************************
    * METHOD: reset
    * DESCRIPTION: Completely resets the graphics state (buffers and color).
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    public void reset() {
        synchronized(this) {
            pendingShapes.clear();
            currentShapes.clear();
        }
        currentColor = Color.BLACK;
        canvas.repaint();
    }

    /**********************************************************
    * METHOD: clear
    * DESCRIPTION: Clears the pending buffer (background logic), 
    * but does NOT wipe the screen immediately.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    public void clear() {
        synchronized(this) {
            pendingShapes.clear();
        }
        currentColor = Color.BLACK;
    }
    
    /**********************************************************
    * METHOD: refresh
    * DESCRIPTION: Swaps the pending buffer to the current buffer
    * and triggers a repaint (updates screen).
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    public void refresh() {
        synchronized(this) {
            currentShapes = new ArrayList<>(pendingShapes);
        }
        canvas.repaint();
    }

    /**********************************************************
    * METHOD: setCurrentColor
    * DESCRIPTION: Sets the drawing color for subsequent shapes.
    * PARAMETERS: int r, int g, int b - RGB values
    * RETURN VALUE: void
    **********************************************************/
    public void setCurrentColor(int r, int g, int b) {
        this.currentColor = new Color(r, g, b);
    }

    /**********************************************************
    * METHOD: addCircle
    * DESCRIPTION: Adds a circle command to the pending buffer.
    * PARAMETERS: int x, int y, int radius - Geometry
    * RETURN VALUE: void
    **********************************************************/
    public void addCircle(int x, int y, int radius) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            });
        }
    }

    /**********************************************************
    * METHOD: addRect
    * DESCRIPTION: Adds a rectangle command to the pending buffer.
    * PARAMETERS: int x, int y, int w, int h - Geometry
    * RETURN VALUE: void
    **********************************************************/
    public void addRect(int x, int y, int w, int h) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.fillRect(x, y, w, h);
            });
        }
    }

    /**********************************************************
    * METHOD: addLine
    * DESCRIPTION: Adds a line command to the pending buffer.
    * PARAMETERS: int x1, int y1, int x2, int y2 - Endpoints
    * RETURN VALUE: void
    **********************************************************/
    public void addLine(int x1, int y1, int x2, int y2) {
        Color c = currentColor;
        synchronized(this) {
            pendingShapes.add(g -> {
                g.setColor(c);
                g.drawLine(x1, y1, x2, y2);
            });
        }
    }

    /**********************************************************
    * METHOD: addTriangle
    * DESCRIPTION: Adds a triangle command to the pending buffer.
    * PARAMETERS: int x1, y1, x2, y2, x3, y3 - Vertices
    * RETURN VALUE: void
    **********************************************************/
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