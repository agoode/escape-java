/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
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
        setBackground(Color.BLACK);
        
        // icon
        InputStream in = ResourceUtils.getLocalResourceAsStream("icon.png");
        try {
            setIconImage(ImageIO.read(in));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(STARTW, STARTH);

        setContentPane(new PlayCanvas(f));
        getRootPane().setDoubleBuffered(false);
    }

    public static void main(String[] args) {
        JFrame m;
        m = new EscapeMain(new File(args[0]));
        m.setVisible(true);
    }
}