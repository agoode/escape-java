/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
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
        getContentPane().setBackground(Color.BLACK);
        getRootPane().setDoubleBuffered(false);

        Insets insets;
        insets = getInsets();
        setSize(new Dimension(STARTW + insets.left + insets.right, STARTH
                + insets.top + insets.bottom));
        setVisible(true);
        insets = getInsets();
        setSize(new Dimension(STARTW + insets.left + insets.right, STARTH
                + insets.top + insets.bottom));

        PlayCanvas pc = new PlayCanvas(f);
        setContentPane(pc);
        validate();
    }

    public static void main(String[] args) {
        new EscapeMain(new File(args[0]));
    }
}