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
                5,
                false
            ),
            Sequence(
                "Reduce Temperature",
                "Reducing temperature...",
                5,
                true,
                outputState = InterfaceHelper.State.REDUCE_TEMP
            ),
            Sequence(
                "Request User for Ultrasonic Cleaner",
                "Requesting user input...",
                0,
                false,
                userPrompt = Sequence.PromptType.INFO,
                userPromptMessage = "Start ultrasonic cleaner"
            ),
            Sequence(
                "Ultrasonic Wash",
                "Washing...",
                12,
                false,
                outputState = InterfaceHelper.State.WASH
            ),
            Sequence(
                "Request User to stop Ultrasonic Cleaner",
                "Requesting user input...",
                0,
                false,
                userPrompt = Sequence.PromptType.INFO,
                userPromptMessage = "Stop ultrasonic cleaner"
            ),
            Sequence(
                "Drain",
                "Draining...",
                5,
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
            SequenceUtil.getDrySequence(15),
            Sequence(
                "Buzzer",
                "Complete!",
                time = 0,
                false,
                outputState = InterfaceHelper.State.ALERT,
                userPrompt = Sequence.PromptType.SUCCESS,
                userPromptMessage = "Completed!"
            )
        )
    ),
    DRY(
        listOf(
            SequenceUtil.getDrySequence(15),
            Sequence(
                "Buzzer",
                "Complete!",
                time = 0,
                false,
                outputState = InterfaceHelper.State.ALERT,
                userPrompt = Sequence.PromptType.SUCCESS,
                userPromptMessage = "Completed!"
            )
        )
    );

    fun getTotalTime(): Int {
        return sequences.sumOf { seq -> seq.time }
    }
}