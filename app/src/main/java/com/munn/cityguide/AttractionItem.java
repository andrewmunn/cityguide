package com.munn.cityguide;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a single attractions information.
 */
@Immutable
public class AttractionItem {

    public static final double NO_RATING = -1.0;

    /**
     * The place id is a unique identifier that we can use to fetch more data about an attraction.
     */
    public final String placeId;

    /**
     * The name of the attraction.
     */
    public final String name;

    /**
     * The straight-line distance to an attraction.
     */
    public final float distanceMiles;

    /**
     * The rating between 1.0 and 5.0 of the attraction. If no rating is available returns
     * NO_RATING
     */
    public final double rating;

    public AttractionItem(String placeId, String name, float distanceMiles, double rating) {
        this.placeId = placeId;
        this.name = name;
        this.distanceMiles = distanceMiles;
        this.rating = rating;
    }
}
