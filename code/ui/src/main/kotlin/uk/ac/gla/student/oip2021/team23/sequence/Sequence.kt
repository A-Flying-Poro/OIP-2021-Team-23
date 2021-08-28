package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

data class Sequence(
    val name: String,
    val displayText: String,
    /** Estimated time in minutes */
    val time: Int,
    val requireAcknowledgement: Boolean,
    val outputState: InterfaceHelper.State = InterfaceHelper.State.NONE,
    val repeatable: Repeatable = Repeatable.NONE,
    val maxRepeatCount: Int = 0,

    val userPrompt: PromptType? = null,
    val userPromptMessage: String? = null
) {
    enum class PromptType {
        WARNING,
        INFO,
        SUCCESS
    }

    enum class Repeatable {
        NONE,
        DRY
    }
}