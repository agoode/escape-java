/*
 * Created on Dec 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import org.spacebar.escape.common.Continuation;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.LevelPack;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LevelChooser extends List implements Continuation, CommandListener {
    public final static String levelPacks[] = { "/t.p" };

    final MIDlet theApp;

    final Continuation theWayOut;

    LevelPack levels;
    
    
    public LevelChooser(MIDlet app, Continuation c) {
        super("Select Level", IMPLICIT);

        // System.out.println("Initializing level chooser...");

        this.theApp = app;
        this.theWayOut = c;

        try {
            levels = new LevelPack(levelPacks[0]);
            Level.MetaData m;
            while ((m = levels.getNextLevelMetaData()) != null) {
                append(m.title + " by " + m.author, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addCommand(EscapeCanvas.EXIT_COMMAND);

        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == SELECT_COMMAND) {
            int i = getSelectedIndex();
            try {
                levels.reset();
                levels.skipLevels(i);
                byte theLevel[] = levels.getNextLevelData();
                Display.getDisplay(theApp).setCurrent(
                        new EscapeCanvas(theLevel, theApp, LevelChooser.this));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (c == EscapeCanvas.EXIT_COMMAND) {
            theWayOut.invoke();
        }
    }

    public void invoke() {
        Display.getDisplay(theApp).setCurrent(this);
    }
}