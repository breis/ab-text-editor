package ab.text.editor;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class Clipboarder {

    public void toClipboard(String s) {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(s);
        clipboard.setContents(strSel, null);
    }

    public String fromClipboard() {

        //TODO: add notification if clipboard contents not text
        String s = null;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            //Make sure the clipboard has a string in it
            s = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException ex) {
            //If the clipboard doesn't have a string in it
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            //if there is another issue
            System.out.println(ex.getMessage());
        }

        return s;
    }

}
