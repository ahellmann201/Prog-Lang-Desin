// New token constants for control flow
private static final int IF = 110;
private static final int WHILE = 111;
private static final int ELSE = 112;
private static final int ENDIF = 113;
private static final int ENDWHILE = 114;
private static final int LESS_THAN = 210;
private static final int GREATER_THAN = 211;
private static final int LESS_EQUAL = 212;
private static final int GREATER_EQUAL = 213;
private static final int EQUAL = 214;
private static final int NOT_EQUAL = 215;

// Control flow state tracking
private Stack<Boolean> blockStack = new Stack<>();
private boolean inElseBlock = false;
private boolean skipBlock = false;
private boolean inLoop = false;
private int loopStartLine = -1;

// Updated getCodeGenerator() for control flow
private String getCodeGenerator(int currentToken, int nextToken, 
                                 List<String> tokens, int index) {
    switch (currentToken) {
        case IF:
            return "start_if";
        case WHILE:
            return "start_while";
        case ELSE:
            return "else";
        case ENDIF:
            return "end_if";
        case ENDWHILE:
            return "end_while";
        case LESS_THAN:
        case GREATER_THAN:
        case LESS_EQUAL:
        case GREATER_EQUAL:
        case EQUAL:
        case NOT_EQUAL:
            return "compare";
        // ... rest of existing cases ...
    }
    return null;
}

// New code generators for control flow
private void executeStartIf(List<String> tokens, int index) {
    // Evaluate condition after "if ("
    if (index + 4 < tokens.size()) {
        String left = tokens.get(index + 2);
        String op = tokens.get(index + 3);
        String right = tokens.get(index + 4);
        
        boolean condition = evaluateCondition(left, op, right);
        blockStack.push(condition);
        
        if (verboseMode) {
            ioHandler.appendToHistory("If condition: " + left + " " + op + " " + 
                                     right + " = " + condition + "\n");
        }
    }
}

private void executeStartWhile(List<String> tokens, int index) {
    // Similar to start_if but stores line number for looping
    if (index + 4 < tokens.size()) {
        String left = tokens.get(index + 2);
        String op = tokens.get(index + 3);
        String right = tokens.get(index + 4);
        
        boolean condition = evaluateCondition(left, op, right);
        blockStack.push(condition);
        inLoop = condition;
        
        if (condition) {
            loopStartLine = currentLineNumber;
        }
        
        if (verboseMode) {
            ioHandler.appendToHistory("While condition: " + left + " " + op + " " + 
                                     right + " = " + condition + "\n");
        }
    }
}

private void executeElse() {
    if (!blockStack.isEmpty()) {
        boolean ifCondition = blockStack.pop();
        // Execute else block only if if condition was false
        blockStack.push(!ifCondition);
        inElseBlock = true;
        
        if (verboseMode) {
            ioHandler.appendToHistory("Else branch active: " + !ifCondition + "\n");
        }
    }
}

private void executeEndIf() {
    if (!blockStack.isEmpty()) {
        blockStack.pop();
        inElseBlock = false;
    }
}

private void executeEndWhile() {
    if (!blockStack.isEmpty()) {
        boolean continueLoop = blockStack.pop();
        if (continueLoop && loopStartLine != -1) {
            // Jump back to start of while loop
            currentLineNumber = loopStartLine - 1;
        } else {
            inLoop = false;
            loopStartLine = -1;
        }
    }
}

private boolean evaluateCondition(String left, String op, String right) {
    int leftVal = getValue(left);
    int rightVal = getValue(right);
    
    switch (op) {
        case "<": return leftVal < rightVal;
        case ">": return leftVal > rightVal;
        case "<=": return leftVal <= rightVal;
        case ">=": return leftVal >= rightVal;
        case "==": return leftVal == rightVal;
        case "!=": return leftVal != rightVal;
        default: return false;
    }
}

private int getValue(String operand) {
    if (Character.isDigit(operand.charAt(0))) {
        return Integer.parseInt(operand);
    } else {
        return variables.getOrDefault(operand, 0);
    }
}
private void initializeKeywordCodes() {
    // Control flow keywords
    keywordCodes.put("if", 110);
    keywordCodes.put("while", 111);
    keywordCodes.put("else", 112);
    keywordCodes.put("endif", 113);
    keywordCodes.put("endwhile", 114);
    // ... existing keywords ...
}

private void initializeOperatorCodes() {
    // Comparison operators
    operatorCodes.put("<", 210);
    operatorCodes.put(">", 211);
    operatorCodes.put("<=", 212);
    operatorCodes.put(">=", 213);
    operatorCodes.put("==", 214);
    operatorCodes.put("!=", 215);
    // ... existing operators ...
}