package ab.text.editor;

import java.io.IOException;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class Open {

    public String fileText;
    private String filePath;

    public void openFile(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT files", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                filePath = chooser.getSelectedFile().getCanonicalPath();
                try {
                    fileText = new String(Files.readAllBytes(Paths.get(filePath)));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
}
