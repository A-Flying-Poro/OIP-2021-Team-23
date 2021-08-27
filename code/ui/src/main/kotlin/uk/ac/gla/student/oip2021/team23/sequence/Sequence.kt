package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

data class Sequence(
    val name: String,
    /** Estimated time in minutes */
    val time: Int,
    val requireAcknowledgement: Boolean,
    var runnable: Runnable? = null,
    val outputState: InterfaceHelper.State = InterfaceHelper.State.NONE
)