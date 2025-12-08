package assign3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Drawing Panel Component
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Control Structures & Multi-line Editor
 * 
 * DESCRIPTION:
 * This class represents the drawing panel where shapes are rendered.
 * It handles mouse interactions for polygon drawing and displays a
 * crosshair with coordinates for precise drawing.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 * 
 * CREDITS:
 * Java Swing Graphics documentation
 */
public class DrawingPanel extends JPanel {
    private InputOutputHandler3 ioHandler;
    private CodeGeneration3 codeGeneration;
    private Point mousePosition = new Point(-1, -1);
    private Point fixedCrosshairPosition = null;
    private boolean showCrosshair = false;
    
    /********************************************************************
     * METHOD: DrawingPanel Constructor
     * DESCRIPTION: Initializes the drawing panel
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 500));
        setupMouseListeners();
    }
    
    /********************************************************************
     * METHOD: setIOHandler
     * DESCRIPTION: Sets the input/output handler for the drawing panel
     * PARAMETERS: InputOutputHandler3 ioHandler - the IO handler to set
     * RETURN VALUE: None
     ********************************************************************/
    public void setIOHandler(InputOutputHandler3 ioHandler) {
        this.ioHandler = ioHandler;
    }
    
    /********************************************************************
     * METHOD: setCodeGeneration
     * DESCRIPTION: Sets the code generation instance for the drawing panel
     * PARAMETERS: CodeGeneration3 codeGeneration - the code generation to set
     * RETURN VALUE: None
     ********************************************************************/
    public void setCodeGeneration(CodeGeneration3 codeGeneration) {
        this.codeGeneration = codeGeneration;
    }
    
    /********************************************************************
     * METHOD: setupMouseListeners
     * DESCRIPTION: Sets up mouse listeners for crosshair and polygon drawing
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void setupMouseListeners() {
        // Add mouse motion listener to track mouse position
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
                if (fixedCrosshairPosition == null) {
                    showCrosshair = true;
                }
                repaint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint();
                if (fixedCrosshairPosition == null) {
                    showCrosshair = true;
                }
                repaint();
            }
        });
        
        // Add mouse listener to hide crosshair when mouse leaves
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (fixedCrosshairPosition == null) {
                    showCrosshair = false;
                }
                repaint();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                mousePosition = e.getPoint();
                if (fixedCrosshairPosition == null) {
                    showCrosshair = true;
                }
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Set or clear fixed crosshair position on click
                if (fixedCrosshairPosition == null) {
                    // Set fixed crosshair at clicked position
                    fixedCrosshairPosition = e.getPoint();
                    showCrosshair = true;
                    if (ioHandler != null) {
                        ioHandler.appendToHistory("Crosshair fixed at: (" + 
                                fixedCrosshairPosition.x + ", " + 
                                fixedCrosshairPosition.y + ")\n");
                    }
                } else {
                    // Clear fixed crosshair
                    fixedCrosshairPosition = null;
                    showCrosshair = true;
                    if (ioHandler != null) {
                        ioHandler.appendToHistory("Crosshair released\n");
                    }
                }
                repaint();
            }
        });
    }
    
    /********************************************************************
     * METHOD: getFixedCrosshairPosition
     * DESCRIPTION: Returns the fixed crosshair position if set
     * PARAMETERS: None
     * RETURN VALUE: Point - the fixed crosshair position, or null if not set
     ********************************************************************/
    public Point getFixedCrosshairPosition() {
        return fixedCrosshairPosition;
    }
    
    /********************************************************************
     * METHOD: clearFixedCrosshair
     * DESCRIPTION: Clears the fixed crosshair position
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clearFixedCrosshair() {
        fixedCrosshairPosition = null;
        repaint();
    }
    
    /********************************************************************
     * METHOD: paintComponent
     * DESCRIPTION: Overrides the paint method to draw shapes and UI elements
     * PARAMETERS: Graphics g - the graphics context to draw on
     * RETURN VALUE: None
     ********************************************************************/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (codeGeneration != null && ioHandler != null) {
            // Draw all permanent shapes
            for (CodeGeneration3.Shape shape : codeGeneration.getShapes()) {
                shape.draw(g);
            }
            
            // Draw loop shapes (temporary preview)
            for (CodeGeneration3.Shape shape : codeGeneration.getLoopShapes()) {
                shape.draw(g);
            }
            
            // Draw current polygon points if in drawing mode
            if (ioHandler.isDrawingPolygon()) {
                drawPolygonInProgress(g);
            }
        }
        
