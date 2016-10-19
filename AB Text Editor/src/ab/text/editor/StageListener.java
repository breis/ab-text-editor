package ab.text.editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


// The stage listener listens for text being inputted and causes
// the parent UI to update the word count

public class StageListener implements KeyListener {

    UserInterface parent;

    public StageListener(UserInterface parent) {
        this.parent = parent;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        parent.textModified = true;
        parent.countWords();
    }

}
