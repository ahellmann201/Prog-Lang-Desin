package assign1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class assign1_9 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private java.util.List<Shape> shapes = new ArrayList<>();
    private java.util.List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private boolean fillShape = false;

    public assign1_9() {
        setTitle("Drawing Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Create components
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        
        // Create fill toggle button
        JToggleButton fillButton = new JToggleButton("Fill: OFF");
        fillButton.addActionListener(e -> {
            fillShape = fillButton.isSelected();
            fillButton.setText(fillShape ? "Fill: ON" : "Fill: OFF");
            historyArea.append("System: Fill mode " + (fillShape ? "enabled\n" : "disabled\n"));
        });
        
        drawingPanel = new DrawingPanel();
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (drawingPolygon) {
                    polygonPoints.add(new Point(e.getX(), e.getY()));
                    drawingPanel.repaint();
                    historyArea.append("Added point: (" + e.getX() + ", " + e.getY() + ")\n");
                }
            }
        });
        
        // Add components to the frame
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, drawingPanel);
        splitPane.setDividerLocation(400);
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(sendButton);
        buttonPanel.add(fillButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(inputPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        sendButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());
        
        // Add help message
        historyArea.append("Available commands:\n");
        historyArea.append("circle, radius, x, y\n");
        historyArea.append("triangle, x, y, angle\n");
        historyArea.append("rectangle, x1, y1, x2, y2\n");
        historyArea.append("square, x, y, size\n");
        historyArea.append("polygon - Start adding points by clicking on the drawing area\n");
        historyArea.append("endpolygon - Finish drawing the polygon\n");
        historyArea.append("clearpolygon - Clear current polygon points\n");
        historyArea.append("points, x1,y1 x2,y2 x3,y3 ... - Draw polygon with specified points\n");
        historyArea.append("fill on / fill off - Toggle fill mode for shapes\n\n");
        
        setVisible(true);
    }
    
    private void processInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            historyArea.append("You: " + text + "\n");
            inputField.setText("");
            
            // Check for fill commands
            if (text.equalsIgnoreCase("fill on")) {
                fillShape = true;
                historyArea.append("System: Fill mode enabled\n");
                return;
            } else if (text.equalsIgnoreCase("fill off")) {
                fillShape = false;
                historyArea.append("System: Fill mode disabled\n");
                return;
            }
            
            // Check for shape commands
            if (text.toLowerCase().startsWith("circle")) {
                processCircleCommand(text);
            } else if (text.toLowerCase().startsWith("triangle")) {
                processTriangleCommand(text);
            } else if (text.toLowerCase().startsWith("rectangle")) {
                processRectangleCommand(text);
            } else if (text.toLowerCase().startsWith("square")) {
                processSquareCommand(text);
            } else if (text.toLowerCase().startsWith("polygon")) {
                startPolygon();
            } else if (text.toLowerCase().startsWith("endpolygon")) {
                endPolygon();
            } else if (text.toLowerCase().startsWith("clearpolygon")) {
                clearPolygon();
            } else if (text.toLowerCase().startsWith("points")) {
                processPointsCommand(text);
            }
        }
    }
    
    private void processCircleCommand(String command) {
        Pattern pattern = Pattern.compile("circle\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = Integer.parseInt(matcher.group(1));
                int x = Integer.parseInt(matcher.group(2));
                int y = Integer.parseInt(matcher.group(3));
                
                shapes.add(new Circle(x, y, radius, fillShape));
                drawingPanel.repaint();
                historyArea.append("System: Circle drawn at (" + x + ", " + y + ") with radius " + radius + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            historyArea.append("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }
    
    private void processTriangleCommand(String command) {
        Pattern pattern = Pattern.compile("triangle\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int angle = Integer.parseInt(matcher.group(3));
                
                shapes.add(new Triangle(x, y, angle, fillShape));
                drawingPanel.repaint();
                historyArea.append("System: Triangle drawn at (" + x + ", " + y + ") with angle " + angle + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in triangle command. Use: triangle, x, y, angle\n");
            }
        } else {
            historyArea.append("System: Invalid triangle command format. Use: triangle, x, y, angle\n");
        }
    }
    
    private void processRectangleCommand(String command) {
        Pattern pattern = Pattern.compile("rectangle\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = Integer.parseInt(matcher.group(1));
                int y1 = Integer.parseInt(matcher.group(2));
                int x2 = Integer.parseInt(matcher.group(3));
                int y2 = Integer.parseInt(matcher.group(4));
                
                shapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                drawingPanel.repaint();
                historyArea.append("System: Rectangle drawn from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + 
                                  ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            historyArea.append("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }
    
    private void processSquareCommand(String command) {
        Pattern pattern = Pattern.compile("square\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int size = Integer.parseInt(matcher.group(3));
                
                shapes.add(new Square(x, y, size, fillShape));
                drawingPanel.repaint();
                historyArea.append("System: Square drawn at (" + x + ", " + y + ") with size " + size + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            historyArea.append("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }
    
    private void startPolygon() {
        drawingPolygon = true;
        polygonPoints.clear();
        historyArea.append("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
    }
    
    private void endPolygon() {
        if (drawingPolygon && polygonPoints.size() >= 3) {
            shapes.add(new Polygon(polygonPoints, fillShape));
            drawingPanel.repaint();
            historyArea.append("System: Polygon drawn with " + polygonPoints.size() + " points (filled: " + fillShape + ")\n");
            polygonPoints.clear();
            drawingPolygon = false;
        } else if (drawingPolygon) {
            historyArea.append("System: Need at least 3 points to draw a polygon\n");
        } else {
            historyArea.append("System: Not currently drawing a polygon\n");
        }
    }
    
    private void clearPolygon() {
        polygonPoints.clear();
        drawingPolygon = false;
        drawingPanel.repaint();
        historyArea.append("System: Polygon points cleared\n");
    }
    
    private void processPointsCommand(String command) {
        try {
            String[] parts = command.split(",\\s*");
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split("\\s*,\\s*|\\s+");
                if (coords.length >= 2) {
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    points.add(new Point(x, y));
                }
            }
            
            if (points.size() >= 3) {
                shapes.add(new Polygon(points, fillShape));
                drawingPanel.repaint();
                historyArea.append("System: Polygon drawn with " + points.size() + " points (filled: " + fillShape + ")\n");
            } else {
                historyArea.append("System: Need at least 3 points to draw a polygon\n");
            }
        } catch (NumberFormatException e) {
            historyArea.append("System: Invalid numbers in points command. Use: points, x1,y1 x2,y2 x3,y3 ...\n");
        }
    }
    
    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw all shapes
            for (Shape shape : shapes) {
                shape.draw(g);
            }
            
            // Draw current polygon points if in drawing mode
            if (drawingPolygon) {
                g.setColor(Color.RED);
                for (int i = 0; i < polygonPoints.size(); i++) {
                    Point p = polygonPoints.get(i);
                    g.fillOval(p.x - 3, p.y - 3, 6, 6);
                    if (i > 0) {
                        Point prev = polygonPoints.get(i - 1);
                        g.drawLine(prev.x, prev.y, p.x, p.y);
                    }
                }
            }
        }
    }
    
    abstract class Shape {
        boolean filled;
        
        Shape(boolean filled) {
            this.filled = filled;
        }
        
        abstract void draw(Graphics g);
    }
    
    class Circle extends Shape {
        int x, y, radius;
        
        Circle(int x, int y, int radius, boolean filled) {
            super(filled);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.BLUE);
            if (filled) {
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            } else {
                g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            }
        }
    }
    
    class Triangle extends Shape {
        int x, y, angle;
        
        Triangle(int x, int y, int angle, boolean filled) {
            super(filled);
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.RED);
            
            // Calculate triangle points based on angle
            int size = 50; // Default size
            int x2 = x + (int)(size * Math.cos(Math.toRadians(angle)));
            int y2 = y - (int)(size * Math.sin(Math.toRadians(angle)));
            int x3 = x + (int)(size * Math.cos(Math.toRadians(angle + 120)));
            int y3 = y - (int)(size * Math.sin(Math.toRadians(angle + 120)));
            
            int[] xPoints = {x, x2, x3};
            int[] yPoints = {y, y2, y3};
            
            if (filled) {
                g.fillPolygon(xPoints, yPoints, 3);
            } else {
                g.drawPolygon(xPoints, yPoints, 3);
            }
        }
    }
    
    class Rectangle extends Shape {
        int x1, y1, x2, y2;
        
        Rectangle(int x1, int y1, int x2, int y2, boolean filled) {
            super(filled);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.GREEN);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            
            if (filled) {
                g.fillRect(x, y, width, height);
            } else {
                g.drawRect(x, y, width, height);
            }
        }
    }
    
    class Square extends Shape {
        int x, y, size;
        
        Square(int x, int y, int size, boolean filled) {
            super(filled);
            this.x = x;
            this.y = y;
            this.size = size;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.MAGENTA);
            if (filled) {
                g.fillRect(x, y, size, size);
            } else {
                g.drawRect(x, y, size, size);
            }
        }
    }
    
    class Polygon extends Shape {
        List<Point> points;
        
        Polygon(List<Point> points, boolean filled) {
            super(filled);
            this.points = new ArrayList<>(points);
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.ORANGE);
            
            int[] xPoints = new int[points.size()];
            int[] yPoints = new int[points.size()];
            
            for (int i = 0; i < points.size(); i++) {
                xPoints[i] = points.get(i).x;
                yPoints[i] = points.get(i).y;
            }
            
            if (filled) {
                g.fillPolygon(xPoints, yPoints, points.size());
            } else {
                g.drawPolygon(xPoints, yPoints, points.size());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new assign1_9());
    }
}