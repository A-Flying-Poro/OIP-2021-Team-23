package uk.ac.gla.student.oip2021.team23

import uk.ac.gla.student.oip2021.team23.gui.GuiMainMenu
import java.awt.*
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.plaf.FontUIResource

fun main(args: Array<String>) {
    fun customiseUI() {
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

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

        val colourBlueSelected = Color(0x33b3fd)
        val colourBlue = Color(0xB2DDF3)
        val colourWhite = Color.WHITE

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

        // Tabbed pane colours
//        UIManager.put("TabbedPane.background", colourBlue)
//        UIManager.put("TabbedPane.selected", colourBlueSelected)
//        UIManager.put("TabbedPane.foreground", colourWhite)
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