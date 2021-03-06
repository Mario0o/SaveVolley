/*
 * Copyright (C) 2016 CaMnter yuanyu.camnter@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.camnter.savevolley.samples.gson;

import android.support.annotation.NonNull;
import com.camnter.savevolley.hurl.Request.Method;
import com.camnter.savevolley.hurl.VolleyError;
import com.camnter.savevolley.hurl.agera.core.SaveVolley;
import com.camnter.savevolley.hurl.agera.gson.SaveVolleys;
import com.camnter.savevolley.samples.BaseTestActivity;
import com.camnter.savevolley.samples.bean.GankData;
import com.camnter.savevolley.samples.bean.GankResultData;
import com.google.android.agera.Function;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import java.util.concurrent.Executor;

import static com.camnter.savevolley.hurl.agera.gson.SaveVolleyCompilerStates.GSON;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Description：HurlAgeraGsonActivity
 * Created by：CaMnter
 * Time：2016-06-23 23:06
 */

public class HurlAgeraGsonActivity extends BaseTestActivity {
    private static final GankResultData INITIAL_VALUE = new GankResultData();
    private final Executor executor = newSingleThreadExecutor();


    @Override protected void initData() {
        SaveVolley saveVolley = SaveVolleys
            .<GankData>request(TEST_URL)
            .method(Method.GET)
            .parseStyle(GSON)
            .classOf(GankData.class)
            .createRequest()
            .context(this)
            .compile();

        final Repository<GankResultData> repository = Repositories
            .repositoryWithInitialValue(INITIAL_VALUE)
            .observe(saveVolley.getReservoir())
            .onUpdatesPerLoop()
            .goTo(executor)
            .attemptGetFrom(saveVolley.getReservoir())
            .orSkip()
            .thenAttemptTransform(new Function<Object, Result<GankResultData>>() {
                /**
                 * Returns the result of applying this function to {@code input}.
                 */
                @NonNull @Override public Result<GankResultData> apply(@NonNull Object input) {
                    if (input instanceof GankData) {
                        return Result.success(((GankData) input).results.get(0));
                    } else if (input instanceof VolleyError) {
                        return Result.failure((VolleyError) input);
                    }
                    return Result.failure();
                }
            })
            .orSkip()
            .compile();

        repository.addUpdatable(new Updatable() {
            @Override public void update() {
                getContentText.setText(repository.get().toString());
            }
        });
    }
}
