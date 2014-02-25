package com.chaseoes.dbo;

import java.awt.TrayIcon;
import java.util.Timer;
import java.util.TimerTask;

public class Nagger {

    Timer timer;
    TrayIcon trayIcon;
    int seconds;

    public Nagger(TrayIcon trayIcon, int seconds) {
        this.trayIcon = trayIcon;
        this.seconds = seconds;
        
        timer = new Timer();
        timer.schedule(new NagTask(), seconds * 1000);
    }

    class NagTask extends TimerTask {
        public void run() {
            if (!Main.naggerPaused) {
                Main.nag(trayIcon);
                Main.updateCounts(trayIcon);
            }
            timer.schedule(new NagTask(), seconds * 1000);
        }
    }

}
