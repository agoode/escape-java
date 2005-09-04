package org.spacebar.escape.midp1;

import java.io.IOException;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

import org.spacebar.escape.common.Level;

public class Backgrounder implements Runnable {

    static private final int TASK_LOAD_LEVEL = 1;
    
    static private final int TASK_PLAY_ALERT = 2;

    final EscapeCanvas canvas;

    final AlertType alert;
    
    final int task;
    
    final Display display;

    private Backgrounder(EscapeCanvas e) {
        canvas = e;
        task = TASK_LOAD_LEVEL;
        alert = null;
        display = null;
    }

    private Backgrounder(Display d, AlertType a) {
        canvas = null;
        task = TASK_PLAY_ALERT;
        alert = a;
        display = d;
    }
    
    public static Runnable makeLoadLevelTask(EscapeCanvas canvas) {
        return new Backgrounder(canvas);
    }
    
    public static Runnable makeAlertPlayTask(Display d, AlertType a) {
        return new Backgrounder(d, a);
    }
    
    public void run() {
        switch (task) {
        case TASK_LOAD_LEVEL:
            canvas.theLevel = null;
            canvas.repaint();
            canvas.serviceRepaints();
            
            try {
                canvas.theLevel = new Level(canvas.levelStream);
                canvas.theLevel.trackDirty(true);
                canvas.levelStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            canvas.updateScroll();
            canvas.repaint();
            
            break;

        case TASK_PLAY_ALERT:
            alert.playSound(display);
            break;
            
        default:
            break;
        }
    }
}
