package org.prebid.mobile.api.data

import org.junit.Test
import org.prebid.mobile.api.data.AdFormat.Companion.fromSet
import java.util.EnumSet
import kotlin.test.assertEquals

class AdFormatTest {
    @Test
    fun adFormatsFromSet_isNotInterstitial_banner() {
        val input = EnumSet.of(AdUnitFormat.BANNER)
        val expected = EnumSet.of(AdFormat.BANNER)

        assertEquals(expected, fromSet(input, false))
    }

    @Test
    fun adFormatsFromSet_isInterstitial_banner() {
        val input = EnumSet.of(AdUnitFormat.BANNER)
        val expected = EnumSet.of(AdFormat.INTERSTITIAL)

        assertEquals(expected, fromSet(input, true))
    }

    @Test
    fun adFormatsFromSet_isNotInterstitial_two() {
        val input = EnumSet.of(AdUnitFormat.VIDEO, AdUnitFormat.BANNER)
        val expected = EnumSet.of(AdFormat.VAST, AdFormat.BANNER)

        assertEquals(expected, fromSet(input, false))
    }

    @Test
    fun adFormatsFromSet_isInterstitial_two() {
        val input = EnumSet.of(AdUnitFormat.VIDEO, AdUnitFormat.BANNER)
        val expected = EnumSet.of(AdFormat.VAST, AdFormat.INTERSTITIAL)

        assertEquals(expected, fromSet(input, true))
    }

    @Test
    fun adFormatsFromSet_video() {
        val input = EnumSet.of(AdUnitFormat.VIDEO)
        val expected = EnumSet.of(AdFormat.VAST)

        assertEquals(expected, fromSet(input, false))
    }
}