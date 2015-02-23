package com.munn.cityguide;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * The only Activity for City guide. Each tab lives inside a Fragment inside a View pager set as
 * the content view of this activity. MainActivity is responsible for requesting the device's
 * location from Google Play Services.
 */
public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SlidingTabView.TabClickListener {

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.pager) ViewPager mPager;
    @InjectView(R.id.sliding_tab) SlidingTabView mSlidingTabView;

    AttractionPagerAdapter mAttractionPagerAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Trying out the Toolbar class! (although quite unnecessary for the app currently)
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, R.string.about, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        mAttractionPagerAdapter = new AttractionPagerAdapter(getSupportFragmentManager());
        mPager.setOnPageChangeListener(mSlidingTabView);
        mPager.setAdapter(mAttractionPagerAdapter);
        mPager.setOffscreenPageLimit(2); // ensures all the tabs are kept attached.
        mSlidingTabView.setOnTabClickListener(this);

        AttractionFetchExecutor.initAttractionFetcher(getCacheDir());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSlidingTabView.onPageSelected(mPager.getCurrentItem());
    }

    @Override
    public void onTabClick(int index) {
        mPager.setCurrentItem(index);
    }

    @Override
    public void onConnected(Bundle bundle) {
        refreshAttractions(RefreshRequest.REFRESH_REQUEST);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: handle more gracefully
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: handle more gracefully
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void refreshAttractions(RefreshRequest refreshRequest) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            EventBusProvider.getBus().post(location);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusProvider.getBus().register(this);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusProvider.getBus().unregister(this);
        mGoogleApiClient.disconnect();
    }
}
