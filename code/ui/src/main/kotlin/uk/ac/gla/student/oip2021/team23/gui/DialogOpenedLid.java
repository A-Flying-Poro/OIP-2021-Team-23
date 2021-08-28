package uk.ac.gla.student.oip2021.team23.gui;

import javax.swing.*;

public class DialogOpenedLid extends JDialog {
    private JPanel contentPane;

    public DialogOpenedLid() {
        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }
}