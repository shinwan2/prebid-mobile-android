/*
 *    Copyright 2018-2021 Prebid.org, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.prebid.mobile.rendering.networking.parameters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.prebid.mobile.*;
import org.prebid.mobile.api.data.AdFormat;
import org.prebid.mobile.api.data.AdUnitFormat;
import org.prebid.mobile.api.rendering.pluginrenderer.PrebidMobilePluginRegister;
import org.prebid.mobile.api.rendering.pluginrenderer.PrebidMobilePluginRenderer;
import org.prebid.mobile.configuration.AdUnitConfiguration;
import org.prebid.mobile.configuration.NativeAdUnitConfiguration;
import org.prebid.mobile.rendering.bidding.data.bid.Prebid;
import org.prebid.mobile.rendering.models.AdPosition;
import org.prebid.mobile.rendering.models.PlacementType;
import org.prebid.mobile.rendering.models.openrtb.BidRequest;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.*;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.devices.Geo;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.imps.Banner;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.imps.Video;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.imps.pmps.Format;
import org.prebid.mobile.rendering.models.openrtb.bidRequests.source.Source;
import org.prebid.mobile.rendering.sdk.ManagersResolver;
import org.prebid.mobile.rendering.session.manager.OmAdSessionManager;
import org.prebid.mobile.rendering.utils.helpers.Utils;
import org.prebid.mobile.testutils.FakePrebidMobilePluginRenderer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.annotation.Target;
import java.util.*;

import static org.junit.Assert.*;
import static org.prebid.mobile.rendering.networking.parameters.BasicParameterBuilder.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 19, qualifiers = "w1920dp-h1080dp")
public class BasicParameterBuilderTest {

    private static final int VIDEO_INTERSTITIAL_PLACEMENT = 5;
    private static final int USER_AGE = 20;
    private static final int USER_YOB = Calendar.getInstance().get(Calendar.YEAR) - USER_AGE;
    private static final float USER_LAT = 1;
    private static final float USER_LON = 0;
    private static final String USER_ID = "id";
    private static final String USER_KEYWORDS = "keywords";
    private static final String USER_CUSTOM = "custom";
    private static final String USER_GENDER = "M";
    private static final String USER_BUYER_ID = "bid";
    private PrebidMobilePluginRenderer otherPlugin = FakePrebidMobilePluginRenderer.getFakePrebidRenderer(null, null, true, "FakePlugin", "1.0");

    private Context context;

    private final boolean browserActivityAvailable = true;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.buildActivity(Activity.class).create().get();
        org.prebid.mobile.PrebidMobile.initializeSdk(context, null);
        ManagersResolver.getInstance().prepare(context);
        TargetingParams.setExternalUserIds(null);
    }

    @After
    public void cleanup() throws Exception {
        TargetingParams.clearUserData();
        TargetingParams.clearUserKeywords();
        TargetingParams.setUserLatLng(null, null);
        TargetingParams.setGender(TargetingParams.GENDER.UNKNOWN);
        TargetingParams.clearStoredExternalUserIds();
        TargetingParams.setBuyerId(null);
        TargetingParams.setUserId(null);
        TargetingParams.setUserCustomData(null);
        TargetingParams.setYearOfBirth(0);
        TargetingParams.setOmidPartnerName(null);
        TargetingParams.setOmidPartnerVersion(null);
        TargetingParams.setGlobalOrtbConfig(null);
        TargetingParams.setExternalUserIds(null);

        PrebidMobile.sendMraidSupportParams = true;
        PrebidMobile.useExternalBrowser = false;
        PrebidMobile.isCoppaEnabled = false;
        PrebidMobile.clearStoredBidResponses();
        PrebidMobile.setStoredAuctionResponse(null);
        PrebidMobile.setPrebidServerAccountId("");
        PrebidMobile.setAuctionSettingsId(null);

        PrebidMobile.unregisterPluginRenderer(otherPlugin);
    }

    @Test
    public void banner_videoDimensionsNotSet_allImpsHaveDimensions() throws JSONException {
        final BannerAdUnit adUnit = new BannerAdUnit(
                "unitId",
                300,
                250,
                EnumSet.of(AdUnitFormat.BANNER, AdUnitFormat.VIDEO)
        );
        final VideoParameters videoParameters = new VideoParameters(Lists.newArrayList("video/mp4"));
        adUnit.setVideoParameters(videoParameters);

        final BidRequest bidRequest = configIntoBidRequest(adUnit.getConfiguration());

        Imp imp = bidRequest.getImp().get(0);
        assertNotNull(imp);
        Banner banner = imp.banner;
        assertNotNull(banner);
        assertEquals("{\"format\":[{\"w\":300,\"h\":250}]}", banner.getJsonObject().toString());

        Video video = imp.video;
        assertNotNull(video);
        assertEquals("{\"delivery\":[3],\"w\":300,\"h\":250,\"mimes\":[\"video\\/mp4\"]}", video.getJsonObject().toString());
    }

    @Test
    public void multiformat_setAllParameterTypes_allTypesInRequestObject() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setIsOriginalAdUnit(true);

        BannerParameters bannerParams = new BannerParameters();
        bannerParams.setAdSizes(Sets.newHashSet(new AdSize(300, 250)));
        bannerParams.setApi(Lists.newArrayList(Signals.Api.MRAID_2, Signals.Api.MRAID_3));
        config.setBannerParameters(bannerParams);

        VideoParameters videoParameters = new VideoParameters(Lists.newArrayList("video/mp4"));
        videoParameters.setAdSize(new AdSize(320, 480));
        videoParameters.setApi(Lists.newArrayList(Signals.Api.OMID_1, Signals.Api.VPAID_1));
        videoParameters.setBattr(Lists.newArrayList(Signals.CreativeAttribute.Flashing, Signals.CreativeAttribute.Flickering));

        config.setVideoParameters(videoParameters);

        NativeAdUnitConfiguration nativeConfiguration = new NativeAdUnitConfiguration();
        NativeTitleAsset titleAsset = new NativeTitleAsset();
        titleAsset.setRequired(true);
        nativeConfiguration.addAsset(titleAsset);
        config.setNativeConfiguration(nativeConfiguration);

        config.setAdFormats(EnumSet.of(AdFormat.BANNER, AdFormat.VAST, AdFormat.NATIVE));


        BidRequest bidRequest = configIntoBidRequest(config);


        Imp imp = bidRequest.getImp().get(0);
        assertNotNull(imp);
        Banner banner = imp.banner;
        assertNotNull(banner);
        assertEquals("{\"format\":[{\"w\":300,\"h\":250}],\"api\":[5,6]}", banner.getJsonObject().toString());

        Video video = imp.video;
        assertNotNull(video);
        assertEquals("{\"delivery\":[3],\"battr\":[10],\"w\":320,\"h\":480,\"api\":[7,1],\"mimes\":[\"video\\/mp4\"]}", video.getJsonObject().toString());

        Native nativeObj = imp.nativeObj;
        assertNotNull(nativeObj);
    }

    private BidRequest configIntoBidRequest(AdUnitConfiguration config) {
        BasicParameterBuilder builder = new BasicParameterBuilder(
                config,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        return adRequestInput.getBidRequest();
    }

    @Test
    public void sourceOmidValues_originalApi_customValues() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setAdFormat(AdFormat.BANNER);
        config.setIsOriginalAdUnit(true);

        TargetingParams.setOmidPartnerName("testOmidValue");
        TargetingParams.setOmidPartnerVersion("testOmidVersion");

        BasicParameterBuilder builder = new BasicParameterBuilder(config,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Source source = adRequestInput.getBidRequest().getSource();
        assertEquals(adRequestInput.getBidRequest().getId(), source.getTid());

        JSONObject sourceExtJson = source.getJsonObject().getJSONObject("ext");
        assertEquals("testOmidValue", sourceExtJson.getString("omidpn"));
        assertEquals("testOmidVersion", sourceExtJson.getString("omidpv"));
    }

    @Test
    public void sourceOmidValues_originalApi_emptyValues() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setAdFormat(AdFormat.BANNER);
        config.setIsOriginalAdUnit(true);

        BasicParameterBuilder builder = new BasicParameterBuilder(config,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Source source = adRequestInput.getBidRequest().getSource();
        assertEquals(adRequestInput.getBidRequest().getId(), source.getTid());

        assertFalse(source.getJsonObject().has("ext"));
    }

    @Test
    public void sourceOmidValues_renderingApi_customValues() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setAdFormat(AdFormat.BANNER);
        config.setIsOriginalAdUnit(false);

        TargetingParams.setOmidPartnerName("testOmidValue");
        TargetingParams.setOmidPartnerVersion("testOmidVersion");

        BasicParameterBuilder builder = new BasicParameterBuilder(config,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Source source = adRequestInput.getBidRequest().getSource();
        assertEquals(adRequestInput.getBidRequest().getId(), source.getTid());

        JSONObject sourceExtJson = source.getJsonObject().getJSONObject("ext");
        assertEquals("testOmidValue", sourceExtJson.getString("omidpn"));
        assertEquals("testOmidVersion", sourceExtJson.getString("omidpv"));
    }

    @Test
    public void sourceOmidValues_renderingApi_defaultValues() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setAdFormat(AdFormat.BANNER);
        config.setIsOriginalAdUnit(false);

        BasicParameterBuilder builder = new BasicParameterBuilder(config,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Source source = adRequestInput.getBidRequest().getSource();
        assertEquals(adRequestInput.getBidRequest().getId(), source.getTid());

        JSONObject sourceExtJson = source.getJsonObject().getJSONObject("ext");
        assertEquals("Prebid", sourceExtJson.getString("omidpn"));
        assertEquals(PrebidMobile.SDK_VERSION, sourceExtJson.getString("omidpv"));
    }

    @Test
    public void whenAppendParametersAndBannerType_ImpWithValidBannerObject() throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));
        adConfiguration.setPbAdSlot("12345");
        PrebidMobile.addStoredBidResponse("bidderTest", "123456");
        PrebidMobile.setStoredAuctionResponse("storedResponse");

        BasicParameterBuilder builder = new BasicParameterBuilder(
                adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        BidRequest expectedBidRequest = getExpectedBidRequest(adConfiguration, actualBidRequest.getId());

        assertEquals(expectedBidRequest.getJsonObject().toString(), actualBidRequest.getJsonObject().toString());
        Imp actualImp = actualBidRequest.getImp().get(0);
        assertNotNull(actualImp.banner);
        assertTrue(actualImp.banner.getFormats().containsAll(expectedBidRequest.getImp().get(0).banner.getFormats()));
        assertNull(actualImp.video);
        assertEquals(1, actualImp.secure.intValue());
        assertEquals(0, actualImp.instl.intValue());
    }

    @Test
    public void setGpid_gpidPresentInRequest() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        String expectedGpid = "/12345/home_screen#identifier";
        adConfiguration.setGpid(expectedGpid);

        BasicParameterBuilder builder = new BasicParameterBuilder(
                adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        Imp imp = actualBidRequest.getImp().get(0);
        String gpid = (String) imp.getExt().getMap().get("gpid");
        assertEquals(expectedGpid, gpid);
    }

    @Test
    public void setImpOrtbConfig_configPresentInRequest() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        String ortbConfig = "{\"arbitraryparamkey1\":\"arbitraryparamvalue1\",\"ext\":{\"otherExtParam\":\"otherParam\"}}";
        TargetingParams.setGlobalOrtbConfig(ortbConfig);

        BasicParameterBuilder builder = new BasicParameterBuilder(
                adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        try {
            assertEquals("arbitraryparamvalue1", actualBidRequest.getJsonObject().getString("arbitraryparamkey1"));
            assertEquals("otherParam", actualBidRequest.getJsonObject().getJSONObject("ext").getString("otherExtParam"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void setImpOrtbConfig_invalidJSONInRequest() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        String ortbConfig = "\"arbitraryparamkey1\":\"arbitraryparamvalue1\"}";
        adConfiguration.setImpOrtbConfig(ortbConfig);

        BasicParameterBuilder builder = new BasicParameterBuilder(
                adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        try {
            assertFalse(actualBidRequest.getJsonObject().has("arbitraryparamkey1"));;
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void setImpOrtbConfig_illegalParametersPresentInRequest() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        String ortbConfig = "{\"arbitraryparamkey1\":\"arbitraryparamvalue1\", \"ext\":{\"otherExtParam\":\"otherParam\"}}";
        TargetingParams.setGlobalOrtbConfig(ortbConfig);

        BasicParameterBuilder builder = new BasicParameterBuilder(
                adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        try {
            assertEquals("arbitraryparamvalue1", actualBidRequest.getJsonObject().getString("arbitraryparamkey1"));
            assertEquals("otherParam", actualBidRequest.getJsonObject().getJSONObject("ext").getString("otherExtParam"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void whenAppendParametersAndBInterstitialType_ImpWithValidBannerObject()
    throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.INTERSTITIAL);
        adConfiguration.setAdPosition(AdPosition.FULLSCREEN);

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        BidRequest expectedBidRequest = getExpectedBidRequest(adConfiguration, actualBidRequest.getId());

        assertEquals(expectedBidRequest.getJsonObject().toString(), actualBidRequest.getJsonObject().toString());
        Imp actualImp = actualBidRequest.getImp().get(0);
        assertNotNull(actualImp.banner);

        assertTrue(actualImp.banner.getFormats().isEmpty());
        assertNull(actualImp.video);
        assertEquals(1, actualImp.secure.intValue());
        assertEquals(1, actualImp.instl.intValue());
    }

    @Test
    public void whenAppendParametersAndVastWithoutPlacementType_ImpWithValidVideoObject()
    throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.VAST);
        adConfiguration.setAdPosition(AdPosition.FULLSCREEN);

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();

        Imp actualImp = actualBidRequest.getImp().get(0);
        assertNotNull(actualImp.video);
        assertNull(actualImp.banner);
        assertNull(actualImp.secure);
        assertNull(actualImp.video.w);
        assertNull(actualImp.video.h);
        assertEquals(VIDEO_INTERSTITIAL_PLACEMENT, actualImp.video.placement.intValue());
        assertEquals(0, actualImp.instl.intValue());
    }

    @Test
    public void whenAppendParametersAndVastWithPlacementType_ImpWithValidVideoObject()
    throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.VAST);
        adConfiguration.setPlacementType(PlacementType.IN_BANNER);
        adConfiguration.setAdPosition(AdPosition.FULLSCREEN);
        adConfiguration.addSize(new AdSize(300, 250));

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        BidRequest expectedBidRequest = getExpectedBidRequest(adConfiguration, actualBidRequest.getId());

        assertEquals(expectedBidRequest.getJsonObject().toString(), actualBidRequest.getJsonObject().toString());
        Imp actualImp = actualBidRequest.getImp().get(0);
        assertNotNull(actualImp.video);
        assertNull(actualImp.banner);
        assertNull(actualImp.secure);
        assertEquals(0, actualImp.instl.intValue());
        assertEquals(300, actualImp.video.w.intValue());
        assertEquals(250, actualImp.video.h.intValue());
        assertNotEquals(VIDEO_INTERSTITIAL_PLACEMENT, actualImp.video.placement.intValue());
    }

    @Test
    public void whenAppendParametersAndCoppaTrue_CoppaEqualsOne() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        PrebidMobile.isCoppaEnabled = true;

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        assertEquals(1, actualBidRequest.getRegs().coppa.intValue());
    }

    @Test
    public void whenAppendParametersAndCoppaFalse_CoppaNull() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualBidRequest = adRequestInput.getBidRequest();
        assertNull(actualBidRequest.getRegs().coppa);
    }

    @Test
    public void whenAppendParametersAndTargetingParamsWereSet_TargetingParamsWereAppend()
    throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        TargetingParams.setUserId(USER_ID);
        TargetingParams.setUserAge(USER_AGE);
        TargetingParams.addUserKeyword(USER_KEYWORDS);
        TargetingParams.setUserCustomData(USER_CUSTOM);
        TargetingParams.setGender(TargetingParams.GENDER.MALE);
        TargetingParams.setBuyerId(USER_BUYER_ID);
        TargetingParams.setUserExt(new Ext());
        TargetingParams.setUserLatLng(USER_LAT, USER_LON);


        ExternalUserId.UniqueId uid1 = new ExternalUserId.UniqueId("11", 111);
        uid1.setExt(new HashMap() {{
            put("category", "shopping");
        }});
        ExternalUserId.UniqueId uid2 = new ExternalUserId.UniqueId("12", 222);
        ExternalUserId id1 = new ExternalUserId("adserver1.com", Arrays.asList(uid1, uid2));
        id1.setExt(new HashMap() {{
            put("user", "1000");
        }});

        ExternalUserId.UniqueId uid3 = new ExternalUserId.UniqueId("22", 333);
        ExternalUserId id2 = new ExternalUserId("adserver2.com", List.of(uid3));

        // With empty unique ids, must be ignored
        ExternalUserId id3 = new ExternalUserId("adserver3.com", List.of());

        TargetingParams.setExternalUserIds(Arrays.asList(id1, id2, id3));

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        User actualUser = adRequestInput.getBidRequest().getUser();
        User expectedUser = getExpectedUser();
        assertEquals(expectedUser.getJsonObject().toString(), actualUser.getJsonObject().toString());
    }

    @Test
    public void appendContextKeywordsAndData() throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        adConfiguration.addExtKeyword("adUnitContextKeyword1");
        adConfiguration.addExtKeyword("adUnitContextKeyword2");

        adConfiguration.addExtData("adUnitContextDataKey1", "adUnitContextDataValue1");
        adConfiguration.addExtData("adUnitContextDataKey2", "adUnitContextDataValue2");

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest actualRequest = adRequestInput.getBidRequest();
        BidRequest expectedRequest = getExpectedBidRequest(adConfiguration, actualRequest.getId());
        assertEquals(expectedRequest.getJsonObject().toString(), actualRequest.getJsonObject().toString());
    }

    @Test
    public void appendTargetingContextKeywords() throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        TargetingParams.addContextKeyword("contextKeyword1");
        TargetingParams.addContextKeyword("contextKeyword2");

        AppInfoParameterBuilder builder = new AppInfoParameterBuilder(adConfiguration);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        JSONObject appJson = adRequestInput.getBidRequest().getApp().getJsonObject();
        assertTrue(appJson.has("keywords"));
        assertEquals("contextKeyword1,contextKeyword2", appJson.getString("keywords"));
    }

    @Test
    public void whenAppendParametersAndSendMraidSupportParamsFalse_NoMraidApi() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        PrebidMobile.sendMraidSupportParams = false;

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Imp actualImp = adRequestInput.getBidRequest().getImp().get(0);
        assertEquals(Arrays.toString(new int[]{7}), Arrays.toString(actualImp.banner.api));
    }

    @Test
    public void whenAppendParametersAndUseExternalBrowserFalseAndBrowserActivityAvailable_ClickBrowserEqualsZero() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        PrebidMobile.useExternalBrowser = false;

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Imp actualImp = adRequestInput.getBidRequest().getImp().get(0);
        assertEquals(0, actualImp.clickBrowser.intValue());
    }

    @Test
    public void whenAppendParametersAndUseExternalBrowserTrueAndBrowserActivityAvailable_ClickBrowserEqualsOne() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        PrebidMobile.useExternalBrowser = true;

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Imp actualImp = adRequestInput.getBidRequest().getImp().get(0);
        assertEquals(1, actualImp.clickBrowser.intValue());
    }

    @Test
    public void whenAppendParametersAndUseExternalBrowserFalseAndBrowserActivityNotAvailable_ClickBrowserEqualsOne() {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setAdFormat(AdFormat.BANNER);
        adConfiguration.addSize(new AdSize(320, 50));

        PrebidMobile.useExternalBrowser = false;

        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        Imp actualImp = adRequestInput.getBidRequest().getImp().get(0);
        assertEquals(1, actualImp.clickBrowser.intValue());
    }

    @Test
    public void whenAppendParametersAndTargetingAccessControlNotEmpty_BiddersAddedToExt()
    throws JSONException {
        TargetingParams.addBidderToAccessControlList("bidder");

        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setConfigId("config");
        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest bidRequest = adRequestInput.getBidRequest();
        JSONObject prebidJson = bidRequest.getExt().getJsonObject().getJSONObject("prebid");
        assertTrue(prebidJson.has("data"));
        JSONArray biddersArray = prebidJson.getJSONObject("data").getJSONArray("bidders");
        assertEquals("bidder", biddersArray.get(0));
    }

    @Test
    public void whenAppendParametersAndTargetingUserDataNotEmpty_UserDataAddedToUserExt()
            throws JSONException {
        TargetingParams.addUserData("user", "userData");

        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setConfigId("config");
        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        User user = adRequestInput.getBidRequest().getUser();
        assertTrue(user.getExt().getMap().containsKey("data"));
        JSONObject userDataJson = (JSONObject) user.getExt().getMap().get("data");
        assertTrue(userDataJson.has("user"));
        assertEquals("userData", userDataJson.getJSONArray("user").get(0));
    }

    @Test
    public void whenAppendUserData_UserDataAddedToUser()
            throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.setConfigId("config");
        DataObject dataObject = new DataObject();
        String testName = "testDataObject";
        dataObject.setName(testName);
        adConfiguration.addUserData(dataObject);
        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        User user = adRequestInput.getBidRequest().getUser();
        assertEquals(1, user.dataObjects.size());
        JSONObject jsonUser = user.getJsonObject();
        assertTrue(jsonUser.has("data"));
        JSONArray jsonData = jsonUser.getJSONArray("data");
        JSONObject jsonDataObject = jsonData.getJSONObject(0);
        assertTrue(jsonDataObject.has("name"));
        String dataName = jsonDataObject.getString("name");
        assertEquals(testName, dataName);
    }

    @Test
    public void whenAppendParametersAndAdConfigContextDataNotEmpty_ContextDataAddedToImpExt()
    throws JSONException {
        AdUnitConfiguration adConfiguration = new AdUnitConfiguration();
        adConfiguration.addExtData("context", "contextData");
        BasicParameterBuilder builder = new BasicParameterBuilder(adConfiguration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        org.prebid.mobile.rendering.models.openrtb.bidRequests.Ext impExt = adRequestInput.getBidRequest().getImp().get(0).getExt();

        Map<String, Object> impExtMap = impExt.getMap();
        assertTrue(impExtMap.containsKey("data"));

        JSONObject dataJson = (JSONObject) impExtMap.get("data");
        assertTrue(dataJson.has("context"));
        assertEquals("contextData", dataJson.getJSONArray("context").get(0));
    }

    @Test
    public void testMultiFormatAdUnit_bannerAndVideoObjectsAreNotNull() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setAdUnitFormats(EnumSet.of(AdUnitFormat.DISPLAY, AdUnitFormat.VIDEO));

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, null, false);

        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp firstImp = bidRequest.getImp().iterator().next();

        assertNotNull(firstImp);

        assertNull(firstImp.nativeObj);
        assertNotNull(firstImp.banner);
        assertNotNull(firstImp.video);
    }

    @Test
    public void testNativeAdUnit_nativeObjectIsNotNull() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.addAdFormat(AdFormat.NATIVE);

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, null, false);
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp firstImp = bidRequest.getImp().iterator().next();

        assertNotNull(firstImp);

        assertNotNull(firstImp.nativeObj);
        assertNull(firstImp.banner);
        assertNull(firstImp.video);
    }

    @Test
    public void testOriginalApiVideoParameters_empty() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(true);
        configuration.setAdFormat(AdFormat.VAST);

        BasicParameterBuilder builder = new BasicParameterBuilder(
            configuration,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp imp = bidRequest.getImp().iterator().next();

        Video video = imp.getVideo();
        assertNotNull(video);
        assertNull(video.w);
        assertNull(video.h);
        assertNotNull(video.delivery);

        assertNull(video.placement);
        assertNull(video.mimes);
        assertNull(video.minduration);
        assertNull(video.maxduration);
        assertNull(video.protocols);
        assertNull(video.api);
        assertNull(video.linearity);
        assertNull(video.minbitrate);
        assertNull(video.maxbitrate);
        assertNull(video.playbackmethod);
        assertNull(video.pos);
        assertNull(video.playbackend);
        assertNull(video.startDelay);
    }

    @Test
    public void testOriginalApiVideoParameters_full() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(true);
        configuration.setAdFormat(AdFormat.VAST);
        configuration.setVideoParameters(createFullVideoParameters());

        BasicParameterBuilder builder = new BasicParameterBuilder(
            configuration,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp imp = bidRequest.getImp().iterator().next();

        Video video = imp.getVideo();
        assertNotNull(video);
        assertNotNull(video.w);
        assertNotNull(video.h);
        assertNotNull(video.delivery);
        assertEquals(new Integer(2), video.placement);

        assertEquals(new Integer(101), video.minduration);
        assertEquals(new Integer(102), video.maxduration);
        assertEquals(new Integer(201), video.minbitrate);
        assertEquals(new Integer(202), video.maxbitrate);
        assertEquals(new Integer(0), video.startDelay);
        assertEquals(new Integer(1), video.linearity);
        assertArrayEquals(new String[]{"Mime1", "Mime2"}, video.mimes);
        assertArrayEquals(new int[]{11, 12}, video.protocols);
        assertArrayEquals(new int[]{21, 22}, video.api);
        assertArrayEquals(new int[]{31, 32}, video.playbackmethod);

        assertNull(video.pos);
        assertNull(video.playbackend);
    }

    @Test
    public void testRenderingApiVideoParameters_empty() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.VAST);

        BasicParameterBuilder builder = new BasicParameterBuilder(
            configuration,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp imp = bidRequest.getImp().iterator().next();

        Video video = imp.getVideo();
        assertNotNull(video);
        assertNull(video.w);
        assertNull(video.h);
        assertEquals(new Integer(5), video.placement);
        assertEquals(new Integer(1), video.linearity);
        assertEquals(new Integer(2), video.playbackend);
        assertArrayEquals(new String[]{"video/mp4", "video/3gpp", "video/webm", "video/mkv"}, video.mimes);
        assertArrayEquals(new int[]{2, 5}, video.protocols);
        assertArrayEquals(new int[]{3}, video.delivery);

        assertNull(video.minduration);
        assertNull(video.maxduration);
        assertNull(video.api);
        assertNull(video.minbitrate);
        assertNull(video.maxbitrate);
        assertNull(video.playbackmethod);
        assertNull(video.pos);
        assertNull(video.startDelay);
    }


    @Test
    public void testRenderingApiVideoParameters_full() {
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.VAST);
        configuration.setVideoParameters(createFullVideoParameters());

        BasicParameterBuilder builder = new BasicParameterBuilder(
            configuration,
            context.getResources(),
            browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);
        BidRequest bidRequest = adRequestInput.getBidRequest();
        Imp imp = bidRequest.getImp().iterator().next();

        Video video = imp.getVideo();
        assertNotNull(video);
        assertNotNull(video.w);
        assertNotNull(video.h);
        assertEquals(new Integer(5), video.placement);

        assertEquals(new Integer(1), video.linearity);
        assertEquals(new Integer(2), video.playbackend);
        assertArrayEquals(new int[]{3}, video.delivery);
        assertArrayEquals(new String[]{"video/mp4", "video/3gpp", "video/webm", "video/mkv"}, video.mimes);
        assertArrayEquals(new int[]{2, 5}, video.protocols);

        assertNull(video.minduration);
        assertNull(video.maxduration);
        assertNull(video.api);
        assertNull(video.minbitrate);
        assertNull(video.maxbitrate);
        assertNull(video.playbackmethod);
        assertNull(video.pos);
        assertNull(video.startDelay);
    }

    @Test
    public void whenSetPluginRendererListAndOriginalAdUnitTrue_pluginRendererListIsNull() throws JSONException {
        // Given
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(true);
        configuration.setAdFormat(AdFormat.BANNER);
        String unwantedObjectNodeKey = "sdk";

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        BidRequest bidRequest = new BidRequest();
        String actualBidRequest = bidRequest.getJsonObject().toString();

        // When
        builder.appendBuilderParameters(adRequestInput);

        // Then
        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        assertFalse(prebidObj.has(unwantedObjectNodeKey));
        assertEquals(actualBidRequest, bidRequest.getJsonObject().toString());
    }

    @Test
    public void whenSetPluginRendererListAndPluginIsDefaultOnly_pluginRendererListIsNull() throws JSONException {
        // Given
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.BANNER);
        String unwantedObjectNodeKey = "sdk";

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        BidRequest bidRequest = new BidRequest();
        String actualBidRequest = bidRequest.getJsonObject().toString();

        // When
        builder.appendBuilderParameters(adRequestInput);

        // Then
        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        assertFalse(prebidObj.has(unwantedObjectNodeKey));
        assertEquals(actualBidRequest, bidRequest.getJsonObject().toString());
    }

    @Test
    public void whenSetPluginRendererList_pluginRendererIsIndexed() throws JSONException {
        // Given
        PrebidMobile.registerPluginRenderer(otherPlugin);
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.BANNER);

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        // When
        builder.appendBuilderParameters(adRequestInput);

        // Then
        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        JSONObject sdkObj = prebidObj.getJSONObject("sdk");
        JSONArray renderersObj = sdkObj.getJSONArray(PluginRendererList.RENDERERS_KEY);
        // Default plugin is indexed and additional plugin is indexed
        assertTrue(renderersObj.length() == 2);
        assertEquals(((JSONObject)renderersObj.get(0)).get("name"), otherPlugin.getName());
        assertEquals(((JSONObject)renderersObj.get(1)).get("name"), PrebidMobilePluginRegister.PREBID_MOBILE_RENDERER_NAME);
    }

    @Test
    public void whenSetAuctionSettingsId_storedRequestIdEqualsToTheAuctionsSettingsId() throws JSONException {
        // Given
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.BANNER);

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        String expectedStoredRequestId = "test-auction-settings-id";
        String serverAccountId = "test-prebid-server-account-id";

        PrebidMobile.setAuctionSettingsId(expectedStoredRequestId);
        PrebidMobile.setPrebidServerAccountId(serverAccountId);

        // When

        builder.appendBuilderParameters(adRequestInput);

        // Then
        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        String id = prebidObj.getJSONObject("storedrequest").getString("id");

        assertNotNull(PrebidMobile.getAuctionSettingsId());
        assertEquals(expectedStoredRequestId, id);
        assertNotEquals(serverAccountId, id);
    }

    @Test
    public void whenSetOnlyServerAccountId_storedRequestIdEqualsToTheServerAccountId() throws JSONException {
        // Given
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.BANNER);

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        String serverAccountId = "test-prebid-server-account-id";

        PrebidMobile.setPrebidServerAccountId(serverAccountId);

        // When

        builder.appendBuilderParameters(adRequestInput);

        // Then

        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        String id = prebidObj.getJSONObject("storedrequest").getString("id");

        assertNull(PrebidMobile.getAuctionSettingsId());
        assertEquals(serverAccountId, id);
    }

    @Test
    public void whenSetInvalidAuctionSettingsId_storedRequestIdEqualsToTheServerAccountId() throws JSONException {
        // Given
        AdUnitConfiguration configuration = new AdUnitConfiguration();
        configuration.setIsOriginalAdUnit(false);
        configuration.setAdFormat(AdFormat.BANNER);

        BasicParameterBuilder builder = new BasicParameterBuilder(configuration, context.getResources(), false);
        AdRequestInput adRequestInput = new AdRequestInput();

        String settingsId = "";
        String serverAccountId = "test-prebid-server-account-id";

        PrebidMobile.setAuctionSettingsId(settingsId);
        PrebidMobile.setPrebidServerAccountId(serverAccountId);

        // When

        builder.appendBuilderParameters(adRequestInput);

        // Then

        JSONObject prebidObj = (JSONObject) adRequestInput.getBidRequest().getExt().getMap().get("prebid");
        String id = prebidObj.getJSONObject("storedrequest").getString("id");

        assertNotNull(PrebidMobile.getAuctionSettingsId());
        assertEquals(serverAccountId, id);
    }

    @Test
    public void instlParameter_notInterstitial() {
        List<EnumSet<AdFormat>> formatsList = Arrays.asList(
                EnumSet.of(AdFormat.BANNER),
                EnumSet.of(AdFormat.VAST),
                EnumSet.of(AdFormat.BANNER, AdFormat.VAST),
                EnumSet.of(AdFormat.NATIVE)
        );
        for (EnumSet<AdFormat> formats : formatsList) {
            AdUnitConfiguration config = new AdUnitConfiguration();
            config.setAdFormats(formats);

            BidRequest actualRequest = getActualRequest(config);

            Integer instl = actualRequest.getImp().get(0).instl;
            assertEquals(Integer.valueOf(0), instl);
        }
    }

    @Test
    public void instlParameter_interstitial() {
        List<EnumSet<AdFormat>> formatsList = Arrays.asList(
                EnumSet.of(AdFormat.INTERSTITIAL),
                EnumSet.of(AdFormat.INTERSTITIAL, AdFormat.VAST)
        );
        for (EnumSet<AdFormat> formats : formatsList) {
            AdUnitConfiguration config = new AdUnitConfiguration();
            config.setAdFormats(formats);

            BidRequest actualRequest = getActualRequest(config);

            Integer instl = actualRequest.getImp().get(0).instl;
            assertEquals(Integer.valueOf(1), instl);
        }
    }


    @Test
    public void setRewarded_rwdd1() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();
        config.setRewarded(true);

        JSONObject impJson = getImpJson(config);

        assertEquals(1, impJson.optInt("rwdd"));
    }

    @Test
    public void notRewarded_rwdd0() throws JSONException {
        AdUnitConfiguration config = new AdUnitConfiguration();

        JSONObject impJson = getImpJson(config);

        assertEquals(-1, (impJson.optInt("rwdd", -1)));
    }

    private JSONObject getImpJson(AdUnitConfiguration config) throws JSONException {
        AdRequestInput adRequestInput = new AdRequestInput();
        BasicParameterBuilder builder = new BasicParameterBuilder(config, context.getResources(), false);
        builder.appendBuilderParameters(adRequestInput);

        return adRequestInput.getBidRequest().getImp().get(0).getJsonObject();
    }

    private BidRequest getActualRequest(AdUnitConfiguration config) {
        BasicParameterBuilder builder = new BasicParameterBuilder(
                config,
                context.getResources(),
                browserActivityAvailable
        );
        AdRequestInput adRequestInput = new AdRequestInput();
        builder.appendBuilderParameters(adRequestInput);

        return adRequestInput.getBidRequest();
    }

    private VideoParameters createFullVideoParameters() {
        ArrayList<String> mimes = new ArrayList<>(2);
        mimes.add("Mime1");
        mimes.add("Mime2");
        VideoParameters parameters = new VideoParameters(mimes);

        parameters.setMinDuration(101);
        parameters.setMaxDuration(102);
        parameters.setMinBitrate(201);
        parameters.setMaxBitrate(202);
        parameters.setPlacement(Signals.Placement.InBanner);
        parameters.setPlcmt(Signals.Plcmt.Standalone);
        parameters.setLinearity(1);
        parameters.setStartDelay(Signals.StartDelay.PreRoll);

        ArrayList<Signals.Protocols> protocols = new ArrayList<>(2);
        protocols.add(new Signals.Protocols(11));
        protocols.add(new Signals.Protocols(12));
        parameters.setProtocols(protocols);

        ArrayList<Signals.Api> api = new ArrayList<>(2);
        api.add(new Signals.Api(21));
        api.add(new Signals.Api(22));
        parameters.setApi(api);

        ArrayList<Signals.PlaybackMethod> playbackMethods = new ArrayList<>(2);
        playbackMethods.add(new Signals.PlaybackMethod(31));
        playbackMethods.add(new Signals.PlaybackMethod(32));
        parameters.setPlaybackMethod(playbackMethods);

        parameters.setAdSize(new AdSize(320, 480));

        return parameters;
    }

    private BidRequest getExpectedBidRequest(
        AdUnitConfiguration adConfiguration,
        String uuid
    ) {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setId(uuid);
        boolean isVideo = adConfiguration.isAdType(AdFormat.VAST);
        bidRequest.getExt().put(
                "prebid",
                Prebid.getJsonObjectForBidRequest(PrebidMobile.getPrebidServerAccountId(), isVideo, adConfiguration)
        );
        //if coppaEnabled - set 1, else No coppa is sent
        if (PrebidMobile.isCoppaEnabled) {
            bidRequest.getRegs().coppa = 1;
        }

        Imp imp = getExpectedImp(adConfiguration, uuid);
        bidRequest.getImp().add(imp);

        Source source = bidRequest.getSource();
        source.setTid(uuid);
        source.getExt().put(KEY_OM_PARTNER_NAME, OmAdSessionManager.PARTNER_NAME);
        source.getExt().put(KEY_OM_PARTNER_VERSION, OmAdSessionManager.PARTNER_VERSION);

        bidRequest.getUser();

        return bidRequest;
    }

    private Imp getExpectedImp(AdUnitConfiguration adConfiguration, String uuid) {
        Imp imp = new Imp();

        imp.displaymanager = BasicParameterBuilder.DISPLAY_MANAGER_VALUE;
        imp.displaymanagerver = PrebidMobile.SDK_VERSION;

        if (!adConfiguration.isAdType(AdFormat.VAST)) {
            imp.secure = 1;
        }
        //Send 1 for interstitial/interstitial video and 0 for banners
        boolean isInterstitial = adConfiguration.isAdType(AdFormat.INTERSTITIAL);
        imp.instl = isInterstitial ? 1 : 0;

        // 0 == embedded, 1 == native
        imp.clickBrowser = !PrebidMobile.useExternalBrowser && browserActivityAvailable ? 0 : 1;
        imp.id = uuid;
        imp.getExt().put("prebid", Prebid.getJsonObjectForImp(adConfiguration));

        if (adConfiguration.isAdType(AdFormat.VAST)) {
            imp.video = getExpectedVideoImpValues(imp, adConfiguration);
        } else {
            imp.banner = getExpectedBannerImpValues(imp, adConfiguration);
        }

        final String pbAdSlot = adConfiguration.getPbAdSlot();
        if (pbAdSlot != null) {
            JSONObject data = new JSONObject();
            Utils.addValue(data, "adslot", pbAdSlot);
            Utils.addValue(data, "pbadslot", pbAdSlot);
            imp.getExt().put("data", data);
        }

        appendImpExtParameters(imp, adConfiguration);

        return imp;
    }

    private void appendImpExtParameters(Imp imp, AdUnitConfiguration config) {
        try {
            Map<String, Set<String>> contextDataDictionary = config.getExtDataDictionary();
            if (contextDataDictionary.size() > 0) {
                JSONObject dataJson = new JSONObject();
                for (String key : contextDataDictionary.keySet()) {
                    dataJson.put(key, new JSONArray(contextDataDictionary.get(key)));
                }
                imp.getExt().put("data", dataJson);
            }

            Set<String> contextKeywordsSet = config.getExtKeywordsSet();
            if (contextKeywordsSet.size() > 0) {
                String join = stringsToCommaSeparatedString(contextKeywordsSet);
                imp.getExt().put("keywords", join);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String stringsToCommaSeparatedString(Set<String> strings) {
        if (strings.size() == 0) {
            return "";
        }

        int index = 0;
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            if (index != 0) {
                builder.append(",");
            }

            builder.append(string);
            index++;
        }

        return builder.toString();
    }

    private Banner getExpectedBannerImpValues(Imp imp, AdUnitConfiguration adConfiguration) {
        Banner banner = new Banner();
        banner.api = new int[]{3, 5, 6, 7};

        if (adConfiguration.isAdType(AdFormat.BANNER)) {
            for (AdSize size : adConfiguration.getSizes()) {
                banner.addFormat(size.getWidth(), size.getHeight());
            }
        }

        if (adConfiguration.isAdPositionValid()) {
            banner.pos = adConfiguration.getAdPositionValue();
        }
        return banner;
    }

    private Video getExpectedVideoImpValues(Imp imp, AdUnitConfiguration adConfiguration) {
        Video video = new Video();
        //Common values for all video reqs
        video.mimes = BasicParameterBuilder.SUPPORTED_VIDEO_MIME_TYPES;
        video.protocols = BasicParameterBuilder.SUPPORTED_VIDEO_PROTOCOLS;
        video.linearity = BasicParameterBuilder.VIDEO_LINEARITY_LINEAR;

        //Interstitial video specific values
        video.playbackend = VIDEO_INTERSTITIAL_PLAYBACK_END;//On Leaving Viewport or when Terminated by User
        video.delivery = new int[]{BasicParameterBuilder.VIDEO_DELIVERY_DOWNLOAD};
        video.pos = AdPosition.FULLSCREEN.getValue();

        if (adConfiguration.getVideoParameters() != null) {
            if (adConfiguration.getVideoParameters().getPlacement() != null) {
                video.plcmt = adConfiguration.getVideoParameters().getPlacement().getValue();
            }
        }
        if (!adConfiguration.isPlacementTypeValid()) {
            video.placement = VIDEO_INTERSTITIAL_PLACEMENT;
            Configuration deviceConfiguration = context.getResources().getConfiguration();
        }
        else {
            video.placement = adConfiguration.getPlacementTypeValue();
            for (AdSize size : adConfiguration.getSizes()) {
                video.w = size.getWidth();
                video.h = size.getHeight();
                break;
            }
        }

        return video;
    }

    private User getExpectedUser() {
        final User user = new User();

        user.id = USER_ID;
        user.yob = USER_YOB;
        user.keywords = USER_KEYWORDS;
        user.customData = USER_CUSTOM;
        user.gender = USER_GENDER;
        user.buyerUid = USER_BUYER_ID;
        List<ExternalUserId> extendedUserIds = TargetingParams.getExternalUserIds();
        if (extendedUserIds != null && extendedUserIds.size() > 0) {
            user.ext = new Ext();
            JSONArray idsJson = new JSONArray();
            for (ExternalUserId id : extendedUserIds) {
                JSONObject idJson = id.getJson();
                if (idJson != null) {
                    idsJson.put(idJson);
                }
            }
            user.ext.put("eids", idsJson);
        }

        final Geo userGeo = user.getGeo();
        userGeo.lat = USER_LAT;
        userGeo.lon = USER_LON;

        return user;
    }

}