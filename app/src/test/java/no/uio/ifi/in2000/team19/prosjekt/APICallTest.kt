package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.ApiUrls
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals

class APICallTest {

    @Test
    fun is_longitudeAndLatitude_right() {

        val longitude = "10"
        val latitude = "60"

        val path = "${ApiUrls.LOCATIONFORECAST_EDR_API}position?coords=POINT($longitude+$latitude)"
        val result = "${ApiUrls.LOCATIONFORECAST_EDR_API}position?coords=POINT(10+60)"

        assertEquals(path, result)
    }

    @Test
    fun is_longitudeAndLatitude_wrong() {

        val longitude = "58"
        val latitude = "59"

        val path = "${ApiUrls.LOCATIONFORECAST_EDR_API}position?coords=POINT($longitude+$latitude)"
        val result = "${ApiUrls.LOCATIONFORECAST_EDR_API}position?coords=POINT(59+58)"

        assertNotEquals(path, result)
    }


}