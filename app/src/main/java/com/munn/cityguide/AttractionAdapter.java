package com.munn.cityguide;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Responsible for displaying a list of attractions.
 */
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private final Resources mResources;

    private AttractionItemResultSet mAttractionItemResultSet;

    public AttractionAdapter(Resources resources) {
        mResources = resources;
    }

    public void setAttractionResultSet(AttractionItemResultSet attractionResultSet) {
        Preconditions.checkNotNull(attractionResultSet);
        mAttractionItemResultSet = attractionResultSet;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View attractionView = inflater.inflate(R.layout.attraction_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(attractionView);
        viewHolder.icon.setImageDrawable(mResources.getDrawable(R.drawable.ic_bar));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        AttractionItem attractionItem = mAttractionItemResultSet.attractionList.get(i);
        viewHolder.name.setText(attractionItem.name);
        viewHolder.distance.setText(
                mResources.getString(R.string.attraction_distance, attractionItem.distanceMiles));
        if (attractionItem.rating != AttractionItem.NO_RATING) {
            viewHolder.ratingBar.setVisibility(View.VISIBLE);
            viewHolder.ratingBar.setRating((float) attractionItem.rating);
        } else {
            viewHolder.ratingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
       return mAttractionItemResultSet != null ? mAttractionItemResultSet.attractionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public @InjectView(R.id.attraction_name) TextView name;
        public @InjectView(R.id.attraction_distance) TextView distance;
        public @InjectView(R.id.attraction_rating) RatingBar ratingBar;
        public @InjectView(R.id.attraction_icon) ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
