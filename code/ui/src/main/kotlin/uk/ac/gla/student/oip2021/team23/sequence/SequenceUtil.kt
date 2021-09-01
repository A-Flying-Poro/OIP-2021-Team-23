package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

class SequenceUtil {
    companion object {
        private const val maxRepeatDry = 2

        @JvmStatic
        fun getDrySequence(time: Int, requireAcknowledgement: Boolean = true): Sequence {
            return Sequence(
                "Dry",
                "Drying...",
                time,
                requireAcknowledgement,
                outputState = InterfaceHelper.State.DRY,
                repeatable = Sequence.Repeatable.DRY,
                maxRepeatCount = maxRepeatDry
            )
        }

        @JvmStatic
        fun getDrySequence() = getDrySequence(drySequenceTime, false)

        @JvmStatic
        val drySequenceTime = 5
    }
}