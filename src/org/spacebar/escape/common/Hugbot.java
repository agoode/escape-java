/*
 * Created on Mar 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Hugbot extends Bot {

    public Hugbot(int x, int y, int d) {
        super(x, y, d, B_HUGBOT);

        iPushPlayer();
        iPushBots();
    }

    IntPair getDirChoices(Entity e) {
        return getMoveToDirChoices(e);
    }
}
