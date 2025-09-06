package assign2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/******************************************************************************
*                     Drawing Chat Application - User Interface               *
*                                                                             *
*    PROGRAMMER:  [Your Name]                                                 *
*    COURSE:  [Course Number and Name]                                        *
*    DATE:  [Date Submitted]                                                  *
*    REQUIREMENT:  Assignment 2                                               *
*                                                                             *
*    DESCRIPTION:                                                             *
*    This file contains the main user interface components for the drawing    *
*    chat application, including the JFrame setup, panel organization, and    *
*    event handling for user interactions.                                    *
*                                                                             *
*    COPYRIGHT:                                                               *
*    This code is copyright (c)2025 [Your Name] and Dean Zeller.              *
*                                                                             *
*    CREDITS:                                                                 *
*    Java Swing documentation and tutorials                                   *
*                                                                             *
******************************************************************************/

public class UserInterface extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private InputOutputHandler ioHandler;
    private CodeGeneration codeGeneration;
    
    private JToggleButton fillButton;
    private JButton startLoopButton, endLoopButton, playLoopButton;
    private JButton showVarsButton, clearVarsButton;

    /**************************************************************************
    *    METHOD:    UserInterface Constructor                                 *
    *    DESCRIPTION:  Initializes the main application window and sets up    *
    *                  all UI components                                      *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public UserInterface() {
        setTitle("Drawing Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Initialize handlers
        ioHandler = new InputOutputHandler();
        codeGeneration = new CodeGeneration();
        
        // Create components
        createUIComponents();
        setupLayout();
        setupEventHandlers();
        
        // Set history area for IO handler
        ioHandler.setHistoryArea(historyArea);
        
        // Add help message
        displayHelp();
        
        setVisible(true);
    }
    
    /**************************************************************************
    *    METHOD:    createUIComponents                                        *
    *    DESCRIPTION:  Creates all UI components for the application          *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void createUIComponents() {
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        
        inputField = new JTextField();
        
        drawingPanel = new DrawingPanel();
        drawingPanel.setIOHandler(ioHandler);
        drawingPanel.setCodeGeneration(codeGeneration);
        
        // Create control buttons
        fillButton = new JToggleButton("Fill: OFF");
        startLoopButton = new JButton("Start Loop");
        endLoopButton = new JButton("End Loop");
        playLoopButton = new JButton("Play Loop");
        showVarsButton = new JButton("Show Variables");
        clearVarsButton = new JButton("Clear Variables");
    }
    
    /**************************************************************************
    *    METHOD:    setupLayout                                               *
    *    DESCRIPTION:  Arranges all UI components in the frame                *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void setupLayout() {
        JScrollPane scrollPane = new JScrollPane(historyArea);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, drawingPanel);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Send"));
        buttonPanel.add(fillButton);
        buttonPanel.add(startLoopButton);
        buttonPanel.add(endLoopButton);
        buttonPanel.add(playLoopButton);
        buttonPanel.add(showVarsButton);
        buttonPanel.add(clearVarsButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    /**************************************************************************
    *    METHOD:    setupEventHandlers                                        *
    *    DESCRIPTION:  Sets up event listeners for all interactive components *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void setupEventHandlers() {
        // Send button and input field
        JButton sendButton = (JButton)((JPanel)((JPanel)getContentPane().getComponent(1)).getComponent(1)).getComponent(0);
        sendButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());
        
        // Fill toggle button
        fillButton.addActionListener(e -> {
            boolean fillMode = fillButton.isSelected();
            codeGeneration.setFillShape(fillMode);
            fillButton.setText(fillMode ? "Fill: ON" : "Fill: OFF");
            ioHandler.appendToHistory("System: Fill mode " + (fillMode ? "enabled\n" : "disabled\n"));
        });
        
        // Loop control buttons
        startLoopButton.addActionListener(e -> {
            codeGeneration.startLoopRecording(ioHandler);
            ioHandler.appendToHistory("System: Started recording loop\n");
        });
        
        endLoopButton.addActionListener(e -> {
            String loopName = ioHandler.showInputDialog("Enter loop name:");
            if (loopName != null && !loopName.trim().isEmpty()) {
                codeGeneration.endLoopRecording(loopName, ioHandler);
                ioHandler.appendToHistory("System: Saved loop as '" + loopName + "'\n");
            }
        });
        
        playLoopButton.addActionListener(e -> {
            List<CodeGeneration.Loop> loops = codeGeneration.getLoops();
            if (loops.isEmpty()) {
                ioHandler.appendToHistory("System: No loops saved\n");
                return;
            }
            
            String[] loopNames = new String[loops.size()];
            for (int i = 0; i < loops.size(); i++) {
                loopNames[i] = loops.get(i).getName();
            }
            
            String selectedLoop = ioHandler.showSelectionDialog("Play Loop", "Select a loop to play:", loopNames);
            if (selectedLoop != null) {
                codeGeneration.playLoop(selectedLoop, drawingPanel, ioHandler);
                ioHandler.appendToHistory("System: Playing loop '" + selectedLoop + "'\n");
            }
        });
        
        // Variable control buttons
        showVarsButton.addActionListener(e -> ioHandler.showVariables());
        clearVarsButton.addActionListener(e -> {
            ioHandler.clearVariables();
            ioHandler.appendToHistory("System: All variables cleared\n");
        });
        
        // Mouse listener for polygon drawing
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ioHandler.isDrawingPolygon()) {
                    ioHandler.addPolygonPoint(new Point(e.getX(), e.getY()));
                    drawingPanel.repaint();
                    ioHandler.appendToHistory("Added point: (" + e.getX() + ", " + e.getY() + ")\n");
                }
            }
        });
    }
    
    /**************************************************************************
    *    METHOD:    processInput                                              *
    *    DESCRIPTION:  Processes user input from the text field               *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            ioHandler.appendToHistory("You: " + text + "\n");
            inputField.setText("");
            
            // Process the command
            if (text.equalsIgnoreCase("polygon")) {
                ioHandler.setDrawingPolygon(true);
                ioHandler.clearPolygonPoints();
                ioHandler.appendToHistory("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
            } 
            else if (text.equalsIgnoreCase("endpolygon")) {
                if (ioHandler.isDrawingPolygon()) {
                    ioHandler.setDrawingPolygon(false);
                    if (ioHandler.getPolygonPoints().size() >= 3) {
                        codeGeneration.endPolygon(ioHandler, codeGeneration.isRecordingLoop());
                        drawingPanel.repaint();
                    } else {
                        ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
                    }
                }
            }
            else if (text.equalsIgnoreCase("clearpolygon")) {
                ioHandler.clearPolygonPoints();
                drawingPanel.repaint();
                ioHandler.appendToHistory("System: Polygon points cleared\n");
            }
            else if (text.startsWith("fill ")) {
                String[] parts = text.split(" ");
                if (parts.length >= 2) {
                    if (parts[1].equalsIgnoreCase("on")) {
                        codeGeneration.setFillShape(true);
                        fillButton.setSelected(true);
                        fillButton.setText("Fill: ON");
                        ioHandler.appendToHistory("System: Fill mode enabled\n");
                    } else if (parts[1].equalsIgnoreCase("off")) {
                        codeGeneration.setFillShape(false);
                        fillButton.setSelected(false);
                        fillButton.setText("Fill: OFF");
                        ioHandler.appendToHistory("System: Fill mode disabled\n");
                    }
                }
            }
            else if (text.startsWith("set ")) {
                String[] parts = text.substring(4).split("=");
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        ioHandler.setVariable(varName, value);
                        ioHandler.appendToHistory("System: Set variable " + varName + " = " + value + "\n");
                    } catch (NumberFormatException e) {
                        ioHandler.appendToHistory("System: Invalid value for variable. Use: set var=value\n");
                    }
                } else {
                    ioHandler.appendToHistory("System: Invalid set command. Use: set var=value\n");
                }
            }
            else if (text.startsWith("move ")) {
                String[] parts = text.substring(5).split(",");
                if (parts.length == 2) {
                    try {
                        int dx = ioHandler.parseValue(parts[0].trim());
                        int dy = ioHandler.parseValue(parts[1].trim());
                        Point current = ioHandler.getCurrentPosition();
                        ioHandler.setCurrentPosition(new Point(current.x + dx, current.y + dy));
                        ioHandler.appendToHistory("System: Moved to (" + (current.x + dx) + ", " + (current.y + dy) + ")\n");
                        drawingPanel.repaint();
                    } catch (NumberFormatException e) {
                        ioHandler.appendToHistory("System: Invalid move command. Use: move dx,dy\n");
                    }
                } else {
                    ioHandler.appendToHistory("System: Invalid move command. Use: move dx,dy\n");
                }
            }
            else if (text.equalsIgnoreCase("home")) {
                ioHandler.setCurrentPosition(new Point(400, 300));
                ioHandler.appendToHistory("System: Returned to home position (400, 300)\n");
                drawingPanel.repaint();
            }
            else {
                // Process shape commands
                if (codeGeneration.isRecordingLoop()) {
                    processCommandForLoop(text);
                } else {
                    processCommand(text);
                }
            }
        }
    }
    
    /**************************************************************************
    *    METHOD:    processCommand                                            *
    *    DESCRIPTION:  Processes a drawing command                            *
    *    PARAMETERS:  String command - the command to process                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processCommand(String command) {
        String lowerCommand = command.toLowerCase();
        
        if (lowerCommand.startsWith("circle")) {
            codeGeneration.processCircleCommand(command, ioHandler);
        } 
        else if (lowerCommand.startsWith("triangle")) {
            codeGeneration.processTriangleCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("rectangle")) {
            codeGeneration.processRectangleCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("square")) {
            codeGeneration.processSquareCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("points")) {
            codeGeneration.processPointsCommand(command, ioHandler);
        }
        else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        
        drawingPanel.repaint();
    }
    
    /**************************************************************************
    *    METHOD:    processCommandForLoop                                     *
    *    DESCRIPTION:  Processes a drawing command for loop recording         *
    *    PARAMETERS:  String command - the command to process                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processCommandForLoop(String command) {
        String lowerCommand = command.toLowerCase();
        
        if (lowerCommand.startsWith("circle")) {
            codeGeneration.processCircleCommandForLoop(command, ioHandler);
        } 
        else if (lowerCommand.startsWith("triangle")) {
            codeGeneration.processTriangleCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("rectangle")) {
            codeGeneration.processRectangleCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("square")) {
            codeGeneration.processSquareCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("points")) {
            codeGeneration.processPointsCommandForLoop(command, ioHandler);
        }
        else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        
        drawingPanel.repaint();
    }
    
    /**************************************************************************
    *    METHOD:    displayHelp                                               *
    *    DESCRIPTION:  Displays the help message in the history area          *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void displayHelp() {
        ioHandler.appendToHistory("Available commands:\n");
        ioHandler.appendToHistory("circle, radius, x, y\n");
        ioHandler.appendToHistory("triangle, x, y, angle\n");
        ioHandler.appendToHistory("rectangle, x1, y1, x2, y2\n");
        ioHandler.appendToHistory("square, x, y, size\n");
        ioHandler.appendToHistory("polygon - Start adding points by clicking on the drawing area\n");
        ioHandler.appendToHistory("endpolygon - Finish drawing the polygon\n");
        ioHandler.appendToHistory("clearpolygon - Clear current polygon points\n");
        ioHandler.appendToHistory("points, x1,y1 x2,y2 x3,y3 ... - Draw polygon with specified points\n");
        ioHandler.appendToHistory("fill on / fill off - Toggle fill mode for shapes\n");
        ioHandler.appendToHistory("loop start - Start recording a loop\n");
        ioHandler.appendToHistory("loop end - End recording and save the loop\n");
        ioHandler.appendToHistory("loop play [name] - Play a saved loop\n");
        ioHandler.appendToHistory("set var=value - Set a variable (e.g., set size=50)\n");
        ioHandler.appendToHistory("move x,y - Move current position\n");
        ioHandler.appendToHistory("home - Return to default position (400, 300)\n");
        ioHandler.appendToHistory("show vars - Show all variables\n");
        ioHandler.appendToHistory("clear vars - Clear all variables\n\n");
    }
    
    /**************************************************************************
    *    METHOD:    getHistoryArea                                            *
    *    DESCRIPTION:  Returns the history text area component                *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  JTextArea - the history area component                *
    **************************************************************************/
    public JTextArea getHistoryArea() {
        return historyArea;
    }
    
    /**************************************************************************
    *    METHOD:    getDrawingPanel                                           *
    *    DESCRIPTION:  Returns the drawing panel component                    *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  DrawingPanel - the drawing panel component            *
    **************************************************************************/
    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserInterface());
    }
}

