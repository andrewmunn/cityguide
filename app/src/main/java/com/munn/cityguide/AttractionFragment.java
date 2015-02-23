package com.munn.cityguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Renders a list of attractions that supports pull to refresh.
 */
public class AttractionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_TYPE = "arg_type";

    AttractionAdapter mAttractionAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.attraction_list) RecyclerView mAttractionList;
    private AttractionType mAttractionType;

    public static AttractionFragment newInstance(AttractionType type) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TYPE, type);
        AttractionFragment fragment = new AttractionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AttractionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttractionType = getArguments().getParcelable(ARG_TYPE);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.attraction_fragment, container, false);
        ButterKnife.inject(this, mSwipeRefreshLayout);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAttractionAdapter = new AttractionAdapter(getResources());
        mAttractionList.setAdapter(mAttractionAdapter);
        mAttractionList.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAttractionList.setLayoutManager(layoutManager);
        mAttractionList.setItemAnimator(new DefaultItemAnimator());

        return mSwipeRefreshLayout;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNewAttractionResultSet(AttractionItemResultSet attractionItemResultSet) {
        if (!attractionItemResultSet.didRequestSucceed) {
            Toast.makeText(getActivity(), R.string.fetch_failed, Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (attractionItemResultSet.attractionType != mAttractionType) {
            return;
        }

        mAttractionAdapter.setAttractionResultSet(attractionItemResultSet);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusProvider.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusProvider.getBus().unregister(this);
    }

    @Override
    public void onRefresh() {
        EventBusProvider.getBus().post(RefreshRequest.REFRESH_REQUEST);
    }
}
