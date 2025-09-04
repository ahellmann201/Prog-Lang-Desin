// GifImage.java
package SM_PAINT;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;

// Simplified GIF handling - just show first frame for stability
class GifImage {
    private BufferedImage firstFrame;
    private int width, height;
    
    public GifImage(File file) throws Exception {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(file);
        reader.setInput(input);
        
        // Just get the first frame for simplicity
        firstFrame = reader.read(0);
        this.width = firstFrame.getWidth();
        this.height = firstFrame.getHeight();
        
        input.close();
        reader.dispose();
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public boolean advanceFrame() {
        return false; // No animation in this simplified version
    }
    
    public BufferedImage getCurrentFrame() {
        return firstFrame;
    }
}