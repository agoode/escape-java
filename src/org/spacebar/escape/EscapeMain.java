/*
 * Created on Dec 14, 2004
 */
package org.spacebar.escape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.spacebar.escape.common.Misc;

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

        // used to get out of the canvas
        Continuation c = new Continuation() {
            public void invoke() {
                System.exit(0);
            }
        };

        FileInputStream fis;
        byte[] level = null;
        try {
            fis = new FileInputStream(f);
            level = Misc.getByteArrayFromInputStream(fis);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final PlayCanvas pc = new PlayCanvas(level, c);
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