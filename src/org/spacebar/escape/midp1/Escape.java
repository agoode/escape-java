/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.spacebar.escape.common.Misc;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Escape extends MIDlet {

    Display display;

    private EscapeCanvas canvas;

    public Escape() {
        display = Display.getDisplay(this);
        byte theLevel[];
        try {
            theLevel = Misc.getByteArrayFromInputStream(getClass()
                    .getResourceAsStream("/lev204.esx"));
            canvas = new EscapeCanvas(theLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startApp() throws MIDletStateChangeException {
        display.setCurrent(canvas);
    }

    public void pauseApp() {

    }

    public void destroyApp(boolean unconditional)
            throws MIDletStateChangeException {
        display.setCurrent(null);
    }

}