package org.spacebar.escape.common;

public class DefaultEffects implements Effects {

    private final static DefaultEffects d = new DefaultEffects();
    
    protected DefaultEffects() {}
    
    public static Effects getDefaultEffects() {
        return d;
    }
    
    public void doBroken() {
        
    }

    public void doElectricOff() {
        
    }

    public void doExit() {
        
    }

    public void doHole() {
        
    }

    public void doLaser() {
        
    }

    public void doNoStep() {
        
    }

    public void doPulse() {
       
    }

    public void doSlide() {
        
    }

    public void doStep() {
        
    }

    public void doSwap() {
        
    }

    public void doTransport() {
        
    }

    public void doZap() {
        
    }

    public void requestRedraw() {
        
    }
}
