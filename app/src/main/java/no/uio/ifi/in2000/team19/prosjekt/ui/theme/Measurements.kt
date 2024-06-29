package no.uio.ifi.in2000.team19.prosjekt.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


// Tried to use the compact recomendations from MD3 https://m3.material.io/foundations/layout/applying-layout/compact
// Will also help further development for larger screens, by being able to switch these values here based on screen instead
// of having hard-coded padding... etc.

enum class Measurements(val measurement: Dp) {

    HorizontalPadding(16.dp),

    BetweenSectionVerticalGap(16.dp),

    WithinSectionVerticalGap(12.dp),
    WithinSectionHorizontalGap(16.dp),

    AdviceCardHeight(200.dp),
    GraphHeight(250.dp)
}
