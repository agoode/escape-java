/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import org.spacebar.escape.util.BitInputStream;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeMain extends JFrame {

    public final static int STARTW = 800;

    public final static int STARTH = 600;

    public EscapeMain(Level l) {
        super("Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(STARTW, STARTH);
        getContentPane().add(new EscapeLevelView(l));
    }

    public static void main(String[] args) {
        EscapeMain m;
        try {
            m = new EscapeMain(new Level(new BitInputStream(
                    new FileInputStream(new File(args[0])))));
            m.setVisible(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}