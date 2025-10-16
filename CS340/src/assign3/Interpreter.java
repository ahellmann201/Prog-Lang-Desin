package assign3;

import java.util.*;
import javax.swing.JOptionPane;

/***********************************************************************
 * CLASS: Interpreter
 * DESCRIPTION: Handles interpretation of the programming language
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * COPYRIGHT: This code is copyright (c)2025 [Your Name] and Dean Zeller.
 ***********************************************************************/
public class Interpreter {
    private Map<String, Integer> variables = new HashMap<>();
    private TokenEncoder tokenEncoder;
    private boolean verboseMode = false;
    private InputOutputHandler3 ioHandler;
    
    // CONO Table definitions
    private static final int INTEGER = 100;
    private static final int INPUT = 101;
    private static final int PRINT = 102;
    private static final int ASSIGN = 200;
    private static final int LEFT_PAREN = 201;
    private static final int RIGHT_PAREN = 202;
    private static final int SEMICOLON = 203;
    
    private String currentOperation = "";
    private String currentVariable = "";
    private int currentValue = 0;
    
    public Interpreter(TokenEncoder tokenEncoder, InputOutputHandler3 ioHandler) {
        this.tokenEncoder = tokenEncoder;
        this.ioHandler = ioHandler;
    }
    
    /***********************************************************************
     * METHOD: setVerboseMode
     * DESCRIPTION: Sets the interpreter mode (verbose or actual)
     * PARAMETERS: boolean verbose - true for verbose mode, false for actual mode
     * RETURN VALUE: None
     ***********************************************************************/
    public void setVerboseMode(boolean verbose) {
        this.verboseMode = verbose;
    }
    
    /***********************************************************************
     * METHOD: getVerboseMode
     * DESCRIPTION: Returns the current interpreter mode
     * PARAMETERS: None
     * RETURN VALUE: boolean - true if verbose mode is enabled
     ***********************************************************************/
    public boolean getVerboseMode() {
        return verboseMode;
    }
    
    /***********************************************************************
     * METHOD: interpretLine
     * DESCRIPTION: Interprets a single line of code
     * PARAMETERS: String line - the line to interpret
     * RETURN VALUE: None
     ***********************************************************************/
    public void interpretLine(String line) {
        // Remove comments (everything after #)
        int commentIndex = line.indexOf('#');
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }
        
        line = line.trim();
        if (line.isEmpty()) {
            return;
        }
        
        if (verboseMode) {
            ioHandler.appendToHistory(">>> " + line + "\n");
        }
        
        // Tokenize and encode the line
        List<Integer> tokenCodes = tokenEncoder.encodeCommand(line);
        List<String> tokens = tokenizeLine(line);
        
        if (verboseMode) {
            ioHandler.appendToHistory("Tokens: " + String.join(" ", tokens) + "\n");
            ioHandler.appendToHistory("TokenIDs: " + tokenCodes + "\n");
        }
        
        // Process tokens using CONO table
        List<String> codeGenerators = processCONO(tokenCodes, tokens);
        
