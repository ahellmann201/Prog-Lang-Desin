package finalProject;

/*******************************************************************
* Name of program: FileManager
* PROGRAMMER: Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignment 2 (IDE Interface)
*
* DESCRIPTION:
* This class handles the file Input/Output operations for the IDE.
* It uses JFileChooser to allow the user to browse for files to 
* open or select a location to save files.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Assisted by Artificial Intelligence.
*******************************************************************/

import javax.swing.*;
import java.io.*;

public class FileManager {
    private JFrame parent;
    private JFileChooser fileChooser;

    /**********************************************************
    * METHOD: FileManager (Constructor)
    * DESCRIPTION: Initializes the file manager with a parent frame
    * for dialogs.
    * PARAMETERS: JFrame parent - The main window frame
    * RETURN VALUE: N/A
    **********************************************************/
    public FileManager(JFrame parent) {
        this.parent = parent;
        this.fileChooser = new JFileChooser();
        this.fileChooser.setCurrentDirectory(new File(".")); 
    }

    /**********************************************************
    * METHOD: openFile
    * DESCRIPTION: Opens a file dialog and reads the selected file.
    * PARAMETERS: None
    * RETURN VALUE: String - The content of the file, or null if cancelled/error
    **********************************************************/
    public String openFile() {
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error reading file: " + e.getMessage());
            }
        }
        return null;
    }

    /**********************************************************
    * METHOD: saveFile
    * DESCRIPTION: Opens a save dialog and writes content to the selected file.
    * PARAMETERS: String content - The text to write to the file
    * RETURN VALUE: void
    **********************************************************/
    public void saveFile(String content) {
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(content);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error saving file: " + e.getMessage());
            }
        }
    }
}