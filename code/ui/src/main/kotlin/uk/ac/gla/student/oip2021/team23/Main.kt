package uk.ac.gla.student.oip2021.team23

import uk.ac.gla.student.oip2021.team23.gui.GuiMainMenu
import java.awt.EventQueue
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.plaf.FontUIResource

fun main(args: Array<String>) {
    fun customiseUI() {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        fun loadFonts() {
            val fontFiles = mapOf(
                "Roboto" to arrayOf(
                    "Roboto-Black.ttf",
                    "Roboto-BlackItalic.ttf",
                    "Roboto-Bold.ttf",
                    "Roboto-BoldItalic.ttf",
                    "Roboto-Italic.ttf",
                    "Roboto-Light.ttf",
                    "Roboto-LightItalic.ttf",
                    "Roboto-Medium.ttf",
                    "Roboto-MediumItalic.ttf",
                    "Roboto-Regular.ttf",
                    "Roboto-Thin.ttf",
                    "Roboto-ThinItalic.ttf"
                )
            )

            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            fontFiles.forEach { (fontFamily, fontNames) ->
                fontNames.forEach { fontName ->
                    val fontDirectory = "font/$fontFamily/$fontName"
                    val fontStream = ClassLoader.getSystemResourceAsStream(fontDirectory)
                    if (fontStream == null) {
                        System.err.println("Error locating font: $fontDirectory")
                        return@forEach
                    }
                    try {
                        val font = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                        ge.registerFont(font)
                    } catch (e : FontFormatException) {
                        System.err.println("Could not load font: $fontName ($fontFamily)")
                        e.printStackTrace()
                    }
                }
            }
        }
        fun setFont(font: FontUIResource) {
            val keys = UIManager.getDefaults().keys()
            while (keys.hasMoreElements()) {
                val key = keys.nextElement()
                val value = UIManager.get(key)
                if (value is FontUIResource)
                    UIManager.put(key, font)
            }
        }
        loadFonts()
        setFont(FontUIResource("Roboto", Font.PLAIN, 22))

        // Tabbed pane insets
        val keyTabInsets = "TabbedPane.tabInsets"
        val paddingTab = 20
        val insetsTab = UIManager.getInsets(keyTabInsets)
        insetsTab.top += paddingTab
        insetsTab.bottom += paddingTab
        insetsTab.left += paddingTab
        insetsTab.right += paddingTab
        UIManager.put(keyTabInsets, insetsTab)
    }
    customiseUI()

    EventQueue.invokeLater {
        val gui = JFrame("OIP UI")
        val mainMenu = GuiMainMenu()
        gui.contentPane = mainMenu.mainPanel;
        gui.isVisible = true
        gui.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        gui.extendedState = gui.extendedState.or(JFrame.MAXIMIZED_BOTH)
    }
}