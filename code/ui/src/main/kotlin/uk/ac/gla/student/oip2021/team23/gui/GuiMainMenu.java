package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.MainKt;
import uk.ac.gla.student.oip2021.team23.sequence.WashSequences;

import javax.swing.*;

public class GuiMainMenu {
    public enum WashMode {
        WASH(10),
        DRY(10),
        SELF_CLEAN(10);

        private final int time;

        WashMode(int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }
    }

    private JPanel panelMain;
    private JButton buttonFullWash;
    private JButton buttonDrying;
    private JButton buttonSelfClean;

    public GuiMainMenu() {
        buttonFullWash.addActionListener(e -> onStartButtonPress(WashSequences.WASH));
        buttonDrying.addActionListener(e -> onStartButtonPress(WashSequences.WASH));
        buttonSelfClean.addActionListener(e -> onStartButtonPress(WashSequences.WASH));
    }

    public JPanel getMainPanel() {
        return panelMain;
    }

    private void onStartButtonPress(WashSequences mode) {
        // TODO Add logic to start process
        GuiProgress gui = new GuiProgress(mode);
        JFrame mainWindow = MainKt.getGuiWindow();
        mainWindow.setContentPane(gui.getMainPanel());
        mainWindow.setVisible(true);
    }
}