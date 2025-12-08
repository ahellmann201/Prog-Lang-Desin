package assign3;
import java.util.*;

/**
 * Code Block Parser for Control Structures
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Control Structures
 * 
 * DESCRIPTION:
 * This class parses multi-line code with control structures (if, while, for)
 * and converts them into executable statements with proper indentation handling.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 */
public class CodeBlockParser {
    
    /********************************************************************
     * METHOD: parseProgram
     * DESCRIPTION: Parses a complete program into executable statements
     * PARAMETERS: String program - the complete program text
     * RETURN VALUE: List<CodeStatement> - list of parsed statements
     ********************************************************************/
    public List<CodeStatement> parseProgram(String program) {
        List<CodeStatement> statements = new ArrayList<>();
        String[] lines = program.split("\n");
        Stack<ControlBlock> blockStack = new Stack<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // Skip empty lines and comments
            }
            
            // Calculate indentation level
            int indentLevel = calculateIndentLevel(lines[i]);
            
            // Handle block endings
            while (!blockStack.isEmpty() && indentLevel <= blockStack.peek().indentLevel) {
                blockStack.pop();
            }
            
            // Parse the line
            CodeStatement stmt = parseLine(line, i + 1, indentLevel, blockStack);
            if (stmt != null) {
                statements.add(stmt);
                
                // If this starts a block, push it onto stack
                if (stmt.getType() == StatementType.IF || 
                    stmt.getType() == StatementType.WHILE ||
                    stmt.getType() == StatementType.FOR) {
                    blockStack.push(new ControlBlock(stmt.getType(), indentLevel));
                }
            }
        }
        
        return statements;
    }
    
    /********************************************************************
     * METHOD: parseLine
     * DESCRIPTION: Parses a single line into a CodeStatement
     * PARAMETERS: String line - the line to parse
     *             int lineNumber - line number for error reporting
     *             int indentLevel - indentation level
     *             Stack<ControlBlock> blockStack - current block context
     * RETURN VALUE: CodeStatement - parsed statement, or null if invalid
     ********************************************************************/
    private CodeStatement parseLine(String line, int lineNumber, int indentLevel, 
                                   Stack<ControlBlock> blockStack) {
        // Remove inline comments
        int commentIndex = line.indexOf("#");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex).trim();
        }
        
        if (line.isEmpty()) {
            return null;
        }
        
        // Check for control structures
        if (line.startsWith("if ")) {
            return parseIfStatement(line, lineNumber, indentLevel);
        } else if (line.startsWith("while ")) {
            return parseWhileStatement(line, lineNumber, indentLevel);
        } else if (line.startsWith("for ")) {
            return parseForStatement(line, lineNumber, indentLevel);
        } else if (line.equals("{")) {
            return new CodeStatement("{", StatementType.BLOCK_START, lineNumber, indentLevel);
        } else if (line.equals("}")) {
            return new CodeStatement("}", StatementType.BLOCK_END, lineNumber, indentLevel);
        } else {
            // Regular statement
            return new CodeStatement(line, StatementType.REGULAR, lineNumber, indentLevel);
        }
    }
    
    /********************************************************************
     * METHOD: parseIfStatement
     * DESCRIPTION: Parses an if statement
     * PARAMETERS: String line - the if statement line
     *             int lineNumber - line number
     *             int indentLevel - indentation level
     * RETURN VALUE: CodeStatement - parsed if statement
     ********************************************************************/
    private CodeStatement parseIfStatement(String line, int lineNumber, int indentLevel) {
        // Extract condition from: if (condition) {
        String pattern = "if\\s*\\(\\s*(.+?)\\s*\\)\\s*\\{?";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(line);
        
        if (matcher.find()) {
            String condition = matcher.group(1);
            return new CodeStatement(line, StatementType.IF, lineNumber, indentLevel, condition);
        } else {
            // Simple if without braces
            String condition = line.substring(3).trim();
            if (condition.endsWith("{")) {
                condition = condition.substring(0, condition.length() - 1).trim();
            }
            return new CodeStatement(line, StatementType.IF, lineNumber, indentLevel, condition);
        }
    }
    
    /********************************************************************
     * METHOD: parseWhileStatement
     * DESCRIPTION: Parses a while statement
     * PARAMETERS: String line - the while statement line
     *             int lineNumber - line number
     *             int indentLevel - indentation level
     * RETURN VALUE: CodeStatement - parsed while statement
     ********************************************************************/
    private CodeStatement parseWhileStatement(String line, int lineNumber, int indentLevel) {
        // Extract condition from: while (condition) {
        String pattern = "while\\s*\\(\\s*(.+?)\\s*\\)\\s*\\{?";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(line);
        
        if (matcher.find()) {
            String condition = matcher.group(1);
            return new CodeStatement(line, StatementType.WHILE, lineNumber, indentLevel, condition);
        } else {
            // Simple while without braces
            String condition = line.substring(6).trim();
            if (condition.endsWith("{")) {
                condition = condition.substring(0, condition.length() - 1).trim();
            }
            return new CodeStatement(line, StatementType.WHILE, lineNumber, indentLevel, condition);
        }
    }
    
    /********************************************************************
     * METHOD: parseForStatement
     * DESCRIPTION: Parses a for statement
     * PARAMETERS: String line - the for statement line
     *             int lineNumber - line number
     *             int indentLevel - indentation level
     * RETURN VALUE: CodeStatement - parsed for statement
     ********************************************************************/
    private CodeStatement parseForStatement(String line, int lineNumber, int indentLevel) {
        // Extract parts from: for (init; condition; update) {
        String pattern = "for\\s*\\(\\s*(.+?)\\s*;\\s*(.+?)\\s*;\\s*(.+?)\\s*\\)\\s*\\{?";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(line);
        
        if (matcher.find()) {
            String init = matcher.group(1);
            String condition = matcher.group(2);
            String update = matcher.group(3);
            return new CodeStatement(line, StatementType.FOR, lineNumber, indentLevel, 
                                   init, condition, update);
        } else {
            // Try alternative format
            pattern = "for\\s*\\(\\s*(.+?)\\s*\\)\\s*\\{?";
            regex = java.util.regex.Pattern.compile(pattern);
            matcher = regex.matcher(line);
            
            if (matcher.find()) {
                String params = matcher.group(1);
                return new CodeStatement(line, StatementType.FOR, lineNumber, indentLevel, params);
            }
        }
        
        // Fallback to regular statement
        return new CodeStatement(line, StatementType.REGULAR, lineNumber, indentLevel);
    }
    
    /********************************************************************
     * METHOD: calculateIndentLevel
     * DESCRIPTION: Calculates indentation level from leading whitespace
     * PARAMETERS: String line - the line with indentation
     * RETURN VALUE: int - indentation level (0 = no indent)
     ********************************************************************/
    private int calculateIndentLevel(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\t') {
                count++;
            } else if (c == ' ') {
                // Count 4 spaces as one level
                if (i + 3 < line.length() && 
                    line.charAt(i+1) == ' ' && 
                    line.charAt(i+2) == ' ' && 
                    line.charAt(i+3) == ' ') {
                    count++;
                    i += 3;
                } else {
                    break; // Not standard indentation
                }
            } else {
                break; // Non-whitespace character
            }
        }
        return count;
    }
    
    /********************************************************************
     * METHOD: validateProgram
     * DESCRIPTION: Validates program structure (matching braces, etc.)
     * PARAMETERS: String program - the program to validate
     * RETURN VALUE: List<String> - list of errors, empty if valid
     ********************************************************************/
    public List<String> validateProgram(String program) {
        List<String> errors = new ArrayList<>();
        String[] lines = program.split("\n");
        Stack<Integer> braceStack = new Stack<>();
        Stack<Integer> blockStack = new Stack<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            
            // Check for unmatched braces
            if (line.contains("{")) {
                braceStack.push(i + 1);
            }
            if (line.contains("}")) {
                if (braceStack.isEmpty()) {
                    errors.add("Line " + (i + 1) + ": Unexpected '}'");
                } else {
                    braceStack.pop();
                }
            }
            
            // Check indentation
            int indentLevel = calculateIndentLevel(lines[i]);
            if (!blockStack.isEmpty() && indentLevel <= blockStack.peek()) {
                blockStack.pop();
            }
            
            // Check for control structures
            if (line.startsWith("if ") || line.startsWith("while ") || line.startsWith("for ")) {
                blockStack.push(indentLevel);
                
                // Check if control structure has body
                if (!line.endsWith("{") && i + 1 < lines.length) {
                    int nextIndent = calculateIndentLevel(lines[i + 1]);
                    if (nextIndent <= indentLevel) {
                        errors.add("Line " + (i + 1) + ": Control structure missing body");
                    }
                }
            }
        }
        
        // Check for unmatched opening braces
        while (!braceStack.isEmpty()) {
            errors.add("Line " + braceStack.pop() + ": Unmatched '{'");
        }
        
        return errors;
    }
    
    /**
     * Inner class for control blocks
     */
    private class ControlBlock {
        StatementType type;
        int indentLevel;
        
        ControlBlock(StatementType type, int indentLevel) {
            this.type = type;
            this.indentLevel = indentLevel;
        }
    }
}

/**
 * Code Statement Types
 */
enum StatementType {
    REGULAR, IF, WHILE, FOR, BLOCK_START, BLOCK_END
}

/**
 * Code Statement Class
 */
class CodeStatement {
    private String source;
    private StatementType type;
    private int lineNumber;
    private int indentLevel;
    private String[] parameters;
    
    public CodeStatement(String source, StatementType type, int lineNumber, int indentLevel) {
        this.source = source;
        this.type = type;
        this.lineNumber = lineNumber;
        this.indentLevel = indentLevel;
        this.parameters = new String[0];
    }
    
    public CodeStatement(String source, StatementType type, int lineNumber, int indentLevel, 
                        String... parameters) {
        this.source = source;
        this.type = type;
        this.lineNumber = lineNumber;
        this.indentLevel = indentLevel;
        this.parameters = parameters;
    }
    
    public String getSource() { return source; }
    public StatementType getType() { return type; }
    public int getLineNumber() { return lineNumber; }
    public int getIndentLevel() { return indentLevel; }
    public String[] getParameters() { return parameters; }
    
    @Override
    public String toString() {
        return String.format("Line %d [Level %d]: %s", lineNumber, indentLevel, source);
    }
}