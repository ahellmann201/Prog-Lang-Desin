// PaintPanel.java
package SM_PAINT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

// Drawing panel class
class PaintPanel extends JPanel {
	private BufferedImage canvas;
    private Graphics2D g2d;
    private int startX, startY;
    private int currentX, currentY;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private String tool = "Pencil";
    private String brushType = "Marker"; // Default brush type
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<ImageElement> images = new ArrayList<>();
    private ArrayList<TextElement> texts = new ArrayList<>();
    private Shape currentShape;
    private TextElement currentText = null;
    private TextElement selectedText = null;
    private ImageElement selectedImage = null;
    private boolean isResizing = false;
    private boolean isCropping = false;
    private boolean isSelecting = false;
    private Rectangle selectionRect = null;
    private Rectangle cropRect = null;
    private int resizeHandle = -1;
    private final int HANDLE_SIZE = 8;
    private Timer gifTimer;
    
    // New variables for movable selection
    private SelectionElement selectedArea = null;
    private boolean isMovingSelection = false;
    private int selectionOffsetX, selectionOffsetY;
    
    // Text editing dialog
    private JDialog textEditDialog;
    private int textX, textY;
    private Shape selectedShape = null;
    
    public PaintPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        
        // Set up keyboard focus and bindings
        setFocusable(true);
        requestFocusInWindow();
        
