import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouncingBalls extends JPanel implements ActionListener {
    // ========== CONFIGURABLE PARAMETERS ==========
    // Window settings
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 900;
    
    // Ball settings
    private static final int INITIAL_BALL_SIZE = 10;
    private static final int INITIAL_NUM_BALLS = 90;
    private static final int MAX_BALLS = 300;           // Maximum number of balls allowed
    private static final int MAX_BALL_SIZE = 30;      // Size at which balls automatically explode
    
    // Collision settings
    private static final double SIZE_INCREASE_MULTIPLIER = 5;  // Size multiplier when balls combine
    private static final int EXPLOSION_BALL_MULTIPLIER = 5;       // Multiplier for balls created during explosion
    
    // Timing settings
    private static final int DELAY = 15;
    private static final int EXPLOSION_DELAY = 200;    // Frames before combined balls explode
    
    // Performance settings
    private static final boolean COLLISION_DETECTION_ENABLED = true;
    private static final int COLLISION_CHECK_THRESHOLD = 30;      // Skip collision checks when ball count is high
    // ========== END OF CONFIGURABLE PARAMETERS ==========
    
    private List<Ball> balls;
    private Timer timer;
    private Random random;

    public BouncingBalls() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        
        random = new Random();
        balls = new ArrayList<>();
        
        // Create balls using a loop
        for (int i = 0; i < INITIAL_NUM_BALLS; i++) {
            addNewBall(INITIAL_BALL_SIZE);
        }
        
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void addNewBall(int size) {
        if (balls.size() >= MAX_BALLS) return; // Respect the maximum ball limit
        
        // Random starting position (avoid edges)
        int x = random.nextInt(WIDTH - size * 2) + size;
        int y = random.nextInt(HEIGHT - size * 2) + size;
        
        // Random velocity (between 2-6 pixels per frame)
        double dx = (random.nextBoolean() ? 1 : -1) * (random.nextDouble() * 4 + 2);
        double dy = (random.nextBoolean() ? 1 : -1) * (random.nextDouble() * 4 + 2);
        
        // Random color
        Color color = new Color(
            random.nextInt(200) + 55,
            random.nextInt(200) + 55,
            random.nextInt(200) + 55
        );
        
        balls.add(new Ball(x, y, dx, dy, size, color, 0));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw all balls
        for (Ball ball : balls) {
            ball.draw(g2d);
        }
        
        // Display ball count and info
        g2d.setColor(Color.WHITE);
        g2d.drawString("Balls: " + balls.size() + " / " + MAX_BALLS, 10, 20);
        g2d.drawString("Combine on head-on collision", 10, 40);
        g2d.drawString("Explode after delay or when too big", 10, 60);
        
        // Performance warning
        if (balls.size() > MAX_BALLS * 0.8) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("High ball count - collision detection reduced", 10, 80);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Check for explosions first
        List<Ball> ballsToRemove = new ArrayList<>();
        List<Ball> ballsToAdd = new ArrayList<>();
        
        for (Ball ball : balls) {
            // Check if ball should explode due to size
            if (ball.size >= MAX_BALL_SIZE) {
                ballsToRemove.add(ball);
                explodeBall(ball, ballsToAdd);
                continue;
            }
            
            // Check if ball should explode due to timer
            if (ball.lifetime > 0 && ball.lifetime >= EXPLOSION_DELAY) {
                ballsToRemove.add(ball);
                explodeBall(ball, ballsToAdd);
            }
        }
        
        // Remove exploded balls and add new ones
        balls.removeAll(ballsToRemove);
        balls.addAll(ballsToAdd);
        ballsToRemove.clear();
        ballsToAdd.clear();
        
        // Update all ball positions and lifetimes
        for (Ball ball : balls) {
            ball.update();
            
            // Increment lifetime for combined balls
            if (ball.lifetime > 0) {
                ball.lifetime++;
            }
            
            // Check for collisions with walls
            if (ball.x <= 0 || ball.x >= WIDTH - ball.size) {
                ball.dx = -ball.dx;
                // Adjust position to prevent sticking to the wall
                ball.x = Math.max(0, Math.min(WIDTH - ball.size, ball.x));
            }
            
            if (ball.y <= 0 || ball.y >= HEIGHT - ball.size) {
                ball.dy = -ball.dy;
                // Adjust position to prevent sticking to the wall
                ball.y = Math.max(0, Math.min(HEIGHT - ball.size, ball.y));
            }
        }
        
        // Check for collisions between balls (with performance optimization)
        if (COLLISION_DETECTION_ENABLED) {
            // Skip some collision checks when ball count is high for better performance
            int checkFrequency = balls.size() > COLLISION_CHECK_THRESHOLD ? 2 : 1;
            
            for (int i = 0; i < balls.size(); i += checkFrequency) {
                for (int j = i + 1; j < balls.size(); j += checkFrequency) {
                    Ball ball1 = balls.get(i);
                    Ball ball2 = balls.get(j);
                    
                    // Calculate distance between ball centers
                    double dx = (ball1.x + ball1.size/2) - (ball2.x + ball2.size/2);
                    double dy = (ball1.y + ball1.size/2) - (ball2.y + ball2.size/2);
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    
                    // Check if balls are colliding
                    if (distance < (ball1.size/2 + ball2.size/2)) {
                        // Check if it's a head-on collision (velocity vectors are opposite)
                        double dotProduct = ball1.dx * ball2.dx + ball1.dy * ball2.dy;
                        
                        if (dotProduct < 0) { // Head-on collision
                            // Mark these balls for removal
                            if (!ballsToRemove.contains(ball1)) ballsToRemove.add(ball1);
                            if (!ballsToRemove.contains(ball2)) ballsToRemove.add(ball2);
                            
                            // Create a new combined ball
                            double newX = (ball1.x + ball2.x) / 2;
                            double newY = (ball1.y + ball2.y) / 2;
                            
                            // Average the velocities
                            double newDx = (ball1.dx + ball2.dx) / 2;
                            double newDy = (ball1.dy + ball2.dy) / 2;
                            
                            // Increase size by configured multiplier
                            int newSize = (int) (Math.max(ball1.size, ball2.size) * SIZE_INCREASE_MULTIPLIER);
                            
                            // Blend the colors
                            Color newColor = new Color(
                                (ball1.color.getRed() + ball2.color.getRed()) / 2,
                                (ball1.color.getGreen() + ball2.color.getGreen()) / 2,
                                (ball1.color.getBlue() + ball2.color.getBlue()) / 2
                            );
                            
                            // Start lifetime counter for combined ball
                            ballsToAdd.add(new Ball(newX, newY, newDx, newDy, newSize, newColor, 1));
                        } else {
                            // Regular collision - just bounce
                            checkBallCollision(ball1, ball2);
                        }
                    }
                }
            }
        }
        
        // Remove combined balls and add new ones
        balls.removeAll(ballsToRemove);
        balls.addAll(ballsToAdd);
        
        repaint();
    }
    
    private void explodeBall(Ball ball, List<Ball> ballsToAdd) {
        // Calculate how many smaller balls to create based on size and multiplier
        int numNewBalls = (ball.size / INITIAL_BALL_SIZE) * EXPLOSION_BALL_MULTIPLIER;
        numNewBalls = Math.max(2, Math.min(numNewBalls, MAX_BALLS - balls.size() + 1)); // Respect limits
        
        // Create smaller balls
        for (int i = 0; i < numNewBalls; i++) {
            // Calculate position around the original ball
            double angle = 2 * Math.PI * i / numNewBalls;
            double distance = ball.size / 2;
            double newX = ball.x + distance * Math.cos(angle);
            double newY = ball.y + distance * Math.sin(angle);
            
            // Calculate velocity away from center
            double speed = 3 + random.nextDouble() * 3;
            double newDx = speed * Math.cos(angle);
            double newDy = speed * Math.sin(angle);
            
            // Random color variation of the original
            Color newColor = new Color(
                Math.min(255, ball.color.getRed() + random.nextInt(40) - 20),
                Math.min(255, ball.color.getGreen() + random.nextInt(40) - 20),
                Math.min(255, ball.color.getBlue() + random.nextInt(40) - 20)
            );
            
            ballsToAdd.add(new Ball(newX, newY, newDx, newDy, INITIAL_BALL_SIZE, newColor, 0));
        }
    }
    
    private void checkBallCollision(Ball ball1, Ball ball2) {
        // Calculate distance between ball centers
        double dx = (ball1.x + ball1.size/2) - (ball2.x + ball2.size/2);
        double dy = (ball1.y + ball1.size/2) - (ball2.y + ball2.size/2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if balls are colliding
        if (distance < (ball1.size/2 + ball2.size/2)) {
            // Calculate collision angle
            double angle = Math.atan2(dy, dx);
            
            // Calculate velocities in the collision direction
            double vel1 = ball1.dx * Math.cos(angle) + ball1.dy * Math.sin(angle);
            double vel2 = ball2.dx * Math.cos(angle) + ball2.dy * Math.sin(angle);
            
            // Calculate velocities perpendicular to collision (unchanged)
            double perpVel1 = -ball1.dx * Math.sin(angle) + ball1.dy * Math.cos(angle);
            double perpVel2 = -ball2.dx * Math.sin(angle) + ball2.dy * Math.cos(angle);
            
            // Swap velocities in collision direction (elastic collision)
            double temp = vel1;
            vel1 = vel2;
            vel2 = temp;
            
            // Convert velocities back to x and y components
            ball1.dx = vel1 * Math.cos(angle) - perpVel1 * Math.sin(angle);
            ball1.dy = vel1 * Math.sin(angle) + perpVel1 * Math.cos(angle);
            ball2.dx = vel2 * Math.cos(angle) - perpVel2 * Math.sin(angle);
            ball2.dy = vel2 * Math.sin(angle) + perpVel2 * Math.cos(angle);
            
            // Move balls apart to prevent sticking
            double overlap = (ball1.size/2 + ball2.size/2) - distance;
            ball1.x += overlap * Math.cos(angle) / 2;
            ball1.y += overlap * Math.sin(angle) / 2;
            ball2.x -= overlap * Math.cos(angle) / 2;
            ball2.y -= overlap * Math.sin(angle) / 2;
        }
    }
    
    private class Ball {
        double x, y;
        double dx, dy;
        int size;
        Color color;
        int lifetime; // For combined balls, tracks how long they've existed
        
        Ball(double x, double y, double dx, double dy, int size, Color color, int lifetime) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.size = size;
            this.color = color;
            this.lifetime = lifetime;
        }
        
        void update() {
            x += dx;
            y += dy;
        }
        
        void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillOval((int)x, (int)y, size, size);
            
            // Add a highlight for 3D effect
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int)x + size/4, (int)y + size/4, size/6, size/6);
            
            // Draw explosion timer for combined balls
            if (lifetime > 0) {
                double progress = (double) lifetime / EXPLOSION_DELAY;
                int arcSize = (int) (progress * 360);
                
                g2d.setColor(Color.WHITE);
                g2d.drawArc((int)x - 5, (int)y - 5, size + 10, size + 10, 90, -arcSize);
                
                // Draw size text for larger balls
                if (size > INITIAL_BALL_SIZE * 1.5) {
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    String sizeText = String.valueOf(size);
                    int textWidth = g2d.getFontMetrics().stringWidth(sizeText);
                    g2d.drawString(sizeText, (int)(x + size/2 - textWidth/2), (int)(y + size/2 + 5));
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bouncing Balls - Configurable Parameters");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            BouncingBalls bouncingBalls = new BouncingBalls();
            frame.add(bouncingBalls);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}