package assign3;
import java.util.*;
import java.awt.Point;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Enhanced Input/Output Handler with Expression Evaluation
 * 
 * PROGRAMMER: [Your Name]  
 * COURSE: [Course Number and Name]  
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Control Structures
 * 
 * DESCRIPTION:
 * This file handles all input/output operations including command parsing,
 * variable management, history logging, and user interaction functions.
 * Enhanced with better mathematical expression evaluation for control structures.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 * 
 * CREDITS:
 * Java Collections framework documentation
 */
public class InputOutputHandler3 {
    private java.util.List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private Map<String, Integer> variables = new HashMap<>(); // Variable storage
    private CodeGeneration3 codeGeneration;
    private JTextArea historyArea;
    private ExpressionEvaluator expressionEvaluator;
    
    /********************************************************************
     * METHOD: InputOutputHandler3 Constructor
     * DESCRIPTION: Initializes the IO handler
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public InputOutputHandler3() {
        this.expressionEvaluator = new ExpressionEvaluator(this);
    }
    
    /********************************************************************
     * METHOD: setHistoryArea
     * DESCRIPTION: Sets the history text area for output
     * PARAMETERS: JTextArea historyArea - the history area to use
     * RETURN VALUE: None
     ********************************************************************/
    public void setHistoryArea(JTextArea historyArea) {
        this.historyArea = historyArea;
    }
    
    /********************************************************************
     * METHOD: appendToHistory
     * DESCRIPTION: Appendix text to the history area
     * PARAMETERS: String text - the text to append
     * RETURN VALUE: None
     ********************************************************************/
    public void appendToHistory(String text) {
        if (historyArea != null) {
            historyArea.append(text);
            // Auto-scroll to bottom
            historyArea.setCaretPosition(historyArea.getDocument().getLength());
        }
    }
    
    /********************************************************************
     * METHOD: clearHistory
     * DESCRIPTION: Clears the history area
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clearHistory() {
        if (historyArea != null) {
            historyArea.setText("");
        }
    }
    
    /********************************************************************
     * METHOD: isDrawingPolygon
     * DESCRIPTION: Checks if polygon drawing mode is active
     * PARAMETERS: None
     * RETURN VALUE: boolean - true if drawing polygon, false otherwise
     ********************************************************************/
    public boolean isDrawingPolygon() {
        return drawingPolygon;
    }
    
    /********************************************************************
     * METHOD: setDrawingPolygon
     * DESCRIPTION: Sets the polygon drawing mode
     * PARAMETERS: boolean drawingPolygon - true to enable polygon drawing
     * RETURN VALUE: None
     ********************************************************************/
    public void setDrawingPolygon(boolean drawingPolygon) {
        this.drawingPolygon = drawingPolygon;
    }
    
    /********************************************************************
     * METHOD: getPolygonPoints
     * DESCRIPTION: Returns the list of polygon points
     * PARAMETERS: None
     * RETURN VALUE: List<Point> - the list of polygon points
     ********************************************************************/
    public List<Point> getPolygonPoints() {
        return polygonPoints;
    }
    
    /********************************************************************
     * METHOD: addPolygonPoint
     * DESCRIPTION: Adds a point to the current polygon
     * PARAMETERS: Point point - the point to add
     * RETURN VALUE: None
     ********************************************************************/
    public void addPolygonPoint(Point point) {
        polygonPoints.add(point);
    }
    
    /********************************************************************
     * METHOD: clearPolygonPoints
     * DESCRIPTION: Clears all polygon points
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clearPolygonPoints() {
        polygonPoints.clear();
    }
    
    /********************************************************************
     * METHOD: parseValue
     * DESCRIPTION: Parses a value, handling both numbers and variables
     *              Enhanced for complex expressions with control structures
     * PARAMETERS: String value - the value to parse
     * RETURN VALUE: int - the parsed integer value
     ********************************************************************/
    public int parseValue(String value) {
        try {
            // Use the expression evaluator
            return expressionEvaluator.evaluate(value);
        } catch (Exception e) {
            appendToHistory("System: Error parsing value: " + value + "\n");
            return 0;
        }
    }
    
