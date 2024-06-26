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

package org.prebid.mobile.rendering.networking;

import android.os.AsyncTask;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowLegacyAsyncTask;

import java.util.concurrent.Executor;

@Implements(AsyncTask.class)
@LooperMode(LooperMode.Mode.LEGACY)
public class MyShadowAsyncTask<Params, Progress, Result>
    extends ShadowLegacyAsyncTask<Params, Progress, Result> {

    @Implementation
    public AsyncTask<Params, Progress, Result> executeOnExecutor(Executor executor, Params... params) {
        return super.execute(params);
    }
}