        // Create text edit dialog
        createTextEditDialog();
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    if (selectedImage != null) {
                        // Delete selected image
                        images.remove(selectedImage);
                        selectedImage = null;
                        repaint();
                    } else if (selectedText != null) {
                        // Delete selected text
                        texts.remove(selectedText);
                        selectedText = null;
                        repaint();
                    } else if (selectedShape != null) {
                        // Delete selected shape
                        shapes.remove(selectedShape);
                        selectedShape = null;
                        repaint();
                    } else if (selectedArea != null) {
                        // Delete selected area
                        deleteSelectedArea();
                        selectedArea = null;
                        repaint();
                    } else if (selectionRect != null) {
                        // Delete everything in the selection area
                        deleteSelectionArea();
                        selectionRect = null;
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && selectedText != null) {
                    // Edit selected text
                    editSelectedText();
                }
            }
        });
    
        
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent e) {
                requestFocusInWindow(); // Ensure panel has focus for keyboard events
                startX = e.getX();
                startY = e.getY();
                
                // Check if we're clicking outside any selected area
                boolean clickedOutsideSelection = true;
                
                // Check if clicked on selected area
                if (selectedArea != null && selectedArea.contains(startX, startY)) {
                    clickedOutsideSelection = false;
                    isMovingSelection = true;
                    selectionOffsetX = startX - selectedArea.getX();
                    selectionOffsetY = startY - selectedArea.getY();
                }
                
                // Check if we're clicking on existing text
                if (tool.equals("Select") || tool.equals("Text")) {
                    selectedText = null;
                    for (int i = texts.size() - 1; i >= 0; i--) {
                        TextElement text = texts.get(i);
                        if (text.contains(startX, startY)) {
                            selectedText = text;
                            clickedOutsideSelection = false;
                            break;
                        }
                    }
                }
                
                // Check if we're clicking on existing shape
                if (tool.equals("Select")) {
                    selectedShape = null;
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        Shape shape = shapes.get(i);
                        if (shape.contains(startX, startY)) {
                            selectedShape = shape;
                            clickedOutsideSelection = false;
                            break;
                        }
                    }
                }
                
                if (tool.equals("Select")) {
                    // Check if clicked on an image or resize handle
                    selectedImage = null;
                    for (int i = images.size() - 1; i >= 0; i--) {
                        ImageElement img = images.get(i);
                        if (img.contains(startX, startY)) {
                            selectedImage = img;
                            clickedOutsideSelection = false;
                            // Check if clicked on a resize handle
                            resizeHandle = img.getResizeHandleAt(startX, startY, HANDLE_SIZE);
                            if (resizeHandle != -1) {
                                isResizing = true;
                            } else {
                                isResizing = false;
                            }
                            break;
                        }
                    }
                    
                    // If we clicked on text and have the Select tool, don't create new selection
                    if (selectedText != null && tool.equals("Select")) {
                        repaint();
                        return;
                    }
                    
                    // If clicked outside all selections, deselect everything
                    if (clickedOutsideSelection) {
                        selectedArea = null;
                        selectedImage = null;
                        selectedText = null;
                        selectedShape = null;
                        
                        // Start new selection rectangle
                        isSelecting = true;
                        selectionRect = new Rectangle(startX, startY, 0, 0);
                    }
                    
                    repaint();
                    return;
                } else if (tool.equals("Crop")) {
                    isCropping = true;
                    cropRect = new Rectangle(startX, startY, 0, 0);
                    repaint();
                    return;
                } else if (tool.equals("Text")) {
                    // If we didn't click on existing text, create new text
                    if (selectedText == null) {
                        showTextEditDialog(startX, startY);
                    } else {
                        // Edit existing text
                        editSelectedText();
                    }
                    repaint();
                    return;
                }
                
                if (tool.equals("Pencil") || tool.equals("Eraser")) {
                    if (canvas == null) {
                        initCanvas();
                    }
                    g2d.setColor(tool.equals("Eraser") ? Color.WHITE : currentColor);
                    setBrushStroke(); // Set brush stroke based on brush type
                    g2d.drawLine(startX, startY, startX, startY);
                } else {
                    currentShape = new Shape(tool, startX, startY, startX, startY, 
                                            currentColor, brushSize);
                }
                repaint();
            }
            
            
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tool.equals("Select")) {
                    if (selectedImage != null) {
                        isResizing = false;
                        resizeHandle = -1;
                    }
                    isSelecting = false;
                    isMovingSelection = false;
                    
                    // If we were selecting and have a valid selection, create a SelectionElement
                    if (selectionRect != null && selectionRect.width > 0 && selectionRect.height > 0) {
                        selectedArea = createSelectionElement(selectionRect);
                        selectionRect = null;
                    }
                } else if (tool.equals("Crop")) {
                    isCropping = false;
                    // Keep the crop rectangle for applying later
                } else if (currentShape != null) {
                    shapes.add(currentShape);
                    currentShape = null;
                }
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentX = e.getX();
                currentY = e.getY();
                
                if (tool.equals("Select")) {
                    if (selectedImage != null) {
                        if (isResizing) {
                            selectedImage.resize(resizeHandle, currentX, currentY);
                        } else {
                            selectedImage.move(currentX - startX, currentY - startY);
                            startX = currentX;
                            startY = currentY;
                        }
                    } else if (selectedText != null) {
                        // Move the selected text
                        selectedText.setPosition(currentX, currentY);
                        startX = currentX;
                        startY = currentY;
                    } else if (selectedShape != null) {
                        // Move the selected shape
                        selectedShape.setEnd(selectedShape.getEndX() + (currentX - startX), 
                                           selectedShape.getEndY() + (currentY - startY));
                        selectedShape.setEnd(selectedShape.getStartX() + (currentX - startX), 
                                           selectedShape.getStartY() + (currentY - startY));
                        startX = currentX;
                        startY = currentY;
                    } else if (isMovingSelection && selectedArea != null) {
                        // Move the selected area
                        selectedArea.setPosition(currentX - selectionOffsetX, currentY - selectionOffsetY);
                        startX = currentX;
                        startY = currentY;
                    } else if (isSelecting) {
                        // Update selection rectangle
                        int x = Math.min(startX, currentX);
                        int y = Math.min(startY, currentY);
                        int width = Math.abs(currentX - startX);
                        int height = Math.abs(currentY - startY);
                        selectionRect = new Rectangle(x, y, width, height);
                    }
                    repaint();
                    return;
                } else if (tool.equals("Crop") && isCropping) {
                    // Update crop rectangle
                    int x = Math.min(startX, currentX);
                    int y = Math.min(startY, currentY);
                    int width = Math.abs(currentX - startX);
                    int height = Math.abs(currentY - startY);
                    cropRect = new Rectangle(x, y, width, height);
                    repaint();
                    return;
                }
                
                if (tool.equals("Pencil") || tool.equals("Eraser")) {
                    if (canvas != null) {
                        g2d.setColor(tool.equals("Eraser") ? Color.WHITE : currentColor);
                        setBrushStroke(); // Set brush stroke based on brush type
                        g2d.drawLine(startX, startY, currentX, currentY);
                        startX = currentX;
                        startY = currentY;
                    }
                } else if (currentShape != null) {
                    currentShape.setEnd(currentX, currentY);
                }
                repaint();
            }
        });
        
        // Timer for GIF animation
        gifTimer = new Timer(100, e -> {
            boolean needsRepaint = false;
            for (ImageElement image : images) {
                if (image.isGif() && image.advanceFrame()) {
                    needsRepaint = true;
                }
            }
            if (needsRepaint) {
                repaint();
            }
        });
        gifTimer.start();
    }
    
    private void createTextEditDialog() {
        textEditDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Text", true);
        textEditDialog.setSize(400, 300);
        textEditDialog.setLayout(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Text field
        JTextField textField = new JTextField();
        controlPanel.add(new JLabel("Text:"));
        controlPanel.add(textField);
        
        // Font size
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 100, 1));
        controlPanel.add(new JLabel("Size:"));
        controlPanel.add(sizeSpinner);
        
        // Font family
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontCombo = new JComboBox<>(fonts);
        fontCombo.setSelectedItem("Arial");
        controlPanel.add(new JLabel("Font:"));
        controlPanel.add(fontCombo);
        
        // Color
        JButton colorButton = new JButton("Choose Color");
        controlPanel.add(new JLabel("Color:"));
        controlPanel.add(colorButton);
        
        // Style options
        JCheckBox boldCheck = new JCheckBox("Bold");
        JCheckBox italicCheck = new JCheckBox("Italic");
        JCheckBox underlineCheck = new JCheckBox("Underline");
        
        JPanel stylePanel = new JPanel(new FlowLayout());
        stylePanel.add(boldCheck);
        stylePanel.add(italicCheck);
        stylePanel.add(underlineCheck);
        
        controlPanel.add(new JLabel("Style:"));
        controlPanel.add(stylePanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        textEditDialog.add(controlPanel, BorderLayout.CENTER);
        textEditDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set up event handlers
        okButton.addActionListener(e -> {
            String text = textField.getText();
            if (text != null && !text.trim().isEmpty()) {
                if (currentText != null) {
                    // Update existing text
                    currentText.setText(text);
                    currentText.setFontSize((Integer)sizeSpinner.getValue());
                    currentText.setFontName((String)fontCombo.getSelectedItem());
                    currentText.setBold(boldCheck.isSelected());
                    currentText.setItalic(italicCheck.isSelected());
                    currentText.setUnderline(underlineCheck.isSelected());
                    currentText.setColor(currentColor);
                } else {
                    // Create new text
                    currentText = new TextElement(
                        text, 
                        textX, textY, 
                        currentColor, 
                        (Integer)sizeSpinner.getValue(),
                        (String)fontCombo.getSelectedItem(),
                        boldCheck.isSelected(),
                        italicCheck.isSelected(),
                        underlineCheck.isSelected()
                    );
                    texts.add(currentText);
                }
                repaint();
            }
            textEditDialog.setVisible(false);
            currentText = null;
        });
        
        cancelButton.addActionListener(e -> {
            textEditDialog.setVisible(false);
            currentText = null;
        });
        
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(textEditDialog, "Choose Color", currentColor);
            if (newColor != null) {
                currentColor = newColor;
            }
        });
        
        textEditDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                textEditDialog.setVisible(false);
                currentText = null;
            }
        });
    }
    
    private void showTextEditDialog(int x, int y) {
        textX = x;
        textY = y;
        
        // Reset dialog fields
        Component[] components = textEditDialog.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                ((JTextField)comp).setText("");
            } else if (comp instanceof JSpinner) {
                ((JSpinner)comp).setValue(12);
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>)comp).setSelectedItem("Arial");
            } else if (comp instanceof Container) {
                // Recursively check containers for components
                checkContainerForComponents((Container)comp);
            }
        }
        
        textEditDialog.setLocationRelativeTo(this);
        textEditDialog.setVisible(true);
    }
    
    private void checkContainerForComponents(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JCheckBox) {
                ((JCheckBox)comp).setSelected(false);
            } else if (comp instanceof Container) {
                checkContainerForComponents((Container)comp);
            }
        }
    }
    
    private void editSelectedText() {
        if (selectedText == null) return;
        
        // Set up dialog with current text properties
        Component[] components = textEditDialog.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                ((JTextField)comp).setText(selectedText.getText());
            } else if (comp instanceof JSpinner) {
                ((JSpinner)comp).setValue(selectedText.getFontSize());
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>)comp).setSelectedItem(selectedText.getFontName());
            } else if (comp instanceof Container) {
                // Recursively check containers for components
                setContainerComponents((Container)comp, selectedText);
            }
        }
        
        currentText = selectedText;
        textEditDialog.setLocationRelativeTo(this);
        textEditDialog.setVisible(true);
    }
    
    private void setContainerComponents(Container container, TextElement text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox)comp;
                if ("Bold".equals(checkBox.getText())) {
                    checkBox.setSelected(text.isBold());
                } else if ("Italic".equals(checkBox.getText())) {
                    checkBox.setSelected(text.isItalic());
                } else if ("Underline".equals(checkBox.getText())) {
                    checkBox.setSelected(text.isUnderline());
                }
            } else if (comp instanceof Container) {
                setContainerComponents((Container)comp, text);
            }
        }
    }
    
    private void initCanvas() {
        canvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(currentColor);
    }
    
    private SelectionElement createSelectionElement(Rectangle selection) {
        // Create a buffered image of the selected area
        BufferedImage selectionImage = new BufferedImage(selection.width, selection.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = selectionImage.createGraphics();
        
        // Draw the canvas portion
        if (canvas != null) {
            g.drawImage(canvas, 
                        0, 0, selection.width, selection.height,
                        selection.x, selection.y, selection.x + selection.width, selection.y + selection.height,
                        null);
        }
        
        // Draw shapes that are within the selection area
        for (Shape shape : shapes) {
            if (isShapeInSelection(shape, selection)) {
                // Adjust shape coordinates relative to the selection
                Shape adjustedShape = new Shape(
                    shape.getType(), 
                    shape.getStartX() - selection.x, 
                    shape.getStartY() - selection.y,
                    shape.getEndX() - selection.x,
                    shape.getEndY() - selection.y,
                    shape.getColor(),
                    shape.getThickness()
                );
                adjustedShape.draw(g);
            }
        }
        
        // Draw text elements that are within the selection area
        for (TextElement text : texts) {
            if (selection.contains(text.getX(), text.getY())) {
                TextElement adjustedText = new TextElement(
                    text.getText(),
                    text.getX() - selection.x,
                    text.getY() - selection.y,
                    text.getColor(),
                    text.getFontSize(),
                    text.getFontName(),
                    text.isBold(),
                    text.isItalic(),
                    text.isUnderline()
                );
                adjustedText.draw(g);
            }
        }
        
        // Draw images that are within the selection area
        for (ImageElement image : images) {
            if (isImageInSelection(image, selection)) {
                // This is a simplified approach - in a real app, you'd need to handle partial overlaps
                ImageElement adjustedImage = new ImageElement(
                    image.getImage(),
                    image.getX() - selection.x,
                    image.getY() - selection.y,
                    image.getWidth(),
                    image.getHeight()
                );
                adjustedImage.draw(g);
            }
        }
        
        g.dispose();
        
        // Remove the selected elements from the main canvas
        deleteSelectionArea();
        
        return new SelectionElement(selectionImage, selection.x, selection.y, selection.width, selection.height);
    }
    
    private void deleteSelectedArea() {
        if (selectedArea == null) return;
        
        // The content is already removed from the main canvas when the selection was created
        // We just need to remove the selection element itself
        selectedArea = null;
    }
    
    private void deleteSelectionArea() {
        if (selectionRect == null) return;
        
        // Erase from canvas if it exists
        if (canvas != null) {
            Graphics2D g = canvas.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            g.setColor(currentColor);
            g.dispose();
        }
        
        // Remove shapes that are entirely within the selection area
        ArrayList<Shape> shapesToRemove = new ArrayList<>();
        for (Shape shape : shapes) {
            if (isShapeInSelection(shape, selectionRect)) {
                shapesToRemove.add(shape);
            }
        }
        shapes.removeAll(shapesToRemove);
        
        // Remove text elements that are within the selection area
        ArrayList<TextElement> textsToRemove = new ArrayList<>();
        for (TextElement text : texts) {
            if (selectionRect.contains(text.getX(), text.getY())) {
                textsToRemove.add(text);
            }
        }
        texts.removeAll(textsToRemove);
        
        // Remove images that are entirely within the selection area
        ArrayList<ImageElement> imagesToRemove = new ArrayList<>();
        for (ImageElement image : images) {
            if (selectionRect.contains(image.getX(), image.getY()) && 
                selectionRect.contains(image.getX() + image.getWidth(), image.getY() + image.getHeight())) {
                imagesToRemove.add(image);
            }
        }
        images.removeAll(imagesToRemove);
    }
    
    private boolean isShapeInSelection(Shape shape, Rectangle selection) {
        // This is a simplified check - in a real application, you'd want more precise collision detection
        int shapeMinX = Math.min(shape.getStartX(), shape.getEndX());
        int shapeMaxX = Math.max(shape.getStartX(), shape.getEndX());
        int shapeMinY = Math.min(shape.getStartY(), shape.getEndY());
        int shapeMaxY = Math.max(shape.getStartY(), shape.getEndY());
        
        return selection.contains(shapeMinX, shapeMinY) && 
               selection.contains(shapeMaxX, shapeMaxY);
    }
    
    private boolean isImageInSelection(ImageElement image, Rectangle selection) {
        return selection.contains(image.getX(), image.getY()) && 
               selection.contains(image.getX() + image.getWidth(), image.getY() + image.getHeight());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the canvas first
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, null);
        }
        
        // Draw all permanent shapes
        for (Shape shape : shapes) {
            shape.draw(g);
        }
        
        // Draw temporary shape
        if (currentShape != null) {
            currentShape.draw(g);
        }
        
        // Draw all images
        for (ImageElement image : images) {
            image.draw(g);
        }
        
        // Draw all text elements
        for (TextElement text : texts) {
            text.draw(g);
        }
        
        // Draw selected area if it exists
        if (selectedArea != null) {
            selectedArea.draw(g);
        }
        
        // Draw selection and resize handles if an image is selected
        if (selectedImage != null) {
            selectedImage.drawSelection(g, HANDLE_SIZE);
        }
        
        // Draw selection rectangle around text if selected
        if (selectedText != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                          0, new float[]{5}, 0)); // Dashed line
            Rectangle bounds = selectedText.getBounds();
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        // Draw selection rectangle around shape if selected
        if (selectedShape != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                          0, new float[]{5}, 0)); // Dashed line
            
            // Draw bounding box around the selected shape
            int minX = Math.min(selectedShape.getStartX(), selectedShape.getEndX());
            int minY = Math.min(selectedShape.getStartY(), selectedShape.getEndY());
            int width = Math.abs(selectedShape.getEndX() - selectedShape.getStartX());
            int height = Math.abs(selectedShape.getEndY() - selectedShape.getStartY());
            
            g2d.drawRect(minX, minY, width, height);
        }
        
        // Draw selection rectangle if selecting
        if (isSelecting && selectionRect != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                          0, new float[]{5}, 0)); // Dashed line
            g2d.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
        
        // Draw crop rectangle if cropping
        if (isCropping && cropRect != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                          0, new float[]{5}, 0)); // Dashed line
            g2d.drawRect(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
        }
    }
    
    private void setBrushStroke() {
        switch (brushType) {
            case "Marker":
                // Smooth, solid stroke with rounded ends
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                break;
            case "Pen":
                // Sharp, precise stroke with square ends
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                break;
            case "Pencil":
                // Slightly textured stroke for pencil effect
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Add some transparency for pencil-like effect
                Color pencilColor = new Color(currentColor.getRed(), currentColor.getGreen(), 
                                            currentColor.getBlue(), 200);
                g2d.setColor(pencilColor);
                break;
            case "Crayon":
                // Rough, textured stroke for crayon effect
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Add some texture by using a custom stroke
                float[] crayonPattern = {2f, 3f};
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                            1.0f, crayonPattern, 0f));
                break;
        }
    }
    
    public void setCurrentColor(Color color) {
        this.currentColor = color;
        if (g2d != null) {
            g2d.setColor(color);
        }
    }
    
    public void setBrushSize(int size) {
        this.brushSize = size;
    }
    
    public void setBrushType(String brushType) {
        this.brushType = brushType;
    }
    
 // Update setTool to deselect shapes when changing tools
    public void setTool(String tool) {
        this.tool = tool;
        selectedImage = null;
        selectedText = null;
        selectedShape = null;
        selectionRect = null;
        cropRect = null;
        repaint();
    }
    
    public void insertImage(File imageFile) {
        try {
            // Check if it's a GIF
            if (imageFile.getName().toLowerCase().endsWith(".gif")) {
                GifImage gif = new GifImage(imageFile);
                // Center the GIF on the canvas
                int x = (getWidth() - gif.getWidth()) / 2;
                int y = (getHeight() - gif.getHeight()) / 2;
                images.add(new ImageElement(gif, x, y, gif.getWidth(), gif.getHeight()));
            } else {
                BufferedImage image = ImageIO.read(imageFile);
                // Center the image on the canvas
                int x = (getWidth() - image.getWidth()) / 2;
                int y = (getHeight() - image.getHeight()) / 2;
                images.add(new ImageElement(image, x, y, image.getWidth(), image.getHeight()));
            }
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void applyCrop() {
        if (cropRect == null || cropRect.width <= 0 || cropRect.height <= 0) {
            JOptionPane.showMessageDialog(this, "Please define a valid crop area first.",
                "Crop Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a new cropped canvas
        BufferedImage newCanvas = new BufferedImage(cropRect.width, cropRect.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D newG2d = newCanvas.createGraphics();
        newG2d.setColor(Color.WHITE);
        newG2d.fillRect(0, 0, cropRect.width, cropRect.height);
        
        // Draw the relevant portion of the old canvas onto the new one
        if (canvas != null) {
            newG2d.drawImage(canvas, 
                            0, 0, cropRect.width, cropRect.height,
                            cropRect.x, cropRect.y, cropRect.x + cropRect.width, cropRect.y + cropRect.height,
                            null);
        }
        
        // Update canvas reference
        canvas = newCanvas;
        g2d = canvas.createGraphics();
        g2d.setColor(currentColor);
        
        // Adjust all shapes and images to new coordinate system
        for (Shape shape : shapes) {
            shape.adjustForCrop(cropRect.x, cropRect.y);
        }
        
        for (ImageElement image : images) {
            image.adjustForCrop(cropRect.x, cropRect.y);
        }
        
        for (TextElement text : texts) {
            text.adjustForCrop(cropRect.x, cropRect.y);
        }
        
        // Adjust selected area if it exists
        if (selectedArea != null) {
            selectedArea.adjustForCrop(cropRect.x, cropRect.y);
        }
        
        cropRect = null;
        repaint();
    }
    
    public void clear() {
        shapes.clear();
        images.clear();
        texts.clear();
        selectedImage = null;
        selectedText = null;
        selectedShape = null;
        selectionRect = null;
        cropRect = null;
        selectedArea = null;
        if (canvas != null) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(currentColor);
        }
        repaint();
    }
    
    
    private BasicStroke getBrushStroke(String brushType, int size) {
        switch (brushType) {
            case "Pen":
                // Pen: thin, precise line with square cap
                return new BasicStroke(Math.max(1, size/2), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
            
            case "Pencil":
                // Pencil: slightly textured, medium opacity
                // We'll simulate pencil texture with a custom stroke
                float[] dashPattern = {1, 2}; // Creates a slightly broken line
                return new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                      1.0f, dashPattern, 0);
            
            case "Crayon":
                // Crayon: rough, textured stroke with variable width
                return new BasicStroke(size * 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                      1.0f, null, 0);
            
            case "Marker":
            default:
                // Marker: smooth, solid stroke (current default)
                return new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
    }
}