    /********************************************************************
     * METHOD: evaluateCondition
     * DESCRIPTION: Evaluates a boolean condition expression
     * PARAMETERS: String condition - the condition to evaluate
     * RETURN VALUE: boolean - true if condition is true
     ********************************************************************/
    public boolean evaluateCondition(String condition) {
        return expressionEvaluator.evaluateCondition(condition);
    }
    
    /********************************************************************
     * METHOD: setVariable
     * DESCRIPTION: Sets a variable with the given name and value
     * PARAMETERS: String varName - the variable name
     *             int value - the variable value
     * RETURN VALUE: None
     ********************************************************************/
    public void setVariable(String varName, int value) {
        variables.put(varName, value);
    }
    
    /********************************************************************
     * METHOD: getVariable
     * DESCRIPTION: Gets a variable value by name
     * PARAMETERS: String varName - the variable name
     * RETURN VALUE: Integer - the variable value, or null if not found
     ********************************************************************/
    public Integer getVariable(String varName) {
        return variables.get(varName);
    }
    
    /********************************************************************
     * METHOD: getVariables
     * DESCRIPTION: Returns all variables
     * PARAMETERS: None
     * RETURN VALUE: Map<String, Integer> - all variables
     ********************************************************************/
    public Map<String, Integer> getVariables() {
        return new HashMap<>(variables);
    }
    
    /********************************************************************
     * METHOD: clearVariables
     * DESCRIPTION: Clears all variables
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clearVariables() {
        variables.clear();
    }
    
    /********************************************************************
     * METHOD: showVariables
     * DESCRIPTION: Displays all variables in the history area
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
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
    
    /********************************************************************
     * METHOD: showInputDialog
     * DESCRIPTION: Shows an input dialog and returns the result
     * PARAMETERS: String message - the dialog message
     * RETURN VALUE: String - the user input, or null if cancelled
     ********************************************************************/
    public String showInputDialog(String message) {
        return JOptionPane.showInputDialog(message);
    }
    
    /********************************************************************
     * METHOD: showMessageDialog
     * DESCRIPTION: Shows a message dialog
     * PARAMETERS: String message - the message to display
     * RETURN VALUE: None
     ********************************************************************/
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    
    /********************************************************************
     * METHOD: showSelectionDialog
     * DESCRIPTION: Shows a selection dialog with given options
     * PARAMETERS: String title - the dialog title
     *             String message - the dialog message
     *             String[] options - the options to choose from
     * RETURN VALUE: String - the selected option, or null if cancelled
     ********************************************************************/
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
    
    /********************************************************************
     * METHOD: setCodeGeneration
     * DESCRIPTION: Sets the code generation instance for math evaluation
     * PARAMETERS: CodeGeneration3 codeGeneration - the CodeGeneration3 instance
     * RETURN VALUE: none
     ********************************************************************/
    public void setCodeGeneration(CodeGeneration3 codeGeneration) {
        this.codeGeneration = codeGeneration;
    }
    
    /********************************************************************
     * METHOD: getCodeGeneration
     * DESCRIPTION: Gets the code generation instance
     * PARAMETERS: None
     * RETURN VALUE: CodeGeneration3 - the code generation instance
     ********************************************************************/
    public CodeGeneration3 getCodeGeneration() {
        return codeGeneration;
    }
    
    /********************************************************************
     * METHOD: evaluateExpression
     * DESCRIPTION: Evaluates a mathematical expression with variables
     * PARAMETERS: String expression - the expression to evaluate
     * RETURN VALUE: int - the result of evaluation
     ********************************************************************/
    public int evaluateExpression(String expression) {
        return expressionEvaluator.evaluate(expression);
    }
}

/**
 * Enhanced Expression Evaluator for Control Structures
 */
class ExpressionEvaluator {
    private InputOutputHandler3 ioHandler;
    
    public ExpressionEvaluator(InputOutputHandler3 ioHandler) {
        this.ioHandler = ioHandler;
    }
    
    /********************************************************************
     * METHOD: evaluate
     * DESCRIPTION: Evaluates a mathematical expression
     * PARAMETERS: String expression - the expression to evaluate
     * RETURN VALUE: int - the result
     ********************************************************************/
    public int evaluate(String expression) {
        try {
            // Replace variables with their values
            String processed = replaceVariables(expression);
            
            // Use JavaScript engine for complex expressions
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            
            if (engine != null) {
                Object result = engine.eval(processed);
                if (result instanceof Number) {
                    return ((Number) result).intValue();
                }
            }
            
            // Fallback to manual evaluation
            return evaluateManually(processed);
            
        } catch (Exception e) {
            ioHandler.appendToHistory("System: Error evaluating expression: " + expression + "\n");
            return 0;
        }
    }
    
