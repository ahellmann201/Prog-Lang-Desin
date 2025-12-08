package assign3;
import java.util.*;

/**
 * Enhanced Token Encoder with Control Structure Support
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Control Structures
 * 
 * DESCRIPTION:
 * This class handles token encoding for the drawing language with
 * support for if statements, while loops, and other control structures.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 */
public class TokenEncoder {
    private Map<String, Integer> keywordCodes = new HashMap<>();
    private Map<String, Integer> operatorCodes = new HashMap<>();
    private Map<String, Integer> symbolTable = new HashMap<>();
    private Map<Integer, Integer> literalTable = new HashMap<>();
    private List<Integer> encodedProgram = new ArrayList<>();
    
    private int nextSymbolCode = 300;
    private int nextLiteralCode = 700;
    
    /********************************************************************
     * METHOD: TokenEncoder Constructor
     * DESCRIPTION: Initializes the token encoder with all keywords
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public TokenEncoder() {
        initializeKeywordCodes();
        initializeOperatorCodes();
    }
    
    /********************************************************************
     * METHOD: initializeKeywordCodes
     * DESCRIPTION: Initializes the keyword coding scheme (100-199)
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void initializeKeywordCodes() {
        // Drawing commands
        keywordCodes.put("circle", 100);
        keywordCodes.put("triangle", 101);
        keywordCodes.put("rectangle", 102);
        keywordCodes.put("square", 103);
        keywordCodes.put("polygon", 104);
        keywordCodes.put("endpolygon", 105);
        keywordCodes.put("clearpolygon", 106);
        keywordCodes.put("points", 107);
        
        // Control commands
        keywordCodes.put("fill", 108);
        keywordCodes.put("set", 109);
        keywordCodes.put("clear", 110);
        keywordCodes.put("for", 111);
        keywordCodes.put("loop", 112);
        
        // Animation commands
        keywordCodes.put("animation", 113);
        keywordCodes.put("keyframe", 114);
        keywordCodes.put("play", 115);
        keywordCodes.put("stop", 116);
        keywordCodes.put("delay", 117);
        
        // Additional keywords
        keywordCodes.put("help", 118);
        
        // Control structure keywords (NEW for Assignment 7)
        keywordCodes.put("if", 119);
        keywordCodes.put("else", 120);
        keywordCodes.put("while", 121);
        
        // Interpreter keywords
        keywordCodes.put("integer", 122);
        keywordCodes.put("input", 123);
        keywordCodes.put("print", 124);
        
        // Line drawing
        keywordCodes.put("line", 125);
    }
    
    /********************************************************************
     * METHOD: initializeOperatorCodes
     * DESCRIPTION: Initializes the operator coding scheme (200-299)
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    private void initializeOperatorCodes() {
        operatorCodes.put("=", 200);
        operatorCodes.put("+", 201);
        operatorCodes.put("-", 202);
        operatorCodes.put("*", 203);
        operatorCodes.put("/", 204);
        operatorCodes.put(",", 205); // Parameter separator
        operatorCodes.put(";", 206); // Statement terminator
        operatorCodes.put("(", 207);
        operatorCodes.put(")", 208);
        operatorCodes.put("<", 209);
        operatorCodes.put(">", 210);
        operatorCodes.put("<=", 211);
        operatorCodes.put(">=", 212);
        operatorCodes.put("==", 213);
        operatorCodes.put("!=", 214);
        operatorCodes.put("++", 215);
        operatorCodes.put("--", 216);
        operatorCodes.put("+=", 217);
        operatorCodes.put("-=", 218);
        operatorCodes.put("*=", 219);
        operatorCodes.put("/=", 220);
        operatorCodes.put("{", 221); // NEW: Block start
        operatorCodes.put("}", 222); // NEW: Block end
    }
    
    /********************************************************************
     * METHOD: encodeCommand
     * DESCRIPTION: Encodes a complete command line into tokens
     * PARAMETERS: String command - the command to encode
     * RETURN VALUE: List<Integer> - list of encoded tokens
     ********************************************************************/
    public List<Integer> encodeCommand(String command) {
        List<Integer> lineCodes = new ArrayList<>();
        List<String> tokens = tokenize(command);
        
        for (String token : tokens) {
            int code = encodeToken(token);
            if (code != -1) {
                lineCodes.add(code);
                encodedProgram.add(code);
            }
        }
        return lineCodes;
    }
    
