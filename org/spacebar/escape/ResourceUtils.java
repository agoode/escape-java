/*
 * Created on Dec 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceUtils {
    public static final String RES_PREFIX = "org/spacebar/escape/resources/";

    public static URL getLocalResource(String name) {
        ClassLoader c = ResourceUtils.class.getClassLoader();
        URL u = c.getResource(RES_PREFIX + name);
        
        return u;
    }

    public static Clip loadClip(String name) {
        URL u = getLocalResource(name);
        
        Clip clip = null;
        try {
            AudioInputStream a = AudioSystem.getAudioInputStream(u);
            clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            clip.open(a);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return clip;
    }
}
