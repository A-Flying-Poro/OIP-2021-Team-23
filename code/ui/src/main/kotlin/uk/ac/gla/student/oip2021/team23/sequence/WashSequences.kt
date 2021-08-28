package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

enum class WashSequences(val sequences: List<Sequence>) {
    WASH(
        listOf(
            Sequence(
                "Fill",
                "Filling...",
                5,
                true,
                outputState = InterfaceHelper.State.FILL
            ),
            Sequence(
                "Soak",
                "Soaking...",
                1,
                false
            ),
            Sequence(
                "Reduce Temperature",
                "Reducing temperature...",
                1,
                true,
                outputState = InterfaceHelper.State.REDUCE_TEMP
            ),
            Sequence(
                "Request User for Ultrasonic Cleaner",
                "Requesting user input...",
                0,
                false,
                userPrompt = Sequence.PromptType.INFO,
                userPromptMessage = "Please press start on the ultrasonic cleaner."
            ),
            Sequence(
                "Ultrasonic Wash",
                "Washing...",
                1,
                false,
                outputState = InterfaceHelper.State.WASH
            ),
            Sequence(
                "Request User to stop Ultrasonic Cleaner",
                "Requesting user input...",
                0,
                false,
                userPrompt = Sequence.PromptType.INFO,
                userPromptMessage = "Please press stop on the ultrasonic cleaner."
            ),
            Sequence(
                "Drain",
                "Draining...",
                2,
                false,
                outputState = InterfaceHelper.State.DRAIN
            ),
            Sequence(
                "Rinse",
                "Rinsing...",
                10,
                true,
                outputState = InterfaceHelper.State.RINSE
            ),
            Sequence(
                "Dry",
                "Drying...",
                15,
                true,
                outputState = InterfaceHelper.State.DRY,
                repeatable = Sequence.Repeatable.DRY,
                maxRepeatCount = 3
            ),
            Sequence(
                "Buzzer",
                "Complete!",
                time = 0,
                false,
                outputState = InterfaceHelper.State.ALERT,
                userPrompt = Sequence.PromptType.SUCCESS,
                userPromptMessage = "COMPLETE"
            )
        )
    ),
    DRY(
        listOf(
            Sequence(
                "Dry",
                "Drying...",
                15,
                true,
                outputState = InterfaceHelper.State.DRY,
                repeatable = Sequence.Repeatable.DRY,
                maxRepeatCount = 3
            ),
            Sequence(
                "Buzzer",
                "Complete!",
                time = 0,
                false,
                outputState = InterfaceHelper.State.ALERT,
                userPrompt = Sequence.PromptType.SUCCESS,
                userPromptMessage = "COMPLETE"
            )
        )
    );

    fun getTotalTime(): Int {
        return sequences.sumOf { seq -> seq.time }
    }

    companion object {
        @JvmStatic
        val dryRepeatable = Sequence(
            "Dry",
            "Dying...",
            5,
            true,
            outputState = InterfaceHelper.State.DRY,
            repeatable = Sequence.Repeatable.DRY,
            maxRepeatCount = 3
        )
    }
}