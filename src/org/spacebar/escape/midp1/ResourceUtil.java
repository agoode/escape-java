/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ResourceUtil {
    static public Image loadImage(String name) {
        try {
            return Image.createImage(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public Image[] loadImages(String prefix, byte n, String suffix) {
        Image imgs[] = new Image[n];
        for (int i = 0; i < n; i++) {
            imgs[i] = loadImage(prefix + i + suffix);
        }
        return imgs;
    }

    static public Image[] unpackTiles(Image img, int tileW, int tileH, int n,
            int stride) {
        Image result[] = new Image[n];

        for (int i = n - 1; i >= 0; i--) {
            Image temp = Image.createImage(tileW, tileH);
            Graphics g = temp.getGraphics();

            int x = (i % stride) * tileW;
            int y = (i / stride) * tileH;
            g.drawImage(img, -x, -y, Graphics.LEFT | Graphics.TOP);
            result[i] = Image.createImage(temp);
        }

        return result;
    }
}