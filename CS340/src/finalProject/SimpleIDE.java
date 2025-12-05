package finalProject;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Entry Point.
 * Matches Assignment 2 requirements for setting up the environment.
 */
public class SimpleIDE {
    public static void main(String[] args) {
        // specific IDE requirement: Run on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for a modern look (Windows/Mac style)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            IDEWindow window = new IDEWindow();
            window.setVisible(true);
        });
    }
}