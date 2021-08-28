package uk.ac.gla.student.oip2021.team23.gui;

import uk.ac.gla.student.oip2021.team23.MainKt;
import uk.ac.gla.student.oip2021.team23.sequence.WashSequences;

import javax.swing.*;

public class GuiMainMenu {
    private JPanel panelMain;
    private JButton buttonFullWash;
    private JButton buttonDrying;
    private JButton buttonSelfClean;
    private JLabel labelWashTime;
    private JLabel labelDryTime;
    private JLabel labelCleanTime;

    public GuiMainMenu() {
        buttonFullWash.addActionListener(e -> onStartButtonPress(WashSequences.WASH));
        buttonDrying.addActionListener(e -> onStartButtonPress(WashSequences.DRY));
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        labelWashTime = new JLabel(WashSequences.WASH.getTotalTime() + " min");
        labelDryTime = new JLabel(WashSequences.DRY.getTotalTime() + " min");
        labelCleanTime = new JLabel(WashSequences.WASH.getTotalTime() + " min");
    }
}