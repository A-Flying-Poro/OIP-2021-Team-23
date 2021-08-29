package uk.ac.gla.student.oip2021.team23.gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogSuccess extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel labelMessage;

    private final String message;

    public DialogSuccess(String message) {
        this.message = message;

        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        getRootPane().setDefaultButton(buttonOK);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

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