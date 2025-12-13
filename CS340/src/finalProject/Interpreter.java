package finalProject;

/*******************************************************************
* Name of program: Interpreter
* PROGRAMMER: Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignments 6, 7, 8 (Interpreter, Math, Logic)
*
* DESCRIPTION:
* This class is the core logic engine of the IDE. It takes the list
* of tokens from the Lexer and executes them using a Recursive Descent
* Parser. It handles Variable storage, Math evaluation (PEMDAS),
* Logic evaluation (AND/OR/NOT), Control Flow (IF/WHILE), and
* dispatches Graphics commands to the GraphicsFrame.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Assisted by Artificial Intelligence.
*******************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.swing.SwingUtilities;

public class Interpreter {
    
    private IDEWindow outputWindow;
    private GraphicsFrame graphicsFrame;
    private Lexer lexer;
    private Map<String, Integer> variables; 
    private Map<String, List<Token>> functions;
    
    // Flag to track if the program has attempted to draw anything
    private boolean graphicsStarted = false;

    // Parser State Helpers
    private int currentTokenIndex;

    /**********************************************************
    * METHOD: Interpreter (Constructor)
    * DESCRIPTION: Initializes the interpreter components.
    * PARAMETERS: IDEWindow outputWindow - The window for console output
    * RETURN VALUE: N/A
    **********************************************************/
    public Interpreter(IDEWindow outputWindow) {
        this.outputWindow = outputWindow;
        this.lexer = new Lexer();
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.graphicsFrame = new GraphicsFrame();
    }

    /**********************************************************
    * METHOD: execute
    * DESCRIPTION: Runs the source code in a separate thread.
    * Handles initialization and cleanup.
    * PARAMETERS: String sourceCode - The raw code string
    * RETURN VALUE: void
    **********************************************************/
    public void execute(String sourceCode) {
        new Thread(() -> {
            try {
                variables.clear();
                functions.clear();
                graphicsStarted = false; 
                
                // Reset graphics state but DO NOT force visible
                graphicsFrame.reset();
                
                List<Token> tokens = lexer.tokenize(sourceCode);
                printSafe("--- Starting Execution ---");
                
                parse(tokens);
                
                // Force final redraw for static images
                graphicsFrame.refresh(); 
                
                printSafe("\n--- Execution Finished ---");
            } catch (Exception e) {
                printSafe("RUNTIME ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**********************************************************
    * METHOD: printSafe
    * DESCRIPTION: Helper to print to console on the Event Dispatch Thread.
    * PARAMETERS: String msg - The message to print
    * RETURN VALUE: void
    **********************************************************/
    private void printSafe(String msg) {
        SwingUtilities.invokeLater(() -> outputWindow.printToConsole(msg));
    }

    /**********************************************************
    * METHOD: parse
    * DESCRIPTION: The main loop that iterates through tokens and 
    * executes commands (Keywords, Assignments, Blocks).
    * PARAMETERS: List<Token> tokens - The list of instruction tokens
    * RETURN VALUE: void
    **********************************************************/
    private void parse(List<Token> tokens) {
        Stack<Integer> whileStack = new Stack<>();
        Stack<String> blockStack = new Stack<>(); 
        
        int i = 0;
        while (i < tokens.size()) {
            
            // Stop execution if user closed the graphics window
            if (graphicsStarted && !graphicsFrame.isOpen()) {
                printSafe("\nProgram Stopped: Graphics Window Closed.");
                break;
            }

            Token t = tokens.get(i);

            // --- ANIMATIONS ---
            if (t.value.equals("anim")) {
                i++; 
                if (i < tokens.size()) {
                    String funcName = tokens.get(i++).value;
                    if (check(tokens, i, "{")) {
                        i++; 
                        List<Token> bodyTokens = new ArrayList<>();
                        int braces = 1;
                        while (braces > 0 && i < tokens.size()) {
                            Token bodyT = tokens.get(i);
                            if (bodyT.value.equals("{")) braces++;
                            if (bodyT.value.equals("}")) braces--;
                            if (braces > 0) bodyTokens.add(bodyT);
                            i++;
                        }
                        functions.put(funcName, bodyTokens);
                        printSafe("Defined Animation: " + funcName);
                    }
                }
            }
            else if (t.value.equals("run")) {
                i++; 
                if (i < tokens.size()) {
                    String funcName = tokens.get(i++).value;
                    if (check(tokens, i, "(")) i++; if (check(tokens, i, ")")) i++;
                    if (functions.containsKey(funcName)) parse(functions.get(funcName));
                    else printSafe("Error: Animation '" + funcName + "' not defined.");
                    if (check(tokens, i, ";")) i++;
                }
            }
            // --- IF / ELSE ---
            else if (t.value.equals("if")) {
                i++; 
                List<Token> conditionTokens = extractExpressionTokens(tokens, i, "{");
                i += conditionTokens.size();
                
                boolean result = evaluateLogic(conditionTokens);
                
                if (check(tokens, i, "{")) {
                    i++; 
                    if (result) {
                        blockStack.push("IF"); 
                    } else {
                        // Skip block
                        int braces = 1;
                        while (braces > 0 && i < tokens.size()) {
                            if (tokens.get(i).value.equals("{")) braces++;
                            if (tokens.get(i).value.equals("}")) braces--;
                            if (braces > 0) i++;
                        }
                        i++; 
                        
                        // Check for ELSE
                        if (i < tokens.size() && tokens.get(i).value.equals("else")) {
                            i++; 
                            if (check(tokens, i, "{")) {
                                i++;
                                blockStack.push("ELSE"); 
                            }
                        }
                    }
                }
            }
            else if (t.value.equals("else")) {
                i++; 
                if (check(tokens, i, "{")) {
                    i++; 
                    int braces = 1;
                    while (braces > 0 && i < tokens.size()) {
                        if (tokens.get(i).value.equals("{")) braces++;
                        if (tokens.get(i).value.equals("}")) braces--;
                        if (braces > 0) i++;
                    }
                    i++;
                }
            }
            // --- WHILE ---
            else if (t.value.equals("while")) {
                List<Token> conditionTokens = extractExpressionTokens(tokens, i + 1, "{");
                boolean result = evaluateLogic(conditionTokens);
                
                if (!result) {
                    i += 1 + conditionTokens.size();
                    if (check(tokens, i, "{")) {
                        i++; 
                        int braces = 1;
                        while (braces > 0 && i < tokens.size()) {
                            if (tokens.get(i).value.equals("{")) braces++;
                            if (tokens.get(i).value.equals("}")) braces--;
                            if (braces > 0) i++;
                        }
                        i++;
                    }
                } else {
                    whileStack.push(i); 
                    i += 1 + conditionTokens.size(); 
                    if (check(tokens, i, "{")) {
                        i++;
                        blockStack.push("WHILE"); 
                    }
                }
            }
            // --- CLOSING BRACE } ---
            else if (t.value.equals("}")) {
                if (!blockStack.isEmpty()) {
                    String type = blockStack.pop();
                    if (type.equals("WHILE")) {
                        if (!whileStack.isEmpty()) i = whileStack.pop(); 
                    } else {
                        i++;
                    }
                } else {
                    i++;
                }
            }
            // --- KEYWORDS ---
            else if (t.type == Token.Type.KEYWORD) {
                switch (t.value) {
                    case "print": i = handlePrint(tokens, i); break;
                    case "clear": graphicsFrame.clear(); i++; if(check(tokens,i,";")) i++; break;
                    case "color": i = handleColor(tokens, i); break;
                    case "circle": i = handleCircle(tokens, i); break;
                    case "rect": i = handleRect(tokens, i); break;
                    case "line": i = handleLine(tokens, i); break;
                    case "triangle": i = handleTriangle(tokens, i); break;
                    case "sleep": i = handleSleep(tokens, i); break;
                    case "help": printHelp(); i++; break;
                    default: i++; break;
                }
            }
            // --- ASSIGNMENT ---
            else if (t.type == Token.Type.ID && check(tokens, i+1, "=")) {
                String varName = t.value;
                i += 2; 
                List<Token> mathTokens = extractExpressionTokens(tokens, i, ";");
                int val = evaluateMath(mathTokens);
                variables.put(varName, val);
                i += mathTokens.size();
                if (check(tokens, i, ";")) i++;
            } 
            else {
                i++;
            }
        }
    }

    /**********************************************************
    * METHOD: printHelp
    * DESCRIPTION: Outputs the detailed documentation to the console.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    private void printHelp() {
        String help = 
            "\n========== LANGUAGE REFERENCE ==========\n\n" +
            "1. VARIABLES & MATH\n" +
            "   x = 10\n" +
            "   y = x + 5 * (20 / 2)\n" +
            "   Supported: + - * / ( ) (PEMDAS rules apply)\n\n" +
            
            "2. LOGIC & CONTROL FLOW\n" +
            "   if x < 10 and y > 5 { ... } else { ... }\n" +
            "   while x < 100 { ... }\n" +
            "   Ops: <, >, <=, >=, ==, !=, and, or, not\n\n" +
            
            "3. GRAPHICS COMMANDS\n" +
            "   color(r, g, b)    : Set RGB color (0-255)\n" +
            "   circle(x, y, r)   : Draw circle at (x,y) with radius r\n" +
            "   rect(x, y, w, h)  : Draw rectangle at (x,y)\n" +
            "   line(x1,y1,x2,y2) : Draw line from p1 to p2\n" +
            "   triangle(x1,y1, x2,y2, x3,y3) : Draw triangle\n" +
            "   clear             : Clear screen\n\n" +
            
            "4. ANIMATION & FUNCTIONS\n" +
            "   anim Name { ... } : Define a reusable block/frame\n" +
            "   run Name          : Execute the animation block\n" +
            "   sleep(ms)         : Pause execution (updates screen)\n\n" +
            
            "5. SYSTEM\n" +
            "   print(value)      : Print number or string to console\n" +
            "   help              : Show this menu\n" +
            "========================================\n";
        printSafe(help);
    }

    // --- LOGIC, MATH, and HELPERS ---

    /**********************************************************
    * METHOD: evaluateLogic
    * DESCRIPTION: Entry point for Logic Evaluation.
    * PARAMETERS: List<Token> tokens - The logic expression
    * RETURN VALUE: boolean - Result of evaluation
    **********************************************************/
    private boolean evaluateLogic(List<Token> tokens) {
        if (tokens.isEmpty()) return false;
        currentTokenIndex = 0;
        return parseLogicOr(tokens);
    }

    /**********************************************************
    * METHOD: parseLogicOr
    * DESCRIPTION: Handles OR operations.
    * PARAMETERS: List<Token> tokens
    * RETURN VALUE: boolean
    **********************************************************/
    private boolean parseLogicOr(List<Token> tokens) {
        boolean left = parseLogicAnd(tokens);
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).value.equals("or")) {
            currentTokenIndex++;
            boolean right = parseLogicAnd(tokens);
            left = left || right;
        }
        return left;
    }

    /**********************************************************
    * METHOD: parseLogicAnd
    * DESCRIPTION: Handles AND operations.
    * PARAMETERS: List<Token> tokens
    * RETURN VALUE: boolean
    **********************************************************/
    private boolean parseLogicAnd(List<Token> tokens) {
        boolean left = parseLogicNot(tokens);
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).value.equals("and")) {
            currentTokenIndex++;
            boolean right = parseLogicNot(tokens);
            left = left && right;
        }
        return left;
    }
    
    /**********************************************************
    * METHOD: parseLogicNot
    * DESCRIPTION: Handles NOT operations.
    * PARAMETERS: List<Token> tokens
    * RETURN VALUE: boolean
    **********************************************************/
    private boolean parseLogicNot(List<Token> tokens) {
        if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).value.equals("not")) {
            currentTokenIndex++;
            return !parseLogicNot(tokens);
        }
        return parseComparison(tokens);
    }

    /**********************************************************
    * METHOD: parseComparison
    * DESCRIPTION: Handles relational operators (==, <, >).
    * PARAMETERS: List<Token> tokens
    * RETURN VALUE: boolean
    **********************************************************/
    private boolean parseComparison(List<Token> tokens) {
        int start = currentTokenIndex;
        int end = start;
        int paren = 0;
        while(end < tokens.size()) {
            String v = tokens.get(end).value;
            if (v.equals("(")) paren++;
            else if (v.equals(")")) { if (paren > 0) paren--; else break; }
            else if (paren == 0 && (v.equals("and") || v.equals("or"))) break;
            end++;
        }
        List<Token> sub = tokens.subList(start, end);
        currentTokenIndex = end; 
        
        int opIndex = -1; String op = ""; int p = 0;
        for(int k=0; k<sub.size(); k++) {
            String v = sub.get(k).value;
            if(v.equals("(")) p++; else if(v.equals(")")) p--;
            else if (p == 0 && v.matches("==|!=|<=|>=|<|>")) { opIndex = k; op = v; break; }
        }

        if (opIndex != -1) {
            int leftVal = evaluateMath(sub.subList(0, opIndex));
            int rightVal = evaluateMath(sub.subList(opIndex + 1, sub.size()));
            switch(op) {
                case "<": return leftVal < rightVal;
                case ">": return leftVal > rightVal;
                case "<=": return leftVal <= rightVal;
                case ">=": return leftVal >= rightVal;
                case "==": return leftVal == rightVal;
                case "!=": return leftVal != rightVal;
            }
        }
        if (sub.size() > 0 && sub.get(0).value.equals("(")) return evaluateMath(sub) != 0;
        return evaluateMath(sub) != 0;
    }

    /**********************************************************
    * METHOD: evaluateMath
    * DESCRIPTION: Entry point for Math Evaluation.
    * PARAMETERS: List<Token> tokens - The math expression
    * RETURN VALUE: int - Result of evaluation
    **********************************************************/
    private int evaluateMath(List<Token> tokens) {
        if (tokens.isEmpty()) return 0;
        return parseExpression(tokens, new int[]{0});
    }

    /**********************************************************
    * METHOD: parseExpression
    * DESCRIPTION: Handles Addition and Subtraction.
    * PARAMETERS: List<Token> tokens, int[] idx
    * RETURN VALUE: int
    **********************************************************/
    private int parseExpression(List<Token> tokens, int[] idx) {
        int left = parseTerm(tokens, idx);
        while (idx[0] < tokens.size()) {
            String op = tokens.get(idx[0]).value;
            if (!op.equals("+") && !op.equals("-")) break;
            idx[0]++;
            int right = parseTerm(tokens, idx);
            if (op.equals("+")) left += right; else left -= right;
        }
        return left;
    }

    /**********************************************************
    * METHOD: parseTerm
    * DESCRIPTION: Handles Multiplication and Division.
    * PARAMETERS: List<Token> tokens, int[] idx
    * RETURN VALUE: int
    **********************************************************/
    private int parseTerm(List<Token> tokens, int[] idx) {
        int left = parseFactor(tokens, idx);
        while (idx[0] < tokens.size()) {
            String op = tokens.get(idx[0]).value;
            if (!op.equals("*") && !op.equals("/")) break;
            idx[0]++;
            int right = parseFactor(tokens, idx);
            if (op.equals("*")) left *= right; else if (right != 0) left /= right;
        }
        return left;
    }

    /**********************************************************
    * METHOD: parseFactor
    * DESCRIPTION: Handles Factors (Numbers, Variables, Parens).
    * PARAMETERS: List<Token> tokens, int[] idx
    * RETURN VALUE: int
    **********************************************************/
    private int parseFactor(List<Token> tokens, int[] idx) {
        if (idx[0] >= tokens.size()) return 0;
        Token t = tokens.get(idx[0]);
        idx[0]++;
        if (t.type == Token.Type.LITERAL) {
             if (t.value.startsWith("\"")) return 0;
             return Integer.parseInt(t.value.replace("\"", ""));
        }
        if (t.type == Token.Type.ID) return variables.getOrDefault(t.value, 0);
        if (t.value.equals("(")) {
            int val = parseExpression(tokens, idx);
            if (idx[0] < tokens.size() && tokens.get(idx[0]).value.equals(")")) idx[0]++;
            return val;
        }
        return 0;
    }

    /**********************************************************
    * METHOD: extractExpressionTokens
    * DESCRIPTION: Extracts a subset of tokens for a single expression.
    * Stops at delimiters like semicolons or keywords.
    * PARAMETERS: List<Token> allTokens, int start, String delimiter
    * RETURN VALUE: List<Token>
    **********************************************************/
    private List<Token> extractExpressionTokens(List<Token> allTokens, int start, String delimiter) {
        List<Token> subset = new ArrayList<>();
        int i = start;
        int parens = 0;
        while (i < allTokens.size()) {
            Token t = allTokens.get(i);
            if (t.value.equals(")") && parens == 0) break;
            if (t.value.equals("(")) parens++; else if (t.value.equals(")")) parens--;
            
            if (parens == 0) {
                if (t.value.equals(delimiter)) break;
                if (t.value.equals("{") || t.value.equals("}")) break;
                if (t.type == Token.Type.KEYWORD) {
                    String v = t.value;
                    if (v.matches("print|if|while|else|anim|run|sleep|clear|color|circle|rect|line|triangle|help")) break;
                }
                if (t.type == Token.Type.ID && i + 1 < allTokens.size() && allTokens.get(i+1).value.equals("=")) break;
            }
            subset.add(t);
            i++;
        }
        return subset;
    }

    /**********************************************************
    * METHOD: resolveValue
    * DESCRIPTION: Converts a token into its string value (or variable value).
    * PARAMETERS: Token t
    * RETURN VALUE: String
    **********************************************************/
    private String resolveValue(Token t) {
        if (t.type == Token.Type.LITERAL) return t.value.replace("\"", "");
        if (t.type == Token.Type.ID) return variables.getOrDefault(t.value, 0).toString();
        return "0";
    }

    /**********************************************************
    * METHOD: check
    * DESCRIPTION: Helper to safely check the value of a token at an index.
    * PARAMETERS: List<Token> tokens, int i, String val
    * RETURN VALUE: boolean
    **********************************************************/
    private boolean check(List<Token> tokens, int i, String val) {
        return i < tokens.size() && tokens.get(i).value.equals(val);
    }
    
    // -- GRAPHICS HANDLERS --
    /**********************************************************
    * METHOD: handleSleep
    * DESCRIPTION: Pauses execution and triggers graphics refresh.
    * PARAMETERS: List<Token> tokens, int i
    * RETURN VALUE: int - new index
    **********************************************************/
    private int handleSleep(List<Token> tokens, int i) {
        i++; if(check(tokens,i,"(")) i++;
        int ms = evaluateMath(extractExpressionTokens(tokens, i, ")")); 
        while(i < tokens.size() && !tokens.get(i).value.equals(")")) i++;
        if(check(tokens,i,")")) i++;
        try { graphicsFrame.refresh(); Thread.sleep(ms); } catch(Exception e){}
        if(check(tokens,i,";")) i++; return i;
    }
    
    /**********************************************************
    * METHOD: handlePrint
    * DESCRIPTION: Prints to console. Handles strings vs math expressions.
    * PARAMETERS: List<Token> tokens, int i
    * RETURN VALUE: int - new index
    **********************************************************/
    private int handlePrint(List<Token> tokens, int i) {
        i++; if(check(tokens,i,"(")) i++;
        List<Token> content = extractExpressionTokens(tokens, i, ")");
        if (content.size() == 1 && content.get(0).type == Token.Type.LITERAL && content.get(0).value.startsWith("\"")) {
            printSafe("> " + content.get(0).value.replace("\"", ""));
        } else {
            int val = evaluateMath(content);
            printSafe("> " + val);
        }
        i += content.size();
        if(check(tokens,i,")")) i++; if(check(tokens,i,";")) i++; return i;
    }

    private int handleColor(List<Token> tokens, int i) { return genericGraphics(tokens, i, (args)->graphicsFrame.setCurrentColor(args[0],args[1],args[2]), 3); }
    private int handleCircle(List<Token> tokens, int i) { return genericGraphics(tokens, i, (args)->graphicsFrame.addCircle(args[0],args[1],args[2]), 3); }
    private int handleRect(List<Token> tokens, int i) { return genericGraphics(tokens, i, (args)->graphicsFrame.addRect(args[0],args[1],args[2],args[3]), 4); }
    private int handleLine(List<Token> tokens, int i) { return genericGraphics(tokens, i, (args)->graphicsFrame.addLine(args[0],args[1],args[2],args[3]), 4); }
    private int handleTriangle(List<Token> tokens, int i) { return genericGraphics(tokens, i, (args)->graphicsFrame.addTriangle(args[0],args[1],args[2],args[3],args[4],args[5]), 6); }

    private interface GfxAction { void apply(int[] args); }
    
    /**********************************************************
    * METHOD: genericGraphics
    * DESCRIPTION: Parsing logic for all graphics commands.
    * Extracts arguments, shows window, adds command to buffer.
    * PARAMETERS: List<Token> tokens, int i, GfxAction action, int count
    * RETURN VALUE: int - new index
    **********************************************************/
    private int genericGraphics(List<Token> tokens, int i, GfxAction action, int count) {
        i++; if(check(tokens,i,"(")) i++;
        int[] args = new int[count];
        for(int k=0; k<count; k++) {
            List<Token> argTokens = extractExpressionTokens(tokens, i, ",");
             if (argTokens.isEmpty() && !check(tokens, i, ",")) {
                 argTokens = extractExpressionTokens(tokens, i, ")");
             }
            args[k] = evaluateMath(argTokens);
            i += argTokens.size();
            if(k<count-1 && check(tokens,i,",")) i++;
        }
        if(check(tokens,i,")")) i++;
        
        // Only show window when graphics are actually used
        graphicsStarted = true;
        graphicsFrame.setVisible(true);
        
        action.apply(args);
        if(check(tokens,i,";")) i++;
        return i;
    }
}