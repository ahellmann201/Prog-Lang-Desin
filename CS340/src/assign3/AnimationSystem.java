package assign3;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.SwingUtilities;

/****************************************************************************
 * Animation System Module                                                  *
 *                                                                          *
 *    PROGRAMMER:    [Your Name]                                            *
 *    COURSE:  CS340 Programming Lang/Design                               *
 *    DATE:    September 2025                                               *
 *    REQUIREMENT:    Assignment 4 - Graphics                              *
 *                                                                          *
 *    DESCRIPTION:                                                          *
 *    This module handles animation creation and management for the         *
 *    drawing application. It provides methods for creating keyframes,      *
 *    interpolating between frames, and playing animations.                 *
 *                                                                          *
 ***************************************************************************/
public class AnimationSystem {
    private List<Animation> animations = new ArrayList<>();
    private Animation currentAnimation = null;
    private boolean isPlaying = false;
    private int currentFrame = 0;
    private long frameDelay = 100; // milliseconds between frames
    private Timer animationTimer;
    
    private UserInterface3 ui;
    private CodeGeneration3 codeGeneration;
    private InputOutputHandler3 ioHandler;

    public AnimationSystem(UserInterface3 ui) {
        this.ui = ui;
        this.codeGeneration = ui.getCodeGeneration();
        this.ioHandler = ui.getIOHandler();
    }

    /************************************************************************
     *    METHOD:    createAnimation                                        *
     *    DESCRIPTION: Creates a new animation with the given name          *
     *    PARAMETERS: String name - the animation name                      *
     *    RETURN VALUE: Animation - the created animation                   *
     ************************************************************************/
    public Animation createAnimation(String name) {
        Animation animation = new Animation(name);
        animations.add(animation);
        currentAnimation = animation;
        ioHandler.appendToHistory("System: Created animation '" + name + "' - Total animations: " + animations.size() + "\n");
        return animation;
    }
    /************************************************************************
     *    METHOD:    addKeyframe                                            *
     *    DESCRIPTION: Adds a keyframe to the current animation             *
     *    PARAMETERS: String frameName - name for this keyframe             *
     *    RETURN VALUE: none                                                *
     ************************************************************************/
    public void addKeyframe(String frameName) {
        if (currentAnimation == null) {
            ioHandler.appendToHistory("System: No current animation. Create one first.\n");
            return;
        }
        
        // Capture current state as a keyframe
        AnimationFrame frame = new AnimationFrame(frameName);
        
        // Copy all current shapes
        for (CodeGeneration3.Shape shape : codeGeneration.getShapes()) {
            frame.addShape(cloneShape(shape));
        }
        
        currentAnimation.addFrame(frame);
        ioHandler.appendToHistory("System: Added keyframe '" + frameName + "' with " + 
                                frame.getShapes().size() + " shapes\n");
    }

