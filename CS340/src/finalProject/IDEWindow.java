package finalProject;

import javax.swing.*;
import java.awt.*;

/**
 * The User Interface.
 * Updated to support Threading for Animation.
 */
public class IDEWindow extends JFrame {

    private JTextArea codeEditor;
    private JTextArea consoleOutput;
    private FileManager fileManager;
    private Interpreter interpreter;

    public IDEWindow() {
        setTitle("CS340 Language IDE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        fileManager = new FileManager(this);
        interpreter = new Interpreter(this);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Toolbar ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnOpen = new JButton("Open");
        JButton btnSave = new JButton("Save");
        JButton btnRun = new JButton("Run / Execute");
        
        btnOpen.addActionListener(e -> openFile());
        btnSave.addActionListener(e -> saveFile());
        btnRun.addActionListener(e -> runProgram());

        toolBar.add(btnOpen);
        toolBar.add(btnSave);
        toolBar.addSeparator();
        toolBar.add(btnRun);

        add(toolBar, BorderLayout.NORTH);

        // --- Editor & Console ---
        codeEditor = new JTextArea();
        codeEditor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane codeScroll = new JScrollPane(codeEditor);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Source Code (.dlz)"));

        consoleOutput = new JTextArea();
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleOutput.setBackground(new Color(230, 230, 230));
        consoleOutput.setEditable(false);
        JScrollPane consoleScroll = new JScrollPane(consoleOutput);
        consoleScroll.setBorder(BorderFactory.createTitledBorder("Console / Trace Output"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, consoleScroll);
        splitPane.setResizeWeight(0.7); 
        add(splitPane, BorderLayout.CENTER);
    }

    private void runProgram() {
        consoleOutput.setText(""); 
        String sourceCode = codeEditor.getText();
        
        consoleOutput.append("--- Starting Background Execution ---\n");
        
        // Run Interpreter in a separate Thread to allow Animation (sleep)
        new Thread(() -> {
            interpreter.execute(sourceCode);
            printToConsole("\n--- Execution Finished ---");
        }).start();
    }

    private void openFile() {
        String content = fileManager.openFile();
        if (content != null) codeEditor.setText(content);
    }

    private void saveFile() {
        fileManager.saveFile(codeEditor.getText());
    }

    // Thread-safe console printing
    public void printToConsole(String text) {
        SwingUtilities.invokeLater(() -> consoleOutput.append(text + "\n"));
    }
}