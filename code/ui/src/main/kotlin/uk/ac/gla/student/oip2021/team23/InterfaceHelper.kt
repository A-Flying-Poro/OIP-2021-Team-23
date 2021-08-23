package uk.ac.gla.student.oip2021.team23

import java.io.File

class InterfaceHelper {
    companion object {
        private val folder = File("interface")
        private val script = File(folder, "test.py")

        private val runtime = Runtime.getRuntime()

        fun test(): Boolean {
            var success: Boolean = true

            val commands = mutableListOf("python")
            commands.add(script.path.replace('\\', '/'))

            val process = runtime.exec(commands.toTypedArray())
            process.waitFor()

            println("Output:")
            process.inputStream.bufferedReader().forEachLine(::println)
            println("Error:")
            process.errorStream.bufferedReader().forEachLine(::println)

            if (process.exitValue() != 0)
                return false

            return success
        }
    }
}