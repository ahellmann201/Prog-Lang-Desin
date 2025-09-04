package assign2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;
//Hello  World
public class assign2 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private java.util.List<Shape> shapes = new ArrayList<>();
    private java.util.List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private boolean fillShape = false;
    private boolean recordingLoop = false;
    private List<String> loopCommands = new ArrayList<>();
    private List<Loop> loops = new ArrayList<>();
    private List<Shape> loopShapes = new ArrayList<>(); // Shapes in the current loop
    private Map<String, Integer> variables = new HashMap<>(); // Variable storage
    private int currentX = 400; // Default starting position
    private int currentY = 300; // Default starting position

    public assign2() {
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
        
        // Create loop control buttons
        JButton startLoopButton = new JButton("Start Loop");
        JButton endLoopButton = new JButton("End Loop");
        JButton playLoopButton = new JButton("Play Loop");
        
        // Create variable control buttons
        JButton showVarsButton = new JButton("Show Variables");
        JButton clearVarsButton = new JButton("Clear Variables");
        
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
        buttonPanel.add(startLoopButton);
        buttonPanel.add(endLoopButton);
        buttonPanel.add(playLoopButton);
        buttonPanel.add(showVarsButton);
        buttonPanel.add(clearVarsButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(inputPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        sendButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());
        
        startLoopButton.addActionListener(e -> startLoopRecording());
        endLoopButton.addActionListener(e -> endLoopRecording());
        playLoopButton.addActionListener(e -> showLoopSelectionDialog());
        showVarsButton.addActionListener(e -> showVariables());
        clearVarsButton.addActionListener(e -> clearVariables());
        
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
        historyArea.append("fill on / fill off - Toggle fill mode for shapes\n");
        historyArea.append("loop start - Start recording a loop\n");
        historyArea.append("loop end - End recording and save the loop\n");
        historyArea.append("loop play [name] - Play a saved loop\n");
        historyArea.append("set var=value - Set a variable (e.g., set size=50)\n");
        historyArea.append("move x,y - Move current position\n");
        historyArea.append("move rel dx,dy - Move relative to current position\n");
        historyArea.append("forward distance - Move forward in current direction\n");
        historyArea.append("turn angle - Turn by specified angle\n");
        historyArea.append("home - Return to default position (400, 300)\n");
        historyArea.append("show vars - Show all variables\n");
        historyArea.append("clear vars - Clear all variables\n\n");
        
        setVisible(true);
    }
    
    private void processInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            historyArea.append("You: " + text + "\n");
            inputField.setText("");
            
            // Check for variable commands
            if (text.toLowerCase().startsWith("set ")) {
                processSetCommand(text);
                return;
            } else if (text.equalsIgnoreCase("show vars")) {
                showVariables();
                return;
            } else if (text.equalsIgnoreCase("clear vars")) {
                clearVariables();
                return;
            } else if (text.toLowerCase().startsWith("move ")) {
                processMoveCommand(text);
                return;
            } else if (text.toLowerCase().startsWith("forward ")) {
                processForwardCommand(text);
                return;
            } else if (text.toLowerCase().startsWith("turn ")) {
                processTurnCommand(text);
                return;
            } else if (text.equalsIgnoreCase("home")) {
                currentX = 400;
                currentY = 300;
                historyArea.append("System: Moved to home position (400, 300)\n");
                return;
            }
            
            // Check for loop commands
            if (text.equalsIgnoreCase("loop start")) {
                startLoopRecording();
                return;
            } else if (text.equalsIgnoreCase("loop end")) {
                endLoopRecording();
                return;
            } else if (text.toLowerCase().startsWith("loop play")) {
                String[] parts = text.split(" ");
                if (parts.length >= 3) {
                    playLoop(parts[2]);
                } else {
                    showLoopSelectionDialog();
                }
                return;
            }
            
            // If recording a loop, add command to loop and process it
            if (recordingLoop) {
                loopCommands.add(text);
                processLoopCommand(text);
                return;
            }
            
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
            processShapeCommand(text);
        }
    }
    
    private int parseValue(String value) {
        try {
            // Check if it's a variable reference
            if (variables.containsKey(value)) {
                return variables.get(value);
            }
            // Otherwise parse as integer
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            historyArea.append("System: Invalid number or undefined variable: " + value + "\n");
            return 0;
        }
    }
    
    private void processSetCommand(String command) {
        String[] parts = command.substring(4).split("=");
        if (parts.length == 2) {
            String varName = parts[0].trim();
            try {
                int value = parseValue(parts[1].trim());
                variables.put(varName, value);
                historyArea.append("System: Set variable " + varName + " = " + value + "\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid value for variable " + varName + "\n");
            }
        } else {
            historyArea.append("System: Invalid set command format. Use: set var=value\n");
        }
    }
    
    private void processMoveCommand(String command) {
        String[] parts = command.substring(5).split(",");
        if (parts.length == 2) {
            try {
                int x = parseValue(parts[0].trim());
                int y = parseValue(parts[1].trim());
                currentX = x;
                currentY = y;
                historyArea.append("System: Moved to position (" + x + ", " + y + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid coordinates for move command\n");
            }
        } else if (command.toLowerCase().startsWith("move rel ")) {
            String[] relParts = command.substring(9).split(",");
            if (relParts.length == 2) {
                try {
                    int dx = parseValue(relParts[0].trim());
                    int dy = parseValue(relParts[1].trim());
                    currentX += dx;
                    currentY += dy;
                    historyArea.append("System: Moved relative by (" + dx + ", " + dy + ") to (" + currentX + ", " + currentY + ")\n");
                } catch (NumberFormatException e) {
                    historyArea.append("System: Invalid relative move coordinates\n");
                }
            }
        } else {
            historyArea.append("System: Invalid move command format. Use: move x,y or move rel dx,dy\n");
        }
    }
    
    private void processForwardCommand(String command) {
        try {
            int distance = parseValue(command.substring(8).trim());
            // Simple forward movement (could be enhanced with direction tracking)
            currentY -= distance; // Move "up" by default
            historyArea.append("System: Moved forward " + distance + " units to (" + currentX + ", " + currentY + ")\n");
        } catch (NumberFormatException e) {
            historyArea.append("System: Invalid distance for forward command\n");
        }
    }
    
    private void processTurnCommand(String command) {
        try {
            int angle = parseValue(command.substring(5).trim());
            // For now, just acknowledge the turn (could be enhanced with direction tracking)
            historyArea.append("System: Turned by " + angle + " degrees\n");
        } catch (NumberFormatException e) {
            historyArea.append("System: Invalid angle for turn command\n");
        }
    }
    
    private void showVariables() {
        if (variables.isEmpty()) {
            historyArea.append("System: No variables defined\n");
        } else {
            historyArea.append("System: Variables:\n");
            for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                historyArea.append("  " + entry.getKey() + " = " + entry.getValue() + "\n");
            }
        }
    }
    
    private void clearVariables() {
        variables.clear();
        historyArea.append("System: All variables cleared\n");
    }
    
    private void processShapeCommand(String text) {
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
    
    private void processLoopCommand(String text) {
        // Process the command but only add to temporary loop shapes
        if (text.equalsIgnoreCase("fill on")) {
            fillShape = true;
            historyArea.append("System: Fill mode enabled (loop)\n");
        } else if (text.equalsIgnoreCase("fill off")) {
            fillShape = false;
            historyArea.append("System: Fill mode disabled (loop)\n");
        } else if (text.toLowerCase().startsWith("set ")) {
            processSetCommand(text);
        } else if (text.toLowerCase().startsWith("move ")) {
            processMoveCommand(text);
        } else if (text.toLowerCase().startsWith("forward ")) {
            processForwardCommand(text);
        } else if (text.toLowerCase().startsWith("turn ")) {
            processTurnCommand(text);
        } else if (text.equalsIgnoreCase("home")) {
            currentX = 400;
            currentY = 300;
            historyArea.append("System: Moved to home position (400, 300)\n");
        } else {
            // Process shape commands for the loop preview
            if (text.toLowerCase().startsWith("circle")) {
                processCircleCommandForLoop(text);
            } else if (text.toLowerCase().startsWith("triangle")) {
                processTriangleCommandForLoop(text);
            } else if (text.toLowerCase().startsWith("rectangle")) {
                processRectangleCommandForLoop(text);
            } else if (text.toLowerCase().startsWith("square")) {
                processSquareCommandForLoop(text);
            } else if (text.toLowerCase().startsWith("points")) {
                processPointsCommandForLoop(text);
            }
        }
        drawingPanel.repaint();
    }
    
    private void startLoopRecording() {
        if (recordingLoop) {
            historyArea.append("System: Already recording a loop. End current loop first.\n");
            return;
        }
        
        recordingLoop = true;
        loopCommands.clear();
        loopShapes.clear();
        historyArea.append("System: Started recording loop. Enter commands to add to loop.\n");
    }
    
    private void endLoopRecording() {
        if (!recordingLoop) {
            historyArea.append("System: Not currently recording a loop.\n");
            return;
        }
        
        if (loopCommands.isEmpty()) {
            historyArea.append("System: Loop recording ended with no commands.\n");
            recordingLoop = false;
            loopShapes.clear();
            drawingPanel.repaint();
            return;
        }
        
        // Ask for loop name
        String name = JOptionPane.showInputDialog(this, "Enter a name for this loop:");
        if (name != null && !name.trim().isEmpty()) {
            loops.add(new Loop(name, new ArrayList<>(loopCommands)));
            historyArea.append("System: Loop '" + name + "' saved with " + loopCommands.size() + " commands.\n");
            
            // Clear the loop preview
            loopShapes.clear();
            drawingPanel.repaint();
        } else {
            historyArea.append("System: Loop recording cancelled.\n");
        }
        
        recordingLoop = false;
        loopCommands.clear();
    }
    
    private void playLoop(String loopName) {
        for (Loop loop : loops) {
            if (loop.name.equalsIgnoreCase(loopName)) {
                historyArea.append("System: Playing loop '" + loop.name + "'\n");
                
                // Execute all commands in the loop
                for (String command : loop.commands) {
                    processInputForLoopPlayback(command);
                }
                return;
            }
        }
        historyArea.append("System: Loop '" + loopName + "' not found.\n");
    }
    
    private void processInputForLoopPlayback(String command) {
        // Simulate entering the command during loop playback
        if (command.equalsIgnoreCase("fill on")) {
            fillShape = true;
        } else if (command.equalsIgnoreCase("fill off")) {
            fillShape = false;
        } else if (command.toLowerCase().startsWith("set ")) {
            processSetCommand(command);
        } else if (command.toLowerCase().startsWith("move ")) {
            processMoveCommand(command);
        } else if (command.toLowerCase().startsWith("forward ")) {
            processForwardCommand(command);
        } else if (command.toLowerCase().startsWith("turn ")) {
            processTurnCommand(command);
        } else if (command.equalsIgnoreCase("home")) {
            currentX = 400;
            currentY = 300;
        } else {
            processShapeCommand(command);
        }
    }
    
    private void showLoopSelectionDialog() {
        if (loops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No loops saved yet.");
            return;
        }
        
        String[] loopNames = new String[loops.size()];
        for (int i = 0; i < loops.size(); i++) {
            loopNames[i] = loops.get(i).name;
        }
        
        String selectedLoop = (String) JOptionPane.showInputDialog(
            this,
            "Select a loop to play:",
            "Play Loop",
            JOptionPane.QUESTION_MESSAGE,
            null,
            loopNames,
            loopNames[0]
        );
        
        if (selectedLoop != null) {
            playLoop(selectedLoop);
        }
    }
    
    private void processCircleCommand(String command) {
        Pattern pattern = Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = parseValue(matcher.group(1).trim());
                int x = parseValue(matcher.group(2).trim());
                int y = parseValue(matcher.group(3).trim());
                
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
    
    private void processCircleCommandForLoop(String command) {
        Pattern pattern = Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = parseValue(matcher.group(1).trim());
                int x = parseValue(matcher.group(2).trim());
                int y = parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Circle(x, y, radius, fillShape));
                historyArea.append("System: Circle added to loop at (" + x + ", " + y + ") with radius " + radius + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            historyArea.append("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }
    
    private void processTriangleCommand(String command) {
        Pattern pattern = Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = parseValue(matcher.group(1).trim());
                int y = parseValue(matcher.group(2).trim());
                int angle = parseValue(matcher.group(3).trim());
                
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
    
    private void processTriangleCommandForLoop(String command) {
        Pattern pattern = Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = parseValue(matcher.group(1).trim());
                int y = parseValue(matcher.group(2).trim());
                int angle = parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Triangle(x, y, angle, fillShape));
                historyArea.append("System: Triangle added to loop at (" + x + ", " + y + ") with angle " + angle + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in triangle command. Use: triangle, x, y, angle\n");
            }
        } else {
            historyArea.append("System: Invalid triangle command format. Use: triangle, x, y, angle\n");
        }
    }
    
    private void processRectangleCommand(String command) {
        Pattern pattern = Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = parseValue(matcher.group(1).trim());
                int y1 = parseValue(matcher.group(2).trim());
                int x2 = parseValue(matcher.group(3).trim());
                int y2 = parseValue(matcher.group(4).trim());
                
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
    
    private void processRectangleCommandForLoop(String command) {
        Pattern pattern = Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = parseValue(matcher.group(1).trim());
                int y1 = parseValue(matcher.group(2).trim());
                int x2 = parseValue(matcher.group(3).trim());
                int y2 = parseValue(matcher.group(4).trim());
                
                loopShapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                historyArea.append("System: Rectangle added to loop from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + 
                                  ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            historyArea.append("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }
    
    private void processSquareCommand(String command) {
        Pattern pattern = Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = parseValue(matcher.group(1).trim());
                int y = parseValue(matcher.group(2).trim());
                int size = parseValue(matcher.group(3).trim());
                
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
    
    private void processSquareCommandForLoop(String command) {
        Pattern pattern = Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = parseValue(matcher.group(1).trim());
                int y = parseValue(matcher.group(2).trim());
                int size = parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Square(x, y, size, fillShape));
                historyArea.append("System: Square added to loop at (" + x + ", " + y + ") with size " + size + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                historyArea.append("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            historyArea.append("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }
    
    private void processPointsCommand(String command) {
        try {
            String[] parts = command.split(",\\s*");
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split("\\s*,\\s*|\\s+");
                if (coords.length >= 2) {
                    int x = parseValue(coords[0]);
                    int y = parseValue(coords[1]);
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
    
    private void processPointsCommandForLoop(String command) {
        try {
            String[] parts = command.split(",\\s*");
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split("\\s*,\\s*|\\s+");
                if (coords.length >= 2) {
                    int x = parseValue(coords[0]);
                    int y = parseValue(coords[1]);
                    points.add(new Point(x, y));
                }
            }
            
            if (points.size() >= 3) {
                loopShapes.add(new Polygon(points, fillShape));
                historyArea.append("System: Polygon added to loop with " + points.size() + " points (filled: " + fillShape + ")\n");
            } else {
                historyArea.append("System: Need at least 3 points to draw a polygon\n");
            }
        } catch (NumberFormatException e) {
            historyArea.append("System: Invalid numbers in points command. Use: points, x1,y1 x2,y2 x3,y3 ...\n");
        }
    }
    
    private void startPolygon() {
        drawingPolygon = true;
        polygonPoints.clear();
        historyArea.append("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
    }
    
    private void endPolygon() {
        if (drawingPolygon && polygonPoints.size() >= 3) {
            if (recordingLoop) {
                loopShapes.add(new Polygon(new ArrayList<>(polygonPoints), fillShape));
                historyArea.append("System: Polygon added to loop with " + polygonPoints.size() + " points (filled: " + fillShape + ")\n");
            } else {
                shapes.add(new Polygon(polygonPoints, fillShape));
                historyArea.append("System: Polygon drawn with " + polygonPoints.size() + " points (filled: " + fillShape + ")\n");
            }
            drawingPanel.repaint();
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
    
    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw all permanent shapes
            for (Shape shape : shapes) {
                shape.draw(g);
            }
            
            // Draw loop shapes (temporary preview)
            for (Shape shape : loopShapes) {
                shape.draw(g);
            }
            
            // Draw current position indicator
            g.setColor(Color.BLACK);
            g.fillOval(currentX - 3, currentY - 3, 6, 6);
            g.drawString("Current", currentX + 5, currentY - 5);
            
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
    
    class Loop {
        String name;
        List<String> commands;
        
        Loop(String name, List<String> commands) {
            this.name = name;
            this.commands = commands;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new assign2());
    }
}