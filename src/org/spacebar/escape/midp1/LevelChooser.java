/*
 * Created on Dec 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.IOException;

import javax.microedition.lcdui.*;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Continuation;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.Misc;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LevelChooser extends List implements Continuation, CommandListener {
    public final static String levels[] = { "/lev151.esx", "/lev152.esx",
            "/lev153.esx", "/lev202.esx", "/lev155.esx", "/lev156.esx",
            "/lev203.esx", "/lev204.esx", "/lev205.esx", "/lev160.esx",
            "/lev161.esx", "/lev162.esx", "/lev163.esx" };

    final Escape theApp;

    final Continuation theWayOut;

    LevelChooser(Escape app, Continuation c) {
        super("Select Level", IMPLICIT);

        this.theApp = app;
        this.theWayOut = c;

        for (int i = 0; i < levels.length; i++) {
            try {
                BitInputStream in = new BitInputStream(getClass()
                        .getResourceAsStream(levels[i]));
                Level.MetaData m = Level.getMetaData(in);

                append(m.title + " by " + m.author, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addCommand(Escape.EXIT_COMMAND);

        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == SELECT_COMMAND) {
            int i = getSelectedIndex();
            try {
                byte theLevel[] = Misc
                        .getByteArrayFromInputStream(getClass()
                                .getResourceAsStream(levels[i]));
                Display.getDisplay(theApp).setCurrent(
                        new EscapeCanvas(theLevel, theApp,
                                LevelChooser.this));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (c == Escape.EXIT_COMMAND) {
            theWayOut.invoke();
        }
    }
    public void invoke() {
        Display.getDisplay(theApp).setCurrent(this);
    }
}