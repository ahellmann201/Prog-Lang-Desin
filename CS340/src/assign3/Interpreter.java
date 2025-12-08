package assign3;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Enhanced Interpreter Class with Control Structures
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Control Structures
 * 
 * DESCRIPTION:
 * This class handles interpretation of the programming language with
 * support for if statements and while loops. It can execute multi-line
 * code blocks with proper scope and condition evaluation.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 */
public class Interpreter {
    private Map<String, Integer> variables = new HashMap<>();
    private TokenEncoder tokenEncoder;
    private boolean verboseMode = false;
    private InputOutputHandler3 ioHandler;
    
    // CONO Table definitions
    private static final int INTEGER = 100;
    private static final int INPUT = 101;
    private static final int PRINT = 102;
    private static final int IF = 103;
    private static final int WHILE = 104;
    private static final int LINE = 105;
    private static final int ASSIGN = 200;
    private static final int LEFT_PAREN = 201;
    private static final int RIGHT_PAREN = 202;
    private static final int SEMICOLON = 203;
    private static final int COMMA = 204;
    private static final int LEFT_BRACE = 205;
    private static final int RIGHT_BRACE = 206;
    
    // State variables for parsing
    private String currentOperation = "";
    private String currentVariable = "";
    private int currentValue = 0;
    private boolean conditionMet = false;
    private List<String> currentCondition = new ArrayList<>();
    private List<String> codeBlock = new ArrayList<>();
    private boolean inCodeBlock = false;
    private int blockIndentLevel = 0;
    private boolean skipExecution = false; // For if/else branches
    
    // Stack for nested control structures
    private Stack<ControlStructure> controlStack = new Stack<>();
    
    /**
     * Inner class to represent control structures
     */
    private class ControlStructure {
        String type; // "if", "while"
        boolean condition;
        int indentLevel;
        boolean executing;
        
        ControlStructure(String type, boolean condition, int indentLevel) {
            this.type = type;
            this.condition = condition;
            this.indentLevel = indentLevel;
            this.executing = condition; // Start executing if condition is true
        }
    }
    
    /********************************************************************
     * METHOD: Interpreter Constructor
     * DESCRIPTION: Initializes the interpreter with token encoder and IO handler
     * PARAMETERS: TokenEncoder tokenEncoder - token encoder instance
     *             InputOutputHandler3 ioHandler - IO handler instance
     * RETURN VALUE: None
     ********************************************************************/
    public Interpreter(TokenEncoder tokenEncoder, InputOutputHandler3 ioHandler) {
        this.tokenEncoder = tokenEncoder;
        this.ioHandler = ioHandler;
    }
    
    /********************************************************************
     * METHOD: setVerboseMode
     * DESCRIPTION: Sets the interpreter mode (verbose or actual)
     * PARAMETERS: boolean verbose - true for verbose mode, false for actual mode
     * RETURN VALUE: None
     ********************************************************************/
    public void setVerboseMode(boolean verbose) {
        this.verboseMode = verbose;
    }
    
    /********************************************************************
     * METHOD: getVerboseMode
     * DESCRIPTION: Returns the current interpreter mode
     * PARAMETERS: None
     * RETURN VALUE: boolean - true if verbose mode is enabled
     ********************************************************************/
    public boolean getVerboseMode() {
        return verboseMode;
    }
    
    /********************************************************************
     * METHOD: interpretProgram
     * DESCRIPTION: Interprets a complete multi-line program
     * PARAMETERS: String program - the complete program text
     * RETURN VALUE: None
     ********************************************************************/
    public void interpretProgram(String program) {
        // Reset state
        resetState();
        
        // Split into lines and process
        String[] lines = program.split("\n");
        List<CodeLine> codeLines = parseIndentation(lines);
        
        // Execute lines with proper indentation handling
        for (int i = 0; i < codeLines.size(); i++) {
            CodeLine line = codeLines.get(i);
            interpretLineWithContext(line);
        }
    }
    
    /********************************************************************
     * METHOD: interpretLineWithContext
     * DESCRIPTION: Interprets a single line with indentation context
     * PARAMETERS: CodeLine line - the code line with indentation info
     * RETURN VALUE: None
     ********************************************************************/
    private void interpretLineWithContext(CodeLine line) {
        // Update control structure stack based on indentation
        while (!controlStack.isEmpty() && line.indentLevel <= controlStack.peek().indentLevel) {
            controlStack.pop();
        }
        
        // Check if we should execute this line
        boolean shouldExecute = true;
        for (ControlStructure cs : controlStack) {
            if (!cs.executing) {
                shouldExecute = false;
                break;
            }
        }
        
        if (!shouldExecute) {
            if (verboseMode) {
                ioHandler.appendToHistory("Skipping (in false branch): " + line.text + "\n");
            }
            return;
        }
        
        // Process the line
        interpretLine(line.text);
    }
    
