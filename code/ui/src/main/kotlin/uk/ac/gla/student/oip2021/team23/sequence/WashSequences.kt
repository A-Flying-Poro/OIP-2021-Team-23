package uk.ac.gla.student.oip2021.team23.sequence

enum class WashSequences(val sequences: List<Sequence>) {
    WASH(
        listOf(
            Sequence(
                "Fill",
                5,
                true
            ),
            Sequence(
                "Soak",
                5,
                false
            ),
            Sequence(
                "Reduce Temperature",
                5,
                true
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
                12,
                false
            ),
            Sequence(
                "Drain",
                2,
                false
            ),
            Sequence(
                "Rinse",
                5,
                true
            ),
            Sequence(
                "Drying",
                15,
                true
            )
        )
    )
}