/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import javax.sound.sampled.Clip;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NESEffects implements Effects {

    private static final Clip broken = ResourceUtils.loadClip("nes/broken.wav");

    private static final Clip electricOff = ResourceUtils
            .loadClip("nes/electric-off.wav");

    private static final Clip exit = ResourceUtils.loadClip("nes/exit.wav");

    private static final Clip hole = ResourceUtils.loadClip("nes/hole.wav");

    private static final Clip laser = ResourceUtils.loadClip("nes/laser.wav");

    private static final Clip noStep = ResourceUtils
            .loadClip("nes/no-step.wav");

    private static final Clip pulse = ResourceUtils.loadClip("nes/pulse.wav");

    private static final Clip slide = ResourceUtils.loadClip("nes/slide.wav");

    private static final Clip step = ResourceUtils.loadClip("nes/step.wav");

    private static final Clip swap = ResourceUtils.loadClip("nes/swap.wav");

    private static final Clip transport = ResourceUtils
            .loadClip("nes/transport.wav");

    private static final Clip zap = ResourceUtils.loadClip("nes/zap.wav");

    private void rewindAndPlay(Clip c) {
        System.out.println("playing " + c);
        c.setFramePosition(0);
        c.loop(0);
    }
    
    public void doBroken() {
        rewindAndPlay(broken);
    }

    public void doElectricOff() {
        rewindAndPlay(electricOff);
    }

    public void doExit() {
        rewindAndPlay(exit);
    }

    public void doHole() {
        rewindAndPlay(hole);
    }

    public void doLaser() {
        rewindAndPlay(laser);
    }

    public void doNoStep() {
        rewindAndPlay(noStep);
    }

    public void doPulse() {
        rewindAndPlay(pulse);
    }

    public void doSlide() {
        rewindAndPlay(slide);
    }

    public void doStep() {
        rewindAndPlay(step);
    }

    public void doSwap() {
        rewindAndPlay(swap);
    }

    public void doTransport() {
        rewindAndPlay(transport);
    }

    public void doZap() {
        rewindAndPlay(zap);
    }
}