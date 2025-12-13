package finalProject;

/*******************************************************************
* Name of program: Graphical IDE (SimpleIDE)
* PROGRAMMER:Tanner Sweigart & Olivia Hornbeck
* COURSE: CS340 Programming Languages
* DATE: December 13, 2025
* REQUIREMENT: Assignment 2 (IDE Interface), Final Project
*
* DESCRIPTION:
* This file serves as the main entry point for the application.
* It initializes the Swing UI Look and Feel and launches the 
* main IDEWindow on the Event Dispatch Thread to ensure 
* thread safety for the GUI.
*
* COPYRIGHT:
* This code is copyright (c)2025 Tanner Sweigart, Olivia Hornbeck and Dean Zeller.
*
* CREDITS:
* Concepts derived from course lectures.
* Code structure assisted by Artificial Intelligence.
*******************************************************************/

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SimpleIDE {

    /**********************************************************
    * METHOD: main
    * DESCRIPTION: The main method that launches the application.
    * PARAMETERS: String[] args - Command line arguments
    * RETURN VALUE: void
    **********************************************************/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            IDEWindow window = new IDEWindow();
            window.setVisible(true);
        });
    }
}