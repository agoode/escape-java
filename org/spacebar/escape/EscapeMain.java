/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.io.File;

import javax.swing.JFrame;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeMain extends JFrame {

    public final static int STARTW = 800;

    public final static int STARTH = 600;

    public EscapeMain(File f) {
        super("Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(STARTW, STARTH);

        getContentPane().add(new EscapeLevelView(f));
    }

    public static void main(String[] args) {
        EscapeMain m;
        m = new EscapeMain(new File(args[0]));
        m.setVisible(true);
    }
}