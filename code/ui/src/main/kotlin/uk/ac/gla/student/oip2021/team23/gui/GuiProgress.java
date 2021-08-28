package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper;
import uk.ac.gla.student.oip2021.team23.sequence.Sequence;
import uk.ac.gla.student.oip2021.team23.sequence.WashSequences;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class GuiProgress {
    private static final int frequency = 1; // number of times to run per second

    private final Timer timer = new Timer("Clock Timer");
    private final Runnable timerAsyncRunnable = () -> {
        updateTimeText();

        // Status check
        if (InterfaceHelper.checkArduinoLidStatus()) {
            System.out.println("Lid is currently open");
            if (!this.dialogOpenedLid.isVisible())
                this.dialogOpenedLid.setVisible(true);
            return;
        }
        this.dialogOpenedLid.setVisible(false);

        Sequence currentSequence = this.sequencesLeft.getFirst();
        System.out.println("Current sequence: " + currentSequence.getName() + ", Time left: " + (currentSequence.getTime() * 60L - this.timeCurrentRunning / 1000) + "s");
        if (this.timeCurrentRunning == 0) {
            // First run
            InterfaceHelper.writePinsValue(currentSequence.getOutputState());
        }

        boolean acknowledgementReceived = currentSequence.getRequireAcknowledgement() && InterfaceHelper.checkAcknowledged();
        boolean sequenceTimeCompleted = currentSequence.getTime() > 0 && this.timeCurrentRunning >= currentSequence.getTime() * 60 * 1000L;

        // When current sequence is done
        if (acknowledgementReceived || sequenceTimeCompleted) {
            // Used to limit restarts
            boolean repeatedSequence = false;
            if (currentSequence.getRepeatable() == Sequence.Repeatable.DRY) {
                // Image detection to check if a restart is needed
                if (this.threadImageDetection == null) { // Start image detection
                    InterfaceHelper.writePinsValue(InterfaceHelper.State.NONE);
                    this.threadImageDetection = new DetectionThread();
                    this.threadImageDetection.start();

                    // Skip any actions after this
                    System.out.println("Waiting for image detection...");
                    updateCurrentStatus("Detecting moisture...");
                    return;
                } else if (!this.threadImageDetection.isAlive()) { // Image detection done
                    // Checking image detection result
                    // dry -> continue
                    // wet -> restart
                    // unknown -> restart
                    InterfaceHelper.Dryness imageDetectionResult = this.threadImageDetection.result;
                    System.out.println("Image detection done, result: " + imageDetectionResult.toString());
                    if (imageDetectionResult == InterfaceHelper.Dryness.DRY || this.currentSequenceRepeated >= currentSequence.getMaxRepeatCount()) {
                        repeatedSequence = false;
                        System.out.println("No restarts required, resuming...");
                    } else {
                        this.currentSequenceRepeated ++;
                        this.sequencesLeft.add(1, WashSequences.getDryRepeatable());
                        repeatedSequence = true;
                        System.out.println("Restart requested, current restart count: " + this.currentSequenceRepeated + "/" + currentSequence.getMaxRepeatCount());
                    }

                    this.threadImageDetection = null;
                } else { // Image detection not ready yet
                    System.out.println("Waiting for image detection...");
                    return;
                }
            }

            this.sequencesLeft.removeFirst();
            InterfaceHelper.resetAcknowledgement();
            if (!this.sequencesLeft.isEmpty()) { // Start next sequence
                Sequence nextSequence = this.sequencesLeft.getFirst();
                InterfaceHelper.writePinsValue(nextSequence.getOutputState());
            } else {
                InterfaceHelper.writePinsValue(InterfaceHelper.State.NONE);
            }
            updateCurrentStatus();
            this.timeCurrentRunning = 1000 / frequency;
            if (!repeatedSequence)
                this.currentSequenceRepeated = 0;
        } else {
            this.timeCurrentRunning += 1000 / frequency;
        }
    };
    private static class DetectionThread extends Thread {
        private InterfaceHelper.Dryness result = null;

        @Override
        public void run() {
            this.result = InterfaceHelper.detectDryWet();
        }
    }

    private LinkedList<Sequence> sequencesLeft;
    private long timeCurrentRunning = 0;
    private int currentSequenceRepeated = 0;

    private DetectionThread threadImageDetection = null;

    private JPanel panelMain;
    private JButton buttonStop;
    private JLabel labelProgress;
    private JLabel labelStatus;

    private final DialogOpenedLid dialogOpenedLid = new DialogOpenedLid();

    public GuiProgress(WashSequences sequences) {
        this.sequencesLeft = new LinkedList<>(sequences.getSequences());

        dialogOpenedLid.pack();
        dialogOpenedLid.setLocationRelativeTo(null);

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

    private void updateCurrentStatus() {
        if (sequencesLeft.isEmpty()) {
            updateCurrentStatus("Done!");
        } else {
            updateCurrentStatus(sequencesLeft.getFirst().getDisplayText());
        }
    }

    private void updateCurrentStatus(String status) {
        labelStatus.setText(status);
    }

    public void start() {
        TimerTask timerTask = generateNewTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000 / frequency);
    }

    public JPanel getMainPanel() {
        return panelMain;
    }
}