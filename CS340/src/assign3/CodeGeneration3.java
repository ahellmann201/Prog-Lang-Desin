package assign3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/****************************************************************************
    * Code Generation Module												*
    *																		*
    *    PROGRAMMER:    Andrew Hellmann, Olivia Hornbeck, Tanner Sweigart   *
    *    COURSE:  CS340 Programming Lang/Design     						*
    *    DATE:    September 11 , 2025    									*
    *    REQUIREMENT:    Assignment 2    									*
    *																		*
    *    DESCRIPTION:    													*
    *    This module handles the generation and management of shapes, 		*
    *    including circles, triangles, rectangles, squares, and polygons.	*
    *    It provides methods for creating, storing, and drawing shapes,		*
    *    as well as managing loops and variables for the drawing 			*
    *    application.														*
    *																		*
    *    COPYRIGHT:    														*
    *    This code is copyright (c)2025 Andrew Hellmann, Olivia Hornbeck,	* 
    *    Tanner Sweigart and Dean Zeller.									*
    *																		*
    *    CREDITS:    														*
    *    Java API Documentation, Course materials							*
    *																		*
    ************************************************************************/

public class CodeGeneration3 {
    private List<Shape> shapes = new ArrayList<>();
    private List<Point> polygonPoints = new ArrayList<>();
    private boolean fillShape = false;
    private List<Loop> loops = new ArrayList<>();
    private List<Shape> loopShapes = new ArrayList<>(); // Shapes in the current loop
    private boolean isRecordingLoop = false;
    
