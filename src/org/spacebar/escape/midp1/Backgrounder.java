package org.spacebar.escape.midp1;

import java.io.IOException;

import org.spacebar.escape.common.Level;

public class Backgrounder implements Runnable {

    static public final int TASK_LOAD_LEVEL = 1;

    final EscapeCanvas canvas;

    final int task;

    public Backgrounder(EscapeCanvas canvas, int task) {
        this.canvas = canvas;
        this.task = task;
    }

    public void run() {
        switch (task) {
        case TASK_LOAD_LEVEL:
            try {
                canvas.theLevel = new Level(canvas.levelStream);
                canvas.levelStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }

            break;

        default:
            break;
        }
    }
}
