/*
 * Created on Apr 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.j2se;

import java.awt.Frame;

import org.spacebar.escape.common.Effects;
import org.spacebar.escape.common.Level;

/**
 * @author agoode
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DrawnLevel extends Level {

    Frame f;
    LevelCanvas lc;
    
    public DrawnLevel(Level l) {
        super(l);
        
        f = new Frame("\"" + getTitle() + "\" by " + getAuthor());
        f.setSize(800, 600);
        
        lc = new LevelCanvas(this);
        f.add(lc);
        
        f.setVisible(true);
    }
    
    public boolean move(int d) {
        boolean r = super.move(d);
        afterMove();
        return r;
    }
    
    public boolean move(int d, Effects e) {
        boolean r = super.move(d, e);
        afterMove();
        return r;
    }
    
    private void afterMove() {
        lc.bufferRepaint();
    }
    
    public void dispose() {
        f.dispose();
    }
}