        if (verboseMode && !codeGenerators.isEmpty()) {
            ioHandler.appendToHistory("Code generators called: " + String.join(" ", codeGenerators) + "\n");
        }
    }
    
    /***********************************************************************
     * METHOD: tokenizeLine
     * DESCRIPTION: Tokenizes a line of code
     * PARAMETERS: String line - the line to tokenize
     * RETURN VALUE: List<String> - list of tokens
     ***********************************************************************/
    private List<String> tokenizeLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else if (c == ';' || c == '(' || c == ')' || c == '=' || c == '#') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                currentToken.append(c);
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
    }
    
    /***********************************************************************
     * METHOD: processCONO
     * DESCRIPTION: Processes tokens using the CONO table
     * PARAMETERS: List<Integer> tokenCodes - encoded tokens
     *             List<String> tokens - original tokens
     * RETURN VALUE: List<String> - list of code generators called
     ***********************************************************************/
    private List<String> processCONO(List<Integer> tokenCodes, List<String> tokens) {
        List<String> codeGenerators = new ArrayList<>();
        
        for (int i = 0; i < tokenCodes.size(); i++) {
            int currentToken = tokenCodes.get(i);
            int nextToken = (i < tokenCodes.size() - 1) ? tokenCodes.get(i + 1) : -1;
            
            String codeGenerator = getCodeGenerator(currentToken, nextToken, tokens, i);
            if (codeGenerator != null && !codeGenerator.equals("no_op")) {
                codeGenerators.add(codeGenerator);
            }
            
            // Execute the code generator immediately
            executeCodeGenerator(codeGenerator, tokens, i);
        }
        
        return codeGenerators;
    }
    
    /***********************************************************************
     * METHOD: getCodeGenerator
     * DESCRIPTION: Gets the appropriate code generator for a CONO pair
     * PARAMETERS: int currentToken - current token code
     *             int nextToken - next token code
     *             List<String> tokens - original tokens
     *             int index - current token index
     * RETURN VALUE: String - code generator name
     ***********************************************************************/
    private String getCodeGenerator(int currentToken, int nextToken, List<String> tokens, int index) {
        // CONO Table implementation
        switch (currentToken) {
            case INTEGER:
                if (nextToken == ASSIGN) return "start_define";
                if (nextToken == SEMICOLON) return "end_define";
                break;
                
            case INPUT:
                if (nextToken == LEFT_PAREN) return "start_input";
                break;
                
            case PRINT:
                if (nextToken == LEFT_PAREN) return "start_print";
                break;
                
            case ASSIGN:
                if (nextToken == SEMICOLON) return "end_define";
                break;
                
            case LEFT_PAREN:
                break;
                
            case RIGHT_PAREN:
                if (nextToken == SEMICOLON) return "no_op";
                break;
                
            case SEMICOLON:
                return "no_op";
        }
        
        return null; // Syntax error
    }
    
    /***********************************************************************
     * METHOD: executeCodeGenerator
     * DESCRIPTION: Executes the appropriate code generator
     * PARAMETERS: String codeGenerator - code generator to execute
     *             List<String> tokens - original tokens
     *             int index - current token index
     * RETURN VALUE: None
     ***********************************************************************/
    private void executeCodeGenerator(String codeGenerator, List<String> tokens, int index) {
        if (codeGenerator == null) return;
        
        switch (codeGenerator) {
            case "start_define":
                startDefine(tokens, index);
                break;
                
            case "end_define":
                endDefine();
                break;
                
            case "start_input":
                startInput();
                break;
                
            case "start_print":
                startPrint();
                break;
                
            case "end_paren":
                endParen(tokens, index);
                break;
                
            case "no_op":
                // Do nothing
                break;
        }
    }
    
    /***********************************************************************
     * METHOD: startDefine
     * DESCRIPTION: Starts variable definition process
     * PARAMETERS: List<String> tokens - tokens
     *             int index - current token index
     * RETURN VALUE: None
     ***********************************************************************/
    private void startDefine(List<String> tokens, int index) {
        if (index + 1 < tokens.size()) {
            currentVariable = tokens.get(index + 1);
            currentOperation = "define";
            
            if (verboseMode) {
                ioHandler.appendToHistory("Starting definition of variable: " + currentVariable + "\n");
            }
        }
    }
    
    /***********************************************************************
     * METHOD: endDefine
     * DESCRIPTION: Completes variable definition
     * PARAMETERS: None
     * RETURN VALUE: None
     ***********************************************************************/
    private void endDefine() {
        if ("define".equals(currentOperation) && !currentVariable.isEmpty()) {
            variables.put(currentVariable, currentValue);
            
            if (verboseMode) {
                ioHandler.appendToHistory("Defined variable " + currentVariable + " = " + currentValue + "\n");
            }
            
            resetState();
        }
    }
    
    /***********************************************************************
     * METHOD: startInput
     * DESCRIPTION: Starts input process
     * PARAMETERS: None
     * RETURN VALUE: None
     ***********************************************************************/
    private void startInput() {
        currentOperation = "input";
    }
    
    /***********************************************************************
     * METHOD: startPrint
     * DESCRIPTION: Starts print process
     * PARAMETERS: None
     * RETURN VALUE: None
     ***********************************************************************/
    private void startPrint() {
        currentOperation = "print";
    }
    
    /***********************************************************************
     * METHOD: endParen
     * DESCRIPTION: Handles end of parentheses for input/print
     * PARAMETERS: List<String> tokens - tokens
     *             int index - current token index
     * RETURN VALUE: None
     ***********************************************************************/
    private void endParen(List<String> tokens, int index) {
        if (index - 1 >= 0) {
            String operand = tokens.get(index - 1);
            
            if ("input".equals(currentOperation)) {
                // Handle input
                String inputValue = JOptionPane.showInputDialog("Enter value for " + operand + ":");
                if (inputValue != null) {
                    try {
                        int value = Integer.parseInt(inputValue.trim());
                        variables.put(operand, value);
                        
                        if (!verboseMode) {
                            ioHandler.appendToHistory("=> " + value + "\n");
                        } else {
                            ioHandler.appendToHistory("Input received: " + operand + " = " + value + "\n");
                        }
                    } catch (NumberFormatException e) {
                        ioHandler.appendToHistory("Error: Invalid integer input\n");
                    }
                }
            } else if ("print".equals(currentOperation)) {
                // Handle print
                int value;
                try {
                    // Check if it's a literal or variable
                    if (Character.isDigit(operand.charAt(0))) {
                        value = Integer.parseInt(operand);
                    } else {
                        value = variables.getOrDefault(operand, 0);
                    }
                    
                    if (!verboseMode) {
                        ioHandler.appendToHistory(value + "\n");
                    } else {
                        ioHandler.appendToHistory("Printing: " + value + "\n");
                    }
                } catch (NumberFormatException e) {
                    ioHandler.appendToHistory("Error: Cannot print " + operand + "\n");
                }
            }
            
            resetState();
        }
    }
    
    /***********************************************************************
     * METHOD: resetState
     * DESCRIPTION: Resets interpreter state
     * PARAMETERS: None
     * RETURN VALUE: None
     ***********************************************************************/
    private void resetState() {
        currentOperation = "";
        currentVariable = "";
        currentValue = 0;
    }
    
    /***********************************************************************
     * METHOD: getVariables
     * DESCRIPTION: Returns all variables
     * PARAMETERS: None
     * RETURN VALUE: Map<String, Integer> - variable map
     ***********************************************************************/
    public Map<String, Integer> getVariables() {
        return new HashMap<>(variables);
    }
    
    /***********************************************************************
     * METHOD: clearVariables
     * DESCRIPTION: Clears all variables
     * PARAMETERS: None
     * RETURN VALUE: None
     ***********************************************************************/
    public void clearVariables() {
        variables.clear();
        resetState();
    }
}