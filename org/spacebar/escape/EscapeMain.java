/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

import javax.swing.JFrame;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EscapeMain extends JFrame {

    public final static int STARTW = 800;
    public final static int STARTH = 600;
    
    public static void main(String[] args) {
        EscapeMain m = new EscapeMain();
        m.setVisible(true);
        URL laser = EscapeMain.class.getClassLoader().getResource("org/spacebar/escape/resources/laser.wav");
        AudioClip a = Applet.newAudioClip(laser);

        System.out.println("Lasered!");
        a.play();
    }
    
    public EscapeMain() {
        super("Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(STARTW, STARTH);
    }
}
