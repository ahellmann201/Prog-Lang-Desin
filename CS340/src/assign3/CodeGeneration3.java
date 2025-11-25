package assign3;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/****************************************************************************
 * Code Generation Module                                                   *
 *                                                                          *
 *    PROGRAMMER:    Andrew Hellmann, Olivia Hornbeck, Tanner Sweigart      *
 *    COURSE:  CS340 Programming Lang/Design                               *
 *    DATE:    September 11 , 2025                                         *
 *    REQUIREMENT:    Assignment 2 + Final Project Enhancements            *
 *                                                                          *
 *    DESCRIPTION:                                                          *
 *    This module handles the generation and management of shapes,          *
 *    including circles, triangles, rectangles, squares, and polygons.      *
 *    It provides methods for creating, storing, and drawing shapes,        *
 *    as well as managing loops, variables, and mathematical operations     *
 *    for the drawing application.                                          *
 *                                                                          *
 *    COPYRIGHT:                                                            *
 *    This code is copyright (c)2025 Andrew Hellmann, Olivia Hornbeck,      *
 *    Tanner Sweigart and Dean Zeller.                                      *
 *                                                                          *
 *    CREDITS:                                                              *
 *    Java API Documentation, Course materials                              *
 *                                                                          *
 ***************************************************************************/

public class CodeGeneration3 {
    private List<Shape> shapes = new ArrayList<>();
    private List<Point> polygonPoints = new ArrayList<>();
    private boolean fillShape = false;
    private List<Loop> loops = new ArrayList<>();
    private List<Shape> loopShapes = new ArrayList<>();
    private boolean isRecordingLoop = false;
    private Stack<LoopContext> loopStack = new Stack<>();
    private FunctionManager functionManager = new FunctionManager();
    
    private List<List<Shape>> shapeHistory = new ArrayList<>();
    private int currentHistoryIndex = -1;

    // ==================== SHAPE PROCESSING METHODS ====================

    public void processCircleCommand(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = ioHandler.parseValue(matcher.group(1).trim());
                int x = ioHandler.parseValue(matcher.group(2).trim());
                int y = ioHandler.parseValue(matcher.group(3).trim());
                
                if (isRecordingLoop) {
                    loopShapes.add(new Circle(x, y, radius, fillShape));
                    ioHandler.appendToHistory("System: Circle added to loop at (" + x + ", " + y + ") with radius " + radius + " (filled: " + fillShape + ")\n");
                } else {
                    shapes.add(new Circle(x, y, radius, fillShape));
                    ioHandler.appendToHistory("System: Circle drawn at (" + x + ", " + y + ") with radius " + radius + " (filled: " + fillShape + ")\n");
                }
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }

