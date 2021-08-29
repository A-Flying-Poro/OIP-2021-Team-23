package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

enum class WashSequences(val sequences: List<Sequence>) {
    WASH(
        listOf(
            Sequence(
                "Fill",
                "Filling...",
                2,
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
                2,
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
                5,
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
                15,
                true,
                outputState = InterfaceHelper.State.RINSE
            ),
            WashSequences.getDrySequence(15),
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
                maxRepeatCount = WashSequences.maxRepeatDry
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
        private const val maxRepeatDry = 1

        @JvmStatic
        fun getDrySequence(time: Int): Sequence {
            return Sequence(
                "Dry",
                "Dying...",
                time,
                true,
                outputState = InterfaceHelper.State.DRY,
                repeatable = Sequence.Repeatable.DRY,
                maxRepeatCount = maxRepeatDry
            )
        }

        @JvmStatic
        fun getDrySequence() = getDrySequence(drySequenceTime)

        @JvmStatic
        val drySequenceTime = 5
    }
}