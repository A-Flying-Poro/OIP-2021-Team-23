package uk.ac.gla.student.oip2021.team23

import uk.ac.gla.student.oip2021.team23.gui.GuiMainMenu
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    val gui = JFrame()
    val mainMenu = GuiMainMenu()
    gui.contentPane = mainMenu.mainPanel;
    gui.isVisible = true
    gui.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    gui.size = Dimension(1280, 720)
    gui.extendedState = gui.extendedState.or(JFrame.MAXIMIZED_BOTH)
}