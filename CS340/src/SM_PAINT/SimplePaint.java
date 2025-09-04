// SimplePaint.java
package SM_PAINT;

import javax.swing.*;

// Main class that sets up the application
public class SimplePaint {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintFrame frame = new PaintFrame();
            frame.setVisible(true);
        });
    }
}