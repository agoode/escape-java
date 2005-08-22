package org.spacebar.escape.common;


/**
 * @author adam
 */
public class TextEffects implements Effects {

    public void doBroken() {
        System.out.println("broken");
    }

    public void doElectricOff() {
        System.out.println("electric off");
    }

    public void doExit() {
        System.out.println("exit");
    }

    public void doHole() {
        System.out.println("hole");
    }

    public void doLaser() {
        System.out.println("laser");
    }

    public void doNoStep() {
        System.out.println("no step");
    }

    public void doPulse() {
        System.out.println("pulse");
    }

    public void doSlide() {
        System.out.println("slide");
    }

    public void doStep() {
        System.out.println("step");
    }

    public void doSwap() {
        System.out.println("swap");
    }

    public void doTransport() {
        System.out.println("transport");
    }

    public void doZap() {
        System.out.println("zap");
    }

    public void requestRedraw() {
        System.out.println("redraw");
    }
}
