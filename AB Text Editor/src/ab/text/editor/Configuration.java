package ab.text.editor;

import java.io.*;
import javax.swing.JOptionPane;

/*
   The configuration class holds data inputted from the config file
   If the config file is missing or corrupt, default configuration
   values are used.
*/

public class Configuration {

    // Configuration values
    boolean newSelected;
    boolean openSelected;
    boolean saveSelected;
    boolean cutSelected;
    boolean copySelected;
    boolean pasteSelected;
    boolean findSelected;
    boolean printSelected;
    boolean exitSelected;
    boolean spellCheckSelected;
    int saveMethodSelected;

    // Default constructor
    public Configuration() {
        setValues();
    }

    // Default values in case the config file is lost
    private void setDefaultValues() {
        newSelected = true;
        openSelected = true;
        saveSelected = true;
        cutSelected = true;
        copySelected = true;
        pasteSelected = true;
        findSelected = true;
        printSelected = true;
        exitSelected = true;
        spellCheckSelected = true;
        saveMethodSelected = 0;
    }

    /*
      The config file outputs a series of numbers to a text file
      indicated whether features should be turned off or on.
      This method is clunky, but it reads through the config file
      and enables or disables features based on the value.
    */
    private boolean setValues() {
        setDefaultValues();
        try (FileReader configReader = new FileReader("config")) {
            if (configReader.read() == 48) {
                newSelected = false;
            }
            if (configReader.read() == 48) {
                openSelected = false;
            }
            if (configReader.read() == 48) {
                saveSelected = false;
            }
            if (configReader.read() == 48) {
                cutSelected = false;
            }
            if (configReader.read() == 48) {
                copySelected = false;
            }
            if (configReader.read() == 48) {
                pasteSelected = false;
            }
            if (configReader.read() == 48) {
                findSelected = false;
            }
            if (configReader.read() == 48) {
                printSelected = false;
            }
            if (configReader.read() == 48) {
                exitSelected = false;
            }
            if (configReader.read() == 48) {
                spellCheckSelected = false;
            }
            int temp = configReader.read();
            if (temp == 48) {
                saveMethodSelected = 0;
            } else if (temp == 49) {
                saveMethodSelected = 1;
            } else {
                saveMethodSelected = 2;
            }
            configReader.close();
            return true;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Configuration file not found!\n"
                    + "Using default configuration.");
            return false;
        } catch (IOException ex) {
            return false;
        }

    }
}
