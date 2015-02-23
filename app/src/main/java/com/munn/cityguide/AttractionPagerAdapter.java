package com.munn.cityguide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Simple fragment pager that uses the AttractionType enum to decide what fragments to show.
 */
public class AttractionPagerAdapter extends FragmentPagerAdapter {

    public AttractionPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int page) {
        return AttractionFragment.newInstance(AttractionType.values()[page]);
    }

    @Override
    public int getCount() {
        return AttractionType.values().length;
    }
}
