package com.posse.android1.notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class CustomSpinnerAdapter extends ArrayAdapter<Integer> {

    private final ColorCircles mCircles;

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull ColorCircles circles) {
        super(context, 0, circles.getColors());
        mCircles = circles;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        View v = mCircles.getCircle(position);
        if (position == 0) v.setVisibility(View.GONE);
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mCircles.getCircle(position);
        if (position == 0) v.setVisibility(View.GONE);
        return v;
    }
}
