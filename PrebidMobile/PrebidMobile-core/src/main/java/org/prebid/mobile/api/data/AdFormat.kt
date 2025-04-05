package org.prebid.mobile.api.data

import java.util.EnumSet

/**
 * Internal ad format. Must be set up only inside the SDK.
 */
enum class AdFormat {
    BANNER,
    INTERSTITIAL,
    NATIVE,
    VAST;

    companion object {
        @JvmStatic
        fun fromSet(
            adUnitFormats: EnumSet<AdUnitFormat>,
            isInterstitial: Boolean,
        ): EnumSet<AdFormat> {
            require(adUnitFormats.isNotEmpty()) {
                "List of ad unit formats must contain at least one item."
            }
            return adUnitFormats.fold(EnumSet.noneOf(AdFormat::class.java)) { acc, format ->
                when (format) {
                    null -> Unit
                    AdUnitFormat.BANNER -> {
                        if (isInterstitial) {
                            acc.add(INTERSTITIAL)
                        } else {
                            acc.add(BANNER)
                        }
                    }
                    AdUnitFormat.VIDEO -> {
                        acc.add(VAST)
                    }
                }
                acc
            }
        }
    }
}