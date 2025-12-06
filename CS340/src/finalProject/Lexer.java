package finalProject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    // Regex to capture everything we need
    private static final String REGEX = 
        "([a-zA-Z][a-zA-Z0-9]*)|" + // Words (Keywords/IDs)
        "(\\d+)|" +                 // Numbers
        "(\".*?\")|" +              // Strings
        "(==|<=|>=|!=|[+\\-*/=;(),{}<>])"; // Operators (Added * / < >)

    public List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = source.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // FIXED: Handle inline comments (remove everything after # or //)
            int commentIdx = line.indexOf("#");
            if (commentIdx != -1) line = line.substring(0, commentIdx);
            
            commentIdx = line.indexOf("//");
            if (commentIdx != -1) line = line.substring(0, commentIdx);

            if (line.trim().isEmpty()) continue;

            Matcher matcher = Pattern.compile(REGEX).matcher(line);
            while (matcher.find()) {
                String val = matcher.group();
                tokens.add(new Token(determineType(val), val, i + 1));
            }
        }
        return tokens;
    }

    private Token.Type determineType(String val) {
        // Updated with all new keywords
        if (val.matches("if|else|while|print|var|int|draw|circle|rect|line|triangle|color|clear|sleep|help|anim|run|and|or|not")) return Token.Type.KEYWORD;
        if (val.matches("\\d+")) return Token.Type.LITERAL;
        if (val.matches("\".*\"")) return Token.Type.LITERAL;
        if (val.matches("[a-zA-Z][a-zA-Z0-9]*")) return Token.Type.ID;
        return Token.Type.OPERATOR;
    }
}