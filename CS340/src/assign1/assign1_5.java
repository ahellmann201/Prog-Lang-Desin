package assign1;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class assign1_5 extends JPanel {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private Random random = new Random();
    private String currentFlagType;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clear background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Draw sky and ground
        drawBackground(g2d);
        
        // Generate a random flag type
        int flagType = random.nextInt(8);
        
        switch (flagType) {
            case 0: currentFlagType = "Horizontal Stripes"; drawHorizontalStripesFlag(g2d); break;
            case 1: currentFlagType = "Vertical Stripes"; drawVerticalStripesFlag(g2d); break;
            case 2: currentFlagType = "Nordic Cross"; drawNordicCrossFlag(g2d); break;
            case 3: currentFlagType = "Circle"; drawCircleFlag(g2d); break;
            case 4: currentFlagType = "Triangle"; drawTriangleFlag(g2d); break;
            case 5: currentFlagType = "Diagonal"; drawDiagonalFlag(g2d); break;
            case 6: currentFlagType = "Chevron"; drawChevronFlag(g2d); break;
            case 7: currentFlagType = "Star"; drawStarFlag(g2d); break;
        }
        
        // Add flagpole with shadow
        drawFlagpole(g2d);
        
        // Add flag type label
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Flag Type: " + currentFlagType, 20, 30);
    }
    
    private void drawBackground(Graphics2D g2d) {
        // Sky
        g2d.setColor(new Color(135, 206, 235)); // Light blue
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        
        // Ground
        g2d.setColor(new Color(34, 139, 34)); // Forest green
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
        
        // Sun
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(WIDTH - 80, 20, 60, 60);
    }
    
    private void drawFlagpole(Graphics2D g2d) {
        // Flagpole shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(55, HEIGHT / 2 + 5, 15, HEIGHT / 2 - 50);
        
        // Flagpole
        g2d.setColor(new Color(160, 82, 45)); // Sienna
        g2d.fillRect(50, 50, 10, HEIGHT - 100);
        
        // Flagpole top
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(48, 45, 14, 14);
    }
    
    private void drawHorizontalStripesFlag(Graphics2D g2d) {
        int stripes = 3 + random.nextInt(5);
        int stripeHeight = (HEIGHT - 100) / stripes;
        
        for (int i = 0; i < stripes; i++) {
            g2d.setColor(getRandomColor());
            g2d.fillRect(60, 50 + i * stripeHeight, WIDTH - 100, stripeHeight);
        }
    }
    
    private void drawNordicCrossFlag(Graphics2D g2d) {
        // Background
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        // Cross
        g2d.setColor(getRandomColor());
        int crossWidth = (WIDTH - 100) / 6;
        int crossHeight = (HEIGHT - 100) / 6;
        
        // Horizontal bar (offset to left)
        g2d.fillRect(60, 50 + (HEIGHT - 100) / 2 - crossHeight / 2, WIDTH - 100, crossHeight);
        // Vertical bar (offset to top)
        g2d.fillRect(60 + (WIDTH - 100) / 4 - crossWidth / 2, 50, crossWidth, HEIGHT - 100);
    }
    
    private void drawDiagonalFlag(Graphics2D g2d) {
        // Background
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        // Diagonal
        g2d.setColor(getRandomColor());
        Polygon diagonal = new Polygon();
        diagonal.addPoint(60, 50);
        diagonal.addPoint(60 + (WIDTH - 100) / 2, 50);
        diagonal.addPoint(60, 50 + (HEIGHT - 100) / 2);
        g2d.fillPolygon(diagonal);
    }
    
    private void drawChevronFlag(Graphics2D g2d) {
        // Background
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        // Chevron
        g2d.setColor(getRandomColor());
        Polygon chevron = new Polygon();
        chevron.addPoint(60, 50 + (HEIGHT - 100) / 2);
        chevron.addPoint(60 + (WIDTH - 100) / 3, 50);
        chevron.addPoint(60 + 2 * (WIDTH - 100) / 3, 50);
        chevron.addPoint(60 + (WIDTH - 100), 50 + (HEIGHT - 100) / 2);
        chevron.addPoint(60 + 2 * (WIDTH - 100) / 3, 50 + HEIGHT - 100);
        chevron.addPoint(60 + (WIDTH - 100) / 3, 50 + HEIGHT - 100);
        g2d.fillPolygon(chevron);
    }
    
    private void drawStarFlag(Graphics2D g2d) {
        // Background
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        // Stars
        g2d.setColor(getRandomColor());
        int starCount = 5 + random.nextInt(10);
        for (int i = 0; i < starCount; i++) {
            int x = 70 + random.nextInt(WIDTH - 120);
            int y = 60 + random.nextInt(HEIGHT - 120);
            drawStar(g2d, x, y, 8 + random.nextInt(15));
        }
    }
    
    private void drawStar(Graphics2D g2d, int x, int y, int size) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            int radius = (i % 2 == 0) ? size : size / 2;
            xPoints[i] = x + (int)(radius * Math.cos(angle - Math.PI / 2));
            yPoints[i] = y + (int)(radius * Math.sin(angle - Math.PI / 2));
        }
        
        g2d.fillPolygon(xPoints, yPoints, 10);
    }
    
    // Other flag drawing methods similar to previous example...
    private void drawVerticalStripesFlag(Graphics2D g2d) {
        int stripes = 3 + random.nextInt(5);
        int stripeWidth = (WIDTH - 100) / stripes;
        
        for (int i = 0; i < stripes; i++) {
            g2d.setColor(getRandomColor());
            g2d.fillRect(60 + i * stripeWidth, 50, stripeWidth, HEIGHT - 100);
        }
    }
    
    private void drawCircleFlag(Graphics2D g2d) {
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        g2d.setColor(getRandomColor());
        int circleSize = Math.min(WIDTH - 120, HEIGHT - 120);
        g2d.fillOval(60 + (WIDTH - 100 - circleSize) / 2, 50 + (HEIGHT - 100 - circleSize) / 2, 
                     circleSize, circleSize);
    }
    
    private void drawTriangleFlag(Graphics2D g2d) {
        g2d.setColor(getRandomColor());
        g2d.fillRect(60, 50, WIDTH - 100, HEIGHT - 100);
        
        g2d.setColor(getRandomColor());
        int[] xPoints = {60, 60 + (WIDTH - 100) / 2, 60};
        int[] yPoints = {50, 50 + (HEIGHT - 100) / 2, 50 + HEIGHT - 100};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private Color getRandomColor() {
        Color[] colors = {
            Color.RED, Color.BLUE, Color.WHITE, Color.GREEN, Color.YELLOW,
            Color.BLACK, Color.ORANGE, Color.MAGENTA, Color.CYAN,
            new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128),
            new Color(128, 128, 0), new Color(128, 0, 128), new Color(0, 128, 128)
        };
        return colors[random.nextInt(colors.length)];
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Advanced Flag Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);
            
            assign1_5 flagPanel = new assign1_5();
            frame.add(flagPanel);
            
            JPanel buttonPanel = new JPanel();
            JButton generateButton = new JButton("Generate New Flag");
            generateButton.addActionListener(e -> flagPanel.repaint());
            
            JButton saveButton = new JButton("Save Flag");
            saveButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Flag saved!"));
            
            buttonPanel.add(generateButton);
            buttonPanel.add(saveButton);
            
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}