package assign3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/************************************************************************
 * 	  Text File Reader Module											*
 *																		*
 *    PROGRAMMER:    [Olivia, Tanner, Andrew]    						*
 *    COURSE:  CS340 Programming Lang/Design     						*
 *    DATE:    [9/11/2025]    										    *
 *    REQUIREMENT:    Assignment 3    									*
 *																		*
 *    DESCRIPTION:    													*
 *    This module handles reading a text file and tracing each line.	*
 *	  without actually compiling										*
 *																		*
 *    COPYRIGHT:    													*
 *    This code is copyright (c)2025 [Your Name] and Dean Zeller.		*
 *																		*
 *    CREDITS:    														*
 *    Java API Documentation, Course materials							*
 *																		*
 ************************************************************************/

public class TextFileRead extends JFrame {

    private JTextArea traceArea;
    private JTextField inputField;
    private int interactiveLine = 1;
    
    /***************************************************************************
     *    METHOD:    TextFileRead  Constructor                                 *
     *    DESCRIPTION:  Initializes the main application window and sets up    *
     *                  all UI components                                      *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  None                                                  *
     **************************************************************************/
    public TextFileRead() {
        setTitle("Fake Compiler & Interpreter");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel (Interactive Input) ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        JButton executeButton = new JButton("Execute Line");
        topPanel.add(new JLabel("Interactive Input:"), BorderLayout.WEST);
        topPanel.add(inputField, BorderLayout.CENTER);
        topPanel.add(executeButton, BorderLayout.EAST);

        // --- Middle Panel (Load File Button) ---
        JButton loadFileButton = new JButton("Load File");

        // --- Output Area ---
        traceArea = new JTextArea();
        traceArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(traceArea);

        // --- Layout ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(loadFileButton, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        executeButton.addActionListener(e -> executeLine());
        inputField.addActionListener(e -> executeLine()); // Enter key support
        loadFileButton.addActionListener(e -> loadFile());

        setVisible(true);
    }

    /***************************************************************************
     *    METHOD:    executeLine				                               *
     *    DESCRIPTION:  Traces input from user and outputs "Executing -input-  *
     *    now"				   					                               *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  None                                                  *
     **************************************************************************/
    private void executeLine() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            traceArea.append("Executing line " + interactiveLine + ": " + input + "\n");
            interactiveLine++;
            inputField.setText("");
        }
    }

    /**************************************************************************
     *    METHOD:    loadFile					                               *
     *    DESCRIPTION:  lets user choose and load text file into compiler      *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  None                                                  *
     **************************************************************************/
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                traceArea.append("--- Compiling file: " + file.getName() + " ---\n");
                String line;
                int lineNum = 1;
                while ((line = reader.readLine()) != null) {
                    traceArea.append("Line " + lineNum + ": " + line + "\n");
                    traceArea.append("Compiling line " + lineNum + ": " + line + "\n");
                    lineNum++;
                }
                traceArea.append("--- Compilation finished ---\n\n");
            } catch (IOException e) {
                traceArea.append("Error reading file: " + e.getMessage() + "\n");
            }
        }
    }
    
    /***************************************************************************
     *    METHOD:    Main                      						           *
     *    DESCRIPTION:  Launches user interface and utilities in compiler	   *
     *    PARAMETERS:  None                                                    *
     *    RETURN VALUE:  None                                                  *
     **************************************************************************/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextFileRead::new);
    }
}
