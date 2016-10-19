package ab.text.editor;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class Save {

    private String filePath;
    JFileChooser chooser;
    String text;
    boolean unresolved;

    public void saveFile(JFrame parent, String inputText, int saveMethod) {

        // Boolean for while loop
        unresolved = true;
        
        // Text to be saved
        text = inputText;
        
        // File chooser
        chooser = new JFileChooser();
        
        // txt files are what is saved
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT files", "txt");
        chooser.setFileFilter(filter);
        
        // While loop ensures the save dialog doesn't close prematurely
        while (unresolved) {
            int returnVal = chooser.showSaveDialog(parent);

            // If "save" is pressed
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                // Get the chosen path
                try {
                    filePath = chooser.getSelectedFile().getCanonicalPath();

                    // Make file objects to test for the file's existence
                    File testFile = new File(filePath);
                    File testFileWithExtension = new File(filePath + ".txt");

                    // If the save method is overwrite with prompt and the
                    // file already exists, promt the user
                    if ((testFile.exists() | testFileWithExtension.exists()) && 
                            saveMethod == 1) {
                        int result = JOptionPane.showConfirmDialog(null, "Are "
                                + "you sure you want\nto save over the original"
                                + " file?", "File Overwrite",
                                JOptionPane.INFORMATION_MESSAGE);
                        // Overwrite the file if "yes" is chosen
                        if (result == 0) {
                            writeFile(filePath);
                            unresolved = false;
                            // Return to the dialog if "no" is chosen
                        } else if (result == 1) {
                            // Close all dialogs if "cancel" is chosen
                        } else if (result == 2) {
                            unresolved = false;
                        }
                        
                        // If the save method is "do not allow overwrites"
                        // prompt the user when the file exists
                    } else if ((testFile.exists() |
                            testFileWithExtension.exists()) && saveMethod == 2)
                    {
                        JOptionPane.showMessageDialog(null, "That file already"
                                + " exists!\n Pick a different name.");
                    } else {
                        writeFile(filePath);
                        unresolved = false;
                    }
                } catch (IOException ex) {
                }
                // If the "cancel" button is selected, exit the loop
                // (close the dialog)
            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                unresolved = false;
            }
        }

    }

    
    // Method to write to file
    
    private void writeFile(String path) {
        FileWriter fw = null;
        try {

            // Make sure .txt appending isn't redundant
            if (filePath.endsWith(".txt")) {
                fw = new FileWriter(path);
                System.out.println("trying to write " + text + " to " + filePath);
                fw.write(text);

            } else {
                fw = new FileWriter(path + ".txt");
                System.out.println("Trying to write " + text);
                fw.write(text);
            }
            unresolved = false;
        } catch (IOException ex) {
            System.out.println("There was a problem:");
            System.out.println(ex.getMessage());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
