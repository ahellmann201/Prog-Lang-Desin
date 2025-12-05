package finalProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.swing.SwingUtilities;

/**
 * The Logic Engine.
 * Updated with IF/ELSE and TRIANGLE support.
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
        new Thread(() -> {
            try {
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

    private void printSafe(String msg) {
        SwingUtilities.invokeLater(() -> outputWindow.printToConsole(msg));
    }

    private void parse(List<Token> tokens) {
        Stack<Integer> whileStack = new Stack<>();
        
        int i = 0;
        while (i < tokens.size()) {
            Token t = tokens.get(i);

            // --- IF / ELSE ---
            if (t.value.equals("if")) {
                int result = evaluateCondition(tokens, i + 1); // Returns 1 (true) or 0 (false) or -1 (error)
                i += 4; // Skip if, var, op, target
                
                if (check(tokens, i, "{")) i++; // Enter block
                
                if (result == 1) {
                    // Condition TRUE: Continue normally inside the block
                } else {
                    // Condition FALSE: Skip the IF block
                    i = findMatchingBrace(tokens, i);
                    
                    // Check for ELSE
                    if (i < tokens.size() && tokens.get(i).value.equals("else")) {
                        i++; // Skip 'else'
                        if (check(tokens, i, "{")) i++;
                        // Enter ELSE block
                    }
                }
            }
            // --- ELSE (Skipping) ---
            else if (t.value.equals("else")) {
                // If we encounter 'else' here, it means we just finished a TRUE if-block.
                // So we must SKIP the else block.
                i++; // Skip 'else'
                if (check(tokens, i, "{")) {
                    i++; // Enter block just to find the end
                    i = findMatchingBrace(tokens, i);
                }
            }
            // --- WHILE LOOP ---
            else if (t.value.equals("while")) {
                int result = evaluateCondition(tokens, i + 1);
                
                if (result == 0) {
                    i = findMatchingBrace(tokens, i + 1 + 3); // Skip logic
                } else {
                    whileStack.push(i); 
                    i += 4; // skip while, var, op, target
                    if (check(tokens, i, "{")) i++;
                }
            }
            // --- END OF BLOCK ---
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
                    case "clear": graphicsFrame.clear(); i++; if(check(tokens,i,";")) i++; break;
                    case "color": i = handleColor(tokens, i); break;
                    case "circle": i = handleCircle(tokens, i); break;
                    case "rect": i = handleRect(tokens, i); break;
                    case "line": i = handleLine(tokens, i); break;
                    case "triangle": i = handleTriangle(tokens, i); break; // NEW
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

    // Helper to evaluate "var op target"
    private int evaluateCondition(List<Token> tokens, int startIdx) {
        if (startIdx + 2 >= tokens.size()) return -1;
        String varName = tokens.get(startIdx).value;
        String op = tokens.get(startIdx+1).value;
        int target = Integer.parseInt(resolveValue(tokens.get(startIdx+2)));
        int current = variables.getOrDefault(varName, 0);
        
        if (op.equals("<")) return (current < target) ? 1 : 0;
        if (op.equals(">")) return (current > target) ? 1 : 0;
        if (op.equals("==")) return (current == target) ? 1 : 0;
        if (op.equals("!=")) return (current != target) ? 1 : 0;
        return 0;
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
        i += 2; 
        int exprLen = 1; 
        if (i + 1 < tokens.size()) {
             String nextVal = tokens.get(i+1).value;
             if (nextVal.matches("[+\\-*/]")) exprLen = 3; 
        }
        int val = evaluateExpression(tokens, i);
        variables.put(varName, val);
        i += exprLen;
        if (i < tokens.size() && tokens.get(i).value.equals(";")) i++;
        return i;
    }
    
    private int handleSleep(List<Token> tokens, int i) {
        i++; if (check(tokens, i, "(")) i++;
        int ms = Integer.parseInt(resolveValue(tokens.get(i++)));
        if (check(tokens, i, ")")) i++;
        try { 
            // Refresh graphics before sleeping to ensure frame is visible
            graphicsFrame.refresh(); 
            Thread.sleep(ms); 
        } catch (InterruptedException e) {}
        if(check(tokens,i,";")) i++;
        return i;
    }

    // --- Graphics Handlers ---
    private int handleTriangle(List<Token> tokens, int i) {
        i++; if (check(tokens, i, "(")) i++;
        int x1 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int y1 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int x2 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int y2 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int x3 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ",")) i++;
        int y3 = Integer.parseInt(resolveValue(tokens.get(i++))); if (check(tokens, i, ")")) i++;
        
        graphicsFrame.setVisible(true);
        graphicsFrame.addTriangle(x1, y1, x2, y2, x3, y3);
        
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
            "--- HELP ---\n" +
            "SHAPES: circle(x,y,r), rect(x,y,w,h), line(x1,y1,x2,y2), triangle(x1,y1,x2,y2,x3,y3)\n" +
            "LOGIC:  if x < 10 { } else { }, while x < 10 { }\n" +
            "ANIMATION: clear, sleep(ms)\n";
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