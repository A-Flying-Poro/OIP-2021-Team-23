package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.WashMode;

import javax.swing.*;

public class GuiMainMenu {
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