    /************************************************************************
     *    METHOD:    playAnimation                                          *
     *    DESCRIPTION: Plays the specified animation                        *
     *    PARAMETERS: String animationName - name of animation to play      *
     *    RETURN VALUE: none                                                *
     ************************************************************************/
    public void playAnimation(String animationName) {
        Animation animation = findAnimation(animationName);
        if (animation == null) {
            ioHandler.appendToHistory("System: Animation '" + animationName + "' not found\n");
            return;
        }
        
        if (isPlaying) {
            stopAnimation();
        }
        
        currentAnimation = animation;
        currentFrame = 0;
        isPlaying = true;
        
        ioHandler.appendToHistory("System: Playing animation '" + animationName + "' (looping)\n");
        
        // Start animation timer with looping
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (currentFrame < currentAnimation.getFrameCount()) {
                        showFrame(currentFrame);
                        currentFrame++;
                    } else {
                        // Loop back to the beginning
                        currentFrame = 0;
                        showFrame(currentFrame);
                        currentFrame++;
                    }
                });
            }
        }, 0, frameDelay);
    }

    /************************************************************************
     *    METHOD:    stopAnimation                                          *
     *    DESCRIPTION: Stops the current animation                          *
     *    PARAMETERS: none                                                  *
     *    RETURN VALUE: none                                                *
     ************************************************************************/
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
            animationTimer = null;
        }
        isPlaying = false;
        ioHandler.appendToHistory("System: Animation stopped\n");
    }

    /************************************************************************
     *    METHOD:    showFrame                                              *
     *    DESCRIPTION: Displays a specific frame of the animation           *
     *    PARAMETERS: int frameIndex - index of frame to show               *
     *    RETURN VALUE: none                                                *
     ************************************************************************/
    private void showFrame(int frameIndex) {
        if (currentAnimation == null || frameIndex >= currentAnimation.getFrameCount()) {
            return;
        }
        
        // Clear current shapes
        codeGeneration.clearShapes();
        
        // Add shapes from the frame
        AnimationFrame frame = currentAnimation.getFrame(frameIndex);
        for (CodeGeneration3.Shape shape : frame.getShapes()) {
            codeGeneration.getShapes().add(shape);
        }
        
        // Repaint the drawing panel
        ui.getDrawingPanel().repaint();
    }

    /************************************************************************
     *    METHOD:    setFrameDelay                                          *
     *    DESCRIPTION: Sets the delay between animation frames              *
     *    PARAMETERS: long delay - delay in milliseconds                    *
     *    RETURN VALUE: none                                                *
     ************************************************************************/
    public void setFrameDelay(long delay) {
        this.frameDelay = delay;
        ioHandler.appendToHistory("System: Frame delay set to " + delay + "ms\n");
    }

    /************************************************************************
     *    METHOD:    findAnimation                                          *
     *    DESCRIPTION: Finds an animation by name                           *
     *    PARAMETERS: String name - animation name to find                  *
     *    RETURN VALUE: Animation - found animation or null                 *
     ************************************************************************/
    private Animation findAnimation(String name) {
        for (Animation anim : animations) {
            if (anim.getName().equalsIgnoreCase(name)) {
                return anim;
            }
        }
        return null;
    }

    /************************************************************************
     *    METHOD:    cloneShape                                             *
     *    DESCRIPTION: Creates a deep copy of a shape                       *
     *    PARAMETERS: CodeGeneration3.Shape shape - shape to clone          *
     *    RETURN VALUE: CodeGeneration3.Shape - cloned shape                *
     ************************************************************************/
    private CodeGeneration3.Shape cloneShape(CodeGeneration3.Shape shape) {
        // This is a simplified cloning method - you may need to expand this
        // based on your specific shape types and their properties
        if (shape instanceof CodeGeneration3.Circle) {
            CodeGeneration3.Circle circle = (CodeGeneration3.Circle) shape;
            return codeGeneration.new Circle(circle.x, circle.y, circle.radius, circle.filled);
        } else if (shape instanceof CodeGeneration3.Rectangle) {
            CodeGeneration3.Rectangle rect = (CodeGeneration3.Rectangle) shape;
            return codeGeneration.new Rectangle(rect.x1, rect.y1, rect.x2, rect.y2, rect.filled);
        } else if (shape instanceof CodeGeneration3.Square) {
            CodeGeneration3.Square square = (CodeGeneration3.Square) shape;
            return codeGeneration.new Square(square.x, square.y, square.size, square.filled);
        } else if (shape instanceof CodeGeneration3.Triangle) {
            CodeGeneration3.Triangle triangle = (CodeGeneration3.Triangle) shape;
            return codeGeneration.new Triangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, triangle.x3, triangle.y3, triangle.filled);
        }
        // Add more shape types as needed
        return null;
    }

    /************************************************************************
     *    CLASS:    Animation                                               *
     *    DESCRIPTION: Represents an animation sequence                     *
     ************************************************************************/
    class Animation {
        private String name;
        private List<AnimationFrame> frames = new ArrayList<>();
        
        public Animation(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
        public void addFrame(AnimationFrame frame) { frames.add(frame); }
        public AnimationFrame getFrame(int index) { return frames.get(index); }
        public int getFrameCount() { return frames.size(); }
        public List<AnimationFrame> getFrames() { return frames; }
    }

    /************************************************************************
     *    CLASS:    AnimationFrame                                          *
     *    DESCRIPTION: Represents a single frame in an animation            *
     ************************************************************************/
    class AnimationFrame {
        private String name;
        private List<CodeGeneration3.Shape> shapes = new ArrayList<>();
        
        public AnimationFrame(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
        public void addShape(CodeGeneration3.Shape shape) { shapes.add(shape); }
        public List<CodeGeneration3.Shape> getShapes() { return shapes; }
    }

    /************************************************************************
     *    METHOD:    getAnimations                                          *
     *    DESCRIPTION: Returns list of all animations                       *
     *    PARAMETERS: none                                                  *
     *    RETURN VALUE: List<Animation> - all animations                    *
     ************************************************************************/
    public List<Animation> getAnimations() {
        return animations;
    }

    /************************************************************************
     *    METHOD:    getCurrentAnimation                                    *
     *    DESCRIPTION: Returns current animation                            *
     *    PARAMETERS: none                                                  *
     *    RETURN VALUE: Animation - current animation                       *
     ************************************************************************/
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /************************************************************************
     *    METHOD:    isPlaying                                              *
     *    DESCRIPTION: Checks if animation is playing                       *
     *    PARAMETERS: none                                                  *
     *    RETURN VALUE: boolean - true if playing                           *
     ************************************************************************/
    public boolean isPlaying() {
        return isPlaying;
    }
}