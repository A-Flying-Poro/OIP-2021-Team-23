package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class GuiProgress {
    private static final int frequency = 1; // number of times to run per second

    private final Timer timer = new Timer("Clock Timer");
    private final Runnable timerAsyncRunnable = () -> {
        System.out.printf("[%s] (%d) > Timer async task running%n", Thread.currentThread().getName(), this.timerRunsLeft);
        updateTimeText();
        boolean lidOpen = InterfaceHelper.checkArduinoLidStatus();
        System.out.println("Lid open: " + lidOpen);
    };

    private JPanel panelMain;
    private JButton buttonStop;
    private JLabel labelProgress;

    private int timerRunsLeft;

    public GuiProgress(int time) {
        this.timerRunsLeft = time * 60 * frequency;

        start();
        updateTimeText();
    }

    private TimerTask generateNewTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                timerRunsLeft --;
                Thread async = new Thread(timerAsyncRunnable, "Timer Async Thread");
                async.start();

                if (timerRunsLeft <= 0) {
                    System.out.println("Timer ended");
                    this.cancel();
                }
            }
        };
    }

    private void updateTimeText() {
        labelProgress.setText(String.format("%02d min", (int) (Math.ceil(((double) timerRunsLeft) / frequency / 60))));
    }

    public void start() {
        TimerTask timerTask = generateNewTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000 / frequency, 1000 / frequency);
    }

    public JPanel getMainPanel() {
        return panelMain;
    }
}