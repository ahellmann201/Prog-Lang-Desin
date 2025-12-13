package finalProject;

/*******************************************************************
* Name of program: Lexer
* PROGRAMMER: Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignment 3 (Tokenization)
*
* DESCRIPTION:
* This class is responsible for breaking raw source code into a list
* of Token objects. It uses Regular Expressions (Regex) to identify
* keywords, identifiers, numbers, strings, and operators.
* It also handles stripping out comments.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Assisted by Artificial Intelligence.
*******************************************************************/

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
        "(==|<=|>=|!=|[+\\-*/=;(),{}<>])"; // Operators

    /**********************************************************
    * METHOD: tokenize
    * DESCRIPTION: Scans the input string line by line and converts
    * it into a list of Tokens.
    * PARAMETERS: String source - The raw source code
    * RETURN VALUE: List<Token> - The list of identified tokens
    **********************************************************/
    public List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = source.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Handle inline comments (remove everything after # or //)
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

    /**********************************************************
    * METHOD: determineType
    * DESCRIPTION: specific type of a token string (e.g., Keyword vs ID).
    * PARAMETERS: String val - The string value of the token
    * RETURN VALUE: Token.Type - The classification of the token
    **********************************************************/
    private Token.Type determineType(String val) {
        if (val.matches("if|else|while|print|var|int|draw|circle|rect|line|triangle|color|clear|sleep|help|anim|run|and|or|not")) return Token.Type.KEYWORD;
        if (val.matches("\\d+")) return Token.Type.LITERAL;
        if (val.matches("\".*\"")) return Token.Type.LITERAL;
        if (val.matches("[a-zA-Z][a-zA-Z0-9]*")) return Token.Type.ID;
        return Token.Type.OPERATOR;
    }
}