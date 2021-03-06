package uk.ac.gla.student.oip2021.team23

import uk.ac.gla.student.oip2021.team23.gui.GuiMainMenu
import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper
import java.awt.EventQueue
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.plaf.FontUIResource

val guiWindow = JFrame("User Interface").apply {
    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    extendedState = extendedState.or(JFrame.MAXIMIZED_BOTH)
}

fun main(args: Array<String>) {
    fun customiseUI() {
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
                ),
                "Metropolis" to arrayOf(
                    "Metropolis-Black.ttf",
                    "Metropolis-BlackItalic.ttf",
                    "Metropolis-Bold.ttf",
                    "Metropolis-BoldItalic.ttf",
                    "Metropolis-ExtraBold.ttf",
                    "Metropolis-ExtraBoldItalic.ttf",
                    "Metropolis-ExtraLight.ttf",
                    "Metropolis-ExtraLightItalic.ttf",
                    "Metropolis-Light.ttf",
                    "Metropolis-LightItalic.ttf",
                    "Metropolis-Medium.ttf",
                    "Metropolis-MediumItalic.ttf",
                    "Metropolis-Regular.ttf",
                    "Metropolis-RegularItalic.ttf",
                    "Metropolis-SemiBold.ttf",
                    "Metropolis-SemiBoldItalic.ttf",
                    "Metropolis-Thin.ttf",
                    "Metropolis-ThinItalic.ttf"
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
        setFont(FontUIResource("Metropolis", Font.PLAIN, 22))

        // Tabbed pane insets
        val keyTabInsets = "TabbedPane.tabInsets"
        val paddingTabVertical = 20
        val paddingTabHorizontal = 5
        val insetsTab = UIManager.getInsets(keyTabInsets)
        insetsTab.top += paddingTabVertical
        insetsTab.bottom += paddingTabVertical
        insetsTab.left += paddingTabHorizontal
        insetsTab.right += paddingTabHorizontal
        UIManager.put(keyTabInsets, insetsTab)
    }
    customiseUI()

    println("Testing python interface...")
    if (!InterfaceHelper.test()) {
        System.err.println("Python interface test did not complete.")
        return
    }
    InterfaceHelper.setup()
    println("Python interface test success.")

    EventQueue.invokeLater {
        val mainMenu = GuiMainMenu()
        guiWindow.contentPane = mainMenu.mainPanel
        guiWindow.isVisible = true
    }
}