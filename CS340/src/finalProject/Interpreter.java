package finalProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.swing.SwingUtilities;

/**
 * The Logic Engine.
 * Supports: Variables, Math, Graphics, Animation (Sleep), Loops (While).
 */
public class Interpreter {
    
    private IDEWindow outputWindow;
    private GraphicsFrame graphicsFrame;
    private Lexer lexer;
    private Map<String, Integer> variables; 

    public Interpreter(IDEWindow outputWindow) {
        this.outputWindow = outputWindow;
        this.lexer = new Lexer();
        this.variables = new HashMap<>();
        this.graphicsFrame = new GraphicsFrame();
    }

    public void execute(String sourceCode) {
        // Run logic in a separate thread to prevent UI freezing
        new Thread(() -> {
            try {
                // Clear memory only at the start of execution
                variables.clear();
                List<Token> tokens = lexer.tokenize(sourceCode);
                
                printSafe("--- Starting Execution ---");
                parse(tokens);
                printSafe("\n--- Execution Finished ---");
                
            } catch (Exception e) {
                printSafe("RUNTIME ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // Helper to print safely from background thread
    private void printSafe(String msg) {
        SwingUtilities.invokeLater(() -> outputWindow.printToConsole(msg));
    }

    private void parse(List<Token> tokens) {
        Stack<Integer> whileStack = new Stack<>();
        
        int i = 0;
        while (i < tokens.size()) {
            Token t = tokens.get(i);

            // --- WHILE LOOP ---
            if (t.value.equals("while")) {
                int conditionIdx = i + 1;
                if (conditionIdx + 2 >= tokens.size()) break;

                String varName = tokens.get(conditionIdx).value;
                String op = tokens.get(conditionIdx+1).value;
                String targetStr = resolveValue(tokens.get(conditionIdx+2));
                int target = Integer.parseInt(targetStr);
                int current = variables.getOrDefault(varName, 0);
                
                boolean isTrue = false;
                if (op.equals("<")) isTrue = current < target;
                if (op.equals(">")) isTrue = current > target;
                if (op.equals("==")) isTrue = current == target;
                if (op.equals("!=")) isTrue = current != target;

                if (!isTrue) {
                    i = findMatchingBrace(tokens, i);
                } else {
                    whileStack.push(i); 
                    i += 4; // skip while, var, op, target
                    if (check(tokens, i, "{")) i++;
                }
            }
            // --- END OF LOOP ---
            else if (t.value.equals("}")) {
                if (!whileStack.isEmpty()) {
                    i = whileStack.pop(); 
                } else {
                    i++;
                }
            }
            // --- KEYWORDS ---
            else if (t.type == Token.Type.KEYWORD) {
                switch (t.value) {
                    case "print": i = handlePrint(tokens, i); break;
                    case "clear": 
                        graphicsFrame.clear(); 
                        i++; 
                        if(check(tokens,i,";")) i++;
                        break;
                    case "color": i = handleColor(tokens, i); break;
                    case "circle": i = handleCircle(tokens, i); break;
                    case "rect": i = handleRect(tokens, i); break;
                    case "line": i = handleLine(tokens, i); break;
                    case "sleep": i = handleSleep(tokens, i); break;
                    case "help": printHelp(); i++; break;
                    default: i++; break;
                }
            }
            // --- ASSIGNMENT ---
            else if (t.type == Token.Type.ID && check(tokens, i+1, "=")) {
                i = handleAssignment(tokens, i);
            } 
            else {
                i++;
            }
        }
    }

    private int findMatchingBrace(List<Token> tokens, int start) {
        int braces = 0;
        for(int k=start; k<tokens.size(); k++) {
            if(tokens.get(k).value.equals("{")) braces++;
            if(tokens.get(k).value.equals("}")) {
                braces--;
                if(braces == 0) return k + 1;
            }
        }
        return tokens.size(); 
    }

    private int handleAssignment(List<Token> tokens, int i) {
        String varName = tokens.get(i).value;
        i += 2; // skip var and =

        // Check if next part is an expression (e.g., x + 10)
        int exprLen = 1; 
        if (i + 1 < tokens.size()) {
             String nextVal = tokens.get(i+1).value;
             if (nextVal.matches("[+\\-*/]")) {
                 exprLen = 3; // A op B
             }
        }

        int val = evaluateExpression(tokens, i);
        variables.put(varName, val);
        
        i += exprLen;
        if (i < tokens.size() && tokens.get(i).value.equals(";")) i++;
        return i;
    }
    
    private int handleSleep(List<Token> tokens, int i) {
        i++; 
        if (check(tokens, i, "(")) i++;
        int ms = Integer.parseInt(resolveValue(tokens.get(i++)));
        if (check(tokens, i, ")")) i++;
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
        if(check(tokens,i,";")) i++;
        return i;
    }

    private int handleLine(List<Token> tokens, int i) {
        i++; if (check(tokens, i, "(")) i++;
        int x1 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int y1 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int x2 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int y2 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ")")) i++;
        graphicsFrame.setVisible(true);
        graphicsFrame.addLine(x1, y1, x2, y2);
        if(check(tokens,i,";")) i++;
        return i;
    }

    private int handleCircle(List<Token> tokens, int i) {
        i++; if(check(tokens,i,"(")) i++;
        int x = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int y = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int r = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,")")) i++;
        graphicsFrame.setVisible(true);
        graphicsFrame.addCircle(x, y, r);
        if(check(tokens,i,";")) i++;
        return i;
    }

    private int handleRect(List<Token> tokens, int i) {
        i++; if(check(tokens,i,"(")) i++;
        int x = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int y = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int w = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int h = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,")")) i++;
        graphicsFrame.setVisible(true);
        graphicsFrame.addRect(x, y, w, h);
        if(check(tokens,i,";")) i++;
        return i;
    }

    private int handleColor(List<Token> tokens, int i) {
        i++; if(check(tokens,i,"(")) i++;
        int r = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int g = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,",")) i++;
        int b = Integer.parseInt(resolveValue(tokens.get(i++))); if(check(tokens,i,")")) i++;
        graphicsFrame.setCurrentColor(r, g, b);
        if(check(tokens,i,";")) i++;
        return i;
    }

    private int handlePrint(List<Token> tokens, int i) {
        i++;
        if (i < tokens.size() && tokens.get(i).value.equals("(")) {
            i++; 
            Token content = tokens.get(i);
            String result = resolveValue(content);
            printSafe("> " + result);
            i++; 
            if (i < tokens.size() && tokens.get(i).value.equals(")")) i++;
            if (i < tokens.size() && tokens.get(i).value.equals(";")) i++;
        }
        return i;
    }

    private void printHelp() {
        String help = 
            "--- HELP & COMMAND REFERENCE ---\n" +
            "SHAPES:\n" +
            "  circle(x, y, radius)\n" +
            "  rect(x, y, width, height)\n" +
            "  line(x1, y1, x2, y2)\n" +
            "  color(r, g, b)  - Set RGB color (0-255)\n" +
            "  clear           - Clear screen\n\n" +
            "ANIMATION:\n" +
            "  sleep(ms)       - Pause execution (e.g., sleep(100))\n\n" +
            "LOGIC:\n" +
            "  print(x)        - Print variable or number\n" +
            "  x = 10          - Assign variable\n" +
            "  while x < 10 {  - Loop block\n" +
            "     x = x + 1\n" +
            "  }\n";
        printSafe(help);
    }

    private boolean check(List<Token> tokens, int i, String val) {
        return i < tokens.size() && tokens.get(i).value.equals(val);
    }

    private String resolveValue(Token t) {
        if (t.type == Token.Type.LITERAL) return t.value.replace("\"", "");
        if (t.type == Token.Type.ID) return variables.getOrDefault(t.value, 0).toString();
        return "0";
    }

    private int evaluateExpression(List<Token> tokens, int currentIndex) {
        Token op1 = tokens.get(currentIndex);
        int val1 = Integer.parseInt(resolveValue(op1));
        if (currentIndex + 1 < tokens.size()) {
            Token op = tokens.get(currentIndex + 1);
            if (op.value.equals("+")) return val1 + Integer.parseInt(resolveValue(tokens.get(currentIndex + 2)));
            if (op.value.equals("-")) return val1 - Integer.parseInt(resolveValue(tokens.get(currentIndex + 2)));
            if (op.value.equals("*")) return val1 * Integer.parseInt(resolveValue(tokens.get(currentIndex + 2)));
            if (op.value.equals("/")) {
                 int val2 = Integer.parseInt(resolveValue(tokens.get(currentIndex + 2)));
                 return val2 != 0 ? val1 / val2 : 0;
            }
        }
        return val1;
    }
}