package uk.ac.gla.student.oip2021.team23

import java.io.File
import java.util.concurrent.TimeUnit

class InterfaceHelper {
    companion object {
        private val folder = File("interface")

        private val runtime = Runtime.getRuntime()

        fun test(): Boolean {
            val script = File(folder, "test.py")
            val scriptPath = script.path.replace('\\', '/')
            val process = runtime.exec(arrayOf("python", scriptPath))
            val completed: Boolean
            completed = try {
                process.waitFor(10, TimeUnit.SECONDS)
            } catch (e : InterruptedException) {
                println("Interrupted while waiting for test script.")
                process.isAlive
            }

            if (!completed) {
                System.err.println("Timed out waiting for test script to complete.")
                process.destroy()
                return false
            }

            val error = process.errorStream.bufferedReader().readLines()
            if (error.isNotEmpty()) {
                System.err.println("Error output for executing script:")
                error.forEach(System.err::println)
                return false
            }

            if (process.exitValue() != 0) {
                System.err.println("Test script exited with non-zero error code: ${process.exitValue()}")
                return false
            }

            return true
        }

        fun runDetect() {
            val commands = mutableListOf("bash")
            commands.add("-c")
            commands.add("source /usr/local/bin/virtualenvwrapper.sh && workon coral && python interface/pycoral_object_detection.py --model interface/models/version2_edgetpu.tflite --labels interface/models/syringe_labels.txt")

            val process = runtime.exec(commands.toTypedArray())
        }
    }
}