    /********************************************************************
     * METHOD: encodeProgram
     * DESCRIPTION: Encodes an entire multi-line program
     * PARAMETERS: String program - the complete program
     * RETURN VALUE: List<Integer> - list of encoded tokens for entire program
     ********************************************************************/
    public List<Integer> encodeProgram(String program) {
        clear(); // Clear previous encoding
        List<Integer> allTokens = new ArrayList<>();
        String[] lines = program.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue; // Skip empty lines and comments
            }
            List<Integer> lineTokens = encodeCommand(line);
            allTokens.addAll(lineTokens);
        }
        
        return allTokens;
    }
    
    /********************************************************************
     * METHOD: tokenize
     * DESCRIPTION: Splits a command into individual tokens
     * PARAMETERS: String command - the command to tokenize
     * RETURN VALUE: List<String> - list of tokens
     ********************************************************************/
    private List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else if (isOperatorChar(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                // Handle multi-character operators
                if (i + 1 < command.length()) {
                    String twoCharOp = command.substring(i, i + 2);
                    if (operatorCodes.containsKey(twoCharOp)) {
                        tokens.add(twoCharOp);
                        i++; // Skip next character
                        continue;
                    }
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
    
    /********************************************************************
     * METHOD: isOperatorChar
     * DESCRIPTION: Checks if a character is an operator character
     * PARAMETERS: char c - character to check
     * RETURN VALUE: boolean - true if operator character
     ********************************************************************/
    private boolean isOperatorChar(char c) {
        return "=+-*/;,()<>!{}".indexOf(c) != -1;
    }
    
    /********************************************************************
     * METHOD: encodeToken
     * DESCRIPTION: Encodes a single token and returns its code
     * PARAMETERS: String token - token to encode
     * RETURN VALUE: int - encoded token value, -1 if invalid
     ********************************************************************/
    public int encodeToken(String token) {
        // Check if keyword
        if (keywordCodes.containsKey(token.toLowerCase())) {
            return keywordCodes.get(token.toLowerCase());
        }
        
        // Check if operator
        if (operatorCodes.containsKey(token)) {
            return operatorCodes.get(token);
        }
        
        // Check if symbol (variable name)
        if (isSymbol(token)) {
            if (!symbolTable.containsKey(token)) {
                symbolTable.put(token, nextSymbolCode++);
            }
            return symbolTable.get(token);
        }
        
        // Check if literal (number)
        if (isLiteral(token)) {
            try {
                int value = Integer.parseInt(token);
                if (!literalTable.containsKey(value)) {
                    literalTable.put(value, nextLiteralCode++);
                }
                return literalTable.get(value);
            } catch (NumberFormatException e) {
                return -1; // Invalid token
            }
        }
        
        return -1; // Unknown token type
    }
    
    /********************************************************************
     * METHOD: isSymbol
     * DESCRIPTION: Checks if a token is a valid symbol (variable name)
     * PARAMETERS: String token - token to check
     * RETURN VALUE: boolean - true if valid symbol
     ********************************************************************/
    private boolean isSymbol(String token) {
        if (token.isEmpty()) return false;
        
        // Symbols must start with a letter or underscore
        char firstChar = token.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_') {
            return false;
        }
        
        // Check remaining characters
        for (int i = 1; i < token.length(); i++) {
            char c = token.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        
        return true;
    }
    
    /********************************************************************
     * METHOD: isLiteral
     * DESCRIPTION: Checks if a token is a numeric literal
     * PARAMETERS: String token - token to check
     * RETURN VALUE: boolean - true if numeric literal
     ********************************************************************/
    private boolean isLiteral(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /********************************************************************
     * METHOD: getSymbolTable
     * DESCRIPTION: Returns the symbol table
     * PARAMETERS: None
     * RETURN VALUE: Map<String, Integer> - symbol table
     ********************************************************************/
    public Map<String, Integer> getSymbolTable() {
        return new HashMap<>(symbolTable);
    }
    
    /********************************************************************
     * METHOD: getLiteralTable
     * DESCRIPTION: Returns the literal table
     * PARAMETERS: None
     * RETURN VALUE: Map<Integer, Integer> - literal table
     ********************************************************************/
    public Map<Integer, Integer> getLiteralTable() {
        return new HashMap<>(literalTable);
    }
    
    /********************************************************************
     * METHOD: getEncodedProgram
     * DESCRIPTION: Returns the complete encoded program
     * PARAMETERS: None
     * RETURN VALUE: List<Integer> - encoded program
     ********************************************************************/
    public List<Integer> getEncodedProgram() {
        return new ArrayList<>(encodedProgram);
    }
    
    /********************************************************************
     * METHOD: clear
     * DESCRIPTION: Clears all tables and encoded program
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clear() {
        symbolTable.clear();
        literalTable.clear();
        encodedProgram.clear();
        nextSymbolCode = 300;
        nextLiteralCode = 700;
    }
    
    /********************************************************************
     * METHOD: getKeywordName
     * DESCRIPTION: Returns the keyword name for a given code
     * PARAMETERS: int code - the keyword code
     * RETURN VALUE: String - keyword name, or null if not found
     ********************************************************************/
    public String getKeywordName(int code) {
        for (Map.Entry<String, Integer> entry : keywordCodes.entrySet()) {
            if (entry.getValue() == code) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /********************************************************************
     * METHOD: getOperatorSymbol
     * DESCRIPTION: Returns the operator symbol for a given code
     * PARAMETERS: int code - the operator code
     * RETURN VALUE: String - operator symbol, or null if not found
     ********************************************************************/
    public String getOperatorSymbol(int code) {
        for (Map.Entry<String, Integer> entry : operatorCodes.entrySet()) {
            if (entry.getValue() == code) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /********************************************************************
     * METHOD: decodeToken
     * DESCRIPTION: Decodes a token code back to its string representation
     * PARAMETERS: int code - the token code to decode
     * RETURN VALUE: String - decoded token, or null if not found
     ********************************************************************/
    public String decodeToken(int code) {
        // Check keywords
        String keyword = getKeywordName(code);
        if (keyword != null) {
            return keyword;
        }
        
        // Check operators
        String operator = getOperatorSymbol(code);
        if (operator != null) {
            return operator;
        }
        
        // Check symbols
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            if (entry.getValue() == code) {
                return entry.getKey();
            }
        }
        
        // Check literals
        for (Map.Entry<Integer, Integer> entry : literalTable.entrySet()) {
            if (entry.getValue() == code) {
                return entry.getKey().toString();
            }
        }
        
        return null;
    }
    
    /********************************************************************
     * METHOD: decodeProgram
     * DESCRIPTION: Decodes an encoded program back to source code
     * PARAMETERS: List<Integer> encoded - encoded program
     * RETURN VALUE: String - decoded source code
     ********************************************************************/
    public String decodeProgram(List<Integer> encoded) {
        StringBuilder source = new StringBuilder();
        
        for (int i = 0; i < encoded.size(); i++) {
            String token = decodeToken(encoded.get(i));
            if (token != null) {
                source.append(token);
                
                // Add spacing for readability
                if (i < encoded.size() - 1) {
                    String nextToken = decodeToken(encoded.get(i + 1));
                    if (nextToken != null && !nextToken.matches("[,;)]") && 
                        !token.matches("[(]")) {
                        source.append(" ");
                    }
                }
            }
        }
        
        return source.toString();
    }
}