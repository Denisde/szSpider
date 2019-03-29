/*
 * Created on 2004-12-17
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.common;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PageOpenTimer {

	private final Timer timer = new Timer();
    private final int seconds;

    public PageOpenTimer(int seconds) {
        this.seconds = seconds;
    }

    public void start() {
        timer.schedule(new TimerTask() {
            public void run() {
            	timer.cancel();
            }
        }, seconds * 1000);
    }
    
	public static void main(String[] args) {
	}
}
