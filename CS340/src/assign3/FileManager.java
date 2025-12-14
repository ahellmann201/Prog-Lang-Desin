package assign3;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
/**
* Modern File Manager with Recent Files Support
* PROGRAMMER: [Your Name]
* COURSE: CS340 Programming Lang/Design
* DATE: [Current Date]
* DESCRIPTION:
* A modern file manager with recent files history, better visuals, and support for multiple file types.
* COPYRIGHT:
* This code is copyright (c)2025 [Your Name] and Dean Zeller.
*/
public class FileManager {
    private JFrame parent;
    private JFileChooser fileChooser;
    private List<File> recentFiles;
    private Preferences prefs;
    private static final int MAX_RECENT_FILES = 10;

    public FileManager(JFrame parent) {
        this.parent = parent;
        this.recentFiles = new ArrayList<>();
        this.prefs = Preferences.userNodeForPackage(FileManager.class);

        // Initialize file chooser with modern settings
        initializeFileChooser();

        // Load recent files from preferences
        loadRecentFiles();
    }

    /***************************************************************
    * METHOD: initializeFileChooser
    * DESCRIPTION: Sets up the modern file chooser with filters and icons
    * PARAMETERS: None
    * RETURN VALUE: None
    ***************************************************************/
    private void initializeFileChooser() {
        fileChooser = new JFileChooser();

        // Set current directory to user's home or project directory
        File currentDir = new File(".");
        fileChooser.setCurrentDirectory(currentDir);

        // Create file filters
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(
            "Text Files (*.txt)", "txt");
        FileNameExtensionFilter drawFilter = new FileNameExtensionFilter(
            "Drawing Programs (*.draw)", "draw");
        FileNameExtensionFilter allFilter = new FileNameExtensionFilter(
            "All Files (*.*)", "*");

        // Add filters
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(drawFilter);
        fileChooser.addChoosableFileFilter(allFilter);
        fileChooser.setFileFilter(txtFilter); // Default filter

        // Set dialog title
        fileChooser.setDialogTitle("Open Drawing Program");

        // Enable multi-selection
        fileChooser.setMultiSelectionEnabled(false);

        // Set accessory panel for preview (optional)
        setupAccessoryPanel();

        // Set custom icons for different file types
        setupFileIcons();
    }

