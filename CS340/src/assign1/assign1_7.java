package assign1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.*;

public class assign1_7 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private java.util.List<Circle> circles = new ArrayList<>();

    public assign1_7() {
        setTitle("Drawing Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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
        
        setVisible(true);
    }
    
    private void processInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            historyArea.append("You: " + text + "\n");
            inputField.setText("");
            
            // Check if the input is a circle command
            if (text.toLowerCase().startsWith("circle")) {
                processCircleCommand(text);
            }
        }
    }
    
    private void processCircleCommand(String command) {
        // Pattern to match: circle, radius, x, y (case insensitive, with optional spaces)
        Pattern pattern = Pattern.compile("circle\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = Integer.parseInt(matcher.group(1));
                int x = Integer.parseInt(matcher.group(2));
                int y = Integer.parseInt(matcher.group(3));
                
                circles.add(new Circle(x, y, radius));
                drawingPanel.repaint();
                historyArea.append("System: Circle drawn at (" + x + ", " + y + ") with radius " + radius + "\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in circle command\n");
            }
        } else {
            historyArea.append("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }
    
    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw all circles
            for (Circle circle : circles) {
                g.setColor(Color.BLUE);
                g.drawOval(circle.x - circle.radius, circle.y - circle.radius, 
                          circle.radius * 2, circle.radius * 2);
            }
        }
    }
    
    class Circle {
        int x, y, radius;
        
        Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new assign1_7());
    }
}