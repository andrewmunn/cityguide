package com.munn.cityguide;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A sliding tab view that animates a tab selector pill. This view ISN'T general purpose:
 * Next steps would be to support an arbitrary number of tabs and let users choose the titles for
 * each tab. Adding that functionality is straightforward and left as an exercise to the reader ;-)
 */
public class SlidingTabView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private static final int ANIMATION_DURATION_MS = 200;

    private boolean mIsAnimatingToIndex;

    public interface TabClickListener {
        public void onTabClick(int index);
    }

    List<TextView> mTabButtonList = Lists.newArrayList();
    @InjectView(R.id.slider) TextView mSlider;
    @InjectView(R.id.tab_buttons_container) LinearLayout mTabsContainer;

    private TabClickListener mTabClickListener;
    private int mCurrentTabIndex = 0;

    public SlidingTabView(Context context) {
        super(context);
        init();
    }

    public SlidingTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.sliding_tab_view, this);
        ButterKnife.inject(this);

        // Iterate over the tabs and setup on click listeners.
        for (int i = 0; i < mTabsContainer.getChildCount(); i++) {
            TextView button = (TextView) mTabsContainer.getChildAt(i);
            mTabButtonList.add(button);

            final int index = i;
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTabClickListener != null) {
                        mTabClickListener.onTabClick(index);
                    }
                }
            });
        }

        mSlider.setOnTouchListener(new OnTouchListener() {

            private float downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int currentIndex = getIndexFromSliderLocation();
                        float deltaX = event.getX() - downX;
                        float newX = Math.min(
                                Math.max(v.getTranslationX() + deltaX, 0),
                                v.getWidth() * 2);
                        v.setTranslationX(newX);
                        int newIndex = getIndexFromSliderLocation();
                        if (newIndex != currentIndex) {
                            // slider was dragged over the threshold between tabs.
                            mSlider.setText(mTabButtonList.get(newIndex).getText());
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        newIndex = getIndexFromSliderLocation();
                        animateToIndex(newIndex);
                        if (mCurrentTabIndex != newIndex) {
                            mTabClickListener.onTabClick(newIndex);
                            mCurrentTabIndex = newIndex;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private int getIndexFromSliderLocation() {
        float translationOffset = mSlider.getTranslationX() / mSlider.getWidth();
        return Math.round(translationOffset);
    }

    private void animateToIndex(int newIndex) {
        mIsAnimatingToIndex = true;

        mSlider.animate()
                .setInterpolator(new DecelerateInterpolator())
                .translationX(mSlider.getWidth() * newIndex)
                .setDuration(ANIMATION_DURATION_MS)
                .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimatingToIndex = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        mSlider.animate().start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Set the width of the slider to be the same as each of the tab selectors.
        mSlider.setWidth(mTabsContainer.getMeasuredWidth() / mTabButtonList.size());
    }

    public void setOnTabClickListener(TabClickListener listener) {
        mTabClickListener = listener;
    }

    @Override
    public void onPageScrolled(int index, float positionOffset, int positionOffsetPixels) {
        Preconditions.checkArgument(index < mTabButtonList.size());
        if (!mIsAnimatingToIndex) {
            updateSliderPosition(index, positionOffset);
        }
    }

    private void updateSliderPosition(int index, float positionOffset) {
        mCurrentTabIndex = positionOffset < 0.5f ? index : index + 1;
        TextView currentTab = mTabButtonList.get(mCurrentTabIndex);
        mSlider.setText(currentTab.getText());
        mSlider.setTranslationX(mSlider.getWidth() * (index + positionOffset));
    }

    @Override
    public void onPageSelected(int index) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}