    public void processCircleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = ioHandler.parseValue(matcher.group(1).trim());
                int x = ioHandler.parseValue(matcher.group(2).trim());
                int y = ioHandler.parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Circle(x, y, radius, fillShape));
                ioHandler.appendToHistory("System: Circle added to loop at (" + x + ", " + y + ") with radius " + radius + " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }

    public void processTriangleCommand(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                int x3 = ioHandler.parseValue(matcher.group(5).trim());
                int y3 = ioHandler.parseValue(matcher.group(6).trim());
                
                if (isRecordingLoop) {
                    loopShapes.add(new Triangle(x1, y1, x2, y2, x3, y3, fillShape));
                    ioHandler.appendToHistory("System: Triangle added to loop with points (" + x1 + "," + y1 + "), (" + x2 + "," + y2 + "), (" + x3 + "," + y3 + ")\n");
                } else {
                    shapes.add(new Triangle(x1, y1, x2, y2, x3, y3, fillShape));
                    ioHandler.appendToHistory("System: Triangle drawn with points (" + x1 + "," + y1 + "), (" + x2 + "," + y2 + "), (" + x3 + "," + y3 + ")\n");
                }
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in triangle command. Use: triangle, x1, y1, x2, y2, x3, y3\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid triangle command format. Use: triangle, x1, y1, x2, y2, x3, y3\n");
        }
    }

    public void processTriangleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                int x3 = ioHandler.parseValue(matcher.group(5).trim());
                int y3 = ioHandler.parseValue(matcher.group(6).trim());
                
                loopShapes.add(new Triangle(x1, y1, x2, y2, x3, y3, fillShape));
                ioHandler.appendToHistory("System: Triangle added to loop with points (" + x1 + "," + y1 + "), (" + x2 + "," + y2 + "), (" + x3 + "," + y3 + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in triangle command. Use: triangle, x1, y1, x2, y2, x3, y3\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid triangle command format. Use: triangle, x1, y1, x2, y2, x3, y3\n");
        }
    }

    public void processRectangleCommand(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                
                shapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                ioHandler.appendToHistory("System: Rectangle drawn from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }

    public void processRectangleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                
                loopShapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                ioHandler.appendToHistory("System: Rectangle added to loop from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }

    public void processSquareCommand(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = ioHandler.parseValue(matcher.group(1).trim());
                int y = ioHandler.parseValue(matcher.group(2).trim());
                int size = ioHandler.parseValue(matcher.group(3).trim());
                
                shapes.add(new Square(x, y, size, fillShape));
                ioHandler.appendToHistory("System: Square drawn at (" + x + ", " + y + ") with size " + size + " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }

    public void processSquareCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        saveState();
        Pattern pattern = Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = ioHandler.parseValue(matcher.group(1).trim());
                int y = ioHandler.parseValue(matcher.group(2).trim());
                int size = ioHandler.parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Square(x, y, size, fillShape));
                ioHandler.appendToHistory("System: Square added to loop at (" + x + ", " + y + ") with size " + size + " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }

    public void processPointsCommand(String command, InputOutputHandler3 ioHandler) {
        saveState();
        try {
            String[] parts = command.split(",\\s*");
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split("\\s*,\\s*|\\s+");
                if (coords.length >= 2) {
                    int x = ioHandler.parseValue(coords[0]);
                    int y = ioHandler.parseValue(coords[1]);
                    points.add(new Point(x, y));
                }
            }
            
            if (points.size() >= 3) {
                shapes.add(new Polygon(points, fillShape));
                ioHandler.appendToHistory("System: Polygon drawn with " + points.size() + " points (filled: " + fillShape + ")\n");
            } else {
                ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
            }
        } catch (NumberFormatException e) {
            ioHandler.appendToHistory("System: Invalid numbers in points command. Use: points, x1,y1 x2,y2 x3,y3 ...\n");
        }
    }

    public void processPointsCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        saveState();
        try {
            String[] parts = command.split(",\\s*");
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split("\\s*,\\s*|\\s+");
                if (coords.length >= 2) {
                    int x = ioHandler.parseValue(coords[0]);
                    int y = ioHandler.parseValue(coords[1]);
                    points.add(new Point(x, y));
                }
            }
            
            if (points.size() >= 3) {
                loopShapes.add(new Polygon(points, fillShape));
                ioHandler.appendToHistory("System: Polygon added to loop with " + points.size() + " points (filled: " + fillShape + ")\n");
            } else {
                ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
            }
        } catch (NumberFormatException e) {
            ioHandler.appendToHistory("System: Invalid numbers in points command. Use: points, x1,y1 x2,y2 x3,y3 ...\n");
        }
    }

    // ==================== POLYGON METHODS ====================

    public void startPolygon(InputOutputHandler3 ioHandler) {
        saveState();
        polygonPoints.clear();
        ioHandler.appendToHistory("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
    }

    public void addPolygonPoint(int x, int y, InputOutputHandler3 ioHandler) {
        polygonPoints.add(new Point(x, y));
        ioHandler.appendToHistory("Added point: (" + x + ", " + y + ")\n");
    }

    public void endPolygon(InputOutputHandler3 ioHandler, boolean recordingLoop) {
        saveState();
        if (polygonPoints.size() >= 3) {
            if (recordingLoop) {
                loopShapes.add(new Polygon(new ArrayList<>(polygonPoints), fillShape));
                ioHandler.appendToHistory("System: Polygon added to loop with " + polygonPoints.size() + " points (filled: " + fillShape + ")\n");
            } else {
                shapes.add(new Polygon(polygonPoints, fillShape));
                ioHandler.appendToHistory("System: Polygon drawn with " + polygonPoints.size() + " points (filled: " + fillShape + ")\n");
            }
            polygonPoints.clear();
        } else {
            ioHandler.appendToHistory("System: Need at least 3 points to draw a polygon\n");
        }
    }

    public void clearPolygon(InputOutputHandler3 ioHandler) {
        polygonPoints.clear();
        ioHandler.appendToHistory("System: Polygon points cleared\n");
    }

    // ==================== MATH AND PEMDAS ====================

    public int evaluateMathExpressionWithPEMDAS(String expression) {
        try {
            return evaluateExpression(removeWhitespace(expression));
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int evaluateExpression(String expr) {
        // Handle parentheses first
        while (expr.contains("(")) {
            expr = evaluateParentheses(expr);
        }
        
        // Handle exponents (right-to-left)
        expr = evaluateOperations(expr, new String[]{"^"}, true);
        
        // Handle multiplication and division (left-to-right)
        expr = evaluateOperations(expr, new String[]{"*", "/"}, false);
        
        // Handle addition and subtraction (left-to-right)
        expr = evaluateOperations(expr, new String[]{"+", "-"}, false);
        
        return Integer.parseInt(expr);
    }
    
    private String evaluateParentheses(String expr) {
        Pattern pattern = Pattern.compile("\\(([^()]+)\\)");
        Matcher matcher = pattern.matcher(expr);
        
        while (matcher.find()) {
            String innerExpr = matcher.group(1);
            int result = evaluateExpression(innerExpr);
            expr = expr.replace("(" + innerExpr + ")", String.valueOf(result));
        }
        return expr;
    }
    
    private String evaluateOperations(String expr, String[] operators, boolean rightToLeft) {
        for (String op : operators) {
            while (expr.contains(op)) {
                Pattern pattern = Pattern.compile("(-?\\d+)\\s*\\" + op + "\\s*(-?\\d+)");
                Matcher matcher = pattern.matcher(expr);
                
                if (matcher.find()) {
                    int left = Integer.parseInt(matcher.group(1));
                    int right = Integer.parseInt(matcher.group(2));
                    int result = performOperation(left, right, op);
                    
                    expr = expr.replace(matcher.group(0), String.valueOf(result));
                } else {
                    break;
                }
            }
        }
        return expr;
    }
    
    private int performOperation(int left, int right, String operator) {
        switch (operator) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": return right != 0 ? left / right : 0;
            case "^": return (int) Math.pow(left, right);
            default: return 0;
        }
    }
    
    private String removeWhitespace(String str) {
        return str.replaceAll("\\s+", "");
    }

    // ==================== NESTED LOOPS ====================

    class LoopContext {
        String type;
        String varName;
        int startValue;
        String condition;
        String increment;
        List<Shape> loopBody;
        
        public LoopContext(String type, String varName, int startValue, String condition, String increment) {
            this.type = type;
            this.varName = varName;
            this.startValue = startValue;
            this.condition = condition;
            this.increment = increment;
            this.loopBody = new ArrayList<>();
        }
    }
    
    public void startNestedLoop(String loopType, String varName, int startValue, String condition, String increment, InputOutputHandler3 ioHandler) {
        LoopContext context = new LoopContext(loopType, varName, startValue, condition, increment);
        loopStack.push(context);
        isRecordingLoop = true;
        loopShapes.clear();
        ioHandler.appendToHistory("System: Started " + loopType + " loop with " + varName + "=" + startValue + "\n");
    }
    
    public void endNestedLoop(InputOutputHandler3 ioHandler) {
        if (!loopStack.isEmpty()) {
            LoopContext context = loopStack.pop();
            executeNestedLoop(context, ioHandler);
        }
        isRecordingLoop = !loopStack.isEmpty();
    }
    
    private void executeNestedLoop(LoopContext context, InputOutputHandler3 ioHandler) {
        Map<String, Integer> variables = new HashMap<>();
        variables.put(context.varName, context.startValue);
        
        int iterationCount = 0;
        int maxIterations = 1000;
        
        while (iterationCount < maxIterations && evaluateCondition(context.condition, variables)) {
            // Execute loop body
            for (Shape shape : context.loopBody) {
                Shape evaluatedShape = processShapeWithVariables(shape, variables);
                shapes.add(evaluatedShape);
            }
            
            // Apply increment
            applyLoopIncrement(context, variables);
            iterationCount++;
        }
        
        ioHandler.appendToHistory("System: " + context.type + " loop completed with " + iterationCount + " iterations\n");
    }
    
    private boolean evaluateCondition(String condition, Map<String, Integer> variables) {
        // Replace variables with values
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            condition = condition.replace(entry.getKey(), entry.getValue().toString());
        }
        
        // Evaluate comparisons
        if (condition.contains("<=")) {
            String[] parts = condition.split("<=");
            return evaluateMathExpressionWithPEMDAS(parts[0]) <= evaluateMathExpressionWithPEMDAS(parts[1]);
        } else if (condition.contains(">=")) {
            String[] parts = condition.split(">=");
            return evaluateMathExpressionWithPEMDAS(parts[0]) >= evaluateMathExpressionWithPEMDAS(parts[1]);
        } else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            return evaluateMathExpressionWithPEMDAS(parts[0]) < evaluateMathExpressionWithPEMDAS(parts[1]);
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            return evaluateMathExpressionWithPEMDAS(parts[0]) > evaluateMathExpressionWithPEMDAS(parts[1]);
        } else if (condition.contains("==")) {
            String[] parts = condition.split("==");
            return evaluateMathExpressionWithPEMDAS(parts[0]) == evaluateMathExpressionWithPEMDAS(parts[1]);
        } else if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            return evaluateMathExpressionWithPEMDAS(parts[0]) != evaluateMathExpressionWithPEMDAS(parts[1]);
        }
        
        return false;
    }
    
    private void applyLoopIncrement(LoopContext context, Map<String, Integer> variables) {
        if (context.increment.contains("+=")) {
            String[] parts = context.increment.split("\\+=");
            int incrementValue = evaluateMathExpressionWithPEMDAS(parts[1].trim());
            variables.put(context.varName, variables.get(context.varName) + incrementValue);
        } else if (context.increment.equals(context.varName + "++")) {
            variables.put(context.varName, variables.get(context.varName) + 1);
        } else if (context.increment.equals(context.varName + "--")) {
            variables.put(context.varName, variables.get(context.varName) - 1);
        } else if (context.increment.contains("=")) {
            String[] parts = context.increment.split("=");
            if (parts[0].trim().equals(context.varName)) {
                int newValue = evaluateMathExpressionWithPEMDAS(parts[1].trim());
                variables.put(context.varName, newValue);
            }
        }
    }

    // ==================== VARIABLE SHAPE PROCESSING ====================

    private Shape processShapeWithVariables(Shape shape, Map<String, Integer> variables) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            int radius = evaluateShapeParameter(Integer.toString(circle.radius), variables);
            int x = evaluateShapeParameter(Integer.toString(circle.x), variables);
            int y = evaluateShapeParameter(Integer.toString(circle.y), variables);
            return new Circle(x, y, radius, circle.filled);
        } 
        else if (shape instanceof Triangle) {
            Triangle triangle = (Triangle) shape;
            int x1 = evaluateShapeParameter(Integer.toString(triangle.x1), variables);
            int y1 = evaluateShapeParameter(Integer.toString(triangle.y1), variables);
            int x2 = evaluateShapeParameter(Integer.toString(triangle.x2), variables);
            int y2 = evaluateShapeParameter(Integer.toString(triangle.y2), variables);
            int x3 = evaluateShapeParameter(Integer.toString(triangle.x3), variables);
            int y3 = evaluateShapeParameter(Integer.toString(triangle.y3), variables);
            return new Triangle(x1, y1, x2, y2, x3, y3, triangle.filled);
        }
        else if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            int x1 = evaluateShapeParameter(Integer.toString(rectangle.x1), variables);
            int y1 = evaluateShapeParameter(Integer.toString(rectangle.y1), variables);
            int x2 = evaluateShapeParameter(Integer.toString(rectangle.x2), variables);
            int y2 = evaluateShapeParameter(Integer.toString(rectangle.y2), variables);
            return new Rectangle(x1, y1, x2, y2, rectangle.filled);
        } 
        else if (shape instanceof Square) {
            Square square = (Square) shape;
            int x = evaluateShapeParameter(Integer.toString(square.x), variables);
            int y = evaluateShapeParameter(Integer.toString(square.y), variables);
            int size = evaluateShapeParameter(Integer.toString(square.size), variables);
            return new Square(x, y, size, square.filled);
        } 
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            List<Point> evaluatedPoints = new ArrayList<>();
            for (Point point : polygon.points) {
                int x = evaluateShapeParameter(Integer.toString(point.x), variables);
                int y = evaluateShapeParameter(Integer.toString(point.y), variables);
                evaluatedPoints.add(new Point(x, y));
            }
            return new Polygon(evaluatedPoints, polygon.filled);
        }
        
        return shape;
    }

    private int evaluateShapeParameter(String param, Map<String, Integer> variables) {
        try {
            if (param == null || param.trim().isEmpty()) {
                return 0;
            }
            
            String expression = param.trim();
            
            // Replace variables with their values
            for (String varName : variables.keySet()) {
                expression = expression.replaceAll("\\b" + varName + "\\b", variables.get(varName).toString());
            }
            
            // Evaluate any mathematical expressions
            return evaluateMathExpressionWithPEMDAS(expression);
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== FUNCTION SUPPORT ====================

    public void defineFunction(String name, List<String> parameters, List<String> bodyCommands, InputOutputHandler3 ioHandler) {
        functionManager.defineFunction(name, parameters, bodyCommands, ioHandler);
    }
    
    public void callFunction(String name, List<String> arguments, InputOutputHandler3 ioHandler) {
        functionManager.callFunction(name, arguments, this, ioHandler);
    }
    
    public boolean functionExists(String name) {
        return functionManager.functionExists(name);
    }

    // ==================== LOOP MANAGEMENT ====================

    public void startLoopRecording(InputOutputHandler3 ioHandler) {
        loopShapes.clear();
        isRecordingLoop = true;
        ioHandler.appendToHistory("System: Loop recording started. All shapes will be added to the loop.\n");
    }

    public void endLoopRecording(String name, InputOutputHandler3 ioHandler) {
        if (isRecordingLoop && !loopShapes.isEmpty()) {
            List<String> commands = new ArrayList<>();
            
            for (Shape shape : loopShapes) {
                if (shape instanceof Circle) {
                    Circle circle = (Circle) shape;
                    commands.add("circle, " + circle.radius + ", " + circle.x + ", " + circle.y);
                } else if (shape instanceof Triangle) {
                    Triangle triangle = (Triangle) shape;
                    commands.add("triangle, " + triangle.x1 + ", " + triangle.y1 + ", " + 
                                             triangle.x2 + ", " + triangle.y2 + ", " + 
                                             triangle.x3 + ", " + triangle.y3);
                } else if (shape instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) shape;
                    commands.add("rectangle, " + rectangle.x1 + ", " + rectangle.y1 + ", " + 
                                rectangle.x2 + ", " + rectangle.y2);
                } else if (shape instanceof Square) {
                    Square square = (Square) shape;
                    commands.add("square, " + square.x + ", " + square.y + ", " + square.size);
                } else if (shape instanceof Polygon) {
                    Polygon polygon = (Polygon) shape;
                    StringBuilder pointsCommand = new StringBuilder("points");
                    for (Point point : polygon.points) {
                        pointsCommand.append(", ").append(point.x).append(",").append(point.y);
                    }
                    commands.add(pointsCommand.toString());
                }
            }
            
            loops.add(new Loop(name, commands));
            loopShapes.clear();
            isRecordingLoop = false;
            
            ioHandler.appendToHistory("System: Loop '" + name + "' saved with " + commands.size() + " commands\n");
        } else {
            ioHandler.appendToHistory("System: No loop recording in progress or no shapes recorded\n");
        }
    }

    public void playLoop(String name, DrawingPanel panel, InputOutputHandler3 ioHandler) {
        Loop targetLoop = null;
        
        for (Loop loop : loops) {
            if (loop.getName().equalsIgnoreCase(name)) {
                targetLoop = loop;
                break;
            }
        }
        
        if (targetLoop != null) {
            ioHandler.appendToHistory("System: Playing loop '" + name + "'\n");
            
            for (String command : targetLoop.getCommands()) {
                String lowerCommand = command.toLowerCase().trim();
                
                if (lowerCommand.startsWith("circle")) {
                    processCircleCommand(command, ioHandler);
                } else if (lowerCommand.startsWith("triangle")) {
                    processTriangleCommand(command, ioHandler);
                } else if (lowerCommand.startsWith("rectangle")) {
                    processRectangleCommand(command, ioHandler);
                } else if (lowerCommand.startsWith("square")) {
                    processSquareCommand(command, ioHandler);
                } else if (lowerCommand.startsWith("points")) {
                    processPointsCommand(command, ioHandler);
                }
            }
            
            panel.repaint();
        } else {
            ioHandler.appendToHistory("System: Loop '" + name + "' not found\n");
        }
    }

    // ==================== UNDO/REDO ====================

    public void saveState() {
        if (currentHistoryIndex < shapeHistory.size() - 1) {
            shapeHistory.subList(currentHistoryIndex + 1, shapeHistory.size()).clear();
        }
        
        List<Shape> stateCopy = new ArrayList<>();
        for (Shape shape : getShapes()) {
            stateCopy.add(cloneShape(shape));
        }
        
        shapeHistory.add(stateCopy);
        currentHistoryIndex = shapeHistory.size() - 1;
        
        if (shapeHistory.size() > 50) {
            shapeHistory.remove(0);
            currentHistoryIndex--;
        }
    }

    public boolean undoLastCommand(InputOutputHandler3 ioHandler) {
        if (currentHistoryIndex > 0) {
            currentHistoryIndex--;
            
            getShapes().clear();
            List<Shape> previousState = shapeHistory.get(currentHistoryIndex);
            for (Shape shape : previousState) {
                getShapes().add(cloneShape(shape));
            }
            
            ioHandler.appendToHistory("System: Undo successful - reverted to previous state\n");
            return true;
        } else if (currentHistoryIndex == 0) {
            getShapes().clear();
            currentHistoryIndex = -1;
            shapeHistory.clear();
            ioHandler.appendToHistory("System: Undo successful - cleared all shapes\n");
            return true;
        } else {
            ioHandler.appendToHistory("System: Nothing to undo\n");
            return false;
        }
    }

    private Shape cloneShape(Shape shape) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            return new Circle(circle.x, circle.y, circle.radius, circle.filled);
        } else if (shape instanceof Triangle) {
            Triangle triangle = (Triangle) shape;
            return new Triangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, 
                               triangle.x3, triangle.y3, triangle.filled);
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            return new Rectangle(rect.x1, rect.y1, rect.x2, rect.y2, rect.filled);
        } else if (shape instanceof Square) {
            Square square = (Square) shape;
            return new Square(square.x, square.y, square.size, square.filled);
        } else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            List<Point> pointsCopy = new ArrayList<>(polygon.points);
            return new Polygon(pointsCopy, polygon.filled);
        }
        return null;
    }

    // ==================== UTILITY METHODS ====================

    public void clearScreen(InputOutputHandler3 ioHandler) {
        saveState();
        shapes.clear();
        polygonPoints.clear();
        loopShapes.clear();
        ioHandler.appendToHistory("System: Screen cleared - all shapes removed\n");
    }

    // ==================== GETTERS AND SETTERS ====================

    public List<Shape> getShapes() { return shapes; }
    public List<Point> getPolygonPoints() { return polygonPoints; }
    public List<Shape> getLoopShapes() { return loopShapes; }
    public List<Loop> getLoops() { return loops; }
    public boolean isRecordingLoop() { return isRecordingLoop; }
    public void setFillShape(boolean fillShape) { this.fillShape = fillShape; }
    public boolean isFillShape() { return fillShape; }
    public void clearShapes() { shapes.clear(); }
    public void clearLoopShapes() { loopShapes.clear(); }

    // ==================== INNER CLASSES ====================

    abstract class Shape {
        boolean filled;
        
        Shape(boolean filled) {
            this.filled = filled;
        }
        
        abstract void draw(Graphics g);
    }

    class Circle extends Shape {
        int x, y, radius;
        
        Circle(int x, int y, int radius, boolean filled) {
            super(filled);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.BLUE);
            if (filled) {
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            } else {
                g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            }
        }
    }

    class Triangle extends Shape {
        int x1, y1, x2, y2, x3, y3;
        
        Triangle(int x1, int y1, int x2, int y2, int x3, int y3, boolean filled) {
            super(filled);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.x3 = x3;
            this.y3 = y3;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.RED);
            
            int[] xPoints = {x1, x2, x3};
            int[] yPoints = {y1, y2, y3};
            
            if (filled) {
                g.fillPolygon(xPoints, yPoints, 3);
            } else {
                g.drawPolygon(xPoints, yPoints, 3);
            }
        }
    }

    class Rectangle extends Shape {
        int x1, y1, x2, y2;
        
        Rectangle(int x1, int y1, int x2, int y2, boolean filled) {
            super(filled);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.GREEN);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            
            if (filled) {
                g.fillRect(x, y, width, height);
            } else {
                g.drawRect(x, y, width, height);
            }
        }
    }

    class Square extends Shape {
        int x, y, size;
        
        Square(int x, int y, int size, boolean filled) {
            super(filled);
            this.x = x;
            this.y = y;
            this.size = size;
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.MAGENTA);
            if (filled) {
                g.fillRect(x, y, size, size);
            } else {
                g.drawRect(x, y, size, size);
            }
        }
    }

    class Polygon extends Shape {
        List<Point> points;
        
        Polygon(List<Point> points, boolean filled) {
            super(filled);
            this.points = new ArrayList<>(points);
        }
        
        @Override
        void draw(Graphics g) {
            g.setColor(Color.ORANGE);
            
            int[] xPoints = new int[points.size()];
            int[] yPoints = new int[points.size()];
            
            for (int i = 0; i < points.size(); i++) {
                xPoints[i] = points.get(i).x;
                yPoints[i] = points.get(i).y;
            }
            
            if (filled) {
                g.fillPolygon(xPoints, yPoints, points.size());
            } else {
                g.drawPolygon(xPoints, yPoints, points.size());
            }
        }
    }

    class Loop {
        private String name;
        private List<String> commands;
        
        public Loop(String name, List<String> commands) {
            this.name = name;
            this.commands = new ArrayList<>(commands);
        }
        
        public String getName() {
            return this.name;
        }
        
        public List<String> getCommands() {
            return new ArrayList<>(this.commands);
        }
    }

    class FunctionManager {
        private Map<String, UserFunction> functions = new HashMap<>();
        
        class UserFunction {
            String name;
            List<String> parameters;
            List<String> bodyCommands;
            
            public UserFunction(String name, List<String> parameters, List<String> bodyCommands) {
                this.name = name;
                this.parameters = parameters;
                this.bodyCommands = bodyCommands;
            }
        }
        
        public void defineFunction(String name, List<String> parameters, List<String> bodyCommands, InputOutputHandler3 ioHandler) {
            functions.put(name, new UserFunction(name, parameters, bodyCommands));
            ioHandler.appendToHistory("System: Function '" + name + "' defined with " + parameters.size() + " parameters\n");
        }
        
        public void callFunction(String name, List<String> arguments, CodeGeneration3 codeGen, InputOutputHandler3 ioHandler) {
            UserFunction function = functions.get(name);
            if (function == null) {
                ioHandler.appendToHistory("System: Function '" + name + "' not found\n");
                return;
            }
            
            if (function.parameters.size() != arguments.size()) {
                ioHandler.appendToHistory("System: Parameter count mismatch for function '" + name + "'\n");
                return;
            }
            
            Map<String, Integer> localVars = new HashMap<>();
            for (int i = 0; i < function.parameters.size(); i++) {
                localVars.put(function.parameters.get(i), codeGen.evaluateMathExpressionWithPEMDAS(arguments.get(i)));
            }
            
            for (String command : function.bodyCommands) {
                executeFunctionCommand(command, localVars, codeGen, ioHandler);
            }
        }
        
        private void executeFunctionCommand(String command, Map<String, Integer> localVars, CodeGeneration3 codeGen, InputOutputHandler3 ioHandler) {
            String expandedCommand = command;
            for (Map.Entry<String, Integer> entry : localVars.entrySet()) {
                expandedCommand = expandedCommand.replace(entry.getKey(), entry.getValue().toString());
            }
            
            if (expandedCommand.startsWith("circle")) {
                codeGen.processCircleCommand(expandedCommand, ioHandler);
            } else if (expandedCommand.startsWith("triangle")) {
                codeGen.processTriangleCommand(expandedCommand, ioHandler);
            } else if (expandedCommand.startsWith("rectangle")) {
                codeGen.processRectangleCommand(expandedCommand, ioHandler);
            } else if (expandedCommand.startsWith("square")) {
                codeGen.processSquareCommand(expandedCommand, ioHandler);
            } else if (expandedCommand.startsWith("points")) {
                codeGen.processPointsCommand(expandedCommand, ioHandler);
            }
        }
        
        public boolean functionExists(String name) {
            return functions.containsKey(name);
        }
    }
}