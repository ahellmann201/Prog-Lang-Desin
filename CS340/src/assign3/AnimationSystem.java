package assign3;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.SwingUtilities;
import java.util.Timer;

/**
 * Animation System Module
 * 
 * PROGRAMMER: [Your Name] 
 * COURSE: CS340 Programming Lang/Design 
 * DATE: September 2025 
 * REQUIREMENT: Assignment 4 - Graphics 
 * 
 * DESCRIPTION:
 * This module handles animation creation and management for the drawing application.
 * It provides methods for creating keyframes, interpolating between frames,
 * and playing animations. Enhanced for Assignment 7 integration.
 * 
 * COPYRIGHT:
 * This code is copyright (c)2025 [Your Name] and Dean Zeller.
 */
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
    
    /********************************************************************
     * METHOD: AnimationSystem Constructor
     * DESCRIPTION: Initializes the animation system with UI reference
     * PARAMETERS: UserInterface3 ui - the user interface instance
     * RETURN VALUE: None
     ********************************************************************/
    public AnimationSystem(UserInterface3 ui) {
        this.ui = ui;
        this.codeGeneration = ui.getCodeGeneration();
        this.ioHandler = ui.getIOHandler();
    }
    
    /********************************************************************
     * METHOD: createAnimation
     * DESCRIPTION: Creates a new animation with the given name
     * PARAMETERS: String name - the animation name
     * RETURN VALUE: Animation - the created animation
     ********************************************************************/
    public Animation createAnimation(String name) {
        Animation animation = new Animation(name);
        animations.add(animation);
        currentAnimation = animation;
        ioHandler.appendToHistory("System: Created animation \"" + name + 
                                "\" - Total animations: " + animations.size() + "\n");
        return animation;
    }
    
    /********************************************************************
     * METHOD: addKeyframe
     * DESCRIPTION: Adds a keyframe to the current animation
     * PARAMETERS: String frameName - name for this keyframe
     * RETURN VALUE: none
     ********************************************************************/
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
        ioHandler.appendToHistory("System: Added keyframe \"" + frameName + 
                                "\" with " + frame.getShapes().size() + " shapes\n");
    }
    
    /********************************************************************
     * METHOD: playAnimation
     * DESCRIPTION: Plays the specified animation
     * PARAMETERS: String animationName - name of animation to play
     * RETURN VALUE: none
     ********************************************************************/
    public void playAnimation(String animationName) {
        Animation animation = findAnimation(animationName);
        if (animation == null) {
            ioHandler.appendToHistory("System: Animation \"" + animationName + "\" not found\n");
            return;
        }
        
        if (isPlaying) {
            stopAnimation();
        }
        
        currentAnimation = animation;
        currentFrame = 0;
        isPlaying = true;
        
        ioHandler.appendToHistory("System: Playing animation \"" + animationName + "\" (looping)\n");
        
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
    
    /********************************************************************
     * METHOD: stopAnimation
     * DESCRIPTION: Stops the current animation
     * PARAMETERS: none
     * RETURN VALUE: none
     ********************************************************************/
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
            animationTimer = null;
        }
        isPlaying = false;
        ioHandler.appendToHistory("System: Animation stopped\n");
    }
    
    /********************************************************************
     * METHOD: showFrame
     * DESCRIPTION: Displays a specific frame of the animation
     * PARAMETERS: int frameIndex - index of frame to show
     * RETURN VALUE: none
     ********************************************************************/
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
    
    /********************************************************************
     * METHOD: setFrameDelay
     * DESCRIPTION: Sets the delay between animation frames
     * PARAMETERS: long delay - delay in milliseconds
     * RETURN VALUE: none
     ********************************************************************/
    public void setFrameDelay(long delay) {
        this.frameDelay = delay;
        ioHandler.appendToHistory("System: Frame delay set to " + delay + "ms\n");
    }
    
    /********************************************************************
     * METHOD: getFrameDelay
     * DESCRIPTION: Gets the current frame delay
     * PARAMETERS: None
     * RETURN VALUE: long - current frame delay in milliseconds
     ********************************************************************/
    public long getFrameDelay() {
        return frameDelay;
    }
    
    /********************************************************************
     * METHOD: findAnimation
     * DESCRIPTION: Finds an animation by name
     * PARAMETERS: String name - animation name to find
     * RETURN VALUE: Animation - found animation or null
     ********************************************************************/
    private Animation findAnimation(String name) {
        for (Animation anim : animations) {
            if (anim.getName().equalsIgnoreCase(name)) {
                return anim;
            }
        }
        return null;
    }
    
    /********************************************************************
     * METHOD: cloneShape
     * DESCRIPTION: Creates a deep copy of a shape
     * PARAMETERS: CodeGeneration3.Shape shape - shape to clone
     * RETURN VALUE: CodeGeneration3.Shape - cloned shape
     ********************************************************************/
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
            return codeGeneration.new Triangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, 
                                             triangle.x3, triangle.y3, triangle.filled);
        } else if (shape instanceof CodeGeneration3.Polygon) {
            CodeGeneration3.Polygon polygon = (CodeGeneration3.Polygon) shape;
            List<Point> pointsCopy = new ArrayList<>(polygon.points);
            return codeGeneration.new Polygon(pointsCopy, polygon.filled);
        } else if (shape instanceof CodeGeneration3.Line) {
            CodeGeneration3.Line line = (CodeGeneration3.Line) shape;
            return codeGeneration.new Line(line.x1, line.y1, line.x2, line.y2);
        }
        // Add more shape types as needed
        return null;
    }
    
    /********************************************************************
     * METHOD: getAnimations
     * DESCRIPTION: Returns list of all animations
     * PARAMETERS: none
     * RETURN VALUE: List<Animation> - all animations
     ********************************************************************/
    public List<Animation> getAnimations() {
        return new ArrayList<>(animations);
    }
    
    /********************************************************************
     * METHOD: getCurrentAnimation
     * DESCRIPTION: Returns current animation
     * PARAMETERS: none
     * RETURN VALUE: Animation - current animation
     ********************************************************************/
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
    
    /********************************************************************
     * METHOD: isPlaying
     * DESCRIPTION: Checks if animation is playing
     * PARAMETERS: none
     * RETURN VALUE: boolean - true if playing
     ********************************************************************/
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /********************************************************************
     * METHOD: clearAnimations
     * DESCRIPTION: Clears all animations
     * PARAMETERS: None
     * RETURN VALUE: None
     ********************************************************************/
    public void clearAnimations() {
        animations.clear();
        currentAnimation = null;
        ioHandler.appendToHistory("System: All animations cleared\n");
    }
    
    /********************************************************************
     * METHOD: removeAnimation
     * DESCRIPTION: Removes an animation by name
     * PARAMETERS: String name - name of animation to remove
     * RETURN VALUE: boolean - true if removed, false if not found
     ********************************************************************/
    public boolean removeAnimation(String name) {
        Animation toRemove = findAnimation(name);
        if (toRemove != null) {
            animations.remove(toRemove);
            if (currentAnimation == toRemove) {
                currentAnimation = null;
            }
            ioHandler.appendToHistory("System: Animation \"" + name + "\" removed\n");
            return true;
        }
        return false;
    }
    
    /********************************************************************
     * METHOD: getAnimationNames
     * DESCRIPTION: Gets all animation names
     * PARAMETERS: None
     * RETURN VALUE: List<String> - list of animation names
     ********************************************************************/
    public List<String> getAnimationNames() {
        List<String> names = new ArrayList<>();
        for (Animation anim : animations) {
            names.add(anim.getName());
        }
        return names;
    }
    
    /********************************************************************
     * METHOD: playAnimationOnce
     * DESCRIPTION: Plays animation once without looping
     * PARAMETERS: String animationName - name of animation to play
     * RETURN VALUE: None
     ********************************************************************/
    public void playAnimationOnce(String animationName) {
        Animation animation = findAnimation(animationName);
        if (animation == null) {
            ioHandler.appendToHistory("System: Animation \"" + animationName + "\" not found\n");
            return;
        }
        
        if (isPlaying) {
            stopAnimation();
        }
        
        currentAnimation = animation;
        currentFrame = 0;
        isPlaying = true;
        
        ioHandler.appendToHistory("System: Playing animation \"" + animationName + "\" (once)\n");
        
        // Start animation timer for single playthrough
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (currentFrame < currentAnimation.getFrameCount()) {
                        showFrame(currentFrame);
                        currentFrame++;
                    } else {
                        // Stop at the end
                        stopAnimation();
                        // Show first frame as final state
                        currentFrame = 0;
                        showFrame(currentFrame);
                    }
                });
            }
        }, 0, frameDelay);
    }
    
    /********************************************************************
     * METHOD: previewFrame
     * DESCRIPTION: Previews a specific frame without playing animation
     * PARAMETERS: String animationName - animation name
     *             int frameIndex - frame index to preview
     * RETURN VALUE: None
     ********************************************************************/
    public void previewFrame(String animationName, int frameIndex) {
        Animation animation = findAnimation(animationName);
        if (animation == null) {
            ioHandler.appendToHistory("System: Animation \"" + animationName + "\" not found\n");
            return;
        }
        
        if (frameIndex < 0 || frameIndex >= animation.getFrameCount()) {
            ioHandler.appendToHistory("System: Invalid frame index: " + frameIndex + 
                                    " (animation has " + animation.getFrameCount() + " frames)\n");
            return;
        }
        
        showFrame(frameIndex);
        ioHandler.appendToHistory("System: Previewing frame " + frameIndex + 
                                " of animation \"" + animationName + "\"\n");
    }
    
    /********************************************************************
     * METHOD: getAnimationInfo
     * DESCRIPTION: Gets information about an animation
     * PARAMETERS: String animationName - animation name
     * RETURN VALUE: String - animation information
     ********************************************************************/
    public String getAnimationInfo(String animationName) {
        Animation animation = findAnimation(animationName);
        if (animation == null) {
            return "Animation not found: " + animationName;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Animation: ").append(animation.getName()).append("\n");
        info.append("Frames: ").append(animation.getFrameCount()).append("\n");
        info.append("Current Frame Delay: ").append(frameDelay).append("ms\n");
        
        for (int i = 0; i < animation.getFrameCount(); i++) {
            AnimationFrame frame = animation.getFrame(i);
            info.append("  Frame ").append(i).append(": \"")
                .append(frame.getName()).append("\" (")
                .append(frame.getShapes().size()).append(" shapes)\n");
        }
        
        return info.toString();
    }
    
    /**
     * Animation Class
     * DESCRIPTION: Represents an animation sequence
     */
    public class Animation {
        private String name;
        private List<AnimationFrame> frames = new ArrayList<>();
        
        public Animation(String name) {
            this.name = name;
        }
        
        public String getName() { 
            return name; 
        }
        
        public void addFrame(AnimationFrame frame) { 
            frames.add(frame); 
        }
        
        public AnimationFrame getFrame(int index) { 
            return frames.get(index); 
        }
        
        public int getFrameCount() { 
            return frames.size(); 
        }
        
        public List<AnimationFrame> getFrames() { 
            return new ArrayList<>(frames); 
        }
        
        public void removeFrame(int index) {
            if (index >= 0 && index < frames.size()) {
                frames.remove(index);
            }
        }
        
        public void clearFrames() {
            frames.clear();
        }
    }
    
    /**
     * AnimationFrame Class
     * DESCRIPTION: Represents a single frame in an animation
     */
    public class AnimationFrame {
        private String name;
        private List<CodeGeneration3.Shape> shapes = new ArrayList<>();
        
        public AnimationFrame(String name) {
            this.name = name;
        }
        
        public String getName() { 
            return name; 
        }
        
        public void addShape(CodeGeneration3.Shape shape) { 
            shapes.add(shape); 
        }
        
        public List<CodeGeneration3.Shape> getShapes() { 
            return new ArrayList<>(shapes); 
        }
        
        public int getShapeCount() {
            return shapes.size();
        }
        
        public void clearShapes() {
            shapes.clear();
        }
    }
}