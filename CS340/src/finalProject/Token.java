package finalProject;

/**
 * Represents a single token in the language.
 * Updated with encodings for Math and Logic
 */
public class Token {
    
    public enum Type {
        KEYWORD, ID, LITERAL, OPERATOR, SEPARATOR, UNKNOWN
    }

    public Type type;
    public String value;
    public int lineNumber;
    public int code; 

    public Token(Type type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.code = encode(type, value);
    }

    private int encode(Type type, String val) {
        switch (type) {
            case KEYWORD:
                if (val.equals("print")) return 153;
                if (val.equals("if")) return 100;
                if (val.equals("while")) return 101;
                if (val.equals("else")) return 102;
                
                // Logic
                if (val.equals("and")) return 110;
                if (val.equals("or")) return 111;
                if (val.equals("not")) return 112;

                // Graphics
                if (val.equals("circle")) return 160;
                if (val.equals("rect")) return 161;
                if (val.equals("color")) return 162;
                if (val.equals("clear")) return 163;
                if (val.equals("sleep")) return 164;
                if (val.equals("line")) return 165;
                if (val.equals("help")) return 166;
                if (val.equals("triangle")) return 167;
                
                // Anim
                if (val.equals("anim")) return 180;
                if (val.equals("run")) return 181;
                
                return 199;
            case OPERATOR:
                if (val.equals("=")) return 220;
                if (val.equals("+")) return 248;
                if (val.equals("-")) return 249;
                if (val.equals("*")) return 250; 
                if (val.equals("/")) return 251; 
                
                if (val.equals("==")) return 260;
                if (val.equals("<")) return 261;
                if (val.equals(">")) return 262;
                if (val.equals("!=")) return 263;
                if (val.equals("<=")) return 264;
                if (val.equals(">=")) return 265;
                return 200;
            case ID: return 300; 
            case LITERAL: return 700; 
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-15s | ID: %d", type, value, code);
    }
}