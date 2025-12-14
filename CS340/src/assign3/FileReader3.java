package assign3;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Enhanced File Reader for Multi-line Programs
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: [Course Number and Name]
 * DATE: [Date Submitted]
 * REQUIREMENT: Assignment 7 - Control Structures
 * 
 * DESCRIPTION:
 * This class handles reading commands from text files and executing them
 * in the drawing application. Now supports multi-line programs with
 * control structures.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 * 
 * CREDITS:
 * Java I/O documentation
 */
public class FileReader3 {
    private UserInterface3 ui;
    private InputOutputHandler3 ioHandler;
    private TokenEncoder tokenEncoder;
    
    /********************************************************************
     * METHOD: FileReader3 Constructor
     * DESCRIPTION: Initializes the file reader with UI and IO handler
     * PARAMETERS: UserInterface3 ui - the main user interface
     * RETURN VALUE: None
     ********************************************************************/
    public FileReader3(UserInterface3 ui) {
        this.ui = ui;
        this.ioHandler = ui.getIOHandler();
        this.tokenEncoder = ui.getTokenEncoder();
    }
    
    /********************************************************************
     * METHOD: readFile
     * DESCRIPTION: Opens a file dialog and reads the selected file
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void readFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Text Files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Drawing Program Files (*.draw)", "draw"));
        
        int result = fileChooser.showOpenDialog(ui);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                processFile(filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ui, 
                    "Error reading file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /********************************************************************
     * METHOD: processFile
     * DESCRIPTION: Reads and processes commands from a file
     * PARAMETERS: String filePath - the path to the file to read
     * RETURN VALUE: None
     ********************************************************************/
    public void processFile(String filePath) {
        StringBuilder program = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ioHandler.appendToHistory("System: Reading program from file: " + filePath + "\n");
            
            while ((line = reader.readLine()) != null) {
                program.append(line).append("\n");
            }
            
            // Load the program into the code editor
            ui.loadProgramIntoEditor(program.toString());
            
            ioHandler.appendToHistory("System: Program loaded into editor\n");
            ioHandler.appendToHistory("Click 'Execute Code' to run the program\n");
            
        } catch (IOException e) {
            ioHandler.appendToHistory("System: Error reading file: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(ui, 
                "Error reading file: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /********************************************************************
     * METHOD: processFileAndExecute
     * DESCRIPTION: Reads and immediately executes a file
     * PARAMETERS: String filePath - the path to the file to read
     * RETURN VALUE: None
     ********************************************************************/
    public void processFileAndExecute(String filePath) {
        StringBuilder program = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ioHandler.appendToHistory("System: Executing program from file: " + filePath + "\n");
            
            while ((line = reader.readLine()) != null) {
                program.append(line).append("\n");
            }
            
            // Execute the program directly
            executeProgram(program.toString());
            
            ioHandler.appendToHistory("System: Program execution completed\n");
            
        } catch (IOException e) {
            ioHandler.appendToHistory("System: Error reading file: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(ui, 
                "Error reading file: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /********************************************************************
     * METHOD: executeProgram
     * DESCRIPTION: Executes a multi-line program
     * PARAMETERS: String program - the program to execute
     * RETURN VALUE: None
     ********************************************************************/
    private void executeProgram(String program) {
        // Use the interpreter to execute the program
        Interpreter interpreter = new Interpreter(tokenEncoder, ioHandler);
        interpreter.interpretProgram(program);
        
        // Also process any drawing commands
        String[] lines = program.split("\n");
        CodeGeneration3 codeGen = ui.getCodeGeneration();
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            
            // Skip interpreter commands (handled above)
            if (line.startsWith("integer ") || line.startsWith("input ") || 
                line.startsWith("print ") || line.startsWith("if ") || 
                line.startsWith("while ") || line.startsWith("for ")) {
                continue;
            }
            
            // Process drawing commands
            processDrawingCommand(line);
        }
        
        ui.getDrawingPanel().repaint();
    }
    
    /********************************************************************
     * METHOD: processDrawingCommand
     * DESCRIPTION: Processes a drawing command from file
     * PARAMETERS: String command - the command to process
     * RETURN VALUE: None
     ********************************************************************/
    private void processDrawingCommand(String command) {
        CodeGeneration3 codeGen = ui.getCodeGeneration();
        String lowerCommand = command.toLowerCase();
        
        if (lowerCommand.startsWith("circle")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processCircleCommandForLoop(command, ioHandler);
            } else {
                codeGen.processCircleCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("triangle")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processTriangleCommandForLoop(command, ioHandler);
            } else {
                codeGen.processTriangleCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("rectangle")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processRectangleCommandForLoop(command, ioHandler);
            } else {
                codeGen.processRectangleCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("square")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processSquareCommandForLoop(command, ioHandler);
            } else {
                codeGen.processSquareCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("points")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processPointsCommandForLoop(command, ioHandler);
            } else {
                codeGen.processPointsCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("line")) {
            if (codeGen.isRecordingLoop()) {
                codeGen.processLineCommandForLoop(command, ioHandler);
            } else {
                codeGen.processLineCommand(command, ioHandler);
            }
        } else if (lowerCommand.startsWith("animation ")) {
            if (command.length() > 10) {
                String animName = command.substring(10).trim();
                ui.getAnimationSystem().createAnimation(animName);
            }
        } else if (lowerCommand.startsWith("keyframe ")) {
            if (command.length() > 9) {
                String frameName = command.substring(9).trim();
                ui.getAnimationSystem().addKeyframe(frameName);
            }
        } else if (lowerCommand.startsWith("play ")) {
            if (command.length() > 5) {
                String animName = command.substring(5).trim();
                ui.getAnimationSystem().playAnimation(animName);
            }
        } else if (lowerCommand.equals("stop")) {
            ui.getAnimationSystem().stopAnimation();
        } else if (lowerCommand.startsWith("delay ")) {
            if (command.length() > 6) {
                try {
                    long delay = Long.parseLong(command.substring(6).trim());
                    ui.getAnimationSystem().setFrameDelay(delay);
                } catch (NumberFormatException e) {
                    // Ignore parse errors in file
                }
            }
        } else if (lowerCommand.startsWith("fill ")) {
            String[] parts = command.split(" ");
            if (parts.length >= 2) {
                if (parts[1].equalsIgnoreCase("on")) {
                    codeGen.setFillShape(true);
                } else if (parts[1].equalsIgnoreCase("off")) {
                    codeGen.setFillShape(false);
                }
            }
        } else if (lowerCommand.startsWith("set ")) {
            String[] parts = command.substring(4).split("=");
            if (parts.length == 2) {
                String varName = parts[0].trim();
                try {
                    int value = Integer.parseInt(parts[1].trim());
                    ioHandler.setVariable(varName, value);
                } catch (NumberFormatException e) {
                    // Ignore parse errors
                }
            }
        } else if (lowerCommand.equals("clear")) {
            codeGen.clearScreen(ioHandler);
        } else if (lowerCommand.equals("polygon")) {
            ioHandler.setDrawingPolygon(true);
            ioHandler.clearPolygonPoints();
        } else if (lowerCommand.equals("endpolygon")) {
            if (ioHandler.isDrawingPolygon()) {
                ioHandler.setDrawingPolygon(false);
                if (ioHandler.getPolygonPoints().size() >= 3) {
                    codeGen.endPolygon(ioHandler, codeGen.isRecordingLoop());
                }
            }
        } else if (lowerCommand.equals("clearpolygon")) {
            ioHandler.clearPolygonPoints();
        }
        // Note: Control structures (if, while) are handled by the interpreter
    }
}