package assign3;

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

public class UserInterface3 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private DrawingPanel drawingPanel;
    private InputOutputHandler3 ioHandler;
    private CodeGeneration3 codeGeneration;
    
    private JToggleButton fillButton;
    private JButton clearScreenButton;
    
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;

    /**************************************************************************
    *    METHOD:    UserInterface Constructor                                 *
    *    DESCRIPTION:  Initializes the main application window and sets up    *
    *                  all UI components                                      *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public UserInterface3() {
        setTitle("Drawing Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Initialize handlers
        ioHandler = new InputOutputHandler3();
        codeGeneration = new CodeGeneration3();
        ioHandler.setCodeGeneration(codeGeneration);
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
        clearScreenButton = new JButton("Clear Screen");
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Create dropdown menus for better organization
        JMenuBar menuBar = new JMenuBar();
        
        // Loop menu
        JMenu loopMenu = new JMenu("Loop");
        JMenuItem startLoopItem = new JMenuItem("Start Loop");
        JMenuItem endLoopItem = new JMenuItem("End Loop");
        JMenuItem playLoopItem = new JMenuItem("Play Loop");
        
        startLoopItem.addActionListener(e -> {
            codeGeneration.startLoopRecording(ioHandler);
            ioHandler.appendToHistory("System: Started recording loop\n");
        });
        
        endLoopItem.addActionListener(e -> {
            String loopName = ioHandler.showInputDialog("Enter loop name:");
            if (loopName != null && !loopName.trim().isEmpty()) {
                codeGeneration.endLoopRecording(loopName, ioHandler);
                ioHandler.appendToHistory("System: Saved loop as '" + loopName + "'\n");
            }
        });
        
        playLoopItem.addActionListener(e -> {
            List<CodeGeneration3.Loop> loops = codeGeneration.getLoops();
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
        
        loopMenu.add(startLoopItem);
        loopMenu.add(endLoopItem);
        loopMenu.add(playLoopItem);
        
        // Variables menu
        JMenu varsMenu = new JMenu("Variables");
        JMenuItem showVarsItem = new JMenuItem("Show Variables");
        JMenuItem clearVarsItem = new JMenuItem("Clear Variables");
        
        showVarsItem.addActionListener(e -> ioHandler.showVariables());
        clearVarsItem.addActionListener(e -> {
            ioHandler.clearVariables();
            ioHandler.appendToHistory("System: All variables cleared\n");
        });
        
        varsMenu.add(showVarsItem);
        varsMenu.add(clearVarsItem);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadFileItem = new JMenuItem("Load File");
        
        loadFileItem.addActionListener(e -> {
            FileReader3 fileReader = new FileReader3(this);
            fileReader.readFile();
        });
        
        fileMenu.add(loadFileItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(loopMenu);
        menuBar.add(varsMenu);
        
        // Add buttons and menu bar to button panel
        buttonPanel.add(new JButton("Send"));
        buttonPanel.add(fillButton);
        buttonPanel.add(clearScreenButton);
        buttonPanel.add(menuBar);
        
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
        
        // Clear screen button
        clearScreenButton.addActionListener(e -> {
            codeGeneration.clearScreen(ioHandler);
            drawingPanel.repaint();
            ioHandler.appendToHistory("System: Screen cleared\n");
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
        
        // Add keyboard history navigation (UP and DOWN arrows)
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!commandHistory.isEmpty()) {
                        if (historyIndex > 0) {
                            historyIndex--;
                        }
                        inputField.setText(commandHistory.get(historyIndex));
                        // Move cursor to end of text for easy editing
                        inputField.setCaretPosition(inputField.getText().length());
                    }
                    e.consume(); // Prevent default behavior
                } 
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!commandHistory.isEmpty()) {
                        if (historyIndex < commandHistory.size() - 1) {
                            historyIndex++;
                            inputField.setText(commandHistory.get(historyIndex));
                            // Move cursor to end of text for easy editing
                            inputField.setCaretPosition(inputField.getText().length());
                        } else {
                            historyIndex = commandHistory.size();
                            inputField.setText("");
                        }
                    }
                    e.consume(); // Prevent default behavior
                }
            }
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
        
        // Add keyboard history navigation (UP and DOWN arrows)
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!commandHistory.isEmpty()) {
                        if (historyIndex > 0) {
                            historyIndex--;
                        }
                        inputField.setText(commandHistory.get(historyIndex));
                        // Move cursor to end of text for easy editing
                        inputField.setCaretPosition(inputField.getText().length());
                    }
                    e.consume(); // Prevent default behavior
                } 
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!commandHistory.isEmpty()) {
                        if (historyIndex < commandHistory.size() - 1) {
                            historyIndex++;
                            inputField.setText(commandHistory.get(historyIndex));
                            // Move cursor to end of text for easy editing
                            inputField.setCaretPosition(inputField.getText().length());
                        } else {
                            historyIndex = commandHistory.size();
                            inputField.setText("");
                        }
                    }
                    e.consume(); // Prevent default behavior
                }
            }
        });
        
        
        
        /**************************************************************************
        *    METHOD:    MouseListener for Polygon Drawing                         *
        *    DESCRIPTION:  Handles mouse click events to add points when in       *
        *                  polygon drawing mode. Adds clicked coordinates to      *
        *                  the polygon points list, repaints the panel, and       *
        *                  logs the action to history.                            *
        *    PARAMETERS:  None                                                    *
        *    RETURN VALUE:  None                                                  *
        **************************************************************************/
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
            // Add to command history
            commandHistory.add(text);
            historyIndex = commandHistory.size(); // Reset to end of history
            
            ioHandler.appendToHistory("You: " + text + "\n");
            inputField.setText("");
            
            // Check if this is a for loop
            if (text.toLowerCase().startsWith("for(")) {
                processForLoop(text);
                return;
            }
            
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
            else {
                // Process shape commands
                if (codeGeneration.isRecordingLoop()) {
                    processCommandForLoop(text);
                } else {
                    processCommand(text);
                }
            }}
            else if (text.equalsIgnoreCase("clear")) {
                codeGeneration.clearScreen(ioHandler);
                drawingPanel.repaint();
                ioHandler.appendToHistory("System: Screen cleared\n");
            }
        }
    
    /**************************************************************************
    *    METHOD:    processForLoop                                           *
    *    DESCRIPTION:  Processes a for loop command                          *
    *    PARAMETERS:  String command - the for loop command to process       *
    *    RETURN VALUE:  None                                                 *
    **************************************************************************/
    protected void processForLoop(String command) {
        try {
            // Parse for loop syntax: for(var=start;condition;increment): command
            String pattern = "for\\s*\\(\\s*([^=]+)=([^;]+);([^;]+);([^)]+)\\)\\s*:\\s*(.+)";
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = regex.matcher(command);
            
            if (matcher.find()) {
                String varName = matcher.group(1).trim();
                String startExpr = matcher.group(2).trim();
                String condition = matcher.group(3).trim();
                String increment = matcher.group(4).trim();
                String loopCommand = matcher.group(5).trim();
                
                // Parse start value (handle expressions like 50+x)
                String expandedStart = expandVariables(startExpr);
                int startValue = evaluateSimpleMath(expandedStart);
                
                // Set initial variable value
                ioHandler.setVariable(varName, startValue);
                
                // Execute the loop
                int iterationCount = 0;
                int maxIterations = 1000; // Safety limit to prevent infinite loops
                
                ioHandler.appendToHistory("System: Starting for loop with " + varName + "=" + startValue + "\n");
                
                while (iterationCount < maxIterations) {
                    // Check condition
                    if (!evaluateCondition(varName, condition)) {
                        ioHandler.appendToHistory("System: Loop condition failed, breaking\n");
                        break;
                    }
                    
                    // Execute the command with current variable value
                    String expandedCommand = expandVariables(loopCommand);
                    ioHandler.appendToHistory("System: Iteration " + (iterationCount + 1) + ": " + varName + "=" + 
                                             ioHandler.getVariable(varName) + ", executing: " + expandedCommand + "\n");
                    
                    if (codeGeneration.isRecordingLoop()) {
                        processCommandForLoop(expandedCommand);
                    } else {
                        processCommand(expandedCommand);
                    }
                    
                    // Apply increment
                    applyIncrement(varName, increment);
                    
                    iterationCount++;
                }
                
                if (iterationCount >= maxIterations) {
                    ioHandler.appendToHistory("System: Loop terminated after " + maxIterations + " iterations (safety limit)\n");
                } else {
                    ioHandler.appendToHistory("System: Loop completed with " + iterationCount + " iterations\n");
                }
                
                drawingPanel.repaint();
            } else {
                ioHandler.appendToHistory("System: Invalid for loop syntax. Use: for(var=start;condition;increment): command\n");
                ioHandler.appendToHistory("Example: for(x=0;x<=200;x+=5): circle,50+x,250,250\n");
            }
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error processing for loop: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    /**************************************************************************
    *    METHOD:    evaluateCondition                                        *
    *    DESCRIPTION:  Evaluates a loop condition                            *
    *    PARAMETERS:  String varName - the loop variable name                *
    *                 String condition - the condition to evaluate           *
    *    RETURN VALUE:  boolean - true if condition is met, false otherwise  *
    **************************************************************************/
    private boolean evaluateCondition(String varName, String condition) {
        try {
            // Get current variable value
            Integer currentValue = ioHandler.getVariable(varName);
            if (currentValue == null) {
                return false;
            }
            
            // Parse condition (supports: var < value, var <= value, var > value, var >= value, var == value)
            if (condition.contains("<=")) {
                String[] parts = condition.split("<=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue <= compareValue;
                }
            } else if (condition.contains(">=")) {
                String[] parts = condition.split(">=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue >= compareValue;
                }
            } else if (condition.contains("<")) {
                String[] parts = condition.split("<");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue < compareValue;
                }
            } else if (condition.contains(">")) {
                String[] parts = condition.split(">");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue > compareValue;
                }
            } else if (condition.contains("==")) {
                String[] parts = condition.split("==");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue == compareValue;
                }
            } else if (condition.contains("!=")) {
                String[] parts = condition.split("!=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int compareValue = ioHandler.parseValue(parts[1].trim());
                    return currentValue != compareValue;
                }
            }
            
            return false;
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error evaluating condition: " + e.getMessage() + "\n");
            return false;
        }
    }

    /**************************************************************************
    *    METHOD:    applyIncrement                                           *
    *    DESCRIPTION:  Applies an increment to a variable                    *
    *    PARAMETERS:  String varName - the variable name                     *
    *                 String increment - the increment expression            *
    *    RETURN VALUE:  None                                                 *
    **************************************************************************/
    private void applyIncrement(String varName, String increment) {
        try {
            Integer currentValue = ioHandler.getVariable(varName);
            if (currentValue == null) {
                ioHandler.appendToHistory("System: Variable " + varName + " not found\n");
                return;
            }
            
            // Parse increment (supports: var++, var--, var+=value, var-=value, var=value)
            if (increment.equals(varName + "++")) {
                ioHandler.setVariable(varName, currentValue + 1);
            } else if (increment.equals(varName + "--")) {
                ioHandler.setVariable(varName, currentValue - 1);
            } else if (increment.contains("+=")) {
                String[] parts = increment.split("\\+=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int incrementValue = ioHandler.parseValue(parts[1].trim());
                    ioHandler.setVariable(varName, currentValue + incrementValue);
                }
            } else if (increment.contains("-=")) {
                String[] parts = increment.split("-=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int decrementValue = ioHandler.parseValue(parts[1].trim());
                    ioHandler.setVariable(varName, currentValue - decrementValue);
                }
            } else if (increment.contains("=")) {
                String[] parts = increment.split("=");
                if (parts.length == 2 && parts[0].trim().equals(varName)) {
                    int newValue = ioHandler.parseValue(parts[1].trim());
                    ioHandler.setVariable(varName, newValue);
                }
            } else {
                ioHandler.appendToHistory("System: Invalid increment expression: " + increment + "\n");
            }
            
            // Debug output to see the increment working
            ioHandler.appendToHistory("System: Incremented " + varName + " to " + ioHandler.getVariable(varName) + "\n");
            
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error applying increment: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    /**************************************************************************
    *    METHOD:    expandVariables                                          *
    *    DESCRIPTION:  Expands variables and evaluates mathematical expressions*
    *    PARAMETERS:  String command - the command with variables            *
    *    RETURN VALUE:  String - the command with variables expanded         *
    **************************************************************************/
    private String expandVariables(String command) {
        // First, replace all variables with their values
        String result = command;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b");
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            Integer value = ioHandler.getVariable(varName);
            if (value != null && !isReservedWord(varName)) {
                result = result.replace(varName, value.toString());
            }
        }
        
        // Now evaluate mathematical expressions in parentheses
        result = evaluateMathExpressions(result);
        
        return result;
    }

    /**************************************************************************
    *    METHOD:    evaluateMathExpressions                                  *
    *    DESCRIPTION:  Evaluates mathematical expressions in parentheses     *
    *    PARAMETERS:  String input - the string with math expressions        *
    *    RETURN VALUE:  String - the string with math evaluated              *
    **************************************************************************/
    private String evaluateMathExpressions(String input) {
        // Pattern to find expressions like (50+x) or (100+i*20)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(([^()]+)\\)");
        java.util.regex.Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String expression = matcher.group(1);
            try {
                int value = evaluateSimpleMath(expression);
                matcher.appendReplacement(result, String.valueOf(value));
            } catch (Exception e) {
                // If we can't evaluate it, leave it as is
                matcher.appendReplacement(result, "(" + expression + ")");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    /**************************************************************************
    *    METHOD:    evaluateSimpleMath                                       *
    *    DESCRIPTION:  Evaluates simple mathematical expressions             *
    *    PARAMETERS:  String expression - the math expression to evaluate    *
    *    RETURN VALUE:  int - the result of the evaluation                   *
    **************************************************************************/
    private int evaluateSimpleMath(String expression) {
        try {
            // Remove spaces
            String expr = expression.replaceAll("\\s+", "");
            
            // Handle multiplication first (higher precedence)
            if (expr.contains("*")) {
                String[] parts = expr.split("\\*");
                if (parts.length == 2) {
                    int left = evaluateSimpleMath(parts[0]);
                    int right = evaluateSimpleMath(parts[1]);
                    return left * right;
                }
            }
            
            // Handle addition and subtraction
            if (expr.contains("+") && !expr.startsWith("+")) {
                String[] parts = expr.split("\\+");
                if (parts.length == 2) {
                    int left = evaluateSimpleMath(parts[0]);
                    int right = evaluateSimpleMath(parts[1]);
                    return left + right;
                }
            }
            
            if (expr.contains("-") && !expr.startsWith("-")) {
                String[] parts = expr.split("-");
                if (parts.length == 2) {
                    int left = evaluateSimpleMath(parts[0]);
                    int right = evaluateSimpleMath(parts[1]);
                    return left - right;
                }
            }
            
            // If no operators, just parse the number
            return Integer.parseInt(expr);
        } catch (Exception e) {
            throw new RuntimeException("Invalid mathematical expression: " + expression);
        }
    }

    /**************************************************************************
    *    METHOD:    isReservedWord                                           *
    *    DESCRIPTION:  Checks if a word is a reserved keyword                *
    *    PARAMETERS:  String word - the word to check                        *
    *    RETURN VALUE:  boolean - true if reserved, false otherwise          *
    **************************************************************************/
    private boolean isReservedWord(String word) {
        String[] reservedWords = {"circle", "triangle", "rectangle", "square", "polygon", 
                                 "endpolygon", "clearpolygon", "points", "fill", "set", 
                                 "move", "home", "for", "loop"};
        for (String reserved : reservedWords) {
            if (reserved.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
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
        ioHandler.appendToHistory("square, x, y, size\n\n");
        
        ioHandler.appendToHistory("polygon - Start adding points by clicking on the drawing area\n");
        ioHandler.appendToHistory("endpolygon - Finish drawing the polygon\n");
        ioHandler.appendToHistory("clearpolygon - Clear current polygon points\n");
        ioHandler.appendToHistory("points, x1,y1 x2,y2 x3,y3 ... - Draw polygon with specified points\n\n");
        
        ioHandler.appendToHistory("for(var=start;condition;increment): command - Execute a for loop\n");
        ioHandler.appendToHistory("  Example: for(x=0;x<=200;x+=5): circle,50+x,250,250\n\n");
        ioHandler.appendToHistory("set var=value - Set a variable (e.g., set size=50)\n");
        ioHandler.appendToHistory("show vars - Show all variables\n");
        ioHandler.appendToHistory("clear vars - Clear all variables\n\n");
        
        ioHandler.appendToHistory("fill on / fill off - Toggle fill mode for shapes\n");
        ioHandler.appendToHistory("clear - Clear all shapes from the screen\n");
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
        SwingUtilities.invokeLater(() -> new UserInterface3());
    }
    /**************************************************************************
     *    METHOD:    getIOHandler                                              *
     *    DESCRIPTION:  Returns the IO handler instance                        *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  InputOutputHandler3 - the IO handler                  *
     **************************************************************************/
     public InputOutputHandler3 getIOHandler() {
         return ioHandler;
     }

     /**************************************************************************
     *    METHOD:    getCodeGeneration                                         *
     *    DESCRIPTION:  Returns the code generation instance                   *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  CodeGeneration3 - the code generation instance        *
     **************************************************************************/
     public CodeGeneration3 getCodeGeneration() {
         return codeGeneration;
     }
}





































class DrawingPanel extends JPanel {
    private InputOutputHandler3 ioHandler;
    private CodeGeneration3 codeGeneration;
    private Point mousePosition = new Point(-1, -1);
    private Point fixedCrosshairPosition = null;
    private boolean showCrosshair = false;
    
    /**************************************************************************
    *    METHOD:    setIOHandler                                              *
    *    DESCRIPTION:  Sets the input/output handler for the drawing panel    *
    *    PARAMETERS:  InputOutputHandler ioHandler - the IO handler to set    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setIOHandler(InputOutputHandler3 ioHandler) {
        this.ioHandler = ioHandler;
    }
    
    /**************************************************************************
    *    METHOD:    setCodeGeneration                                         *
    *    DESCRIPTION:  Sets the code generation for the drawing panel         *
    *    PARAMETERS:  CodeGeneration codeGeneration - the code generation to set *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setCodeGeneration(CodeGeneration3 codeGeneration) {
        this.codeGeneration = codeGeneration;
        
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
                    ioHandler.appendToHistory("Crosshair fixed at: (" + fixedCrosshairPosition.x + ", " + fixedCrosshairPosition.y + ")\n");
                } else {
                    // Clear fixed crosshair
                    fixedCrosshairPosition = null;
                    showCrosshair = true;
                    ioHandler.appendToHistory("Crosshair released\n");
                }
                repaint();
            }
        });
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
            for (CodeGeneration3.Shape shape : codeGeneration.getShapes()) {
                shape.draw(g);
            }
            
            // Draw loop shapes (temporary preview)
            for (CodeGeneration3.Shape shape : codeGeneration.getLoopShapes()) {
                shape.draw(g);
            }
            
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
            
            // Draw crosshair and coordinates
            if (showCrosshair) {
                Point crosshairPoint = (fixedCrosshairPosition != null) ? fixedCrosshairPosition : mousePosition;
                
                g.setColor(Color.GRAY);
                
                // Draw horizontal line
                g.drawLine(0, crosshairPoint.y, getWidth(), crosshairPoint.y);
                
                // Draw vertical line
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
        }
    }
    
   



}