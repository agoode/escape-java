/*
 * Created on Dec 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import org.spacebar.escape.common.Effects;


/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompoundEffects implements Effects {
    static final int INITIAL_SIZE = 2;
    
    int count;
    Effects theEffects[];

    CompoundEffects() {
        clear();  // initalize
    }
    
    public void clear() {
        count = 0;
        theEffects = new Effects[INITIAL_SIZE];
    }
    
    public void add(Effects e) {
        // expand if necessary
        if(count == theEffects.length) {
            Effects[] newE = new Effects[theEffects.length * 2];
            System.arraycopy(theEffects, 0, newE, 0, theEffects.length);
            theEffects = newE;
        }
        
        theEffects[count] = e;
        count++;
    }
    
    public void remove(Effects e) {
        // copy the whole array, because this is infrequent
        Effects old[] = theEffects;
        int oldCount = count;
        
        clear();
        
        for (int i = 0; i < oldCount; i++) {
            Effects item = old[i];
            if (!item.equals(e)) {
                add(item);
            }
        }
    }
    
    public void doBroken() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doBroken();
        }
    }

    public void doElectricOff() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doElectricOff();
        }
    }

    public void doExit() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doExit();
        }
    }

    public void doHole() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doHole();
        }
    }

    public void doLaser() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doLaser();
        }
    }

    public void doNoStep() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doNoStep();
        }
    }

    public void doPulse() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doPulse();
        }
    }

    public void doSlide() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doSlide();
        }
    }

    public void doStep() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doStep();
        }
    }

    public void doSwap() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doSwap();
        }
    }

    public void doTransport() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doTransport();
        }
    }

    public void doZap() {
        for (int i = 0; i < count; i++) {
            theEffects[i].doZap();
        }
    }
}
