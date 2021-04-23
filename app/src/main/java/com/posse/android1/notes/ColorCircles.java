package com.posse.android1.notes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class ColorCircles {

    private final ArrayList<Integer> mColorList;

    private final int mPadding;
    private final Context mContext;
    private final int mCircleSize;

    public ColorCircles(Context context) {
        mContext = context;
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mPadding = actionBarSize / 20;
        mCircleSize = actionBarSize - mPadding * 6;
        mColorList = new ArrayList<Integer>() {{
            add(android.R.color.transparent);
            add(android.R.color.holo_purple);
            add(android.R.color.holo_blue_dark);
            add(android.R.color.holo_blue_bright);
            add(android.R.color.holo_green_dark);
            add(android.R.color.holo_green_light);
            add(android.R.color.holo_orange_light);
            add(android.R.color.holo_orange_dark);
            add(android.R.color.holo_red_light);
            add(android.R.color.holo_red_dark);
            add(-1);
        }};
    }

    public ArrayList<Integer> getColors() {
        return mColorList;
    }

    public int getPadding() {
        return mPadding;
    }

    public ImageView getCircle(int position) {
        ImageView circle = new ImageView(mContext);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(mCircleSize, mCircleSize);
        int color = mColorList.get(position);
        if (color != -1) {
            color = ResourcesCompat.getColor(mContext.getResources(), color, null);
        } else {
            TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        drawable.setColor(color);
        drawable.setStroke(mPadding, Color.BLACK);
        circle.setImageDrawable(drawable);
        circle.setPadding(0, mPadding, mPadding * 3, mPadding);
        return circle;
    }

    public int getCircleSize() {
        return mCircleSize;
    }
}
