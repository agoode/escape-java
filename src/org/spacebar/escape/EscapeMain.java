/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeMain extends Frame {

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

        final PlayCanvas pc = new PlayCanvas(f);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // simulate press of ESCAPE
                KeyEvent ke = new KeyEvent(EscapeMain.this,
                        KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                        KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
                pc.dispatchEvent(ke);
            }
        });

        add(pc);
        pc.setPreferredSize(new Dimension(STARTW, STARTH));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new EscapeMain(new File(args[0]));
    }
}