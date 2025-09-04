package assign1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.*;

public class assign1_8 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private java.util.List<Shape> shapes = new ArrayList<>();

    public assign1_8() {
        setTitle("Drawing Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());
        
        // Create components
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        
        drawingPanel = new DrawingPanel();
        
        // Add components to the frame
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, drawingPanel);
        splitPane.setDividerLocation(400);
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        add(inputPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        sendButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());
        
        // Add help message
        historyArea.append("Available commands:\n");
        historyArea.append("circle, radius, x, y\n");
        historyArea.append("triangle, x, y, angle\n");
        historyArea.append("rectangle, x1, y1, x2, y2\n");
        historyArea.append("square, x, y, size\n\n");
        
        setVisible(true);
    }
    
    private void processInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            historyArea.append("You: " + text + "\n");
            inputField.setText("");
            
            // Check for shape commands
            if (text.toLowerCase().startsWith("circle")) {
                processCircleCommand(text);
            } else if (text.toLowerCase().startsWith("triangle")) {
                processTriangleCommand(text);
            } else if (text.toLowerCase().startsWith("rectangle")) {
                processRectangleCommand(text);
            } else if (text.toLowerCase().startsWith("square")) {
                processSquareCommand(text);
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
                
                shapes.add(new Circle(x, y, radius));
                drawingPanel.repaint();
                historyArea.append("System: Circle drawn at (" + x + ", " + y + ") with radius " + radius + "\n");
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
                
                shapes.add(new Triangle(x, y, angle));
                drawingPanel.repaint();
                historyArea.append("System: Triangle drawn at (" + x + ", " + y + ") with angle " + angle + "\n");
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
                
                shapes.add(new Rectangle(x1, y1, x2, y2));
                drawingPanel.repaint();
                historyArea.append("System: Rectangle drawn from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ")\n");
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
                
                shapes.add(new Square(x, y, size));
                drawingPanel.repaint();
                historyArea.append("System: Square drawn at (" + x + ", " + y + ") with size " + size + "\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            historyArea.append("System: Invalid square command format. Use: square, x, y, size\n");
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
        }
    }
    
    abstract class Shape {
        abstract void draw(Graphics g);
    }
    
    class Circle extends Shape {
        int x, y, radius;
        
        Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }
    
    class Triangle extends Shape {
        int x, y, angle;
        
        Triangle(int x, int y, int angle) {
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
            
            g.drawPolygon(xPoints, yPoints, 3);
        }
    }
    
    class Rectangle extends Shape {
        int x1, y1, x2, y2;
        
        Rectangle(int x1, int y1, int x2, int y2) {
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
            g.drawRect(x, y, width, height);
        }
    }
    
    class Square extends Shape {
        int x, y, size;
        
        Square(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.MAGENTA);
            g.drawRect(x, y, size, size);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new assign1_8());
    }
}