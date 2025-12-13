package finalProject;

/*******************************************************************
* Name of program: IDEWindow
* PROGRAMMER: Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignment 2 (IDE Interface)
*
* DESCRIPTION:
* This class defines the main Graphical User Interface (GUI) for the IDE.
* It creates the main window frame, the toolbar with buttons (Open, Save, Run),
* the text editor area for source code, and the console output area.
* It acts as the View in the architecture, delegating logic to the Interpreter.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Assisted by Artificial Intelligence.
*******************************************************************/

import javax.swing.*;
import java.awt.*;

public class IDEWindow extends JFrame {

    private JTextArea codeEditor;
    private JTextArea consoleOutput;
    private FileManager fileManager;
    private Interpreter interpreter;

    /**********************************************************
    * METHOD: IDEWindow (Constructor)
    * DESCRIPTION: Initializes the frame, file manager, and interpreter.
    * Calls initComponents to build the UI.
    * PARAMETERS: None
    * RETURN VALUE: N/A
    **********************************************************/
    public IDEWindow() {
        setTitle("CS340 Language IDE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        fileManager = new FileManager(this);
        interpreter = new Interpreter(this);

        initComponents();
    }

    /**********************************************************
    * METHOD: initComponents
    * DESCRIPTION: Sets up the layout, buttons, text areas, and 
    * scroll panes for the application window.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Toolbar (Top) ---
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

        // --- Split Pane (Center) ---
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

    /**********************************************************
    * METHOD: runProgram
    * DESCRIPTION: Clears the console and triggers the interpreter 
    * to execute the code in the editor.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    private void runProgram() {
        consoleOutput.setText(""); // Clear console
        String sourceCode = codeEditor.getText();
        
        consoleOutput.append("--- Starting Background Execution ---\n");
        
        // Pass code to interpreter
        interpreter.execute(sourceCode);
    }

    /**********************************************************
    * METHOD: openFile
    * DESCRIPTION: Uses the FileManager to load content from a file
    * into the editor.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    private void openFile() {
        String content = fileManager.openFile();
        if (content != null) {
            codeEditor.setText(content);
        }
    }

    /**********************************************************
    * METHOD: saveFile
    * DESCRIPTION: Uses the FileManager to save the current editor
    * content to a file.
    * PARAMETERS: None
    * RETURN VALUE: void
    **********************************************************/
    private void saveFile() {
        fileManager.saveFile(codeEditor.getText());
    }

    /**********************************************************
    * METHOD: printToConsole
    * DESCRIPTION: Appends text to the console output area in a 
    * thread-safe manner.
    * PARAMETERS: String text - The message to print
    * RETURN VALUE: void
    **********************************************************/
    public void printToConsole(String text) {
        SwingUtilities.invokeLater(() -> consoleOutput.append(text + "\n"));
    }
}