    /************************************************************************************
    *    METHOD:    processCircleCommand    											*
    *    DESCRIPTION:    Processes a circle drawing command    							*
    *    PARAMETERS:    command - the command string containing circle parameters    	*
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processCircleCommand(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = ioHandler.parseValue(matcher.group(1).trim());
                int x = ioHandler.parseValue(matcher.group(2).trim());
                int y = ioHandler.parseValue(matcher.group(3).trim());
                
                if (isRecordingLoop) {
                    loopShapes.add(new Circle(x, y, radius, fillShape));
                    ioHandler.appendToHistory("System: Circle added to loop at (" + x + ", " + y + ") with radius " + radius + 
                                      " (filled: " + fillShape + ")\n");
                } else {
                    shapes.add(new Circle(x, y, radius, fillShape));
                    ioHandler.appendToHistory("System: Circle drawn at (" + x + ", " + y + ") with radius " + radius + 
                                      " (filled: " + fillShape + ")\n");
                }
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processCircleCommandForLoop    										*
    *    DESCRIPTION:    Processes a circle command for loop recording    				*
    *    PARAMETERS:    command - the command string containing circle parameters    	*
    *    RETURN VALUE:    none    														*
    *************************************************************************************/
    public void processCircleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("circle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int radius = ioHandler.parseValue(matcher.group(1).trim());
                int x = ioHandler.parseValue(matcher.group(2).trim());
                int y = ioHandler.parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Circle(x, y, radius, fillShape));
                ioHandler.appendToHistory("System: Circle added to loop at (" + x + ", " + y + ") with radius " + radius + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in circle command. Use: circle, radius, x, y\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid circle command format. Use: circle, radius, x, y\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processTriangleCommand    											*
    *    DESCRIPTION:    Processes a triangle drawing command with 3 points    		*
    *    PARAMETERS:    command - the command string containing triangle parameters    	*
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processTriangleCommand(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
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
    
    /************************************************************************************
    *    METHOD:    processTriangleCommandForLoop    									*
    *    DESCRIPTION:    Processes a triangle command for loop recording with 3 points *
    *    PARAMETERS:    command - the command string containing triangle parameters    *
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processTriangleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("triangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
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
    /************************************************************************************
    *    METHOD:    processRectangleCommand    											*
    *    DESCRIPTION:    Processes a rectangle drawing command    						*
    *    PARAMETERS:    command - the command string containing rectangle parameters    *
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processRectangleCommand(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                
                shapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                ioHandler.appendToHistory("System: Rectangle drawn from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + 
                                  ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processRectangleCommandForLoop    									*
    *    DESCRIPTION:    Processes a rectangle command for loop recording    			*
    *    PARAMETERS:    command - the command string containing rectangle parameters    *
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processRectangleCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("rectangle\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x1 = ioHandler.parseValue(matcher.group(1).trim());
                int y1 = ioHandler.parseValue(matcher.group(2).trim());
                int x2 = ioHandler.parseValue(matcher.group(3).trim());
                int y2 = ioHandler.parseValue(matcher.group(4).trim());
                
                loopShapes.add(new Rectangle(x1, y1, x2, y2, fillShape));
                ioHandler.appendToHistory("System: Rectangle added to loop from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + 
                                  ") (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in rectangle command. Use: rectangle, x1, y1, x2, y2\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid rectangle command format. Use: rectangle, x1, y1, x2, y2\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processSquareCommand    											*
    *    DESCRIPTION:    Processes a square drawing command    							*
    *    PARAMETERS:    command - the command string containing square parameters    	*
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processSquareCommand(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = ioHandler.parseValue(matcher.group(1).trim());
                int y = ioHandler.parseValue(matcher.group(2).trim());
                int size = ioHandler.parseValue(matcher.group(3).trim());
                
                shapes.add(new Square(x, y, size, fillShape));
                ioHandler.appendToHistory("System: Square drawn at (" + x + ", " + y + ") with size " + size + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processSquareCommandForLoop    										*
    *    DESCRIPTION:    Processes a square command for loop recording    				*
    *    PARAMETERS:    command - the command string containing square parameters    	*
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processSquareCommandForLoop(String command, InputOutputHandler3 ioHandler) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("square\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            try {
                int x = ioHandler.parseValue(matcher.group(1).trim());
                int y = ioHandler.parseValue(matcher.group(2).trim());
                int size = ioHandler.parseValue(matcher.group(3).trim());
                
                loopShapes.add(new Square(x, y, size, fillShape));
                ioHandler.appendToHistory("System: Square added to loop at (" + x + ", " + y + ") with size " + size + 
                                  " (filled: " + fillShape + ")\n");
            } catch (NumberFormatException e) {
                ioHandler.appendToHistory("System: Invalid numbers in square command. Use: square, x, y, size\n");
            }
        } else {
            ioHandler.appendToHistory("System: Invalid square command format. Use: square, x, y, size\n");
        }
    }
    
    /************************************************************************************
    *    METHOD:    processPointsCommand    											*
    *    DESCRIPTION:    Processes a polygon drawing command with specified points    	*
    *    PARAMETERS:    command - the command string containing polygon points    		*
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void processPointsCommand(String command, InputOutputHandler3 ioHandler) {
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
    
    /********************************************************************************************
    *    METHOD:    processPointsCommandForLoop    												*
    *    DESCRIPTION:    Processes a polygon command for loop recording with specified points   *
    *    PARAMETERS:    command - the command string containing polygon points    				*
    *    RETURN VALUE:    none    																*
    ********************************************************************************************/
    public void processPointsCommandForLoop(String command, InputOutputHandler3 ioHandler) {
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
    
    /********************************************************************************************
    *    METHOD:    startPolygon    															*
    *    DESCRIPTION:    Initiates polygon drawing mode    										*
    *    PARAMETERS:    ioHandler - the input/output handler for displaying messages    		*
    *    RETURN VALUE:    none    																*
    ********************************************************************************************/
    public void startPolygon(InputOutputHandler3 ioHandler) {
        polygonPoints.clear();
        ioHandler.appendToHistory("System: Click on the drawing area to add polygon points. Type 'endpolygon' when done.\n");
    }
    
    /********************************************************************************************
    *    METHOD:    addPolygonPoint    															*
    *    DESCRIPTION:    Adds a point to the current polygon    								*
    *    PARAMETERS:    x - the x coordinate of the point, y - the y coordinate of the point    *
    *    RETURN VALUE:    none    																*
    ********************************************************************************************/
    public void addPolygonPoint(int x, int y, InputOutputHandler3 ioHandler) {
        polygonPoints.add(new Point(x, y));
        ioHandler.appendToHistory("Added point: (" + x + ", " + y + ")\n");
    }
    
    /********************************************************************************************
    *    METHOD:    endPolygon    																*
    *    DESCRIPTION:    Finalizes and adds the current polygon to shapes    					*
    *    PARAMETERS:    ioHandler - the input/output handler for displaying messages    		*
    *    RETURN VALUE:    none    																*
    ********************************************************************************************/
    public void endPolygon(InputOutputHandler3 ioHandler, boolean recordingLoop) {
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
    
    /************************************************************************************
    *    METHOD:    clearPolygon    													*
    *    DESCRIPTION:    Clears the current polygon points    							*
    *    PARAMETERS:    ioHandler - the input/output handler for displaying messages    *
    *    RETURN VALUE:    none    														*
    ************************************************************************************/
    public void clearPolygon(InputOutputHandler3 ioHandler) {
        polygonPoints.clear();
        ioHandler.appendToHistory("System: Polygon points cleared\n");
    }
    
    /********************************************************
    *    METHOD:    getShapes    							*
    *    DESCRIPTION:    Returns the list of all shapes    	*
    *    PARAMETERS:    none    							*
    *    RETURN VALUE:    List<Shape> - the list of shapes  *
    *********************************************************/
    public List<Shape> getShapes() {
        return shapes;
    }
    
    /********************************************************************
    *    METHOD:    getPolygonPoints    								*
    *    DESCRIPTION:    Returns the list of current polygon points    	*
    *    PARAMETERS:    none    										*
    *    RETURN VALUE:    List<Point> - the list of polygon points    	*
    ********************************************************************/
    public List<Point> getPolygonPoints() {
        return polygonPoints;
    }
    
    /************************************************************************
    *    METHOD:    getLoopShapes    										*
    *    DESCRIPTION:    Returns the list of shapes in the current loop    	*
    *    PARAMETERS:    none    											*
    *    RETURN VALUE:    List<Shape> - the list of loop shapes    			*
    ************************************************************************/
    public List<Shape> getLoopShapes() {
        return loopShapes;
    }
    
    /********************************************************************************
    *    METHOD:    setFillShape    												*
    *    DESCRIPTION:    Sets the fill mode for shapes    							*
    *    PARAMETERS:    fillShape - true to fill shapes, false to draw outlines    	*
    *    RETURN VALUE:    none    													*
    ********************************************************************************/
    public void setFillShape(boolean fillShape) {
        this.fillShape = fillShape;
    }
    
    /********************************************************************************
    *    METHOD:    isFillShape    													*
    *    DESCRIPTION:    Returns the current fill mode    							*
    *    PARAMETERS:    none    													*
    *    RETURN VALUE:    boolean - true if shapes are filled, false otherwise    	*
    ********************************************************************************/
    public boolean isFillShape() {
        return fillShape;
    }
    
    /****************************************************************************************************
    *    METHOD:    addLoop    																			*
    *    DESCRIPTION:    Adds a new loop to the list of loops    										*
    *    PARAMETERS:    name - the name of the loop, commands - the list of commands in the loop    	*
    *    RETURN VALUE:    none    																		*
    ****************************************************************************************************/
    public void addLoop(String name, List<String> commands) {
        loops.add(new Loop(name, commands));
    }
    
    /********************************************************
    *    METHOD:    getLoops    							*
    *    DESCRIPTION:    Returns the list of all loops    	*
    *    PARAMETERS:    none    							*
    *    RETURN VALUE:    List<Loop> - the list of loops    *
    ********************************************************/
    public List<Loop> getLoops() {
        return loops;
    }
    
    /************************************************************************
    *    METHOD:    clearLoopShapes    										*
    *    DESCRIPTION:    Clears the list of shapes in the current loop    	*
    *    PARAMETERS:    none    											*
    *    RETURN VALUE:    none    											*
    ************************************************************************/
    public void clearLoopShapes() {
        loopShapes.clear();
    }
    
    /****************************************************
    *    METHOD:    clearShapes    						*
    *    DESCRIPTION:    Clears all shapes    			*
    *    PARAMETERS:    none    						*
    *    RETURN VALUE:    none    						*
    ****************************************************/
    public void clearShapes() {
        shapes.clear();
    }
    
    /************************************************************
    *    CLASS:    Shape    									*
    *    DESCRIPTION:    Abstract base class for all shapes    	*
    ************************************************************/
    abstract class Shape {
        boolean filled;
        
        Shape(boolean filled) {
            this.filled = filled;
        }
        
        abstract void draw(Graphics g);
    }
    
    /****************************************************
    *    CLASS:    Circle    							*
    *    DESCRIPTION:    Represents a circle shape    	*
    ****************************************************/
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
    
    /****************************************************
    *    CLASS:    Triangle   						 	*
    *    DESCRIPTION:    Represents a triangle shape    *
    ****************************************************/
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
        
    
    /****************************************************
    *    CLASS:    Rectangle    						*
    *    DESCRIPTION:    Represents a rectangle shape   *
    ****************************************************/
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
    
    /****************************************************
    *    CLASS:    Square    							*
    *    DESCRIPTION:    Represents a square shape    	*
    ****************************************************/
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
    
    /****************************************************
    *    CLASS:    Polygon    							*
    *    DESCRIPTION:    Represents a polygon shape    	*
    ****************************************************/
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
        
        
     // Add these methods to your CodeGeneration3 class

        /************************************************************************************
        *    METHOD:    parseLoopCondition    												*
        *    DESCRIPTION:    Parses and evaluates a loop condition with variables    		*
        *    PARAMETERS:    condition - the condition string, variables - variable map    	*
        *    RETURN VALUE:    boolean - true if condition is met, false otherwise    		*
        ************************************************************************************/
        private boolean parseLoopCondition(String condition, java.util.Map<String, Integer> variables) {
            try {
                // Replace variables with their values
                for (String varName : variables.keySet()) {
                    condition = condition.replace(varName, variables.get(varName).toString());
                }
                
                // Evaluate simple conditions
                if (condition.contains("<=")) {
                    String[] parts = condition.split("<=");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left <= right;
                } else if (condition.contains(">=")) {
                    String[] parts = condition.split(">=");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left >= right;
                } else if (condition.contains("<")) {
                    String[] parts = condition.split("<");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left < right;
                } else if (condition.contains(">")) {
                    String[] parts = condition.split(">");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left > right;
                } else if (condition.contains("==")) {
                    String[] parts = condition.split("==");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left == right;
                } else if (condition.contains("!=")) {
                    String[] parts = condition.split("!=");
                    int left = evaluateMathExpression(parts[0].trim());
                    int right = evaluateMathExpression(parts[1].trim());
                    return left != right;
                }
                
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        /************************************************************************************
        *    METHOD:    processLoopCommand    												*
        *    DESCRIPTION:    Processes a loop command with variable iteration    			*
        *    PARAMETERS:    command - the loop command, ioHandler - for I/O operations    	*
        *    RETURN VALUE:    none    														*
        ************************************************************************************/
        public void processLoopCommand(String command, InputOutputHandler3 ioHandler) {
            try {
                // Parse loop command format: loop, variable, start, end, step, condition
                String[] parts = command.split(",\\s*");
                if (parts.length >= 5) {
                    String varName = parts[1].trim();
                    int start = ioHandler.parseValue(parts[2].trim());
                    int end = ioHandler.parseValue(parts[3].trim());
                    int step = ioHandler.parseValue(parts[4].trim());
                    String condition = (parts.length > 5) ? parts[5].trim() : varName + "<=" + end;
                    
                    java.util.Map<String, Integer> variables = new java.util.HashMap<>();
                    variables.put(varName, start);
                    
                    // Store the current recording state and start recording
                    boolean wasRecording = isRecordingLoop;
                    List<Shape> previousLoopShapes = new ArrayList<>(loopShapes);
                    
                    // Process the loop body
                    while (parseLoopCondition(condition, variables)) {
                        // Execute the commands in the current loop recording
                        for (Shape shape : loopShapes) {
                            // Process the shape with current variable values
                            Shape evaluatedShape = processShapeWithVariables(shape, variables);
                            shapes.add(evaluatedShape);
                        }
                        
                        // Update the variable
                        variables.put(varName, variables.get(varName) + step);
                    }
                    
                    // Restore previous recording state
                    isRecordingLoop = wasRecording;
                    loopShapes = previousLoopShapes;
                    
                    
                } else {
                    ioHandler.appendToHistory("System: Invalid loop command format. Use: loop, variable, start, end, step, [condition]\n");
                }
            } catch (Exception e) {
                ioHandler.appendToHistory("System: Error processing loop command: " + e.getMessage() + "\n");
            }
        }

        /************************************************************************************
        *    METHOD:    evaluateShapeParameter    											*
        *    DESCRIPTION:    Evaluates shape parameters that may contain variables or      *
        *                   mathematical expressions with variables    					*
        *    PARAMETERS:    param - the parameter string, variables - variable map    		*
        *    RETURN VALUE:    int - the evaluated parameter value    						*
        ************************************************************************************/
        private int evaluateShapeParameter(String param, java.util.Map<String, Integer> variables) {
            try {
                if (param == null || param.trim().isEmpty()) {
                    return 0;
                }
                
                String expression = param.trim();
                
                // Replace variables with their values
                for (String varName : variables.keySet()) {
                    // Use word boundaries to avoid partial matches
                    expression = expression.replaceAll("\\b" + varName + "\\b", variables.get(varName).toString());
                }
                
                // Evaluate any mathematical expressions
                return evaluateMathExpression(expression);
            } catch (Exception e) {
                System.err.println("Error evaluating shape parameter: " + param + " - " + e.getMessage());
                return 0;
            }
        }
        /************************************************************************************
        *    METHOD:    evaluateMathExpression    											*
        *    DESCRIPTION:    Evaluates mathematical expressions with proper operator       *
        *                   precedence and parentheses support    							*
        *    PARAMETERS:    expression - the mathematical expression string    			*
        *    RETURN VALUE:    int - the result of the evaluation    						*
        ************************************************************************************/
        public int evaluateMathExpression(String expression) {
            try {
                // Remove any whitespace
                expression = expression.replaceAll("\\s+", "");
                
                // Handle parentheses first (recursively)
                while (expression.contains("(") && expression.contains(")")) {
                    int openParen = expression.lastIndexOf("(");
                    int closeParen = expression.indexOf(")", openParen);
                    
                    if (closeParen == -1) break;
                    
                    String innerExpr = expression.substring(openParen + 1, closeParen);
                    int innerResult = evaluateMathExpression(innerExpr);
                    expression = expression.substring(0, openParen) + innerResult + 
                                expression.substring(closeParen + 1);
                }
                
                // Handle multiplication and division
                java.util.regex.Matcher mdMatcher = java.util.regex.Pattern.compile("([-+]?\\d+)([*/])([-+]?\\d+)").matcher(expression);
                while (mdMatcher.find()) {
                    int left = Integer.parseInt(mdMatcher.group(1));
                    String operator = mdMatcher.group(2);
                    int right = Integer.parseInt(mdMatcher.group(3));
                    int result = operator.equals("*") ? left * right : left / right;
                    expression = expression.replace(mdMatcher.group(0), Integer.toString(result));
                    mdMatcher = java.util.regex.Pattern.compile("([-+]?\\d+)([*/])([-+]?\\d+)").matcher(expression);
                }
                
                // Handle addition and subtraction
                java.util.regex.Matcher asMatcher = java.util.regex.Pattern.compile("([-+]?\\d+)([+-])([-+]?\\d+)").matcher(expression);
                while (asMatcher.find()) {
                    int left = Integer.parseInt(asMatcher.group(1));
                    String operator = asMatcher.group(2);
                    int right = Integer.parseInt(asMatcher.group(3));
                    int result = operator.equals("+") ? left + right : left - right;
                    expression = expression.replace(asMatcher.group(0), Integer.toString(result));
                    asMatcher = java.util.regex.Pattern.compile("([-+]?\\d+)([+-])([-+]?\\d+)").matcher(expression);
                }
                
                // Final result
                return Integer.parseInt(expression);
            } catch (Exception e) {
                System.err.println("Error evaluating math expression: " + expression + " - " + e.getMessage());
                return 0;
            }
        }

        /************************************************************************************
        *    METHOD:    processShapeWithVariables    										*
        *    DESCRIPTION:    Processes shapes with variable parameters in loops    			*
        *    PARAMETERS:    shape - the shape to process, variables - variable map    		*
        *    RETURN VALUE:    Shape - the processed shape with evaluated parameters        *
        ************************************************************************************/
        private Shape processShapeWithVariables(Shape shape, java.util.Map<String, Integer> variables) {
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
            
            return shape; // Return original shape if type not recognized
        }
        
        
        
        
        
        
        
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /****************************************************************
    *    CLASS:    Loop    											*
    *    DESCRIPTION:    Represents a recorded loop of commands    	*
    ****************************************************************/
    class Loop {
        String name;
        List<String> commands;
        
        Loop(String name, List<String> commands) {
            this.name = name;
            this.commands = commands;
        }
        
        public String getName() {
            return name;
        }
        
        public List<String> getCommands() {
            return commands;
        }
        
        // Add these methods to the Loop class
        public boolean isRecordingLoop() {
            // This method might not be needed for the Loop class itself
            // since the recording state is managed by the parent CodeGeneration class
            return false;
        }

        public void endLoopRecording(String name) {
            // This method should probably be in the parent class only
        }

        public void playLoop(String name, DrawingPanel panel, InputOutputHandler3 ioHandler) {
            // This method should probably be in the parent class only
        }
    }
        
        /****************************************************
        *    METHOD:    getName    							*
        *    DESCRIPTION:    Returns the name of the loop   *
        *    PARAMETERS:    none    						*
        *    RETURN VALUE:    String - the name of the loop *
        ****************************************************/
        public String getName() {
            return getName();
        }
        
        /************************************************************
        *    METHOD:    getCommands    								*
        *    DESCRIPTION:    Returns the commands in the loop    	*
        *    PARAMETERS:    none    								*
        *    RETURN VALUE:    List<String> - the list of commands   *
        ************************************************************/
        public List<String> getCommands() {
            return getCommands();
        }
        /************************************************************************************
        *    METHOD:    startLoopRecording    												*
        *    DESCRIPTION:    Initiates loop recording mode    								*
        *    PARAMETERS:    ioHandler - the input/output handler for displaying messages    *
        *    RETURN VALUE:    none    														*
        ************************************************************************************/
        public void startLoopRecording(InputOutputHandler3 ioHandler) {
            loopShapes.clear();
            isRecordingLoop = true;
            ioHandler.appendToHistory("System: Loop recording started. All shapes will be added to the loop.\n");
        }

        /****************************************************************************
        *    METHOD:    isRecordingLoop    											*
        *    DESCRIPTION:    Checks if loop recording is active    					*
        *    PARAMETERS:    none    												*
        *    RETURN VALUE:    boolean - true if recording loop, false otherwise    	*
        ****************************************************************************/
        public boolean isRecordingLoop() {
            return isRecordingLoop;
        }

        /****************************************************************************************
        *    METHOD:    endLoopRecording   														*
        *    DESCRIPTION:    Saves the current loop with the given name    						*
        *    PARAMETERS:    name - the name of the loop, ioHandler - for displaying messages    *
        *    RETURN VALUE:    none    															*
        ****************************************************************************************/
        public void endLoopRecording(String name, InputOutputHandler3 ioHandler) {
            if (isRecordingLoop && !loopShapes.isEmpty()) {
                // Convert the loop shapes to commands for storage
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
                
                // Add the loop to the loops list
                loops.add(new Loop(name, commands));
                
                // Clear the temporary loop shapes and reset recording state
                loopShapes.clear();
                isRecordingLoop = false;
                
                ioHandler.appendToHistory("System: Loop '" + name + "' saved with " + commands.size() + " commands\n");
            } else {
                ioHandler.appendToHistory("System: No loop recording in progress or no shapes recorded\n");
            }
        }
        /****************************************************************************************
         *    METHOD:    endLoopRecording    													*
         *    DESCRIPTION:    Saves the current loop with the given name    					*
         *    PARAMETERS:    name - the name of the loop, ioHandler - for displaying messages   *
         *    RETURN VALUE:    none    															*
         ***************************************************************************************/
        public void playLoop(String name, DrawingPanel panel, InputOutputHandler3 ioHandler) {
            // Play back the loop with the given name
            Loop targetLoop = null;
            
            // Find the loop with the specified name
            for (Loop loop : loops) {
                if (loop.getName().equalsIgnoreCase(name)) {
                    targetLoop = loop;
                    break;
                }
            }
            
            if (targetLoop != null) {
                ioHandler.appendToHistory("System: Playing loop '" + name + "'\n");
                
                // Execute each command in the loop
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
                
                // Repaint the panel to show the drawn shapes
                panel.repaint();
            } else {
                ioHandler.appendToHistory("System: Loop '" + name + "' not found\n");
            }
        }
        
        /************************************************************************************
        *    METHOD:    clearScreen    														*
        *    DESCRIPTION:    Clears all shapes from the drawing screen    					*
        *    PARAMETERS:    ioHandler - the input/output handler for displaying messages    *
        *    RETURN VALUE:    none    														*
        ************************************************************************************/
        public void clearScreen(InputOutputHandler3 ioHandler) {
            shapes.clear();
            polygonPoints.clear();
            loopShapes.clear();
            ioHandler.appendToHistory("System: Screen cleared - all shapes removed\n");
        }
        
        /************************************************************************************
        *    METHOD:    evaluateMathExpression    											*
        *    DESCRIPTION:    Evaluates mathematical expressions in commands    			*
        *    PARAMETERS:    expression - the mathematical expression string    			*
        *    RETURN VALUE:    int - the result of the evaluation    						*
        ************************************************************************************/
        public int evaluateMathExpression(String expression) {
            try {
                // Remove any whitespace
                expression = expression.replaceAll("\\s+", "");
                
                // Handle simple arithmetic operations
                if (expression.contains("+")) {
                    String[] parts = expression.split("\\+");
                    int sum = 0;
                    for (String part : parts) {
                        sum += Integer.parseInt(part);
                    }
                    return sum;
                } else if (expression.contains("-")) {
                    String[] parts = expression.split("-");
                    int result = Integer.parseInt(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        result -= Integer.parseInt(parts[i]);
                    }
                    return result;
                } else if (expression.contains("*")) {
                    String[] parts = expression.split("\\*");
                    int product = 1;
                    for (String part : parts) {
                        product *= Integer.parseInt(part);
                    }
                    return product;
                } else if (expression.contains("/")) {
                    String[] parts = expression.split("/");
                    int result = Integer.parseInt(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        result /= Integer.parseInt(parts[i]);
                    }
                    return result;
                } else {
                    // No operators found, just parse the number
                    return Integer.parseInt(expression);
                }
            } catch (NumberFormatException e) {
                return 0; // Return 0 if parsing fails
            }
        }
        
        
        
    }