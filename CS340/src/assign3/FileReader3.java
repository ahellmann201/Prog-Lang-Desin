package assign3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/******************************************************************************
*                     Drawing Chat Application - File Reader                  *
*                                                                             *
*    PROGRAMMER:  [Your Name]                                                 *
*    COURSE:  [Course Number and Name]                                        *
*    DATE:  [Date Submitted]                                                  *
*    REQUIREMENT:  Assignment 3                                               *
*                                                                             *
*    DESCRIPTION:                                                             *
*    This class handles reading commands from text files and executing        *
*    them in the drawing application.                                         *
*                                                                             *
*    COPYRIGHT:                                                               *
*    This code is copyright (c)2025 [Your Name] and Dean Zeller.              *
*                                                                             *
*    CREDITS:                                                                 *
*    Java I/O documentation                                                   *
*                                                                             *
******************************************************************************/

public class FileReader3 {
    private UserInterface3 ui;
    private InputOutputHandler3 ioHandler;
    
    /**************************************************************************
    *    METHOD:    FileReader Constructor                                    *
    *    DESCRIPTION:  Initializes the file reader with UI and IO handler     *
    *    PARAMETERS:  UserInterface3 ui - the main user interface             *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public FileReader3(UserInterface3 ui) {
        this.ui = ui;
        this.ioHandler = ui.getIOHandler();
    }
    
    /**************************************************************************
    *    METHOD:    readFile                                                  *
    *    DESCRIPTION:  Opens a file dialog and reads the selected file        *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void readFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(ui);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                processFile(filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ui, "Error reading file: " + ex.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**************************************************************************
    *    METHOD:    processFile                                               *
    *    DESCRIPTION:  Reads and processes commands from a file               *
    *    PARAMETERS:  String filePath - the path to the file to read          *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void processFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ioHandler.appendToHistory("System: Reading commands from file: " + filePath + "\n");
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
                
                // Process the command as if it was entered by the user
                processCommand(line);
            }
            
            ioHandler.appendToHistory("System: Finished processing file\n");
        } catch (IOException e) {
            ioHandler.appendToHistory("System: Error reading file: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(ui, "Error reading file: " + e.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**************************************************************************
    *    METHOD:    processCommand                                            *
    *    DESCRIPTION:  Processes a single command from the file               *
    *    PARAMETERS:  String command - the command to process                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processCommand(String command) {
        // Simulate the user entering this command
        ioHandler.appendToHistory("File: " + command + "\n");
        
        // Check if this is a for loop
        if (command.toLowerCase().startsWith("for(")) {
            ui.processForLoop(command);
            return;
        }
        
        // Process the command based on its type
        String lowerCommand = command.toLowerCase();
        
        if (lowerCommand.equals("polygon")) {
            ioHandler.setDrawingPolygon(true);
            ioHandler.clearPolygonPoints();
            ioHandler.appendToHistory("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
        } 
        else if (lowerCommand.equals("endpolygon")) {
            if (ioHandler.isDrawingPolygon()) {
                ioHandler.setDrawingPolygon(false);
                if (ioHandler.getPolygonPoints().size() >= 3) {
                    CodeGeneration3 codeGen = ui.getCodeGeneration();
                    codeGen.endPolygon(ioHandler, codeGen.isRecordingLoop());
                    ui.getDrawingPanel().repaint();
                } else {
                    ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
                }
            }
        }
        else if (lowerCommand.equals("clearpolygon")) {
            ioHandler.clearPolygonPoints();
            ui.getDrawingPanel().repaint();
            ioHandler.appendToHistory("System: Polygon points cleared\n");
        }
        else if (lowerCommand.startsWith("fill ")) {
            String[] parts = command.split(" ");
            if (parts.length >= 2) {
                if (parts[1].equalsIgnoreCase("on")) {
                    CodeGeneration3 codeGen = ui.getCodeGeneration();
                    codeGen.setFillShape(true);
                    // Don't try to access UI components that don't exist
                    ioHandler.appendToHistory("System: Fill mode enabled\n");
                } else if (parts[1].equalsIgnoreCase("off")) {
                    CodeGeneration3 codeGen = ui.getCodeGeneration();
                    codeGen.setFillShape(false);
                    // Don't try to access UI components that don't exist
                    ioHandler.appendToHistory("System: Fill mode disabled\n");
                }
            }
        }
        else if (lowerCommand.startsWith("set ")) {
            String[] parts = command.substring(4).split("=");
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
        else if (lowerCommand.equals("clear")) {
            CodeGeneration3 codeGen = ui.getCodeGeneration();
            codeGen.clearScreen(ioHandler);
            ui.getDrawingPanel().repaint();
            ioHandler.appendToHistory("System: Screen cleared\n");
        }
        else {
            // Process shape commands
            CodeGeneration3 codeGen = ui.getCodeGeneration();
            if (codeGen.isRecordingLoop()) {
                processCommandForLoop(command);
            } else {
                processShapeCommand(command);
            }
        }
    }
    
    /**************************************************************************
    *    METHOD:    processCommandForLoop                                     *
    *    DESCRIPTION:  Processes a command for loop recording                 *
    *    PARAMETERS:  String command - the command to process                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processCommandForLoop(String command) {
        String lowerCommand = command.toLowerCase();
        CodeGeneration3 codeGen = ui.getCodeGeneration();
        
        if (lowerCommand.startsWith("circle")) {
            codeGen.processCircleCommandForLoop(command, ioHandler);
        } 
        else if (lowerCommand.startsWith("triangle")) {
            codeGen.processTriangleCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("rectangle")) {
            codeGen.processRectangleCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("square")) {
            codeGen.processSquareCommandForLoop(command, ioHandler);
        }
        else if (lowerCommand.startsWith("points")) {
            codeGen.processPointsCommandForLoop(command, ioHandler);
        }
        else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        
        ui.getDrawingPanel().repaint();
    }
    
    /**************************************************************************
    *    METHOD:    processShapeCommand                                       *
    *    DESCRIPTION:  Processes a shape drawing command                      *
    *    PARAMETERS:  String command - the command to process                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    private void processShapeCommand(String command) {
        String lowerCommand = command.toLowerCase();
        CodeGeneration3 codeGen = ui.getCodeGeneration();
        
        if (lowerCommand.startsWith("circle")) {
            codeGen.processCircleCommand(command, ioHandler);
        } 
        else if (lowerCommand.startsWith("triangle")) {
            codeGen.processTriangleCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("rectangle")) {
            codeGen.processRectangleCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("square")) {
            codeGen.processSquareCommand(command, ioHandler);
        }
        else if (lowerCommand.startsWith("points")) {
            codeGen.processPointsCommand(command, ioHandler);
        }
        else {
            ioHandler.appendToHistory("System: Unknown command: " + command + "\n");
        }
        
        ui.getDrawingPanel().repaint();
    }
}