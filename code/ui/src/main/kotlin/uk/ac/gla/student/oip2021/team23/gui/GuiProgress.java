package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper;
import uk.ac.gla.student.oip2021.team23.sequence.Sequence;
import uk.ac.gla.student.oip2021.team23.sequence.WashSequences;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class GuiProgress {
    private static final int frequency = 1; // number of times to run per second

    private final Timer timer = new Timer("Clock Timer");
    private final Runnable timerAsyncRunnable = () -> {
        updateTimeText();

        // Status check
        if (InterfaceHelper.checkArduinoLidStatus()) {
            System.out.println("Lid is currently open");
            return;
        }

        Sequence currentSequence = this.sequencesLeft.getFirst();
        System.out.println("Current sequence: " + currentSequence.getName() + ", Time left for current sequence: " + (currentSequence.getTime() * 60 * 1000L - this.timeCurrentRunning));
        if (this.timeCurrentRunning == 0) {
            // First run
            InterfaceHelper.writePinsValue(currentSequence.getOutputState());
        }

        if ((currentSequence.getRequireAcknowledgement() && InterfaceHelper.checkAcknowledged()) || this.timeCurrentRunning >= currentSequence.getTime() * 60 * 1000L) {
            this.sequencesLeft.removeFirst();
            if (!this.sequencesLeft.isEmpty()) {
                Sequence next = this.sequencesLeft.getFirst();
                InterfaceHelper.writePinsValue(next.getOutputState());
            } else {
                InterfaceHelper.writePinsValue(InterfaceHelper.State.NONE);
            }
            this.timeCurrentRunning = 1000 / frequency;
        } else {
            this.timeCurrentRunning += 1000 / frequency;
        }
    };

    private LinkedList<Sequence> sequencesLeft;
    private long timeCurrentRunning = 0;

    private JPanel panelMain;
    private JButton buttonStop;
    private JLabel labelProgress;

    public GuiProgress(WashSequences sequences) {
        this.sequencesLeft = new LinkedList<>(sequences.getSequences());

        start();
        updateTimeText();
    }

    private TimerTask generateNewTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (sequencesLeft.isEmpty()) {
                    System.out.println("Timer ended");
                    this.cancel();

                } else {
                    Thread async = new Thread(timerAsyncRunnable, "Timer Async Thread");
                    async.start();
                }
            }
        };
    }

    private long getTimeLeft() {
        return sequencesLeft.stream().mapToInt(Sequence::getTime).sum() * 60 * 1000L - timeCurrentRunning;
    }

    private void updateTimeText() {
        long secondsLeft = getTimeLeft() / 1000;
        labelProgress.setText(String.format("%02d min", (int) (Math.ceil(secondsLeft / 60d))));
    }

    public void start() {
        TimerTask timerTask = generateNewTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000 / frequency);
    }

    public JPanel getMainPanel() {
        return panelMain;
    }
}