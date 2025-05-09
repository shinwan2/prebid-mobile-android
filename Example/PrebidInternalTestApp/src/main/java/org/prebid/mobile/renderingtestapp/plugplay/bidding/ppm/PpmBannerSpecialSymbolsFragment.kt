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

package org.prebid.mobile.renderingtestapp.plugplay.bidding.ppm

import android.annotation.SuppressLint
import org.prebid.mobile.AdSize
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.rendering.utils.helpers.AppInfoManager
import org.prebid.mobile.renderingtestapp.utils.CommandLineArgumentParser

class PpmBannerSpecialSymbolsFragment : PpmBannerFragment() {

    private val previousAppName = AppInfoManager.getAppName()

    @SuppressLint("VisibleForTests")
    override fun initAd(): Any? {
        bannerView = BannerView(
            requireContext(),
            configId,
            AdSize(width, height)
        )
        bannerView?.setAutoRefreshDelay(refreshDelay)
        bannerView?.setBannerListener(this)
        binding.viewContainer.addView(bannerView)

        AppInfoManager.setAppName("天気")

        return bannerView
    }

    @SuppressLint("VisibleForTests")
    override fun onDestroyView() {
        super.onDestroyView()
        AppInfoManager.setAppName(previousAppName)
    }

}