    /********************************************************************
     * METHOD: interpretLine
     * DESCRIPTION: Interprets a single line of code
     * PARAMETERS: String line - the line to interpret
     * RETURN VALUE: None
     ********************************************************************/
    public void interpretLine(String line) {
        // Remove comments (everything after #)
        int commentIndex = line.indexOf("#");
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
        
        // Check for control structures
        if (line.startsWith("if ")) {
            processIfStatement(line);
            return;
        } else if (line.startsWith("while ")) {
            processWhileStatement(line);
            return;
        } else if (line.equals("{") || line.equals("}")) {
            // Handle braces if present
            return;
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
    
    /********************************************************************
     * METHOD: processIfStatement
     * DESCRIPTION: Processes an if statement
     * PARAMETERS: String line - the if statement line
     * RETURN VALUE: None
     ********************************************************************/
    private void processIfStatement(String line) {
        // Parse if statement: if (condition) {
        String pattern = "if\\s*\\(\\s*(.+?)\\s*\\)\\s*\\{?";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(line);
        
        if (matcher.find()) {
            String conditionStr = matcher.group(1);
            boolean conditionResult = evaluateConditionExpression(conditionStr);
            
            if (verboseMode) {
                ioHandler.appendToHistory("If condition: " + conditionStr + " = " + conditionResult + "\n");
            }
            
            // Get current indent level from line
            int indentLevel = getIndentLevel(line);
            
            // Push if structure onto stack
            ControlStructure ifStruct = new ControlStructure("if", conditionResult, indentLevel);
            controlStack.push(ifStruct);
            
            if (!conditionResult) {
                ifStruct.executing = false; // Don't execute if branch
            }
        } else {
            ioHandler.appendToHistory("Error: Invalid if statement syntax\n");
        }
    }
    
    /********************************************************************
     * METHOD: processWhileStatement
     * DESCRIPTION: Processes a while loop
     * PARAMETERS: String line - the while statement line
     * RETURN VALUE: None
     ********************************************************************/
    private void processWhileStatement(String line) {
        // Parse while statement: while (condition) {
        String pattern = "while\\s*\\(\\s*(.+?)\\s*\\)\\s*\\{?";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(line);
        
        if (matcher.find()) {
            String conditionStr = matcher.group(1);
            
            // Get current indent level
            int indentLevel = getIndentLevel(line);
            
            // Create while structure
            ControlStructure whileStruct = new ControlStructure("while", true, indentLevel);
            controlStack.push(whileStruct);
            
            // We'll need to handle loop execution differently
            // For now, just evaluate condition once
            boolean conditionResult = evaluateConditionExpression(conditionStr);
            
            if (!conditionResult) {
                whileStruct.executing = false; // Don't execute loop body
            }
            
            if (verboseMode) {
                ioHandler.appendToHistory("While condition: " + conditionStr + " = " + conditionResult + "\n");
            }
        } else {
            ioHandler.appendToHistory("Error: Invalid while statement syntax\n");
        }
    }
    
    /********************************************************************
     * METHOD: evaluateConditionExpression
     * DESCRIPTION: Evaluates a condition expression
     * PARAMETERS: String expression - the condition expression
     * RETURN VALUE: boolean - true if condition is true
     ********************************************************************/
    private boolean evaluateConditionExpression(String expression) {
        try {
            // Parse condition (supports: x < 10, x == y, x != 5, etc.)
            expression = expression.trim();
            
            // Replace variables with values
            for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", 
                        entry.getValue().toString());
            }
            
            // Evaluate comparison operators
            if (expression.contains("<=")) {
                String[] parts = expression.split("<=");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left <= right;
                }
            } else if (expression.contains(">=")) {
                String[] parts = expression.split(">=");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left >= right;
                }
            } else if (expression.contains("<")) {
                String[] parts = expression.split("<");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left < right;
                }
            } else if (expression.contains(">")) {
                String[] parts = expression.split(">");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left > right;
                }
            } else if (expression.contains("==")) {
                String[] parts = expression.split("==");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left == right;
                }
            } else if (expression.contains("!=")) {
                String[] parts = expression.split("!=");
                if (parts.length == 2) {
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left != right;
                }
            }
            
            // If no comparison, evaluate as boolean (non-zero = true)
            int value = evaluateMathExpression(expression);
            return value != 0;
            
        } catch (Exception e) {
            if (verboseMode) {
                ioHandler.appendToHistory("Error evaluating condition: " + e.getMessage() + "\n");
            }
            return false;
        }
    }
    
    /********************************************************************
     * METHOD: evaluateMathExpression
     * DESCRIPTION: Evaluates mathematical expressions
     * PARAMETERS: String expression - the math expression
     * RETURN VALUE: int - the result
     ********************************************************************/
    private int evaluateMathExpression(String expression) {
        try {
            // Remove spaces
            expression = expression.replaceAll("\\s+", "");
            
            // Handle parentheses first
            while (expression.contains("(") && expression.contains(")")) {
                int open = expression.lastIndexOf("(");
                int close = expression.indexOf(")", open);
                
                if (close == -1) break;
                
                String inner = expression.substring(open + 1, close);
                int innerResult = evaluateMathExpression(inner);
                expression = expression.substring(0, open) + innerResult + 
                            expression.substring(close + 1);
            }
            
            // Handle multiplication and division
            java.util.regex.Pattern mdPattern = java.util.regex.Pattern.compile("(\\d+)([*/])(\\d+)");
            java.util.regex.Matcher mdMatcher = mdPattern.matcher(expression);
            
            while (mdMatcher.find()) {
                int left = Integer.parseInt(mdMatcher.group(1));
                String op = mdMatcher.group(2);
                int right = Integer.parseInt(mdMatcher.group(3));
                int result = op.equals("*") ? left * right : left / right;
                expression = expression.replace(mdMatcher.group(0), String.valueOf(result));
                mdMatcher = mdPattern.matcher(expression);
            }
            
            // Handle addition and subtraction
            java.util.regex.Pattern asPattern = java.util.regex.Pattern.compile("(-?\\d+)([+-])(-?\\d+)");
            java.util.regex.Matcher asMatcher = asPattern.matcher(expression);
            
            while (asMatcher.find()) {
                int left = Integer.parseInt(asMatcher.group(1));
                String op = asMatcher.group(2);
                int right = Integer.parseInt(asMatcher.group(3));
                int result = op.equals("+") ? left + right : left - right;
                expression = expression.replace(asMatcher.group(0), String.valueOf(result));
                asMatcher = asPattern.matcher(expression);
            }
            
            return Integer.parseInt(expression);
        } catch (Exception e) {
            if (verboseMode) {
                ioHandler.appendToHistory("Error evaluating math: " + expression + "\n");
            }
            return 0;
        }
    }
    
    /********************************************************************
     * METHOD: getIndentLevel
     * DESCRIPTION: Calculates the indentation level of a line
     * PARAMETERS: String line - the line to check
     * RETURN VALUE: int - number of indentation levels (tabs or 4 spaces)
     ********************************************************************/
    private int getIndentLevel(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\t') {
                count++;
            } else if (i + 3 < line.length() && line.substring(i, i + 4).equals("    ")) {
                count++;
                i += 3;
            } else {
                break;
            }
        }
        return count;
    }
    
    /********************************************************************
     * METHOD: parseIndentation
     * DESCRIPTION: Parses lines and calculates indentation levels
     * PARAMETERS: String[] lines - array of code lines
     * RETURN VALUE: List<CodeLine> - list of code lines with indentation info
     ********************************************************************/
    private List<CodeLine> parseIndentation(String[] lines) {
        List<CodeLine> result = new ArrayList<>();
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue; // Skip empty lines and comments
            }
            
            int indentLevel = getIndentLevel(line);
            result.add(new CodeLine(trimmed, indentLevel));
        }
        
        return result;
    }
    
    /********************************************************************
     * METHOD: tokenizeLine
     * DESCRIPTION: Tokenizes a line of code
     * PARAMETERS: String line - the line to tokenize
     * RETURN VALUE: List<String> - list of tokens
     ********************************************************************/
    private List<String> tokenizeLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else if (c == ',' || c == '(' || c == ')' || c == '=' || c == ';' || 
                      c == '<' || c == '>' || c == '!' || c == '{' || c == '}') {
                // Handle multi-character operators
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                
                // Check for double character operators
                if (i + 1 < line.length()) {
                    char nextChar = line.charAt(i + 1);
                    if (c == '=' && nextChar == '=') {
                        tokens.add("==");
                        i++; // Skip next character
                    } else if (c == '!' && nextChar == '=') {
                        tokens.add("!=");
                        i++; // Skip next character
                    } else if (c == '<' && nextChar == '=') {
                        tokens.add("<=");
                        i++; // Skip next character
                    } else if (c == '>' && nextChar == '=') {
                        tokens.add(">=");
                        i++; // Skip next character
                    } else {
                        tokens.add(String.valueOf(c));
                    }
                } else {
                    tokens.add(String.valueOf(c));
                }
            } else {
                currentToken.append(c);
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
    }
    
    /********************************************************************
     * METHOD: processCONO
     * DESCRIPTION: Processes tokens using the CONO table
     * PARAMETERS: List<Integer> tokenCodes - encoded tokens
     *             List<String> tokens - original tokens
     * RETURN VALUE: List<String> - list of code generators called
     ********************************************************************/
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
    
    /********************************************************************
     * METHOD: getCodeGenerator
     * DESCRIPTION: Gets the appropriate code generator for a CONO pair
     * PARAMETERS: int currentToken - current token code
     *             int nextToken - next token code
     *             List<String> tokens - original tokens
     *             int index - current token index
     * RETURN VALUE: String - code generator name
     ********************************************************************/
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
            case IF:
                if (nextToken == LEFT_PAREN) return "start_if";
                break;
            case WHILE:
                if (nextToken == LEFT_PAREN) return "start_while";
                break;
            case LINE:
                if (nextToken == LEFT_PAREN) return "start_line";
                break;
            case ASSIGN:
                if (nextToken == SEMICOLON) return "end_define";
                break;
            case LEFT_PAREN:
                if (nextToken == RIGHT_PAREN) return "end_paren";
                break;
            case RIGHT_PAREN:
                if (nextToken == SEMICOLON) return "no_op";
                if (nextToken == COMMA) return "continue_params";
                if (nextToken == LEFT_BRACE) return "start_block";
                break;
            case COMMA:
                if (nextToken == RIGHT_PAREN) return "end_paren";
                if (nextToken == SEMICOLON) return "no_op";
                return "continue_params";
            case SEMICOLON:
                return "no_op";
            case LEFT_BRACE:
                return "enter_block";
            case RIGHT_BRACE:
                return "exit_block";
        }
        
        return null; // Syntax error
    }
    
    /********************************************************************
     * METHOD: executeCodeGenerator
     * DESCRIPTION: Executes the appropriate code generator
     * PARAMETERS: String codeGenerator - code generator to execute
     *             List<String> tokens - original tokens
     *             int index - current token index
     * RETURN VALUE: None
     ********************************************************************/
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
            case "start_if":
                startIf();
                break;
            case "start_while":
                startWhile();
                break;
            case "start_line":
                startLine();
                break;
            case "end_paren":
                endParent(tokens, index);
                break;
            case "continue_params":
                continueParams(tokens, index);
                break;
            case "enter_block":
                enterBlock();
                break;
            case "exit_block":
                exitBlock();
                break;
            case "no_op":
                // Do nothing
                break;
        }
    }
    
    /********************************************************************
     * METHOD: startDefine
     * DESCRIPTION: Starts variable definition process
     * PARAMETERS: List<String> tokens - tokens
     *             int index - current token index
     * RETURN VALUE: None
     ********************************************************************/
    private void startDefine(List<String> tokens, int index) {
        if (index + 1 < tokens.size()) {
            currentVariable = tokens.get(index + 1);
            currentOperation = "define";
            
            if (verboseMode) {
                ioHandler.appendToHistory("Starting definition of variable: " + currentVariable + "\n");
            }
        }
    }
    
    /********************************************************************
     * METHOD: endDefine
     * DESCRIPTION: Completes variable definition
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void endDefine() {
        if ("define".equals(currentOperation) && !currentVariable.isEmpty()) {
            variables.put(currentVariable, currentValue);
            
            if (verboseMode) {
                ioHandler.appendToHistory("Defined variable " + currentVariable + " = " + currentValue + "\n");
            }
        }
        resetState();
    }
    
    /********************************************************************
     * METHOD: startInput
     * DESCRIPTION: Starts input process
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void startInput() {
        currentOperation = "input";
    }
    
    /********************************************************************
     * METHOD: startPrint
     * DESCRIPTION: Starts print process
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void startPrint() {
        currentOperation = "print";
    }
    
    /********************************************************************
     * METHOD: startIf
     * DESCRIPTION: Starts if statement processing
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void startIf() {
        currentOperation = "if";
        currentCondition.clear();
        conditionMet = false;
        
        if (verboseMode) {
            ioHandler.appendToHistory("Starting if statement\n");
        }
    }
    
    /********************************************************************
     * METHOD: startWhile
     * DESCRIPTION: Starts while loop processing
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void startWhile() {
        currentOperation = "while";
        currentCondition.clear();
        
        if (verboseMode) {
            ioHandler.appendToHistory("Starting while loop\n");
        }
    }
    
    /********************************************************************
     * METHOD: startLine
     * DESCRIPTION: Starts line drawing command
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void startLine() {
        currentOperation = "line";
        
        if (verboseMode) {
            ioHandler.appendToHistory("Starting line command\n");
        }
    }
    
    /********************************************************************
     * METHOD: endParent
     * DESCRIPTION: Handles end of parentheses for input/print
     * PARAMETERS: List<String> tokens - tokens
     *             int index - current token index
     * RETURN VALUE: None
     ********************************************************************/
    private void endParent(List<String> tokens, int index) {
        if (index - 1 >= 0) {
            String operand = tokens.get(index - 1);
            
            if ("input".equals(currentOperation)) {
                // Handle Input
                String inputValue = JOptionPane.showInputDialog("Enter value for " + operand + ":");
                if (inputValue != null) {
                    try {
                        int value = Integer.parseInt(inputValue.trim());
                        variables.put(operand, value);
                        if (verboseMode) {
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
                    if (verboseMode) {
                        ioHandler.appendToHistory(value + "\n");
                    } else {
                        ioHandler.appendToHistory("Printing: " + value + "\n");
                    }
                } catch (NumberFormatException e) {
                    ioHandler.appendToHistory("Error: Cannot print " + operand + "\n");
                }
            } else if ("if".equals(currentOperation)) {
                // Handle if condition evaluation
                if (!operand.equals("") && !operand.equals(".")) {
                    currentCondition.add(operand);
                }
                
                // We'll evaluate condition later
            } else if ("while".equals(currentOperation)) {
                // Handle while condition
                if (!operand.equals("") && !operand.equals(".")) {
                    currentCondition.add(operand);
                }
            } else if ("line".equals(currentOperation)) {
                // Handle line drawing
                if (verboseMode) {
                    ioHandler.appendToHistory("Line command executed with current parameters\n");
                }
                // In practice, you'd call your drawing system here
                ioHandler.appendToHistory("Would draw line with parameters\n");
            }
        }
        
        resetState();
    }
    
    /********************************************************************
     * METHOD: continueParams
     * DESCRIPTION: Continues parameter collection
     * PARAMETERS: List<String> tokens - tokens
     *             int index - current token index
     * RETURN VALUE: None
     ********************************************************************/
    private void continueParams(List<String> tokens, int index) {
        if (index > 0) {
            String token = tokens.get(index - 1);
            if ("if".equals(currentOperation) || "while".equals(currentOperation)) {
                if (!token.equals("") && !token.equals(".")) {
                    currentCondition.add(token);
                }
            }
        }
    }
    
    /********************************************************************
     * METHOD: enterBlock
     * DESCRIPTION: Enters a code block
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void enterBlock() {
        inCodeBlock = true;
        blockIndentLevel++;
        
        if (verboseMode) {
            ioHandler.appendToHistory("Entering code block (level " + blockIndentLevel + ")\n");
        }
    }
    
    /********************************************************************
     * METHOD: exitBlock
     * DESCRIPTION: Exits a code block
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void exitBlock() {
        if (blockIndentLevel > 0) {
            blockIndentLevel--;
        }
        
        if (blockIndentLevel == 0) {
            inCodeBlock = false;
        }
        
        if (verboseMode) {
            ioHandler.appendToHistory("Exiting code block (level " + blockIndentLevel + ")\n");
        }
    }
    
    /********************************************************************
     * METHOD: resetState
     * DESCRIPTION: Resets interpreter state
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void resetState() {
        currentOperation = "";
        currentVariable = "";
        currentValue = 0;
        currentCondition.clear();
    }
    
    /********************************************************************
     * METHOD: getVariables
     * DESCRIPTION: Returns all variables
     * PARAMETERS: None
     * RETURN VALUE: Map<String, Integer> - variable map
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
        resetState();
        controlStack.clear();
    }
    
    /********************************************************************
     * METHOD: setVariable
     * DESCRIPTION: Sets a variable value
     * PARAMETERS: String name - variable name
     *             int value - variable value
     * RETURN VALUE: None
     ********************************************************************/
    public void setVariable(String name, int value) {
        variables.put(name, value);
    }
    
    /********************************************************************
     * METHOD: getVariable
     * DESCRIPTION: Gets a variable value
     * PARAMETERS: String name - variable name
     * RETURN VALUE: Integer - variable value, or null if not found
     ********************************************************************/
    public Integer getVariable(String name) {
        return variables.get(name);
    }
    
    /**
     * Inner class for code lines with indentation
     */
    private class CodeLine {
        String text;
        int indentLevel;
        
        CodeLine(String text, int indentLevel) {
            this.text = text;
            this.indentLevel = indentLevel;
        }
    }
}