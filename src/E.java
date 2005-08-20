
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.spacebar.escape.common.Continuation;
import org.spacebar.escape.midp1.LevelChooser;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class E extends MIDlet {
    static {
        System.out.println("Loading Escape...");
    }

    final Display display = Display.getDisplay(this);

    private LevelChooser lc = new LevelChooser(this, new Continuation() {
        public void invoke() {
            notifyDestroyed();
        }
    });

    public void startApp() throws MIDletStateChangeException {
        System.out.println("Escape startApp() called");
        // display.setCurrent(canvas);
        displayLevelChooser();
    }

    public void pauseApp() {

    }

    public void destroyApp(boolean unconditional)
            throws MIDletStateChangeException {
        display.setCurrent(null);
    }

    public void displayLevelChooser() {
        display.setCurrent(lc);
    }
}