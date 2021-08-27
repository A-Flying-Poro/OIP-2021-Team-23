package uk.ac.gla.student.oip2021.team23.gui;

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
        buttonFullWash.addActionListener(e -> onStartButtonPress(WashMode.WASH));
        buttonDrying.addActionListener(e -> onStartButtonPress(WashMode.DRY));
        buttonSelfClean.addActionListener(e -> onStartButtonPress(WashMode.SELF_CLEAN));
    }

    public JPanel getMainPanel() {
        return panelMain;
    }

    private void onStartButtonPress(WashMode mode) {
        // TODO Add logic to start process
    }
}