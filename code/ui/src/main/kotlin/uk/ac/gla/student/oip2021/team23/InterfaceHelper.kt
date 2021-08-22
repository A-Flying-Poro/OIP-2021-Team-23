package uk.ac.gla.student.oip2021.team23

import java.io.File

class InterfaceHelper {
    companion object {
        private val folder = File("interface")
        private val script = File(folder, "test.py")

        private val runtime = Runtime.getRuntime()

        fun test(): Boolean {
            println("Start")
            var success: Boolean = true
            val process = runtime.exec(arrayOf("python", script.path))
            process.waitFor()
            process.inputStream.bufferedReader().use { outputStream -> {
                println("Test")
                process.errorStream.bufferedReader().use { errorStream -> {
                    println("Reading output")
                    if (errorStream.readLine() != null)
                        success = false
                } }
            } }

            return success
        }
    }
}