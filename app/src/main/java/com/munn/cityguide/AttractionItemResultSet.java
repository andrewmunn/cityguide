package com.munn.cityguide;

import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

/**
 * A result of a fetch of attractions.
 */
@Immutable
public class AttractionItemResultSet {

    /**
     * A list of Attractions from this result. Null if didRequestSucceed is false.
     */
    public @Nullable final ImmutableList<AttractionItem> attractionList;


    /**
     * The type of attractions this result contains.
     */
    public final AttractionType attractionType;

    /**
     * Will be true if request succeeded. If false, attractionList will be null.
     */
    public final boolean didRequestSucceed;

    public AttractionItemResultSet(
            ImmutableList<AttractionItem> attractionList,
            AttractionType attractionType,
            boolean didRequestSucceed) {
        this.attractionList = attractionList;
        this.attractionType = attractionType;
        this.didRequestSucceed = didRequestSucceed;
    }
}
