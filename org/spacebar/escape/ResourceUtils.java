/*
 * Created on Dec 20, 2004
 *
 */
package org.spacebar.escape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * @author adam
 */
public class ResourceUtils {
    private final static Mixer mixer = AudioSystem.getMixer(null);

    public static InputStream getLocalResourceAsStream(String name) {
        return ResourceUtils.class.getResourceAsStream("resources/" + name);
    }

    public static Clip loadClip(String name) {
        System.out.print("loading " + name + "...");
        System.out.flush();

        InputStream in = getLocalResourceAsStream(name);

        Clip clip = null;
        try {
            AudioInputStream a = AudioSystem.getAudioInputStream(in);
            DataLine.Info dlInfo = new DataLine.Info(Clip.class, a.getFormat());

            clip = (Clip) mixer.getLine(dlInfo);
            //            clip = (Clip) AudioSystem.getLine(dlInfo);
            System.out.print(" " + clip.getLineInfo() + "...");
            System.out.flush();
            clip.open(a);

            System.out.println(" success!");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clip;
    }

    public static BufferedImage[] loadScaledImages(String name, int scales) {
        if (scales < 1) {
            throw new IllegalArgumentException("scales must be > 0");
        }
        BufferedImage imgs[] = new BufferedImage[scales];
        imgs[0] = loadImage(name);
        for (int i = 1; i < scales; i++) {
            BufferedImage img = imgs[i - 1];
            int w = img.getWidth() >> 1;
            int h = img.getHeight() >> 1;

            imgs[i] = createCompatibleImage(w, h, img.getTransparency());

            Graphics2D g = imgs[i].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.scale(0.5, 0.5);
            g.drawImage(img, 0, 0, null);
            g.dispose();
        }

        return imgs;
    }

    public static BufferedImage loadImage(String name) {
        InputStream in = ResourceUtils.getLocalResourceAsStream(name);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage img2 = createCompatibleImage(img.getWidth(), img
                .getHeight(), img.getTransparency());

        Graphics2D g = img2.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        System.out.println(img2);

        return img2;
    }

    private static BufferedImage createCompatibleImage(int width, int height,
            int transparency) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage img = gc.createCompatibleImage(width, height,
                transparency);
        return img;
    }
}