package com.kmshack.newsstand;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();

    private View mHeader;

    private ScrollView mListView;

    private int mActionBarHeight;
    private int mMinHeaderHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private ImageView mHeaderLogo;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    private TypedValue mTypedValue = new TypedValue();
    private SpannableString mSpannableString;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();

        setContentView(R.layout.activity_main);

        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
        mHeader = findViewById(R.id.header);

        mListView = (ScrollView) findViewById(R.id.pager);
        mSpannableString = new SpannableString(getString(R.string.actionbar_title));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
        View placeHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mListView.getScrollY();
//                int scrollY = getScrollY(view);
                ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
                float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mHeaderLogo, findViewById(R.id.header_logo1), sSmoothInterpolator.getInterpolation(ratio));
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));

            }
        });


//        mListView = (ListView) findViewById(R.id.pager);
//        mListView.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, android.R.id.text1, mListItems));
//
//        mSpannableString = new SpannableString(getString(R.string.actionbar_title));
//        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
//
//
//        View placeHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
//        mListView.addHeaderView(placeHolderView);
//        mListView.setOnScrollListener(this);
//
//        ArrayList<String> mListItems = new ArrayList<String>();
//
//        for (int i = 1; i <= 100; i++) {
//            mListItems.add(i + ". item - currnet page: " + (1));
//        }

//		ViewHelper.setAlpha(getActionBarIconView(), 0f);
//        getSupportActionBar().setBackgroundDrawable(null);
    }


    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        ViewHelper.setTranslationX(view1, translationX);
        ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
        ViewHelper.setScaleX(view1, scaleX);
        ViewHelper.setScaleY(view1, scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        if (view == null) return new RectF();
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        }

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    private void setTitleAlpha(float alpha) {
        if (mAlphaForegroundColorSpan == null) return;
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getSupportActionBar().setTitle(mSpannableString);
    }

}