class DrawingPanel extends JPanel {
    private InputOutputHandler ioHandler;
    private CodeGeneration codeGeneration;
    
    /**************************************************************************
    *    METHOD:    setIOHandler                                              *
    *    DESCRIPTION:  Sets the input/output handler for the drawing panel    *
    *    PARAMETERS:  InputOutputHandler ioHandler - the IO handler to set    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setIOHandler(InputOutputHandler ioHandler) {
        this.ioHandler = ioHandler;
    }
    
    /**************************************************************************
    *    METHOD:    setCodeGeneration                                         *
    *    DESCRIPTION:  Sets the code generation for the drawing panel         *
    *    PARAMETERS:  CodeGeneration codeGeneration - the code generation to set *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setCodeGeneration(CodeGeneration codeGeneration) {
        this.codeGeneration = codeGeneration;
    }
    
    /**************************************************************************
    *    METHOD:    paintComponent                                            *
    *    DESCRIPTION:  Overrides the paint method to draw shapes and UI       *
    *                  elements on the panel                                  *
    *    PARAMETERS:  Graphics g - the graphics context to draw on            *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (codeGeneration != null && ioHandler != null) {
            // Draw all permanent shapes
            for (CodeGeneration.Shape shape : codeGeneration.getShapes()) {
                shape.draw(g);
            }
            
            // Draw loop shapes (temporary preview)
            for (CodeGeneration.Shape shape : codeGeneration.getLoopShapes()) {
                shape.draw(g);
            }
            
            // Draw current position indicator
            g.setColor(Color.BLACK);
            Point currentPos = ioHandler.getCurrentPosition();
            g.fillOval(currentPos.x - 3, currentPos.y - 3, 6, 6);
            g.drawString("Current", currentPos.x + 5, currentPos.y - 5);
            
            // Draw current polygon points if in drawing mode
            if (ioHandler.isDrawingPolygon()) {
                g.setColor(Color.RED);
                List<Point> polygonPoints = ioHandler.getPolygonPoints();
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
}