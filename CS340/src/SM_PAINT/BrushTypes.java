package SM_PAINT;

import java.awt.*;

// Utility class for brush types and their properties
public class BrushTypes {
    
    public static Stroke getStroke(String brushType, int brushSize) {
        switch (brushType) {
            case "Marker":
                return new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            case "Pen":
                return new BasicStroke(brushSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
            case "Pencil":
                return new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            case "Crayon":
                float[] pattern = {2f, 3f};
                return new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                     1.0f, pattern, 0f);
            default:
                return new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
    }
    
    public static Color getBrushColor(String brushType, Color baseColor) {
        switch (brushType) {
            case "Pencil":
                // Add some transparency for pencil effect
                return new Color(baseColor.getRed(), baseColor.getGreen(), 
                               baseColor.getBlue(), 200);
            default:
                return baseColor;
        }
    }
}