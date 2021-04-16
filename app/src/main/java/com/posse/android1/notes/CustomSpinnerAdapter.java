package com.posse.android1.notes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<Integer> {

    public static final int PADDING = 5;
    private final ArrayList<Integer> mColors;
    private final Context mContext;
    private final int mActionBarSize;

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull ArrayList<Integer> objects) {
        super(context, 0, objects);
        mContext = context;
        mColors = objects;
        final TypedArray styledAttributes = mContext.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        View v = getCustomView(position);
        if (position == 0) v.setVisibility(View.GONE);
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = getCustomView(position);
        if (position == 0) v.setVisibility(View.GONE);
        return v;
    }

    public ImageView getCustomView(int position) {
        ImageView circle = new ImageView(mContext);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(mActionBarSize - PADDING * 6, mActionBarSize - PADDING * 6);
        int color = mColors.get(position);
        if (color != -1) {
            color = ResourcesCompat.getColor(mContext.getResources(), color, null);
        } else {
            TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        drawable.setColor(color);
        drawable.setStroke(PADDING, Color.BLACK);
        circle.setImageDrawable(drawable);
        circle.setPadding(0, PADDING, PADDING * 2, PADDING);
        return circle;
    }
}
