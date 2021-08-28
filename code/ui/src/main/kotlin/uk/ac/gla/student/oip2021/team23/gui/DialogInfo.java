package uk.ac.gla.student.oip2021.team23.gui;

import javax.swing.*;
import java.util.Objects;

public class DialogInfo extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel labelMessage;

    private final String message;

    public DialogInfo(String message) {
        this.message = Objects.requireNonNullElse(message, "Message");
        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        labelMessage = new JLabel(message);
    }
}