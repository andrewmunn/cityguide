package com.munn.cityguide;


import android.location.Location;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Executes and manages requests to google places through OkHttp. Caches requests so re-fetches
 * from the same location should be cheap.
 */
@Singleton
public class AttractionFetchExecutor {

    private static final String GOOGLE_API_KEY = "PUT YE KEY HERE";

    private static final String BASE_PLACES_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    private static AttractionFetchExecutor attractionFetchExecutor;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Cache mCache;

    public static AttractionFetchExecutor initAttractionFetcher(File cacheDir) {
        Preconditions.checkNotNull(cacheDir);
        if (attractionFetchExecutor == null) {
            attractionFetchExecutor = new AttractionFetchExecutor(cacheDir);
        }
        return attractionFetchExecutor;
    }

    private AttractionFetchExecutor(File cacheDir) {
        try {
            File OkHttpCacheDir = new File(cacheDir, "okhttpcache");
            int cacheSize = 1024 * 1024; // 1MB should be enough for anybody... right?
            mCache = new Cache(OkHttpCacheDir, cacheSize);
            mOkHttpClient.setCache(mCache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EventBusProvider.getBus().register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void fetchAttractionsForLocation(Location location) {
        Preconditions.checkNotNull(location);
        beginAttractionFetch(location, AttractionType.BAR);
        beginAttractionFetch(location, AttractionType.BISTRO);
        beginAttractionFetch(location, AttractionType.CAFE);
    }

    private void beginAttractionFetch(Location location, AttractionType attractionType) {
        String url = buildRequestURL(location, attractionType.matchingPlaceTypes);
        Request request = new Request.Builder()
                .url(url)
                .build();

        AttractionFetchCallback attractionFetchCallback =
                new AttractionFetchCallback(location, attractionType);
        mOkHttpClient.newCall(request).enqueue(attractionFetchCallback);
    }

    private String buildRequestURL(Location location, ImmutableList<String> types) {
        Map<String, String> params = Maps.newHashMap();
        params.put("key", GOOGLE_API_KEY);
        params.put("location", location.getLatitude() + "," + location.getLongitude());
        params.put("radius", "5000" /* meters */); // TODO: user configurable
        params.put("types", Joiner.on("|").join(types));


        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_PLACES_URL);
        for (String param : params.keySet()) {
            urlBuilder.append(URLEncoder.encode(param))
                    .append("=")
                    .append(URLEncoder.encode(params.get(param)))
                    .append("&");
        }

        return urlBuilder.toString();
    }
}