    /********************************************************************
     * METHOD: evaluateCondition
     * DESCRIPTION: Evaluates a boolean condition
     * PARAMETERS: String condition - the condition to evaluate
     * RETURN VALUE: boolean - true if condition is true
     ********************************************************************/
    public boolean evaluateCondition(String condition) {
        try {
            // Replace variables
            String processed = replaceVariables(condition);
            
            // Handle comparison operators
            if (processed.contains("<=")) {
                String[] parts = processed.split("<=");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left <= right;
            } else if (processed.contains(">=")) {
                String[] parts = processed.split(">=");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left >= right;
            } else if (processed.contains("<")) {
                String[] parts = processed.split("<");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left < right;
            } else if (processed.contains(">")) {
                String[] parts = processed.split(">");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left > right;
            } else if (processed.contains("==")) {
                String[] parts = processed.split("==");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left == right;
            } else if (processed.contains("!=")) {
                String[] parts = processed.split("!=");
                int left = evaluate(parts[0].trim());
                int right = evaluate(parts[1].trim());
                return left != right;
            }
            
            // If no comparison, treat as boolean (non-zero = true)
            int value = evaluate(processed);
            return value != 0;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /********************************************************************
     * METHOD: replaceVariables
     * DESCRIPTION: Replaces variables in expression with their values
     * PARAMETERS: String expression - the expression with variables
     * RETURN VALUE: String - expression with variables replaced
     ********************************************************************/
    private String replaceVariables(String expression) {
        String result = expression;
        Map<String, Integer> variables = ioHandler.getVariables();
        
        // Sort by variable name length (longest first) to avoid partial replacements
        List<String> varNames = new ArrayList<>(variables.keySet());
        varNames.sort((a, b) -> Integer.compare(b.length(), a.length()));
        
        for (String varName : varNames) {
            Integer value = variables.get(varName);
            if (value != null) {
                // Replace whole words only (using word boundaries)
                result = result.replaceAll("\\b" + varName + "\\b", value.toString());
            }
        }
        
        return result;
    }
    
    /********************************************************************
     * METHOD: evaluateManually
     * DESCRIPTION: Manual evaluation for simple arithmetic
     * PARAMETERS: String expression - the expression to evaluate
     * RETURN VALUE: int - the result
     ********************************************************************/
    private int evaluateManually(String expression) {
        try {
            // Remove spaces
            expression = expression.replaceAll("\\s+", "");
            
            // Handle parentheses
            while (expression.contains("(") && expression.contains(")")) {
                int open = expression.lastIndexOf("(");
                int close = expression.indexOf(")", open);
                
                if (close == -1) break;
                
                String inner = expression.substring(open + 1, close);
                int innerResult = evaluateManually(inner);
                expression = expression.substring(0, open) + innerResult + 
                            expression.substring(close + 1);
            }
            
            // Handle multiplication and division
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(-?\\d+)([*/])(-?\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(expression);
            
            while (matcher.find()) {
                int left = Integer.parseInt(matcher.group(1));
                String op = matcher.group(2);
                int right = Integer.parseInt(matcher.group(3));
                int result = op.equals("*") ? left * right : left / right;
                expression = expression.replace(matcher.group(0), String.valueOf(result));
                matcher = pattern.matcher(expression);
            }
            
            // Handle addition and subtraction
            pattern = java.util.regex.Pattern.compile("(-?\\d+)([+-])(-?\\d+)");
            matcher = pattern.matcher(expression);
            
            while (matcher.find()) {
                int left = Integer.parseInt(matcher.group(1));
                String op = matcher.group(2);
                int right = Integer.parseInt(matcher.group(3));
                int result = op.equals("+") ? left + right : left - right;
                expression = expression.replace(matcher.group(0), String.valueOf(result));
                matcher = pattern.matcher(expression);
            }
            
            return Integer.parseInt(expression);
        } catch (Exception e) {
            return 0;
        }
    }
}