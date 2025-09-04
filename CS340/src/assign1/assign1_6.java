package assign1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class assign1_6 extends JFrame {
    private JTextArea historyArea;
    private JTextField inputField;
    private List<String> messageHistory;
    private SimpleDateFormat dateFormat;
    private int messageCount = 0;

    public assign1_6() {
        messageHistory = new ArrayList<>();
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        initializeUI();
        loadHistory();
    }

    private void initializeUI() {
        setTitle("Advanced Message App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 
                                                          getWidth(), getHeight(), new Color(230, 230, 250));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // History area with styling
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historyArea.setBackground(new Color(255, 255, 240));
        historyArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("💬 Message History"));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createTitledBorder("✏️ Type your message (Press Enter to send)"));
        
        JButton sendButton = new JButton("🚀 Send");
        sendButton.setPreferredSize(new Dimension(100, 35));
        sendButton.setBackground(new Color(100, 149, 237));
        sendButton.setForeground(Color.WHITE);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Toolbar with buttons
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton clearButton = createToolbarButton("🗑️ Clear", "Clear history");
        JButton saveButton = createToolbarButton("💾 Save", "Save to file");
        JButton loadButton = createToolbarButton("📂 Load", "Load from file");
        JButton exitButton = createToolbarButton("🚪 Exit", "Exit application");
        
        toolBar.add(clearButton);
        toolBar.add(saveButton);
        toolBar.add(loadButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(exitButton);

        // Add components to main panel
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        
        clearButton.addActionListener(e -> clearHistory());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        exitButton.addActionListener(e -> exitApplication());

        add(mainPanel);
    }

    private JButton createToolbarButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            messageCount++;
            String timestamp = dateFormat.format(new Date());
            String formattedMessage = String.format("[%s] #%03d: %s", timestamp, messageCount, message);
            
            messageHistory.add(formattedMessage);
            historyArea.append(formattedMessage + "\n");
            inputField.setText("");
            
            // Auto-scroll to bottom
            historyArea.setCaretPosition(historyArea.getDocument().getLength());
            
            // Save after each message
            saveHistory();
        }
    }

    private void clearHistory() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all message history?",
            "Clear History",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            messageHistory.clear();
            historyArea.setText("");
            messageCount = 0;
            historyArea.append("[" + dateFormat.format(new Date()) + "] History cleared. Ready for new messages...\n");
            saveHistory();
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Messages");
        fileChooser.setSelectedFile(new File("messages.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                for (String message : messageHistory) {
                    writer.println(message);
                }
                JOptionPane.showMessageDialog(this, "Messages saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Messages");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                messageHistory.clear();
                historyArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    messageHistory.add(line);
                    historyArea.append(line + "\n");
                    messageCount++;
                }
                JOptionPane.showMessageDialog(this, "Messages loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveHistory() {
        try (PrintWriter writer = new PrintWriter("autosave.txt")) {
            for (String message : messageHistory) {
                writer.println(message);
            }
        } catch (IOException e) {
            // Silent fail for autosave
        }
    }

    private void loadHistory() {
        File file = new File("autosave.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    messageHistory.add(line);
                    historyArea.append(line + "\n");
                    if (line.contains("#")) {
                        messageCount++;
                    }
                }
            } catch (IOException e) {
                // Silent fail for autoload
            }
        }
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            saveHistory();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            assign1_6 app = new assign1_6();
            app.setVisible(true);
            app.inputField.requestFocus();
        });
    }
}