    /***************************************************************
    * METHOD: setupAccessoryPanel
    * DESCRIPTION: Sets up a preview panel for the file chooser
    * PARAMETERS: None
    * RETURN VALUE: None
    ***************************************************************/
    private void setupAccessoryPanel() {
        JPanel accessory = new JPanel(new BorderLayout());
        accessory.setPreferredSize(new Dimension(200, 200));
        accessory.setBorder(BorderFactory.createTitledBorder("Preview"));
        
        JTextArea previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(previewArea);

        accessory.add(scrollPane, BorderLayout.CENTER);

        // Add file selection listener to update preview
        fileChooser.addPropertyChangeListener(evt -> {
            if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                File file = fileChooser.getSelectedFile();
                if (file != null && file.isFile() && file.canRead()) {
                    previewArea.setText(getFilePreview(file));
                } else {
                    previewArea.setText("No preview available");
                }
            }
        });
        fileChooser.setAccessory(accessory);
    }

    /***********************************************************************
    * METHOD: getFilePreview
    * DESCRIPTION: Gets a preview of the file contents
    * PARAMETERS: File file - the file to preview
    * RETURN VALUE: String - preview text
    ***********************************************************************/
    private String getFilePreview(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return "File not found or not readable";
        }
        // Only preview text files
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".txt") && !name.endsWith(".draw") && !name.endsWith(".java")) {
            return "Preview not available for this file type";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder preview = new StringBuilder();
            String line;
            int lineCount = 0;

            preview.append("File: ").append(file.getName()).append("\n");
            preview.append("Size: ").append(file.length()).append(" bytes\n");
            preview.append("Modified: ").append(new java.util.Date(file.lastModified())).append("\n");
            preview.append("\n--- Content Preview ---\n\n");

            while ((line = reader.readLine()) != null && lineCount < 20) {
                preview.append(line).append("\n");
                lineCount++;
            }

            if (lineCount == 20) {
                preview.append("\n... (truncated)\n");
            }

            return preview.toString();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    /***********************************************************************
    * METHOD: setupFileIcons
    * DESCRIPTION: Sets up custom file icons for the file chooser
    * PARAMETERS: None
    * RETURN VALUE: None
    ***********************************************************************/
    private void setupFileIcons() {
        // Get system file system view for icons
        FileSystemView fsv = FileSystemView.getFileSystemView();

        // Create custom file view to override icons
        fileChooser.setFileView(new FileView() {
            @Override
            public Icon getIcon(File f) {
                if (f == null) return null;

                String name = f.getName().toLowerCase();

                // Return custom icons based on file type
                if (f.isDirectory()) {
                    return UIManager.getIcon("FileView.directoryIcon");
                } else if (name.endsWith(".txt")) {
                    return UIManager.getIcon("FileView.fileIcon");
                } else if (name.endsWith(".draw")) {
                    // Custom icon for drawing files
                    return createColorIcon(Color.BLUE, 16, 16);
                } else if (name.endsWith(".java")) {
                    // Custom icon for Java files
                    return createColorIcon(Color.RED, 16, 16);
                }

                return super.getIcon(f);
            }

            @Override
            public String getTypeDescription(File f) {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".draw")) {
                    return "Drawing Program File";
                } else if (name.endsWith(".txt")) {
                    return "Text File";
                }
                return super.getTypeDescription(f);
            }
        });
    }

    /********************************************************************
    * METHOD: createColorIcon
    * DESCRIPTION: Creates a simple colored icon
    * PARAMETERS: Color color - icon color
    *    int width - icon width
    *    int height - icon height
    * RETURN VALUE: ImageIcon - the created icon
    ********************************************************************/
    private ImageIcon createColorIcon(Color color, int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height,
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width-1, height-1);
        g2d.dispose();
        return new ImageIcon(image);
    }

    /********************************************************************
    * METHOD: openFile
    * DESCRIPTION: Opens a file dialog and returns the file content
    * PARAMETERS: None
    * RETURN VALUE: String - file content, or null if cancelled
    ********************************************************************/
    public String openFile() {
        // Set custom dialog title 
        fileChooser.setDialogTitle("Open Drawing Program"); 
        
        int result = fileChooser.showOpenDialog(parent); 
        if (result == JFileChooser.APPROVE_OPTION) { 
            File selectedFile = fileChooser.getSelectedFile(); 
            addToRecentFiles(selectedFile); 
            
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) { 
                StringBuilder sb = new StringBuilder(); 
                String line; 
                while ((line = reader.readLine()) != null) { 
                    sb.append(line).append("\n"); 
                } 
                return sb.toString(); 
            } catch (IOException e) { 
                JOptionPane.showMessageDialog(parent, 
                    "Error reading file: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE); 
            } 
        } 
        return null; 
    } 

    /******************************************************************** 
    * METHOD: openFileWithRecentMenu 
    * DESCRIPTION: Opens a file with recent files menu 
    * PARAMETERS: None 
    * RETURN VALUE: String - file content, or null if cancelled 
    ********************************************************************/ 
    public String openFileWithRecentMenu() { 
        // Create a custom dialog with recent files 
        JDialog dialog = new JDialog(parent, "Open File", true); 
        dialog.setLayout(new BorderLayout()); 
        dialog.setSize(500, 400); 
        dialog.setLocationRelativeTo(parent); 
        
        // Create main panel 
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        // Recent files panel 
        JPanel recentPanel = new JPanel(new BorderLayout()); 
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recent Files")); 
        
        DefaultListModel<File> listModel = new DefaultListModel<>(); 
        for (File file : recentFiles) { 
            if (file.exists()) { 
                listModel.addElement(file);
            }
        }

        JList<File> recentList = new JList<>(listModel);
        recentList.setCellRenderer(new FileListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(recentList);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton browseButton = new JButton("Browse...");
        JButton openButton = new JButton("Open");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(browseButton);
        buttonPanel.add(openButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        mainPanel.add(recentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // Action listeners
        final String[] result = {null};

        browseButton.addActionListener(e -> {
            dialog.dispose();
            result[0] = openFile();
        });

        openButton.addActionListener(e -> {
            File selected = recentList.getSelectedValue();
            if (selected != null) {
                dialog.dispose();
                try {
                    result[0] = readFileContent(selected);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent,
                        "Error reading file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a file or click Browse",
                    "No File Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });

        recentList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    File selected = recentList.getSelectedValue();
                    if (selected != null) {
                        dialog.dispose();
                        try {
                            result[0] = readFileContent(selected);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(parent,
                                "Error reading file: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        dialog.setVisible(true);
        return result[0];
    }

    /*********************************************************************
    * METHOD: saveFile
    * DESCRIPTION: Saves content to a file
    * PARAMETERS: String content - the content to save
    * RETURN VALUE: File - the saved file, or null if cancelled
    *********************************************************************/

    public File saveFile(String content) {
        fileChooser.setDialogTitle("Save Drawing Program");

        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Add .draw extension if not present
            if (!selectedFile.getName().contains(".")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".draw");
            }

            // Check if file exists
            if (selectedFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(parent,
                    "File already exists. Overwrite?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return null;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(content);
                addToRecentFiles(selectedFile);
                return selectedFile;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Error saving file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    /**********************************************************************
    * METHOD: saveFileAs
    * DESCRIPTION: Saves content with "Save As" dialog
    * PARAMETERS: String content - the content to save
    *    String defaultName - default file name
    * RETURN VALUE: File - the saved file, or null if cancelled
    ***********************************************/
    public File saveFileAs(String content, String defaultName) {
        fileChooser.setDialogTitle("Save As");
        fileChooser.setSelectedFile(new File(defaultName));
        return saveFile(content);
    }

    /**********************************************************************
    * METHOD: addToRecentFiles
    * DESCRIPTION: Adds a file to recent files list
    * PARAMETERS: File file - the file to add
    * RETURN VALUE: None
    ***********************************************/
    private void addToRecentFiles(File file) {
        recentFiles.remove(file);
        recentFiles.add(0, file);
        // Keep only the most recent files
        if (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        // Save to preferences 
        saveRecentFiles(); 
    } 

    /******************************************************************** 
    * METHOD: loadRecentFiles 
    * DESCRIPTION: Loads recent files from preferences 
    * PARAMETERS: None 
    * RETURN VALUE: None 
    ********************************************************************/ 
    private void loadRecentFiles() { 
        recentFiles.clear(); 
        for (int i = 0; i < MAX_RECENT_FILES; i++) { 
            String path = prefs.get("recentFile" + i, null); 
            if (path != null) { 
                File file = new File(path); 
                if (file.exists()) { 
                    recentFiles.add(file); 
                } 
            } 
        } 
    } 

    /******************************************************************** 
    * METHOD: saveRecentFiles 
    * DESCRIPTION: Saves recent files to preferences 
    * PARAMETERS: None 
    * RETURN VALUE: None 
    ********************************************************************/ 
    private void saveRecentFiles() { 
        for (int i = 0; i < MAX_RECENT_FILES; i++) { 
            if (i < recentFiles.size()) { 
                prefs.put("recentFile" + i, recentFiles.get(i).getAbsolutePath()); 
            } else { 
                prefs.remove("recentFile" + i); 
            } 
        } 
    } 

    /******************************************************************** 
    * METHOD: readFileContent 
    * DESCRIPTION: Reads content from a file 
    * PARAMETERS: File file - the file to read 
    * RETURN VALUE: String - file content 
    * THROWS: IOException - if reading fails 
    ********************************************************************/ 
    private String readFileContent(File file) throws IOException { 
        StringBuilder sb = new StringBuilder(); 
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
    }

    /*******************************************************************************
    * METHOD: getRecentFilesMenu
    * DESCRIPTION: Creates a JMenu with recent files
    * PARAMETERS: None
    * RETURN VALUE: JMenu - menu with recent files
    *******************************************************************************/

    public JMenu getRecentFilesMenu() {
        JMenu recentMenu = new JMenu("Recent Files");

        if (recentFiles.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("No recent files");
            emptyItem.setEnabled(false);
            recentMenu.add(emptyItem);
        } else {
            for (File file : recentFiles) {
                if (file.exists()) {
                    JMenuItem item = new JMenuItem(file.getName());
                    item.setToolTipText(file.getAbsolutePath());
                    item.addActionListener(e -> {
                        try {
                            String content = readFileContent(file);
                            if (parent instanceof UserInterface3) {
                                ((UserInterface3) parent).loadProgramIntoEditor(content);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(parent,
                                "Error reading file: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    recentMenu.add(item);
                }
            }

            recentMenu.addSeparator();
            JMenuItem clearItem = new JMenuItem("Clear Recent Files");
            clearItem.addActionListener(e -> {
                recentFiles.clear();
                saveRecentFiles();
            });
            recentMenu.add(clearItem);
        }

        return recentMenu;
    }

    /********************************************************************
    * METHOD: getRecentFiles
    * DESCRIPTION: Gets the list of recent files
    * PARAMETERS: None
    * RETURN VALUE: List<File> - recent files
    ********************************************************************/
    public List<File> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }

    /**
    * Custom cell renderer for file list
    */
    private class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof File) {
                File file = (File) value;
                setText(file.getName());
                setToolTipText(file.getAbsolutePath());

                // Set icon based on file type
                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                if (icon != null) {
                    setIcon(icon);
                }
            }
            return this;
        }
    }
}