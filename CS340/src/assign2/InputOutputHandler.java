package assign2;

import java.util.*;
import java.awt.Point;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/******************************************************************************
*                     Drawing Chat Application - Input/Output Handler         *
*                                                                             *
*    PROGRAMMER:  [Your Name]                                                 *
*    COURSE:  [Course Number and Name]                                        *
*    DATE:  [Date Submitted]                                                  *
*    REQUIREMENT:  Assignment 2                                               *
*                                                                             *
*    DESCRIPTION:                                                             *
*    This file handles all input/output operations including command parsing, *
*    variable management, history logging, and user interaction functions.    *
*                                                                             *
*    COPYRIGHT:                                                               *
*    This code is copyright (c)2025 [Your Name] and Dean Zeller.              *
*                                                                             *
*    CREDITS:                                                                 *
*    Java Collections framework documentation                                 *
*                                                                             *
******************************************************************************/

public class InputOutputHandler {
    private java.util.List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private boolean fillShape = false;
    private Map<String, Integer> variables = new HashMap<>(); // Variable storage
    
    private JTextArea historyArea;

    /**************************************************************************
    *    METHOD:    setHistoryArea                                            *
    *    DESCRIPTION:  Sets the history text area for output                  *
    *    PARAMETERS:  JTextArea historyArea - the history area to use         *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setHistoryArea(JTextArea historyArea) {
        this.historyArea = historyArea;
    }
    
    /**************************************************************************
    *    METHOD:    appendToHistory                                           *
    *    DESCRIPTION:  Appends text to the history area                       *
    *    PARAMETERS:  String text - the text to append                        *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void appendToHistory(String text) {
        if (historyArea != null) {
            historyArea.append(text);
        }
    }
    
    /**************************************************************************
    *    METHOD:    isDrawingPolygon                                          *
    *    DESCRIPTION:  Checks if polygon drawing mode is active               *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  boolean - true if drawing polygon, false otherwise    *
    **************************************************************************/
    public boolean isDrawingPolygon() {
        return drawingPolygon;
    }
    
    /**************************************************************************
    *    METHOD:    setDrawingPolygon                                         *
    *    DESCRIPTION:  Sets the polygon drawing mode                          *
    *    PARAMETERS:  boolean drawingPolygon - true to enable polygon drawing *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setDrawingPolygon(boolean drawingPolygon) {
        this.drawingPolygon = drawingPolygon;
    }
    
    /**************************************************************************
    *    METHOD:    isFillShape                                               *
    *    DESCRIPTION:  Checks if fill mode is enabled                         *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  boolean - true if fill mode enabled, false otherwise  *
    **************************************************************************/
    public boolean isFillShape() {
        return fillShape;
    }
    
    /**************************************************************************
    *    METHOD:    setFillShape                                              *
    *    DESCRIPTION:  Sets the fill mode for shapes                          *
    *    PARAMETERS:  boolean fillShape - true to enable fill mode            *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setFillShape(boolean fillShape) {
        this.fillShape = fillShape;
    }
    
    
    
    /**************************************************************************
    *    METHOD:    getPolygonPoints                                          *
    *    DESCRIPTION:  Returns the list of polygon points                     *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  List<Point> - the list of polygon points              *
    **************************************************************************/
    public List<Point> getPolygonPoints() {
        return polygonPoints;
    }
    
    /**************************************************************************
    *    METHOD:    addPolygonPoint                                           *
    *    DESCRIPTION:  Adds a point to the current polygon                    *
    *    PARAMETERS:  Point point - the point to add                          *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void addPolygonPoint(Point point) {
        polygonPoints.add(point);
    }
    
    /**************************************************************************
    *    METHOD:    clearPolygonPoints                                        *
    *    DESCRIPTION:  Clears all polygon points                              *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void clearPolygonPoints() {
        polygonPoints.clear();
    }
    
    /**************************************************************************
    *    METHOD:    parseValue                                                *
    *    DESCRIPTION:  Parses a value, handling both numbers and variables    *
    *    PARAMETERS:  String value - the value to parse                       *
    *    RETURN VALUE:  int - the parsed integer value                        *
    **************************************************************************/
    public int parseValue(String value) {
        try {
            // Check if it's a variable reference
            if (variables.containsKey(value)) {
                return variables.get(value);
            }
            // Otherwise parse as integer
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            appendToHistory("System: Invalid number or undefined variable: " + value + "\n");
            return 0;
        }
    }
    
    /**************************************************************************
    *    METHOD:    setVariable                                               *
    *    DESCRIPTION:  Sets a variable with the given name and value          *
    *    PARAMETERS:  String varName - the variable name                      *
    *                 int value - the variable value                          *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void setVariable(String varName, int value) {
        variables.put(varName, value);
    }
    
    /**************************************************************************
    *    METHOD:    getVariable                                               *
    *    DESCRIPTION:  Gets a variable value by name                          *
    *    PARAMETERS:  String varName - the variable name                      *
    *    RETURN VALUE:  Integer - the variable value, or null if not found    *
    **************************************************************************/
    public Integer getVariable(String varName) {
        return variables.get(varName);
    }
    
    /**************************************************************************
    *    METHOD:    getVariables                                              *
    *    DESCRIPTION:  Returns all variables                                  *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  Map<String, Integer> - all variables                  *
    **************************************************************************/
    public Map<String, Integer> getVariables() {
        return variables;
    }
    
    /**************************************************************************
    *    METHOD:    clearVariables                                            *
    *    DESCRIPTION:  Clears all variables                                   *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void clearVariables() {
        variables.clear();
    }
    
    /**************************************************************************
    *    METHOD:    showVariables                                             *
    *    DESCRIPTION:  Displays all variables in the history area             *
    *    PARAMETERS:  None                                                    *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void showVariables() {
        if (variables.isEmpty()) {
            appendToHistory("System: No variables defined\n");
        } else {
            appendToHistory("System: Variables:\n");
            for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                appendToHistory("  " + entry.getKey() + " = " + entry.getValue() + "\n");
            }
        }
    }
    
    /**************************************************************************
    *    METHOD:    showInputDialog                                           *
    *    DESCRIPTION:  Shows an input dialog and returns the result           *
    *    PARAMETERS:  String message - the dialog message                     *
    *    RETURN VALUE:  String - the user input, or null if cancelled         *
    **************************************************************************/
    public String showInputDialog(String message) {
        return JOptionPane.showInputDialog(message);
    }
    
    /**************************************************************************
    *    METHOD:    showMessageDialog                                         *
    *    DESCRIPTION:  Shows a message dialog                                 *
    *    PARAMETERS:  String message - the message to display                 *
    *    RETURN VALUE:  None                                                  *
    **************************************************************************/
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    
    /**************************************************************************
    *    METHOD:    showSelectionDialog                                       *
    *    DESCRIPTION:  Shows a selection dialog with given options            *
    *    PARAMETERS:  String title - the dialog title                         *
    *                 String message - the dialog message                     *
    *                 String[] options - the options to choose from           *
    *    RETURN VALUE:  String - the selected option, or null if cancelled    *
    **************************************************************************/
    public String showSelectionDialog(String title, String message, String[] options) {
        return (String) JOptionPane.showInputDialog(
            null,
            message,
            title,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
    }
}