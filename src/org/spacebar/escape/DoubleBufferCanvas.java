/*
 * Created on Dec 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
abstract public class DoubleBufferCanvas extends JComponent {

    private BufferedImage backBuffer;
    
    final private Frame theFrame;
    final private BufferStrategy strategy;

    DoubleBufferCanvas(Frame f) {
        super();
        setBackground(Color.BLACK);

        theFrame = f;
        
        if (f != null) {
            System.out.println("using active rendering");
            ImageCapabilities ic = f.getGraphicsConfiguration()
                    .getImageCapabilities();
            BufferCapabilities bc = new BufferCapabilities(ic, ic, null);
            try {
                f.createBufferStrategy(2, bc);
            } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            strategy = f.getBufferStrategy();
            System.out.println("page flip: "
                    + strategy.getCapabilities().isPageFlipping());
        } else {
            System.out.println("using passive rendering");
            strategy = null;
        }

        setOpaque(true);
    }

    abstract protected void bufferPaint(Graphics2D g);

    public void bufferRepaint() {
        if (strategy != null) {
            // active
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            Insets in = theFrame.getInsets();
            g.translate(in.left, in.top);
            do {
                bufferPaint(g);
            } while (strategy.contentsLost());
            g.dispose();

            //            System.out.println("strategy.show()");
            strategy.show();
        } else {
            // passive
            if (backBuffer == null) {
                return;
            } else {
                Graphics2D g = backBuffer.createGraphics();
                bufferPaint(g);
                g.dispose();
                repaint();
            }
        }
    }

    private void initBackBuffer() {
        System.out.println("*** initializing back buffer");
        backBuffer = (BufferedImage) createImage(getWidth(), getHeight());
        //        System.out.println("backBuffer: " + backBuffer);
    }

    protected void paintComponent(Graphics g) {
        if (strategy != null) {
            // active
            if (strategy.contentsLost()) {
                bufferRepaint();
            } else {
                strategy.show();
            }
        } else {
            // passive
            if (backBuffer == null || backBuffer.getHeight() != getHeight()
                    || backBuffer.getWidth() != getWidth()) {
                initBackBuffer();
                Graphics2D g2 = backBuffer.createGraphics();
                bufferPaint(g2);
                g2.dispose();
            }

            g.drawImage(backBuffer, 0, 0, this);
        }
    }
}