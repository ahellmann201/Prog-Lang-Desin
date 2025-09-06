package SM_PAINT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

// Main frame class
class PaintFrame extends JFrame {
    private PaintPanel paintPanel;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private String tool = "Pencil";
    private String brushType = "Marker"; // Default brush type
    
    public PaintFrame() {
        setTitle("Simple Paint");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        paintPanel = new PaintPanel();
        add(paintPanel, BorderLayout.CENTER);
        
        createToolbar();
    }
    
    private void createToolbar() {
        JToolBar toolbar = new JToolBar();
        
        // Color selection
        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", currentColor);
            if (newColor != null) {
                currentColor = newColor;
                paintPanel.setCurrentColor(newColor);
            }
        });
        toolbar.add(colorButton);
        
        // Tool selection dropdown
        JComboBox<String> toolCombo = new JComboBox<>(new String[]{"Pencil", "Eraser", "Select", "Text"});
        toolCombo.addActionListener(e -> {
            tool = (String) toolCombo.getSelectedItem();
            paintPanel.setTool(tool);
        });
        toolbar.add(new JLabel("Tools:"));
        toolbar.add(toolCombo);
        
        // Shapes dropdown
        JComboBox<String> shapeCombo = new JComboBox<>(new String[]{"Line", "Rectangle", "Oval"});
        shapeCombo.addActionListener(e -> {
            tool = (String) shapeCombo.getSelectedItem();
            paintPanel.setTool(tool);
        });
        toolbar.add(new JLabel("Shapes:"));
        toolbar.add(shapeCombo);
        
        // Brush types dropdown
        JComboBox<String> brushCombo = new JComboBox<>(new String[]{"Marker", "Pen", "Pencil", "Crayon"});
        brushCombo.addActionListener(e -> {
            brushType = (String) brushCombo.getSelectedItem();
            paintPanel.setBrushType(brushType);
        });
        toolbar.add(new JLabel("Brushes:"));
        toolbar.add(brushCombo);
        
        // Image operations dropdown
        JButton insertButton = new JButton("Insert Image");
        insertButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    paintPanel.insertImage(selectedFile);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        toolbar.add(insertButton);
        
        // Crop button
        JButton cropButton = new JButton("Crop");
        cropButton.addActionListener(e -> {
            tool = "Crop";
            paintPanel.setTool(tool);
        });
        toolbar.add(cropButton);
        
        // Apply Crop button
        JButton applyCropButton = new JButton("Apply Crop");
        applyCropButton.addActionListener(e -> paintPanel.applyCrop());
        toolbar.add(applyCropButton);
        
        // Brush size
        toolbar.addSeparator();
        JSlider sizeSlider = new JSlider(1, 50, brushSize);
        sizeSlider.addChangeListener(e -> {
            brushSize = sizeSlider.getValue();
            paintPanel.setBrushSize(brushSize);
        });
        toolbar.add(new JLabel("Size:"));
        toolbar.add(sizeSlider);
        
        // Clear button
        toolbar.addSeparator();
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> paintPanel.clear());
        toolbar.add(clearButton);
        
        add(toolbar, BorderLayout.NORTH);
    }
}