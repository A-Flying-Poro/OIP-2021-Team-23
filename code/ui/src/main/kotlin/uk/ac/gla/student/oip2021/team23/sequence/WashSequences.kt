package uk.ac.gla.student.oip2021.team23.sequence

import uk.ac.gla.student.oip2021.team23.interf.InterfaceHelper

enum class WashSequences(val sequences: List<Sequence>) {
    WASH(
        listOf(
            Sequence(
                "Fill",
                5,
                true,
                outputState = InterfaceHelper.State.FILL
            ),
            Sequence(
                "Soak",
                1,
                false
            ),
            Sequence(
                "Reduce Temperature",
                1,
                true,
                outputState = InterfaceHelper.State.REDUCE_TEMP
            ),
            Sequence(
                "Request User for Ultrasonic Cleaner",
                0,
                false,
                runnable = {
                    // Request user to turn on ultrasonic cleaner
                }
            ),
            Sequence(
                "Ultrasonic Wash",
                1,
                false,
                outputState = InterfaceHelper.State.WASH
            ),
            Sequence(
                "Drain",
                1,
                false,
                outputState = InterfaceHelper.State.DRAIN
            ),
            Sequence(
                "Rinse",
                10,
                true,
                outputState = InterfaceHelper.State.RINSE
            ),
            Sequence(
                "Drying",
                1,
                true,
                outputState = InterfaceHelper.State.DRY
            ),
            Sequence(
                "Buzzer",
                time = 0,
                false,
                outputState = InterfaceHelper.State.ALERT
            )
        )
    )
}