package uk.ac.gla.student.oip2021.team23.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogPause extends JDialog {
    private final GuiProgress guiProgress;
    private JPanel contentPane;
    private JButton buttonResume;
    private JButton buttonCancel;

    public DialogPause(GuiProgress guiProgress) {
        this.guiProgress = guiProgress;

        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        getRootPane().setDefaultButton(buttonResume);

        buttonResume.addActionListener(e -> onResume());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
    }

    private void onResume() {
        EventQueue.invokeLater(guiProgress::start);
        dispose();
    }

    private void onCancel() {
        EventQueue.invokeLater(guiProgress::stop);
        dispose();
    }
}