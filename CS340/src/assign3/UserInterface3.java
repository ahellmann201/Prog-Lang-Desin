package assign3;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Drawing Chat Application - Enhanced User Interface with Code Editor
 * 
 * PROGRAMMER: [Your Name] 
 * COURSE: CS340 Programming Lang/Design 
 * DATE: [Current Date] 
 * REQUIREMENT: Assignment 7 - Control Structures & Multi-line Editor
 * 
 * DESCRIPTION:
 * This file contains the main user interface with multi-line code editor support,
 * control structure implementation (if/while statements), and enhanced interpreter
 * functionality. It replaces the single-line input with a full code editor.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 * 
 * CREDITS:
 * Java Swing documentation, JTextArea component usage
 */
public class UserInterface3 extends JFrame {
    private JTextArea historyArea;
    private JTextArea inputArea;  // Multi-line input area
    private JTextArea lineNumberArea;
    private DrawingPanel drawingPanel;
    private InputOutputHandler3 ioHandler;
    private CodeGeneration3 codeGeneration;
    private AnimationSystem animationSystem;
    private TokenEncoder tokenEncoder;
    private Interpreter interpreter;
    private JToggleButton interpreterModeButton;

    private JToggleButton fillButton;
    private JButton clearScreenButton;
    private JButton executeButton; // Execute all code at once
    private JToggleButton encodingButton;
    private JButton undoButton;
    private JButton clearCodeButton;
    
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private boolean encodingMode = false;
    
