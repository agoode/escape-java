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
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Dalek extends Bot {

    public Dalek(int x, int y, int d) {
        super(x, y, d, B_DALEK);

        iCanTeleport();
        iCrushPlayer();
        iWalkIntoBots();
    }

    IntPair getDirChoices(Entity e) {
        return getMoveToDirChoices(e);
    }
}