package assign3;
import java.util.*;

/**
 * Code Formatter for Pretty Printing
 * 
 * PROGRAMMER: [Your Name]
 * COURSE: CS340 Programming Lang/Design
 * DATE: [Current Date]
 * REQUIREMENT: Assignment 7 - Code Editor
 * 
 * DESCRIPTION:
 * This class formats code with proper indentation and spacing.
 * It makes code more readable by standardizing formatting.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 */
public class CodeFormatter {
    
    /********************************************************************
     * METHOD: format
     * DESCRIPTION: Formats code with proper indentation
     * PARAMETERS: String code - the code to format
     * RETURN VALUE: String - formatted code
     ********************************************************************/
    public String format(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "";
        }
        
        String[] lines = code.split("\n");
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        boolean inMultiLineComment = false;
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Handle multi-line comments
            if (inMultiLineComment) {
                formatted.append(getIndent(indentLevel)).append(trimmed).append("\n");
                if (trimmed.contains("*/")) {
                    inMultiLineComment = false;
                }
                continue;
            }
            
            if (trimmed.startsWith("/*")) {
                inMultiLineComment = true;
                formatted.append(getIndent(indentLevel)).append(trimmed).append("\n");
                continue;
            }
            
            // Skip empty lines and single-line comments
            if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("#")) {
                formatted.append(getIndent(indentLevel)).append(trimmed).append("\n");
                continue;
            }
            
            // Handle line with content
            String formattedLine = formatLine(trimmed);
            
            // Adjust indentation based on braces
            if (formattedLine.contains("}")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }
            
            // Apply current indentation
            formatted.append(getIndent(indentLevel)).append(formattedLine).append("\n");
            
            // Increase indentation for next line if needed
            if (formattedLine.contains("{") && !formattedLine.contains("}")) {
                indentLevel++;
            }
        }
        
        return formatted.toString().trim();
    }
    
    /********************************************************************
     * METHOD: formatLine
     * DESCRIPTION: Formats a single line with proper spacing
     * PARAMETERS: String line - the line to format
     * RETURN VALUE: String - formatted line
     ********************************************************************/
    private String formatLine(String line) {
        // Remove extra spaces
        line = line.replaceAll("\\s+", " ");
        line = line.replaceAll("\\s*,\\s*", ", ");
        line = line.replaceAll("\\s*=\\s*", " = ");
        line = line.replaceAll("\\s*;\\s*", "; ");
        line = line.replaceAll("\\s*\\(\\s*", "(");
        line = line.replaceAll("\\s*\\)\\s*", ")");
        line = line.replaceAll("\\s*\\{\\s*", " {");
        line = line.replaceAll("\\s*\\}\\s*", "}");
        
        // Fix spacing around operators
        line = line.replaceAll("\\s*<\\s*", " < ");
        line = line.replaceAll("\\s*>\\s*", " > ");
        line = line.replaceAll("\\s*<=\\s*", " <= ");
        line = line.replaceAll("\\s*>=\\s*", " >= ");
        line = line.replaceAll("\\s*==\\s*", " == ");
        line = line.replaceAll("\\s*!=\\s*", " != ");
        line = line.replaceAll("\\s*\\+\\s*", " + ");
        line = line.replaceAll("\\s*-\\s*", " - ");
        line = line.replaceAll("\\s*\\*\\s*", " * ");
        line = line.replaceAll("\\s*/\\s*", " / ");
        
        // Remove double spaces
        while (line.contains("  ")) {
            line = line.replace("  ", " ");
        }
        
        // Trim and return
        return line.trim();
    }
    
    /********************************************************************
     * METHOD: getIndent
     * DESCRIPTION: Creates indentation string for given level
     * PARAMETERS: int level - indentation level
     * RETURN VALUE: String - indentation string (4 spaces per level)
     ********************************************************************/
    private String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("    "); // 4 spaces per indent
        }
        return indent.toString();
    }
    
    /********************************************************************
     * METHOD: compress
     * DESCRIPTION: Compresses code by removing unnecessary whitespace
     * PARAMETERS: String code - the code to compress
     * RETURN VALUE: String - compressed code
     ********************************************************************/
    public String compress(String code) {
        if (code == null) return "";
        
        // Remove comments
        code = removeComments(code);
        
        // Remove extra whitespace
        code = code.replaceAll("\\s+", " ");
        code = code.replaceAll("\\s*,\\s*", ",");
        code = code.replaceAll("\\s*;\\s*", ";");
        code = code.replaceAll("\\s*\\(\\s*", "(");
        code = code.replaceAll("\\s*\\)\\s*", ")");
        code = code.replaceAll("\\s*\\{\\s*", "{");
        code = code.replaceAll("\\s*\\}\\s*", "}");
        
        return code.trim();
    }
    
    /********************************************************************
     * METHOD: removeComments
     * DESCRIPTION: Removes comments from code
     * PARAMETERS: String code - the code with comments
     * RETURN VALUE: String - code without comments
     ********************************************************************/
    private String removeComments(String code) {
        StringBuilder result = new StringBuilder();
        String[] lines = code.split("\n");
        boolean inMultiLineComment = false;
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            if (inMultiLineComment) {
                if (trimmed.contains("*/")) {
                    inMultiLineComment = false;
                    // Add text after comment end if any
                    int endIndex = trimmed.indexOf("*/") + 2;
                    if (endIndex < trimmed.length()) {
                        String afterComment = trimmed.substring(endIndex).trim();
                        if (!afterComment.isEmpty()) {
                            result.append(afterComment).append("\n");
                        }
                    }
                }
                continue;
            }
            
            if (trimmed.startsWith("/*")) {
                inMultiLineComment = true;
                if (trimmed.contains("*/")) {
                    // Single line multi-line comment
                    inMultiLineComment = false;
                    int endIndex = trimmed.indexOf("*/") + 2;
                    if (endIndex < trimmed.length()) {
                        String afterComment = trimmed.substring(endIndex).trim();
                        if (!afterComment.isEmpty()) {
                            result.append(afterComment).append("\n");
                        }
                    }
                }
                continue;
            }
            
            // Remove single-line comments
            int commentIndex = trimmed.indexOf("//");
            if (commentIndex != -1) {
                trimmed = trimmed.substring(0, commentIndex).trim();
            }
            
            commentIndex = trimmed.indexOf("#");
            if (commentIndex != -1) {
                trimmed = trimmed.substring(0, commentIndex).trim();
            }
            
            if (!trimmed.isEmpty()) {
                result.append(trimmed).append("\n");
            }
        }
        
        return result.toString().trim();
    }
    
    /********************************************************************
     * METHOD: beautify
     * DESCRIPTION: Beautifies code with advanced formatting
     * PARAMETERS: String code - the code to beautify
     * RETURN VALUE: String - beautified code
     ********************************************************************/
    public String beautify(String code) {
        code = format(code);
        String[] lines = code.split("\n");
        StringBuilder beautified = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue; // Skip empty lines
            }
            
            // Add blank lines before control structures
            if (line.trim().startsWith("if ") || 
                line.trim().startsWith("while ") || 
                line.trim().startsWith("for ")) {
                if (beautified.length() > 0 && 
                    !beautified.toString().endsWith("\n\n")) {
                    beautified.append("\n");
                }
            }
            
            beautified.append(line).append("\n");
            
            // Add blank line after closing brace of control structure
            if (line.trim().equals("}")) {
                beautified.append("\n");
            }
        }
        
        return beautified.toString().trim();
    }
}