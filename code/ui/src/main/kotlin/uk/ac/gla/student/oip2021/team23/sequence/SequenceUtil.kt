package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

class SequenceUtil {
    companion object {
        const val maxRepeatDry = 2

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