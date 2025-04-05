package org.prebid.mobile

import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.prebid.mobile.NativeEventTracker.EVENT_TRACKING_METHOD
import org.prebid.mobile.api.data.AdFormat
import org.prebid.mobile.testutils.BaseSetup
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.EnumSet
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [BaseSetup.testSDK], manifest = Config.NONE)
class NativeAdUnitTest {
    @After
    @Throws(Exception::class)
    fun tearDown() {
        EVENT_TRACKING_METHOD.CUSTOM.id = 500
    }

    @Test
    @Throws(Exception::class)
    fun testNativeAdUnitCreation() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        val configuration = nativeUnit.getConfiguration()

        assertNotNull(nativeUnit)
        assertEquals(PBS_CONFIG_ID_NATIVE_APPNEXUS, configuration.configId)
        assertEquals(EnumSet.of(AdFormat.NATIVE), configuration.adFormats)
        assertNotNull(configuration.nativeConfiguration)
    }

    @Test
    fun testNativeAdContextType() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertNotNull(nativeUnit)
        assertNull(nativeUnit.nativeConfiguration.contextType)
        nativeUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.CONTENT_CENTRIC)
        assertNotNull(nativeUnit.nativeConfiguration.contextType)
        assertEquals(
            NativeAdUnit.CONTEXT_TYPE.CONTENT_CENTRIC,
            nativeUnit.nativeConfiguration.contextType
        )
        assertEquals(1, nativeUnit.nativeConfiguration.contextType.id.toLong())
        nativeUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.SOCIAL_CENTRIC)
        assertEquals(
            NativeAdUnit.CONTEXT_TYPE.SOCIAL_CENTRIC,
            nativeUnit.nativeConfiguration.contextType
        )
        assertEquals(2, nativeUnit.nativeConfiguration.contextType.id.toLong())
        nativeUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.PRODUCT)
        assertEquals(
            NativeAdUnit.CONTEXT_TYPE.PRODUCT,
            nativeUnit.nativeConfiguration.contextType
        )
        assertEquals(3, nativeUnit.nativeConfiguration.contextType.id.toLong())
        nativeUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.CUSTOM)
        assertEquals(
            NativeAdUnit.CONTEXT_TYPE.CUSTOM,
            nativeUnit.nativeConfiguration.contextType
        )
        NativeAdUnit.CONTEXT_TYPE.CUSTOM.id = 500
        assertEquals(500, nativeUnit.nativeConfiguration.contextType.id.toLong())
        NativeAdUnit.CONTEXT_TYPE.CUSTOM.id = 600
        assertEquals(600, nativeUnit.nativeConfiguration.contextType.id.toLong())
        NativeAdUnit.CONTEXT_TYPE.CUSTOM.id = 1
        assertEquals(600, nativeUnit.nativeConfiguration.contextType.id.toLong())
        assertFalse(1 == nativeUnit.nativeConfiguration.contextType.id, "Invalid CustomId")
    }

    @Test
    fun testNativeAdContextSubType() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertNotNull(nativeUnit)
        assertNull(nativeUnit.nativeConfiguration.contextSubtype)
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.GENERAL)
        assertNotNull(nativeUnit.nativeConfiguration.contextSubtype)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.GENERAL,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(10, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.ARTICAL)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.ARTICAL,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(11, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.VIDEO)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.VIDEO,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(12, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.AUDIO)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.AUDIO,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(13, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.IMAGE)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.IMAGE,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(14, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.USER_GENERATED)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.USER_GENERATED,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(15, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.GENERAL_SOCIAL)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.GENERAL_SOCIAL,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(20, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.EMAIL)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.EMAIL,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(21, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.CHAT_IM)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.CHAT_IM,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(22, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.SELLING)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.SELLING,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(30, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.APPLICATION_STORE)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.APPLICATION_STORE,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(31, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.PRODUCT_REVIEW_SITES)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.PRODUCT_REVIEW_SITES,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        assertEquals(32, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        nativeUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.CUSTOM)
        assertEquals(
            NativeAdUnit.CONTEXTSUBTYPE.CUSTOM,
            nativeUnit.nativeConfiguration.contextSubtype
        )
        NativeAdUnit.CONTEXTSUBTYPE.CUSTOM.id = 500
        assertEquals(500, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        NativeAdUnit.CONTEXTSUBTYPE.CUSTOM.id = 600
        assertEquals(600, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        NativeAdUnit.CONTEXTSUBTYPE.CUSTOM.id = 10
        assertEquals(600, nativeUnit.nativeConfiguration.contextSubtype.id.toLong())
        assertFalse(
            1 == nativeUnit.nativeConfiguration.contextSubtype.id,
            "Invalid CustomId"
        )
    }

    @Test
    fun testNativeAdPlacementType() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertNotNull(nativeUnit)
        assertNull(nativeUnit.nativeConfiguration.placementType)
        nativeUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.CONTENT_FEED)
        assertNotNull(nativeUnit.nativeConfiguration.placementType)
        assertEquals(
            NativeAdUnit.PLACEMENTTYPE.CONTENT_FEED,
            nativeUnit.nativeConfiguration.placementType
        )
        assertEquals(1, nativeUnit.nativeConfiguration.placementType.id.toLong())
        nativeUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.CONTENT_ATOMIC_UNIT)
        assertEquals(
            NativeAdUnit.PLACEMENTTYPE.CONTENT_ATOMIC_UNIT,
            nativeUnit.nativeConfiguration.placementType
        )
        assertEquals(2, nativeUnit.nativeConfiguration.placementType.id.toLong())
        nativeUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.OUTSIDE_CORE_CONTENT)
        assertEquals(
            NativeAdUnit.PLACEMENTTYPE.OUTSIDE_CORE_CONTENT,
            nativeUnit.nativeConfiguration.placementType
        )
        assertEquals(3, nativeUnit.nativeConfiguration.placementType.id.toLong())
        nativeUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.RECOMMENDATION_WIDGET)
        assertEquals(
            NativeAdUnit.PLACEMENTTYPE.RECOMMENDATION_WIDGET,
            nativeUnit.nativeConfiguration.placementType
        )
        assertEquals(4, nativeUnit.nativeConfiguration.placementType.id.toLong())
        nativeUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.CUSTOM)
        assertEquals(
            NativeAdUnit.PLACEMENTTYPE.CUSTOM,
            nativeUnit.nativeConfiguration.placementType
        )
        NativeAdUnit.PLACEMENTTYPE.CUSTOM.id = 500
        assertEquals(500, nativeUnit.nativeConfiguration.placementType.id.toLong())
        NativeAdUnit.PLACEMENTTYPE.CUSTOM.id = 600
        assertEquals(600, nativeUnit.nativeConfiguration.placementType.id.toLong())
        NativeAdUnit.PLACEMENTTYPE.CUSTOM.id = 1
        assertEquals(600, nativeUnit.nativeConfiguration.placementType.id.toLong())
        assertFalse(1 == nativeUnit.nativeConfiguration.placementType.id, "Invalid CustomId")
    }

    @Test
    fun testNativeAdPlacementCount() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertEquals(1, nativeUnit.nativeConfiguration.placementCount.toLong())
        nativeUnit.setPlacementCount(123)
        assertEquals(123, nativeUnit.nativeConfiguration.placementCount.toLong())
    }

    @Test
    fun testNativeAdSequence() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertEquals(0, nativeUnit.nativeConfiguration.seq.toLong())
        nativeUnit.setSeq(1)
        assertEquals(1, nativeUnit.nativeConfiguration.seq.toLong())
    }

    @Test
    fun testNativeAdAsseturlSupport() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertFalse(nativeUnit.nativeConfiguration.aUrlSupport)
        nativeUnit.setAUrlSupport(true)
        assertTrue(nativeUnit.nativeConfiguration.aUrlSupport)
    }

    @Test
    fun testNativeAdDUrlSupport() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertFalse(nativeUnit.nativeConfiguration.dUrlSupport)
        nativeUnit.setDUrlSupport(true)
        assertTrue(nativeUnit.nativeConfiguration.dUrlSupport)
    }

    @Test
    fun testNativeAdPrivacy() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertFalse(nativeUnit.nativeConfiguration.privacy)
        nativeUnit.setPrivacy(true)
        assertTrue(nativeUnit.nativeConfiguration.privacy)
    }

    @Test
    fun testNativeAdExt() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertNull(nativeUnit.nativeConfiguration.ext)
        val ext = JSONObject()
        try {
            ext.put("key", "value")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        nativeUnit.setExt(ext)
        var value = ""
        try {
            val data = nativeUnit.nativeConfiguration.ext as JSONObject
            value = data.getString("key")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        assertEquals("value", value)
    }

    @Test
    fun testNativeAdEventTrackers() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertEquals(0, nativeUnit.nativeConfiguration.eventTrackers.size.toLong())
        try {
            val methods1 = ArrayList<EVENT_TRACKING_METHOD>()
            methods1.add(EVENT_TRACKING_METHOD.IMAGE)
            methods1.add(EVENT_TRACKING_METHOD.JS)
            val eventTracker1 =
                NativeEventTracker(NativeEventTracker.EVENT_TYPE.IMPRESSION, methods1)

            val methods2 = ArrayList<EVENT_TRACKING_METHOD>()
            methods2.add(EVENT_TRACKING_METHOD.CUSTOM)
            methods2.add(EVENT_TRACKING_METHOD.IMAGE)
            val eventTracker2 =
                NativeEventTracker(NativeEventTracker.EVENT_TYPE.VIEWABLE_MRC50, methods2)

            nativeUnit.addEventTracker(eventTracker1)
            nativeUnit.addEventTracker(eventTracker2)
        } catch (_: Exception) {
        }
        assertEquals(2, nativeUnit.nativeConfiguration.eventTrackers.size.toLong())

        val eventTracker1 = nativeUnit.nativeConfiguration.eventTrackers[0]
        assertEquals(NativeEventTracker.EVENT_TYPE.IMPRESSION, eventTracker1.event)
        assertEquals(1, eventTracker1.event.id.toLong())
        assertEquals(2, eventTracker1.getMethods().size.toLong())
        assertEquals(
            EVENT_TRACKING_METHOD.IMAGE,
            eventTracker1.getMethods()[0]
        )
        assertEquals(1, eventTracker1.getMethods()[0].id.toLong())
        assertEquals(
            EVENT_TRACKING_METHOD.JS,
            eventTracker1.getMethods()[1]
        )
        assertEquals(2, eventTracker1.getMethods()[1].id.toLong())

        val eventTracker2 = nativeUnit.nativeConfiguration.eventTrackers[1]
        assertEquals(NativeEventTracker.EVENT_TYPE.VIEWABLE_MRC50, eventTracker2.event)
        assertEquals(2, eventTracker2.event.id.toLong())
        assertEquals(2, eventTracker2.getMethods().size.toLong())
        assertEquals(
            EVENT_TRACKING_METHOD.CUSTOM,
            eventTracker2.getMethods()[0]
        )
        assertEquals(500, eventTracker2.getMethods()[0].id.toLong())
        try {
            EVENT_TRACKING_METHOD.CUSTOM.id = 600
        } catch (e: Exception) {
            fail()
        }
        assertEquals(600, eventTracker2.getMethods()[0].id.toLong())
        assertEquals(
            EVENT_TRACKING_METHOD.IMAGE,
            eventTracker2.getMethods()[1]
        )
        assertEquals(1, eventTracker2.getMethods()[1].id.toLong())
    }

    @Test
    fun testNativeAdAssets() {
        val nativeUnit = NativeAdUnit(PBS_CONFIG_ID_NATIVE_APPNEXUS)
        assertTrue(nativeUnit.nativeConfiguration.assets.isEmpty())

        val title = NativeTitleAsset()
        title.setLength(25)
        val image = NativeImageAsset(20, 30)
        nativeUnit.nativeConfiguration.addAsset(title)
        nativeUnit.nativeConfiguration.addAsset(image)

        assertNotNull(nativeUnit.nativeConfiguration.assets)
        assertEquals(2, nativeUnit.nativeConfiguration.assets.size.toLong())
        assertEquals(
            25,
            (nativeUnit.nativeConfiguration.assets[0] as NativeTitleAsset).len.toLong()
        )
        assertEquals(
            30,
            (nativeUnit.nativeConfiguration.assets[1] as NativeImageAsset).hMin.toLong()
        )
        assertEquals(
            20,
            (nativeUnit.nativeConfiguration.assets[1] as NativeImageAsset).wMin.toLong()
        )
    }

    companion object {
        const val PBS_CONFIG_ID_NATIVE_APPNEXUS: String = "1f85e687-b45f-4649-a4d5-65f74f2ede8e"
    }
}