        // Draw crosshair and coordinates
        if (showCrosshair) {
            drawCrosshair(g);
        }
    }
    
    /********************************************************************
     * METHOD: drawPolygonInProgress
     * DESCRIPTION: Draws the polygon points currently being added
     * PARAMETERS: Graphics g - the graphics context
     * RETURN VALUE: None
     ********************************************************************/
    private void drawPolygonInProgress(Graphics g) {
        g.setColor(Color.RED);
        java.util.List<Point> polygonPoints = ioHandler.getPolygonPoints();
        
        // Draw points and connecting lines
        for (int i = 0; i < polygonPoints.size(); i++) {
            Point p = polygonPoints.get(i);
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
            
            // Draw line to previous point
            if (i > 0) {
                Point prev = polygonPoints.get(i - 1);
                g.drawLine(prev.x, prev.y, p.x, p.y);
            }
        }
        
        // Draw line from last point to mouse if mouse is in panel
        if (showCrosshair && !polygonPoints.isEmpty()) {
            Point lastPoint = polygonPoints.get(polygonPoints.size() - 1);
            Point currentPoint = (fixedCrosshairPosition != null) ? 
                                fixedCrosshairPosition : mousePosition;
            g.setColor(Color.RED.darker());
            g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
        }
    }
    
    /********************************************************************
     * METHOD: drawCrosshair
     * DESCRIPTION: Draws the crosshair and coordinate display
     * PARAMETERS: Graphics g - the graphics context
     * RETURN VALUE: None
     ********************************************************************/
    private void drawCrosshair(Graphics g) {
        Point crosshairPoint = (fixedCrosshairPosition != null) ? 
                              fixedCrosshairPosition : mousePosition;
        
        g.setColor(Color.GRAY);
        
        // Draw horizontal line across entire panel
        g.drawLine(0, crosshairPoint.y, getWidth(), crosshairPoint.y);
        
        // Draw vertical line across entire panel
        g.drawLine(crosshairPoint.x, 0, crosshairPoint.x, getHeight());
        
        // Draw coordinates text
        String coordText = "(" + crosshairPoint.x + ", " + crosshairPoint.y + ")";
        g.setColor(Color.BLACK);
        g.fillRect(crosshairPoint.x + 10, crosshairPoint.y - 15,
                  g.getFontMetrics().stringWidth(coordText) + 6, 20);
        g.setColor(Color.WHITE);
        g.drawString(coordText, crosshairPoint.x + 13, crosshairPoint.y);
        
        // Draw a small indicator at the crosshair intersection
        g.setColor(Color.RED);
        g.fillRect(crosshairPoint.x - 2, crosshairPoint.y - 2, 5, 5);
        
        // If crosshair is fixed, draw a different indicator
        if (fixedCrosshairPosition != null) {
            g.setColor(Color.BLUE);
            g.drawRect(crosshairPoint.x - 4, crosshairPoint.y - 4, 9, 9);
        }
    }
    
    /********************************************************************
     * METHOD: getMousePosition
     * DESCRIPTION: Returns the current mouse position
     * PARAMETERS: None
     * RETURN VALUE: Point - the mouse position
     ********************************************************************/
    public Point getMousePosition() {
        return mousePosition;
    }
    
    /********************************************************************
     * METHOD: isCrosshairVisible
     * DESCRIPTION: Checks if the crosshair is visible
     * PARAMETERS: None
     * RETURN VALUE: boolean - true if crosshair is visible
     ********************************************************************/
    public boolean isCrosshairVisible() {
        return showCrosshair;
    }
    
    /********************************************************************
     * METHOD: setCrosshairVisible
     * DESCRIPTION: Sets the crosshair visibility
     * PARAMETERS: boolean visible - true to show crosshair
     * RETURN VALUE: None
     ********************************************************************/
    public void setCrosshairVisible(boolean visible) {
        showCrosshair = visible;
        repaint();
    }
}