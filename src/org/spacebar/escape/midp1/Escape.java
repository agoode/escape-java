/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.spacebar.escape.common.Continuation;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Escape extends MIDlet {
    final static Command EXIT_COMMAND = new Command("Exit", Command.EXIT, 1);
    final static Command BACK_COMMAND = new Command("Back", Command.BACK, 1);

    final static Command RESTART_COMMAND = new Command("Restart",
            Command.SCREEN, 1);

    final Display display = Display.getDisplay(this);

    private LevelChooser lc = new LevelChooser(this, new Continuation() {
        public void invoke() {
            notifyDestroyed();
        }
    });

    public void startApp() throws MIDletStateChangeException {
        System.out.println("Escape startApp() called");
        //        display.setCurrent(canvas);
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