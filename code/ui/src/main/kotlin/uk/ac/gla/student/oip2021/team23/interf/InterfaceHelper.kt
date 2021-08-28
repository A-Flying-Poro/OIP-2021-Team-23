package uk.ac.gla.student.oip2021.team23.interf

import java.io.File
import java.util.concurrent.TimeUnit

class InterfaceHelper {
    enum class Dryness {
        WET,
        DRY,
        NOTFOUND
    }

    enum class State(val value: Int) {
        NONE(0),
        FILL(0b001),
        REDUCE_TEMP(0b010),
        WASH(0b011),
        DRAIN(0b100),
        RINSE(0b101),
        DRY(0b110),
        ALERT(0b111)
    }
    companion object {
        private val folder = File("interface")
        private val gpioInputScript = File(folder, "gpio-input.py")
        private val gpioOutputScript = File(folder, "gpio-output.py")
        private val gpioOutputValueScript = File(folder, "gpio-output-value.py")
        private val gpioResetScript = File(folder, "gpio-reset.py")

        private const val pinMsb = 3
        private const val pin2sb = 5
        private const val pinLsb = 7

        private const val pinStop = 11 // out
        private const val pinAck = 13 // in
        private const val pinStopRemote = 15 // in

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
        fun checkLidStatus(): Boolean {
            return readPin(pinStopRemote)
        }

        @JvmStatic
        fun setStopped(stopped: Boolean) {
            writePin(pinStop, stopped)
        }

        var acknowledged = false
        @JvmStatic
        fun checkAcknowledged(): Boolean {
            if (acknowledged)
                return true
            val currentAck = readPin(pinAck)
            if (currentAck)
                acknowledged = currentAck
            return currentAck
        }

        @JvmStatic
        fun resetAcknowledgement() {
            acknowledged = false
        }

        @JvmStatic
        fun readPin(pin: Int): Boolean {
            val commands = arrayOf(
                "python", gpioInputScript.path,
                "--pin", pin.toString()
            )

            val process = runtime.exec(commands)
            process.waitFor(1, TimeUnit.SECONDS)
            println("GPIO Helper > Reading from pin '$pin' with exit code: ${process.exitValue()}")

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
            println("GPIO Helper > Outputting '$value' to pin '$pin' with exit code: ${process.exitValue()}")
            if (process.exitValue() != 0) {
                println("GPIO Helper > Script error code:")
                process.errorStream.bufferedReader().lines().forEach { println("GPIO Helper > $it") }
            }
        }

        @JvmStatic
        fun writePinsValue(value: Int) {
            val commands = arrayOf(
                "python", gpioOutputValueScript.path,
                "--value", value.toString()
            )

            val process = runtime.exec(commands)
            process.waitFor(1, TimeUnit.SECONDS)
            println("GPIO Helper > Outputting '$value' with exit code: ${process.exitValue()}")
            if (process.exitValue() != 0) {
                println("GPIO Helper > Script error code:")
                process.errorStream.bufferedReader().lines().forEach { println("GPIO Helper > $it") }
            }
        }

        @JvmStatic
        fun writePinsValue(state: State) {
            writePinsValue(state.value)
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
                pinStop,
                pinMsb,
                pin2sb,
                pinLsb
            ).forEach { resetPin(it) }
        }

        @JvmStatic
        fun setup() {
            writePin(pinStop, false)
            writePinsValue(0)
        }
    }
}