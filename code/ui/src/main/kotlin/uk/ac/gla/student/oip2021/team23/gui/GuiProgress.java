package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.MainKt;
import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper;
import uk.ac.gla.student.oip2021.team23.sequence.Sequence;
import uk.ac.gla.student.oip2021.team23.sequence.SequenceUtil;
import uk.ac.gla.student.oip2021.team23.sequence.WashSequences;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class GuiProgress {
    private static final int frequency = 1; // number of times to run per second

    private final Timer timer = new Timer("Clock Timer");
    private TimerTask timerTask = null;
    private final Runnable timerAsyncRunnable = () -> {
        updateTimeText();

        // User input check
        if (this.dialogUserInput != null) {
            if (this.dialogUserInput.isVisible()) {
                // System.out.println("Waiting for user's input...");
                return;
            } else {
                this.dialogUserInput = null;
                System.out.println("User input closed, resuming...");
            }
        }
        // Status check
        if (InterfaceHelper.checkLidStatus()) {
            System.out.println("Lid is currently open");
            if (!this.dialogOpenedLid.isVisible())
                EventQueue.invokeLater(() -> this.dialogOpenedLid.setVisible(true));
            return;
        }
        this.dialogOpenedLid.setVisible(false);

        Sequence currentSequence = this.sequencesLeft.getFirst();
        System.out.println("Current sequence: " + currentSequence.getName() + ", Time left: " + (currentSequence.getTime() * 60L - this.timeCurrentRunning / 1000) + "s");
        if (this.timeCurrentRunning == 0) {
            // First run
            InterfaceHelper.writePinsValue(currentSequence.getOutputState());
        }

        if (currentSequence.getRequireAcknowledgement() &&
                this.timeCurrentRunning >= this.timeSignalHeldAcknowledgeTotal &&
                !this.signalWithdrewAcknowledgeSequence) {
            InterfaceHelper.writePinsValue(InterfaceHelper.State.NONE);
            this.signalWithdrewAcknowledgeSequence = true;
        }

        boolean acknowledgementReceived = currentSequence.getRequireAcknowledgement() && InterfaceHelper.checkAcknowledged();
        boolean sequenceTimeCompleted = /*currentSequence.getTime() > 0 &&*/ this.timeCurrentRunning >= currentSequence.getTime() * 60 * 1000L;

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
                        // repeatedSequence = false;
                        System.out.println("No restarts required, resuming...");
                    } else {
                        this.currentSequenceRepeated ++;
                        this.sequencesLeft.add(1, SequenceUtil.getDrySequence());
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
            this.signalWithdrewAcknowledgeSequence = false;
            if (!this.sequencesLeft.isEmpty()) { // Start next sequence
                Sequence nextSequence = this.sequencesLeft.getFirst();
                InterfaceHelper.writePinsValue(nextSequence.getOutputState());

                // User dialog prompts
                if (nextSequence.getUserPrompt() != null) {
                    String message = nextSequence.getUserPromptMessage();
                    JDialog dialog;
                    switch (nextSequence.getUserPrompt()) {
                        case INFO:
                            dialog = new DialogInfo(message);
                            break;
                        case SUCCESS:
                            dialog = new DialogSuccess(message);
                            break;
                        default:
                            dialog = null;
                    }
                    if (dialog != null) {
                        dialog.pack();
                        dialog.setLocationRelativeTo(null);
                        EventQueue.invokeLater(() -> dialog.setVisible(true));
                        this.dialogUserInput = dialog;
                    }
                }
            } else {
                // Cleanup
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
    private JDialog dialogUserInput = null;

    private final int timeSignalHeldAcknowledgeTotal = 5000;
    private boolean signalWithdrewAcknowledgeSequence = false;

    private DetectionThread threadImageDetection = null;

    private JPanel panelMain;
    private JButton buttonStop;
    private JLabel labelProgress;
    private JLabel labelStatus;

    private final DialogOpenedLid dialogOpenedLid = new DialogOpenedLid();

    public GuiProgress(WashSequences sequences) {
        this.sequencesLeft = new LinkedList<>(sequences.getSequences());

        dialogOpenedLid.setLocationRelativeTo(null);

        buttonStop.addActionListener(e -> {
            if (timerTask == null) return;

            InterfaceHelper.setStopped(true);
            timerTask.cancel();
            timerTask = null;
            EventQueue.invokeLater(() -> {
                DialogPause dialogPause = new DialogPause(this);
                dialogPause.setLocationRelativeTo(null);
                dialogPause.setVisible(true);
            });
        });

        start();
        updateTimeText();
        updateCurrentStatus();
    }

    private TimerTask generateNewTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (sequencesLeft.isEmpty()) {
                    System.out.println("Timer ended");
                    this.cancel();

                    stop();
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
        InterfaceHelper.setStopped(false);
        timerTask = generateNewTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000 / frequency);
    }

    public void stop() {
        InterfaceHelper.setStopped(false);
        InterfaceHelper.writePinsValue(InterfaceHelper.State.NONE);

        if (dialogUserInput != null)
            dialogUserInput.dispose();
        dialogOpenedLid.dispose();

        EventQueue.invokeLater(() -> {
            JFrame window = MainKt.getGuiWindow();
            GuiMainMenu mainMenu = new GuiMainMenu();
            window.setContentPane(mainMenu.getMainPanel());
            window.setVisible(true);
        });
    }

    public JPanel getMainPanel() {
        return panelMain;
    }
}