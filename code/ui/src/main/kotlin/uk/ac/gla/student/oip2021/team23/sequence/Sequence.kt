package uk.ac.gla.student.oip2021.team23.sequence

data class Sequence(
    val name: String,
    /** Estimated time in minutes */
    val time: Int,
    val requireAcknowledgement: Boolean,
    val runnable: Runnable? = null
)