/*
 * Created on Dec 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

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
			"/lev161.esx", "/lev162.esx", "/lev163.esx", "/lev367.esx",
			"/lev410.esx", "/lev411.esx", "/lev412.esx", "/lev575.esx",
			"/lev665.esx", "/lev750.esx"};

	final MIDlet theApp;

	final Continuation theWayOut;

	public LevelChooser(MIDlet app, Continuation c) {
		super("Select Level", IMPLICIT);

		System.out.println("Initializing level chooser...");

		this.theApp = app;
		this.theWayOut = c;

		for (int i = 0; i < levels.length; i++) {
			System.out.println("loading level " + levels[i] + "...");
			try {
				System.out.println("getting resource...");
				InputStream lev = getClass().getResourceAsStream(levels[i]);
				System.out.println("initializing BitInputStream...");
				BitInputStream in = new BitInputStream(lev);
				System.out.println("loading metadata...");
				Level.MetaData m = Level.getMetaData(in);

				append(m.title + " by " + m.author, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		addCommand(EscapeCanvas.EXIT_COMMAND);

		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == SELECT_COMMAND) {
			int i = getSelectedIndex();
			try {
				byte theLevel[] = Misc.getByteArrayFromInputStream(getClass()
						.getResourceAsStream(levels[i]));
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