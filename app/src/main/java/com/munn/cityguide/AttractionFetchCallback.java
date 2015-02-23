package com.munn.cityguide;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Callback for a request to the Google places search API that parses the JSON.
 */
public class AttractionFetchCallback implements Callback {

    private static final float METERS_TO_MILES = 0.000621371f;

    private final Location mPhoneLocation;
    private final AttractionType mType;
    private final Handler  mHandler = new Handler();

    public AttractionFetchCallback(Location location, AttractionType type) {
        mPhoneLocation = location;
        mType = type;
    }

    @Override public void onFailure(Request request, IOException exception) {
        postFailedFetch();
        exception.printStackTrace();
    }

    @Override public void onResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            postFailedFetch();
            throw new IOException("Unexpected code " + response);
        }

        try {
            buildAttractionResultSet(response.body().string());
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private void buildAttractionResultSet(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray results = root.getJSONArray("results");
        ImmutableList.Builder<AttractionItem> attractionItems = ImmutableList.builder();
        for (int i = 0; i < results.length(); i++) {
            JSONObject attractionJSON = results.getJSONObject(i);
            String placeId = attractionJSON.getString("place_id");
            String name = attractionJSON.getString("name");
            double rating = attractionJSON.has("rating") ?
                    attractionJSON.getDouble("rating") :
                    AttractionItem.NO_RATING;
            float distanceMiles = computeDistanceInMiles(attractionJSON);
            attractionItems.add(new AttractionItem(placeId, name, distanceMiles, rating));
        }

        final AttractionItemResultSet attractionItemResultSet =
                new AttractionItemResultSet(attractionItems.build(), mType, true);

        // dispatch results to UI.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                EventBusProvider.getBus().post(attractionItemResultSet);
            }
        });
    }

    private float computeDistanceInMiles(JSONObject attractionJSON) throws JSONException{
        // TODO replace 'as the bird flies' distance with travel distance from the Google
        // distance matrix API.
        JSONObject locJSON = attractionJSON.getJSONObject("geometry").getJSONObject("location");
        Location attractionLoc = new Location("");
        attractionLoc.setLatitude(locJSON.getDouble("lat"));
        attractionLoc.setLongitude(locJSON.getDouble("lng"));
        return mPhoneLocation.distanceTo(attractionLoc) * METERS_TO_MILES;
    }

    private void postFailedFetch() {
        // dispatch failed result to UI.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                EventBusProvider.getBus().post(new AttractionItemResultSet(null, null, false));
            }
        });
    }
}
