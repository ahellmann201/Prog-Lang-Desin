package finalProject;

import javax.swing.*;
import java.io.*;

/**
 * Handles File I/O.
 * Assignment 2 requirement: Read files using FileIO commands.
 */
public class FileManager {
    private JFrame parent;
    private JFileChooser fileChooser;

    public FileManager(JFrame parent) {
        this.parent = parent;
        this.fileChooser = new JFileChooser();
        this.fileChooser.setCurrentDirectory(new File(".")); // Start in current folder
    }

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