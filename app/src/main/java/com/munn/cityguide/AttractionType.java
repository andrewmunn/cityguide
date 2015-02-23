package com.munn.cityguide;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * The tabs we show in City guide are defined and ordered here.
 */
public enum AttractionType implements Parcelable {

    // The order of the attraction types are the order the pages are displayed.
    BAR(ImmutableList.of("bar", "night_club")),
    BISTRO(ImmutableList.of("restaurant", "meal_delivery", "meal_takeaway")),
    CAFE(ImmutableList.of("cafe"));

    public ImmutableList<String> matchingPlaceTypes;

    AttractionType(ImmutableList<String> matchingPlaceTypes) {
        this.matchingPlaceTypes = matchingPlaceTypes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<AttractionType> CREATOR =
            new Parcelable.Creator<AttractionType>() {

        public AttractionType createFromParcel(Parcel in) {
            AttractionType attractionType = AttractionType.values()[in.readInt()];
            return attractionType;
        }

        public AttractionType[] newArray(int size) {
            return new AttractionType[size];
        }

    };
}
