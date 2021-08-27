package uk.ac.gla.student.oip2021.team23.interf

import java.io.File
import java.util.concurrent.TimeUnit

class InterfaceHelper {
    companion object {
        enum class Dryness {
            WET,
            DRY,
            NOTFOUND
        }

        private val folder = File("interface")
        private val gpioInputScript = File(folder, "gpio-input.py")
        private val gpioOutputScript = File(folder, "gpio-output.py")
        private val gpioResetScript = File(folder, "gpio-reset.py")

        private const val pinStop = 3 // out
        private const val pinAck = 40 // in

        private const val pinLid = 5 // in

        private val runtime = Runtime.getRuntime()

        fun test(): Boolean {
            val script = File(folder, "test.py")
            val scriptPath = script.path.replace('\\', '/')
            val process = runtime.exec(arrayOf("python", scriptPath))
            val completed: Boolean = try {
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

        @JvmStatic
        fun detectDryWet(): Dryness {
            val modelFolder = File(folder, "models")
            val scriptFile = File(folder, "pycoral_object_detection.py")
            val modelFile = File(modelFolder, "version2_edgetpu.tflite")
            val labelFile = File(modelFolder, "syringe_labels.txt")

            val commands = arrayOf(
                "bash",
                "-c",
                "source /usr/local/bin/virtualenvwrapper.sh && " +
                        "workon coral && " +
                        "python ${scriptFile.path} " +
                        "--model ${modelFile.path} " +
                        "--labels ${labelFile.path} " +
                        "--headless true"
            )

            val process = runtime.exec(commands)
            process.waitFor()

            val errorOutput = process.errorStream.bufferedReader().readLines()
            if (errorOutput.isNotEmpty()) {
                System.err.println("Detection model error:")
                errorOutput.forEach(System.err::println)
                return Dryness.NOTFOUND
            }
            if (process.exitValue() != 0)
                return Dryness.NOTFOUND
            val processOutput = process.inputStream.bufferedReader().readLines()
            if (processOutput.isEmpty())
                return Dryness.NOTFOUND

            return when (processOutput[0]) {
                "dry" -> Dryness.DRY
                "wet" -> Dryness.WET
                else -> Dryness.NOTFOUND
            }
        }

        @JvmStatic
        fun checkArduinoLidStatus(): Boolean {
            return readPin(pinLid)
        }

        @JvmStatic
        fun checkAcknowledged(): Boolean {
            return readPin(pinAck)
        }

        @JvmStatic
        fun setStopped(stopped: Boolean) {
            writePin(pinStop, stopped)
        }

        @JvmStatic
        fun readPin(pin: Int): Boolean {
            val commands = arrayOf(
                "python", gpioInputScript.path,
                "--pin", pin.toString()
            )

            val process = runtime.exec(commands)
            process.waitFor(1, TimeUnit.SECONDS)

            val output = process.inputStream.bufferedReader().readLines()
            if (output.isEmpty())
                return false
            if (process.exitValue() != 0)
                return false

            return output[0] == "1"
        }

        @JvmStatic
        fun writePin(pin: Int, value: Boolean) {
            val commands = arrayOf(
                "python", gpioOutputScript.path,
                "--pin", pin.toString(),
                "--value", if (value) "1" else "0"
            )

            val process = runtime.exec(commands)
            process.waitFor(1, TimeUnit.SECONDS)
        }

        @JvmStatic
        fun resetPin(pin: Int) {
            val commands = arrayOf(
                "python", gpioResetScript.path,
                "--pin", pin.toString()
            )

            val process = runtime.exec(commands)
            process.waitFor(1, TimeUnit.SECONDS)
        }

        @JvmStatic
        fun resetPins() {
            arrayOf(
                pinStop
            ).forEach { resetPin(it) }
        }
    }
}