    /********************************************************************
     * METHOD: UserInterface3 Constructor
     * DESCRIPTION: Initializes the main application window with multi-line editor
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public UserInterface3() {
        setTitle("Drawing Chat Application - Code Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Initialize handlers
        ioHandler = new InputOutputHandler3();
        codeGeneration = new CodeGeneration3();
        ioHandler.setCodeGeneration(codeGeneration);
        animationSystem = new AnimationSystem(this);
        tokenEncoder = new TokenEncoder();
        interpreter = new Interpreter(tokenEncoder, ioHandler);
        
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
    
    /********************************************************************
     * METHOD: createUIComponents
     * DESCRIPTION: Creates all UI components including multi-line editor
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void createUIComponents() {
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Multi-line input area for writing full programs
        inputArea = new JTextArea(10, 50);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputArea.setTabSize(2); // Set tab to 2 spaces for indentation
        
        // Line number area for the editor
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setEditable(false);
        lineNumberArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        drawingPanel = new DrawingPanel();
        drawingPanel.setIOHandler(ioHandler);
        drawingPanel.setCodeGeneration(codeGeneration);
        
        // Create control buttons
        fillButton = new JToggleButton("Fill: OFF");
        clearScreenButton = new JButton("Clear Screen");
        executeButton = new JButton("Execute Code"); // Execute entire program
        encodingButton = new JToggleButton("Encoding: OFF");
        undoButton = new JButton("Undo");
        clearCodeButton = new JButton("Clear Code");
        interpreterModeButton = new JToggleButton("Interpreter: Actual");
    }
    
    /********************************************************************
     * METHOD: setupLayout
     * DESCRIPTION: Arranges all UI components with split panes for editor
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void setupLayout() {
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(400, 0));
        
        // Create editor panel with line numbers
        JPanel editorPanel = new JPanel(new BorderLayout());
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setRowHeaderView(lineNumberArea); // Add line numbers on left
        editorPanel.add(new JLabel("Code Editor (Write your program here):"), BorderLayout.NORTH);
        editorPanel.add(inputScroll, BorderLayout.CENTER);
        
        // Button panel for editor controls
        JPanel editorButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editorButtonPanel.add(executeButton);
        editorButtonPanel.add(clearCodeButton);
        editorPanel.add(editorButtonPanel, BorderLayout.SOUTH);
        
        // Main split pane: history on left, drawing on right
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyScroll, drawingPanel);
        mainSplit.setDividerLocation(400);
        
        // Bottom split: main content on top, editor on bottom
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplit, editorPanel);
        bottomSplit.setDividerLocation(400);
        
        add(bottomSplit, BorderLayout.CENTER);
        
        // Control panel at the top
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(fillButton);
        controlPanel.add(clearScreenButton);
        controlPanel.add(undoButton);
        controlPanel.add(encodingButton);
        controlPanel.add(interpreterModeButton);
        
        // Menu bar for additional functionality
        JMenuBar menuBar = createMenuBar();
        controlPanel.add(menuBar);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Update line numbers when typing
        inputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }
        });
    }
    
    /********************************************************************
     * METHOD: createMenuBar
     * DESCRIPTION: Creates the menu bar with all menu items
     * PARAMETERS: None
     * RETURN VALUE: JMenuBar - the created menu bar
     ********************************************************************/
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadFileItem = new JMenuItem("Load File");
        JMenuItem saveCodeItem = new JMenuItem("Save Code");
        loadFileItem.addActionListener(e -> {
            FileReader3 fileReader = new FileReader3(this);
            fileReader.readFile();
        });
        saveCodeItem.addActionListener(e -> saveCodeToFile());
        fileMenu.add(loadFileItem);
        fileMenu.add(saveCodeItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem formatCodeItem = new JMenuItem("Format Code");
        JMenuItem commentCodeItem = new JMenuItem("Comment Selection");
        formatCodeItem.addActionListener(e -> formatCode());
        commentCodeItem.addActionListener(e -> commentSelection());
        editMenu.add(formatCodeItem);
        editMenu.add(commentCodeItem);
        
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
                ioHandler.appendToHistory("System: Saved loop as " + loopName + "\n");
            }
        });
        playLoopItem.addActionListener(e -> {
            List<Loop> loops = codeGeneration.getLoops();
            if (loops.isEmpty()) {
                ioHandler.appendToHistory("System: No loops saved\n");
                return;
            }
            String[] loopNames = new String[loops.size()];
            for (int i = 0; i < loops.size(); i++) {
                loopNames[i] = loops.get(i).getName();
            }
            String selectedLoop = ioHandler.showSelectionDialog("Play Loop", 
                    "Select a loop to play:", loopNames);
            if (selectedLoop != null) {
                codeGeneration.playLoop(selectedLoop, drawingPanel, ioHandler);
                ioHandler.appendToHistory("System: Playing loop \"" + selectedLoop + "\"\n");
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
        
        // Animation menu
        JMenu animationMenu = new JMenu("Animation");
        JMenuItem createAnimation = new JMenuItem("Create Animation");
        JMenuItem addKeyframeItem = new JMenuItem("Add Keyframe");
        JMenuItem playAnimation = new JMenuItem("Play Animation");
        JMenuItem stopAnimation = new JMenuItem("Stop Animation");
        JMenuItem setDelayItem = new JMenuItem("Set Frame Delay");
        createAnimation.addActionListener(e -> {
            String animName = ioHandler.showInputDialog("Enter animation name:");
            if (animName != null && !animName.trim().isEmpty()) {
                animationSystem.createAnimation(animName);
            }
        });
        addKeyframeItem.addActionListener(e -> {
            String frameName = ioHandler.showInputDialog("Enter keyframe name:");
            if (frameName != null && !frameName.trim().isEmpty()) {
                animationSystem.addKeyframe(frameName);
            }
        });
        playAnimation.addActionListener(e -> {
            List<AnimationSystem.Animation> anims = animationSystem.getAnimations();
            if (anims.isEmpty()) {
                ioHandler.appendToHistory("System: No animations created\n");
                return;
            }
            String[] animNames = new String[anims.size()];
            for (int i = 0; i < anims.size(); i++) {
                animNames[i] = anims.get(i).getName();
            }
            String selectedAnim = ioHandler.showSelectionDialog("Play Animation", 
                    "Select animation to play:", animNames);
            if (selectedAnim != null) {
                animationSystem.playAnimation(selectedAnim);
            }
        });
        stopAnimation.addActionListener(e -> {
            animationSystem.stopAnimation();
        });
        setDelayItem.addActionListener(e -> {
            String delayStr = ioHandler.showInputDialog("Enter frame delay in milliseconds:");
            if (delayStr != null && !delayStr.trim().isEmpty()) {
                try {
                    long delay = Long.parseLong(delayStr);
                    animationSystem.setFrameDelay(delay);
                } catch (NumberFormatException ex) {
                    ioHandler.appendToHistory("System: Invalid delay value\n");
                }
            }
        });
        animationMenu.add(createAnimation);
        animationMenu.add(addKeyframeItem);
        animationMenu.add(playAnimation);
        animationMenu.add(stopAnimation);
        animationMenu.add(setDelayItem);
        
        // Control Structures menu (NEW)
        JMenu controlMenu = new JMenu("Control");
        JMenuItem insertIfItem = new JMenuItem("Insert If Statement");
        JMenuItem insertWhileItem = new JMenuItem("Insert While Loop");
        JMenuItem insertForItem = new JMenuItem("Insert For Loop");
        JMenuItem insertBlockItem = new JMenuItem("Insert Code Block {}");
        insertIfItem.addActionListener(e -> insertTemplate("if"));
        insertWhileItem.addActionListener(e -> insertTemplate("while"));
        insertForItem.addActionListener(e -> insertTemplate("for"));
        insertBlockItem.addActionListener(e -> insertTemplate("block"));
        controlMenu.add(insertIfItem);
        controlMenu.add(insertWhileItem);
        controlMenu.add(insertForItem);
        controlMenu.add(insertBlockItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem showHelpItem = new JMenuItem("Show Commands");
        JMenuItem aboutItem = new JMenuItem("About");
        showHelpItem.addActionListener(e -> displayHelp());
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(showHelpItem);
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(controlMenu);
        menuBar.add(loopMenu);
        menuBar.add(varsMenu);
        menuBar.add(animationMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /********************************************************************
     * METHOD: setupEventHandlers
     * DESCRIPTION: Sets up event listeners for all interactive components
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void setupEventHandlers() {
        // Execute button - runs entire program
        executeButton.addActionListener(e -> executeProgram());
        
        // Clear code button
        clearCodeButton.addActionListener(e -> {
            inputArea.setText("");
            updateLineNumbers();
            ioHandler.appendToHistory("System: Code editor cleared\n");
        });
        
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
        
        // Encoding mode button
        encodingButton.addActionListener(e -> {
            encodingMode = encodingButton.isSelected();
            encodingButton.setText(encodingMode ? "Encoding: ON" : "Encoding: OFF");
            if (encodingMode) {
                tokenEncoder.clear(); // Clear previous encoding state
                ioHandler.appendToHistory("System: Encoding mode enabled\n");
                ioHandler.appendToHistory("Type 'show encoding' to display current encoding state\n");
            } else {
                ioHandler.appendToHistory("System: Encoding mode disabled\n");
            }
        });
        
        // Undo button
        undoButton.addActionListener(e -> {
            if (codeGeneration.undoLastCommand(ioHandler)) {
                drawingPanel.repaint();
            }
        });
        
        // Interpreter mode button
        interpreterModeButton.addActionListener(e -> {
            boolean verbose = interpreterModeButton.isSelected();
            interpreter.setVerboseMode(verbose);
            interpreterModeButton.setText(verbose ? "Interpreter: Verbose" : "Interpreter: Actual");
            ioHandler.appendToHistory("System: Interpreter mode set to " + 
                    (verbose ? "verbose\n" : "actual\n"));
        });
        
        // Add keyboard shortcut for execute (Ctrl+Enter)
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeProgram();
                    e.consume();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveCodeToFile();
                    e.consume();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                    formatCode();
                    e.consume();
                }
            }
        });
        
        // Mouse listener for polygon drawing (unchanged from original)
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
    
    /********************************************************************
     * METHOD: executeProgram
     * DESCRIPTION: Executes the entire program from the code editor
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void executeProgram() {
        String program = inputArea.getText().trim();
        if (program.isEmpty()) {
            ioHandler.appendToHistory("System: No code to execute\n");
            return;
        }
        
        ioHandler.appendToHistory("\n=== EXECUTING PROGRAM ===\n");
        
        // Save to command history
        commandHistory.add(program);
        historyIndex = commandHistory.size();
        
        // Parse and execute the program
        CodeBlockParser parser = new CodeBlockParser();
        List<CodeStatement> statements = parser.parseProgram(program);
        
        for (CodeStatement stmt : statements) {
            if (encodingMode) {
                List<Integer> encodedLine = tokenEncoder.encodeCommand(stmt.getSource());
                ioHandler.appendToHistory("Encoded: " + encodedLine + "\n");
            }
            
            // Process the statement
            processCodeStatement(stmt);
        }
        
        ioHandler.appendToHistory("=== PROGRAM COMPLETED ===\n");
        drawingPanel.repaint();
    }
    
    /********************************************************************
     * METHOD: processCodeStatement
     * DESCRIPTION: Processes a single code statement (handles control structures)
     * PARAMETERS: CodeStatement stmt - the statement to process
     * RETURN VALUE: None
     ********************************************************************/
    private void processCodeStatement(CodeStatement stmt) {
        String line = stmt.getSource().trim();
        
        // Skip empty lines
        if (line.isEmpty()) {
            return;
        }
        
        // Log the line being executed
        ioHandler.appendToHistory("Executing: " + line + "\n");
        
        // Handle interpreter commands
        if (line.startsWith("integer ") || line.startsWith("input ") || 
            line.startsWith("print ") || line.startsWith("if ") || 
            line.startsWith("while ") || line.startsWith("line ")) {
            interpreter.interpretLine(line);
            return;
        }
        
        // Handle for loops (legacy syntax)
        if (line.toLowerCase().startsWith("for")) {
            processForLoop(line);
            return;
        }
        
        // Process regular commands
        String lowerCommand = line.toLowerCase();
        
        // Use switch for better organization
        switch (getFirstWord(lowerCommand)) {
            case "polygon":
                ioHandler.setDrawingPolygon(true);
                ioHandler.clearPolygonPoints();
                ioHandler.appendToHistory("System: Click to add polygon points. Type 'endpolygon' when done.\n");
                break;
            case "endpolygon":
                if (ioHandler.isDrawingPolygon()) {
                    ioHandler.setDrawingPolygon(false);
                    if (ioHandler.getPolygonPoints().size() >= 3) {
                        codeGeneration.endPolygon(ioHandler, codeGeneration.isRecordingLoop());
                        drawingPanel.repaint();
                    } else {
                        ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
                    }
                }
                break;
            case "clearpolygon":
                ioHandler.clearPolygonPoints();
                drawingPanel.repaint();
                ioHandler.appendToHistory("System: Polygon points cleared\n");
                break;
            case "fill":
                handleFillCommand(line);
                break;
            case "set":
                handleSetCommand(line);
                break;
            case "animation":
                handleAnimationCommand(line);
                break;
            case "keyframe":
                handleKeyframeCommand(line);
                break;
            case "play":
                handlePlayCommand(line);
                break;
            case "stop":
                animationSystem.stopAnimation();
                break;
            case "delay":
                handleDelayCommand(line);
                break;
            case "clear":
                codeGeneration.clearScreen(ioHandler);
                drawingPanel.repaint();
                ioHandler.appendToHistory("System: Screen cleared\n");
                break;
            case "help":
                displayHelp();
                break;
            default:
                // Process shape commands
                if (isShapeCommand(lowerCommand)) {
                    if (codeGeneration.isRecordingLoop()) {
                        processCommandForLoop(line);
                    } else {
                        processDrawingCommand(line);
                    }
                } else {
                    ioHandler.appendToHistory("System: Unknown command: " + line + "\n");
                }
                break;
        }
    }
    
    /********************************************************************
     * METHOD: getFirstWord
     * DESCRIPTION: Extracts the first word from a command string
     * PARAMETERS: String command - the command string
     * RETURN VALUE: String - the first word
     ********************************************************************/
    private String getFirstWord(String command) {
        String[] parts = command.split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }
    
    /********************************************************************
     * METHOD: isShapeCommand
     * DESCRIPTION: Checks if a command is a shape drawing command
     * PARAMETERS: String command - the command to check
     * RETURN VALUE: boolean - true if it's a shape command
     ********************************************************************/
    private boolean isShapeCommand(String command) {
        return command.startsWith("circle") || command.startsWith("triangle") || 
               command.startsWith("rectangle") || command.startsWith("square") || 
               command.startsWith("points") || command.startsWith("line");
    }
    
    /********************************************************************
     * METHOD: handleFillCommand
     * DESCRIPTION: Processes the fill command
     * PARAMETERS: String command - the fill command
     * RETURN VALUE: None
     ********************************************************************/
    private void handleFillCommand(String command) {
        String[] parts = command.split(" ");
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
    
    /********************************************************************
     * METHOD: handleSetCommand
     * DESCRIPTION: Processes the set variable command
     * PARAMETERS: String command - the set command
     * RETURN VALUE: None
     ********************************************************************/
    private void handleSetCommand(String command) {
        String[] setParts = command.substring(4).split("=");
        if (setParts.length == 2) {
            String varName = setParts[0].trim();
            try {
                int value = Integer.parseInt(setParts[1].trim());
                ioHandler.setVariable(varName, value);
                ioHandler.appendToHistory("System: Set variable " + varName + " = " + value + "\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid value for variable. Use: set var=value\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid set command. Use: set var=value\n");
        }
    }
    
    /********************************************************************
     * METHOD: handleAnimationCommand
     * DESCRIPTION: Processes animation creation command
     * PARAMETERS: String command - the animation command
     * RETURN VALUE: None
     ********************************************************************/
    private void handleAnimationCommand(String command) {
        if (command.length() > 10) {
            String animName = command.substring(10).trim();
            animationSystem.createAnimation(animName);
        } else {
            ioHandler.appendToHistory("System: Usage: animation <name>\n");
        }
    }
    
    /********************************************************************
     * METHOD: handleKeyframeCommand
     * DESCRIPTION: Processes keyframe creation command
     * PARAMETERS: String command - the keyframe command
     * RETURN VALUE: None
     ********************************************************************/
    private void handleKeyframeCommand(String command) {
        if (command.length() > 9) {
            String frameName = command.substring(9).trim();
            animationSystem.addKeyframe(frameName);
        } else {
            ioHandler.appendToHistory("System: Usage: keyframe <name>\n");
        }
    }
    
    /********************************************************************
     * METHOD: handlePlayCommand
     * DESCRIPTION: Processes animation play command
     * PARAMETERS: String command - the play command
     * RETURN VALUE: None
     ********************************************************************/
    private void handlePlayCommand(String command) {
        if (command.length() > 5) {
            String animName = command.substring(5).trim();
            animationSystem.playAnimation(animName);
        } else {
            ioHandler.appendToHistory("System: Usage: play <animationName>\n");
        }
    }
    
    /********************************************************************
     * METHOD: handleDelayCommand
     * DESCRIPTION: Processes animation delay command
     * PARAMETERS: String command - the delay command
     * RETURN VALUE: None
     ********************************************************************/
    private void handleDelayCommand(String command) {
        if (command.length() > 6) {
            try {
                long delay = Long.parseLong(command.substring(6).trim());
                animationSystem.setFrameDelay(delay);
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid delay value. Usage: delay <milliseconds>\n");
            }
        } else {
            ioHandler.appendToHistory("System: Usage: delay <milliseconds>\n");
        }
    }
    
    /********************************************************************
     * METHOD: processDrawingCommand
     * DESCRIPTION: Processes a drawing command
     * PARAMETERS: String command - the drawing command
     * RETURN VALUE: None
     ********************************************************************/
    private void processDrawingCommand(String command) {
        String lowerCommand = command.toLowerCase();
        if (lowerCommand.startsWith("circle")) {
            codeGeneration.processCircleCommand(command, ioHandler);
        } else if (lowerCommand.startsWith("triangle")) {
            codeGeneration.processTriangleCommand(command, ioHandler);
        } else if (lowerCommand.startsWith("rectangle")) {
            codeGeneration.processRectangleCommand(command, ioHandler);
        } else if (lowerCommand.startsWith("square")) {
            codeGeneration.processSquareCommand(command, ioHandler);
        } else if (lowerCommand.startsWith("points")) {
            codeGeneration.processPointsCommand(command, ioHandler);
        } else if (lowerCommand.startsWith("line")) {
            codeGeneration.processLineCommand(command, ioHandler);
        } else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        drawingPanel.repaint();
    }
    
    /********************************************************************
     * METHOD: processCommandForLoop
     * DESCRIPTION: Processes a command for loop recording
     * PARAMETERS: String command - the command to process
     * RETURN VALUE: None
     ********************************************************************/
    private void processCommandForLoop(String command) {
        String lowerCommand = command.toLowerCase();
        if (lowerCommand.startsWith("circle")) {
            codeGeneration.processCircleCommandForLoop(command, ioHandler);
        } else if (lowerCommand.startsWith("triangle")) {
            codeGeneration.processTriangleCommandForLoop(command, ioHandler);
        } else if (lowerCommand.startsWith("rectangle")) {
            codeGeneration.processRectangleCommandForLoop(command, ioHandler);
        } else if (lowerCommand.startsWith("square")) {
            codeGeneration.processSquareCommandForLoop(command, ioHandler);
        } else if (lowerCommand.startsWith("points")) {
            codeGeneration.processPointsCommandForLoop(command, ioHandler);
        } else if (lowerCommand.startsWith("line")) {
            codeGeneration.processLineCommandForLoop(command, ioHandler);
        } else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        drawingPanel.repaint();
    }
    
    /********************************************************************
     * METHOD: updateLineNumbers
     * DESCRIPTION: Updates the line number display based on editor content
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void updateLineNumbers() {
        String content = inputArea.getText();
        int lines = content.isEmpty() ? 1 : content.split("\n").length;
        StringBuilder numbers = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            numbers.append(i).append("\n");
        }
        lineNumberArea.setText(numbers.toString());
    }
    
    /********************************************************************
     * METHOD: insertTemplate
     * DESCRIPTION: Inserts a code template at the current cursor position
     * PARAMETERS: String type - the type of template to insert
     * RETURN VALUE: None
     ********************************************************************/
    private void insertTemplate(String type) {
        String template = "";
        
        switch (type) {
            case "if":
                template = "if (condition) {\n    // code to execute if true\n}\n";
                break;
            case "while":
                template = "while (condition) {\n    // code to repeat\n}\n";
                break;
            case "for":
                template = "for (variable = start; condition; increment) {\n    // loop body\n}\n";
                break;
            case "block":
                template = "{\n    // code block\n}\n";
                break;
        }
        
        inputArea.insert(template, inputArea.getCaretPosition());
        updateLineNumbers();
    }
    
    /********************************************************************
     * METHOD: formatCode
     * DESCRIPTION: Formats the code with proper indentation
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void formatCode() {
        String code = inputArea.getText();
        CodeFormatter formatter = new CodeFormatter();
        String formatted = formatter.format(code);
        inputArea.setText(formatted);
        updateLineNumbers();
        ioHandler.appendToHistory("System: Code formatted\n");
    }
    
    /********************************************************************
     * METHOD: commentSelection
     * DESCRIPTION: Comments or uncomments the selected lines
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void commentSelection() {
        int start = inputArea.getSelectionStart();
        int end = inputArea.getSelectionEnd();
        
        if (start == end) {
            ioHandler.appendToHistory("System: No text selected\n");
            return;
        }
        
        try {
            int startLine = inputArea.getLineOfOffset(start);
            int endLine = inputArea.getLineOfOffset(end);
            
            String text = inputArea.getText();
            String[] lines = text.split("\n");
            
            // Check if all selected lines are already commented
            boolean allCommented = true;
            for (int i = startLine; i <= endLine && i < lines.length; i++) {
                if (!lines[i].trim().startsWith("//") && !lines[i].trim().isEmpty()) {
                    allCommented = false;
                    break;
                }
            }
            
            // Toggle comments
            StringBuilder newText = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i >= startLine && i <= endLine) {
                    if (allCommented) {
                        // Remove comment
                        if (lines[i].trim().startsWith("//")) {
                            newText.append(lines[i].replaceFirst("//", ""));
                        } else {
                            newText.append(lines[i]);
                        }
                    } else {
                        // Add comment
                        newText.append("//").append(lines[i]);
                    }
                } else {
                    newText.append(lines[i]);
                }
                if (i < lines.length - 1) {
                    newText.append("\n");
                }
            }
            
            inputArea.setText(newText.toString());
            updateLineNumbers();
            ioHandler.appendToHistory("System: Lines " + (startLine + 1) + "-" + (endLine + 1) + 
                    (allCommented ? " uncommented\n" : " commented\n"));
        } catch (BadLocationException e) {
            ioHandler.appendToHistory("System: Error commenting selection\n");
        }
    }
    
    /********************************************************************
     * METHOD: saveCodeToFile
     * DESCRIPTION: Saves the current code to a file
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void saveCodeToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Code");
        fileChooser.setSelectedFile(new java.io.File("program.txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(inputArea.getText());
                ioHandler.appendToHistory("System: Code saved to " + fileChooser.getSelectedFile().getName() + "\n");
            } catch (Exception e) {
                ioHandler.appendToHistory("System: Error saving file: " + e.getMessage() + "\n");
            }
        }
    }
    
    /********************************************************************
     * METHOD: displayHelp
     * DESCRIPTION: Displays help message with available commands
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void displayHelp() {
        ioHandler.appendToHistory("\n=== DRAWING CHAT APPLICATION - HELP ===\n");
        ioHandler.appendToHistory("Write your program in the code editor below, then click 'Execute Code'.\n");
        ioHandler.appendToHistory("Or use Ctrl+Enter to execute.\n\n");
        
        ioHandler.appendToHistory("BASIC SHAPES:\n");
        ioHandler.appendToHistory("circle, radius, x, y\n");
        ioHandler.appendToHistory("triangle, x1, y1, x2, y2, x3, y3\n");
        ioHandler.appendToHistory("rectangle, x1, y1, x2, y2\n");
        ioHandler.appendToHistory("square, x, y, size\n");
        ioHandler.appendToHistory("line, x1, y1, x2, y2\n\n");
        
        ioHandler.appendToHistory("POLYGON COMMANDS:\n");
        ioHandler.appendToHistory("polygon - Start adding points by clicking\n");
        ioHandler.appendToHistory("endpolygon - Finish drawing the polygon\n");
        ioHandler.appendToHistory("clearpolygon - Clear current polygon points\n");
        ioHandler.appendToHistory("points, x1,y1 x2,y2 x3,y3 ... - Draw polygon with points\n\n");
        
        ioHandler.appendToHistory("CONTROL STRUCTURES:\n");
        ioHandler.appendToHistory("if (condition) { ... } - Conditional execution\n");
        ioHandler.appendToHistory("while (condition) { ... } - While loop\n");
        ioHandler.appendToHistory("for (var=start; condition; increment) { ... } - For loop\n");
        ioHandler.appendToHistory("Example: if (x < 100) { circle, 10, x, 100 }\n\n");
        
        ioHandler.appendToHistory("VARIABLES:\n");
        ioHandler.appendToHistory("set var=value - Set a variable (e.g., set size=50)\n");
        ioHandler.appendToHistory("Variables can be used in expressions: circle, size, x+10, y-20\n\n");
        
        ioHandler.appendToHistory("INTERPRETER COMMANDS:\n");
        ioHandler.appendToHistory("integer var = value; - Declare integer variable\n");
        ioHandler.appendToHistory("input(var); - Get user input for variable\n");
        ioHandler.appendToHistory("print(var); - Print variable value\n\n");
        
        ioHandler.appendToHistory("ANIMATION COMMANDS:\n");
        ioHandler.appendToHistory("animation <name> - Create new animation\n");
        ioHandler.appendToHistory("keyframe <name> - Capture current shapes as keyframe\n");
        ioHandler.appendToHistory("play <name> - Play animation\n");
        ioHandler.appendToHistory("stop - Stop animation\n");
        ioHandler.appendToHistory("delay <ms> - Set frame delay\n\n");
        
        ioHandler.appendToHistory("OTHER COMMANDS:\n");
        ioHandler.appendToHistory("fill on/off - Toggle fill mode\n");
        ioHandler.appendToHistory("clear - Clear all shapes\n");
        ioHandler.appendToHistory("help - Show this message\n");
        ioHandler.appendToHistory("========================================\n");
    }
    
    /********************************************************************
     * METHOD: showAbout
     * DESCRIPTION: Shows the about dialog
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "Drawing Chat Application v2.0\n" +
            "Enhanced with multi-line code editor\n" +
            "and control structures (if/while)\n" +
            "\n" +
            "© 2025 CS340 Programming Language Design",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /********************************************************************
     * METHOD: getDrawingPanel
     * DESCRIPTION: Returns the drawing panel component
     * PARAMETERS: None
     * RETURN VALUE: DrawingPanel - the drawing panel
     ********************************************************************/
    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }
    
    /********************************************************************
     * METHOD: getIOHandler
     * DESCRIPTION: Returns the IO handler instance
     * PARAMETERS: None
     * RETURN VALUE: InputOutputHandler3 - the IO handler
     ********************************************************************/
    public InputOutputHandler3 getIOHandler() {
        return ioHandler;
    }
    
    /********************************************************************
     * METHOD: getCodeGeneration
     * DESCRIPTION: Returns the code generation instance
     * PARAMETERS: None
     * RETURN VALUE: CodeGeneration3 - the code generation
     ********************************************************************/
    public CodeGeneration3 getCodeGeneration() {
        return codeGeneration;
    }
    
    /********************************************************************
     * METHOD: getAnimationSystem
     * DESCRIPTION: Returns the animation system instance
     * PARAMETERS: None
     * RETURN VALUE: AnimationSystem - the animation system
     ********************************************************************/
    public AnimationSystem getAnimationSystem() {
        return animationSystem;
    }
    
    /********************************************************************
     * METHOD: getTokenEncoder
     * DESCRIPTION: Returns the token encoder instance
     * PARAMETERS: None
     * RETURN VALUE: TokenEncoder - the token encoder
     ********************************************************************/
    public TokenEncoder getTokenEncoder() {
        return tokenEncoder;
    }
    
    /********************************************************************
     * METHOD: processForLoop
     * DESCRIPTION: Processes a for loop command (legacy support)
     * PARAMETERS: String command - the for loop command
     * RETURN VALUE: None
     ********************************************************************/
    protected void processForLoop(String command) {
        try {
            // Parse for loop syntax: for(var=start;condition;increment): command
            String pattern = "for\\s*\\(\\s*([^=]+)=([^;]+);([^;]+);([^)]+)\\)\\s*:\\s*(.+)";
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern, 
                    java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = regex.matcher(command);
            
            if (matcher.find()) {
                String varName = matcher.group(1).trim();
                String startExpr = matcher.group(2).trim();
                String condition = matcher.group(3).trim();
                String increment = matcher.group(4).trim();
                String loopCommand = matcher.group(5).trim();
                
                // Parse start value
                String expandedStart = expandVariables(startExpr);
                int startValue = evaluateSimpleMath(expandedStart);
                
                // Set initial variable value
                ioHandler.setVariable(varName, startValue);
                
                // Execute the loop
                int iterationCount = 0;
                int maxIterations = 1000;
                ioHandler.appendToHistory("System: Starting for loop with " + varName + "=" + startValue + "\n");
                
                while (iterationCount < maxIterations) {
                    // Check condition
                    if (!evaluateCondition(varName, condition)) {
                        ioHandler.appendToHistory("System: Loop condition failed, breaking\n");
                        break;
                    }
                    
                    // Execute the command
                    String expandedCommand = expandVariables(loopCommand);
                    ioHandler.appendToHistory("System: Iteration " + (iterationCount + 1) + 
                            ": " + varName + "=" + ioHandler.getVariable(varName) + 
                            ", executing: " + expandedCommand + "\n");
                    
                    if (codeGeneration.isRecordingLoop()) {
                        processCommandForLoop(expandedCommand);
                    } else {
                        processDrawingCommand(expandedCommand);
                    }
                    
                    // Apply increment
                    applyIncrement(varName, increment);
                    iterationCount++;
                }
                
                if (iterationCount >= maxIterations) {
                    ioHandler.appendToHistory("System: Loop terminated after " + maxIterations + " iterations\n");
                } else {
                    ioHandler.appendToHistory("System: Loop completed with " + iterationCount + " iterations\n");
                }
                
                drawingPanel.repaint();
            } else {
                ioHandler.appendToHistory("System: Invalid for loop syntax\n");
                ioHandler.appendToHistory("Use: for(var=start;condition;increment): command\n");
            }
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error processing for loop: " + e.getMessage() + "\n");
        }
    }
    
    /********************************************************************
     * METHOD: evaluateCondition
     * DESCRIPTION: Evaluates a loop condition
     * PARAMETERS: String varName - variable name
     *             String condition - condition string
     * RETURN VALUE: boolean - true if condition met
     ********************************************************************/
    private boolean evaluateCondition(String varName, String condition) {
        try {
            Integer currentValue = ioHandler.getVariable(varName);
            if (currentValue == null) return false;
            
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
            return false;
        }
    }
    
    /********************************************************************
     * METHOD: applyIncrement
     * DESCRIPTION: Applies increment to a variable
     * PARAMETERS: String varName - variable name
     *             String increment - increment expression
     * RETURN VALUE: None
     ********************************************************************/
    private void applyIncrement(String varName, String increment) {
        try {
            Integer currentValue = ioHandler.getVariable(varName);
            if (currentValue == null) return;
            
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
            }
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error applying increment\n");
        }
    }
    
    /********************************************************************
     * METHOD: expandVariables
     * DESCRIPTION: Expands variables in a command string
     * PARAMETERS: String command - command with variables
     * RETURN VALUE: String - command with variables expanded
     ********************************************************************/
    private String expandVariables(String command) {
        String result = command;
        Map<String, Integer> variables = ioHandler.getVariables();
        
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            String varName = entry.getKey();
            String value = entry.getValue().toString();
            result = result.replaceAll("\\b" + varName + "\\b", value);
        }
        
        return result;
    }
    
    /********************************************************************
     * METHOD: evaluateSimpleMath
     * DESCRIPTION: Evaluates simple mathematical expressions
     * PARAMETERS: String expression - math expression
     * RETURN VALUE: int - result of evaluation
     ********************************************************************/
    private int evaluateSimpleMath(String expression) {
        try {
            // Remove spaces
            String expr = expression.replaceAll("\\s+", "");
            
            // Handle multiplication first
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
            
            // If no operators, parse as integer
            return Integer.parseInt(expr);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /********************************************************************
     * METHOD: main
     * DESCRIPTION: Main entry point for the application
     * PARAMETERS: String[] args - command line arguments
     * RETURN VALUE: None
     ********************************************************************/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserInterface3());
    }
    /********************************************************************
     * METHOD: loadProgramIntoEditor
     * DESCRIPTION: Loads a program into the code editor
     * PARAMETERS: String program - the program to load
     * RETURN VALUE: None
     ********************************************************************/
    public void loadProgramIntoEditor(String program) {
        inputArea.setText(program);
        updateLineNumbers();
    }
}