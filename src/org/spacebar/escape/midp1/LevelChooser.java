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
public class LevelChooser extends List implements Continuation {
    public final static String levels[] = { "/lev203.esx", "/lev153.esx" };

    final Escape theApp;

    final Continuation theWayOut;

    LevelChooser(Escape app, Continuation c) {
        super("Select A Delicious Level", IMPLICIT);

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

        setCommandListener(new CommandListener() {
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
        });
    }

    public void invoke() {
        Display.getDisplay(theApp).setCurrent(this);
    }
}