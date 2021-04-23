package com.posse.android1.notes.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.ColorCircles;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private PreferencesDataWorker mPreferences;
    private MaterialTextView mColorCaption;
    private CardView mCardHeader;
    private CardView mCardNote;
    private CardView mCardColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new PreferencesDataWorker(requireActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        initHeaderSettings(view, isLandscape);
        initNoteSettings(view, isLandscape);
        initColorSettings(view);
        setCardsColor(mPreferences.getDefaultNoteColor());
        return view;
    }

    private void initHeaderSettings(View view, boolean isLandscape) {
        mCardHeader = view.findViewById(R.id.settings_card_header);
        float headerTextSize = mPreferences.getHeaderTextSize();
        TextView headerText = view.findViewById(R.id.settings_header_caption);
        int headerSize = (int) requireActivity().getResources().getDimension(R.dimen.max_header_size);
        if (!isLandscape) headerSize *= 2;
        headerText.setMinimumHeight(headerSize);
        headerText.setTextSize(headerTextSize);
        Slider headerSlider = view.findViewById(R.id.settings_header_size_slider);
        headerSlider.setValue(headerTextSize);
        headerSlider.addOnChangeListener((slider, value, fromUser) -> {
            headerText.setTextSize(value);
            mPreferences.setHeaderTextSize(value);
        });
    }

    private void initNoteSettings(View view, boolean isLandscape) {
        mCardNote = view.findViewById(R.id.settings_card_note);
        float noteTextSize = mPreferences.getNoteTextSize();
        TextView noteText = view.findViewById(R.id.settings_note_caption);
        int noteSize = (int) requireActivity().getResources().getDimension(R.dimen.max_note_size);
        if (!isLandscape) noteSize *= 2;
        noteText.setMinimumHeight(noteSize);
        noteText.setTextSize(noteTextSize);
        Slider noteSlider = view.findViewById(R.id.settings_note_size_slider);
        noteSlider.setValue(noteTextSize);
        noteSlider.addOnChangeListener((slider, value, fromUser) -> {
            noteText.setTextSize(value);
            mColorCaption.setTextSize(value);
            mPreferences.setNoteTextSize(value);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initColorSettings(View view) {
        mCardColor = view.findViewById(R.id.settings_card_color);
        mColorCaption = view.findViewById(R.id.settings_color_caption);
        mColorCaption.setTextSize(mPreferences.getNoteTextSize());
        Context context = view.getContext();
        ColorCircles circles = new ColorCircles(requireActivity());
        LinearLayoutCompat linearLayout = view.findViewById(R.id.settings_color);
        ArrayList<Integer> colors = circles.getColors();
        colors.remove(0);
        int screenWidth = calculateAvailableCardWidth(context);
        int circleSize = circles.getCircleSize() + circles.getPadding() * 3;
        int circlesWidth = colors.size() * circleSize;
        int multiply = screenWidth / circlesWidth + 1;
        int rows = colors.size() / 6 * multiply + 1;
        for (int i = 0; i < rows; i++) {
            LinearLayoutCompat colorCircles = new LinearLayoutCompat(context);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            colorCircles.setLayoutParams(params);
            colorCircles.setOrientation(LinearLayoutCompat.HORIZONTAL);
            colorCircles.setGravity(Gravity.CENTER);
            int limit = (i + 1) * 6 * multiply;
            if (limit > colors.size()) limit = colors.size();
            for (int j = i * 6 * multiply; j < limit; j++) {
                View circle = circles.getCircle(j);
                int finalJ = j;
                circle.setOnTouchListener((v, event) -> {
                    int color = colors.get(finalJ);
                    mPreferences.setDefaultNoteColor(color);
                    setCardsColor(color);
                    return true;
                });
                colorCircles.addView(circle);
            }
            linearLayout.addView(colorCircles);
        }
    }

    private int calculateAvailableCardWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int[] attrs = {android.R.attr.layout_margin, R.attr.contentPadding};
        TypedArray ta = context.obtainStyledAttributes(R.style.CardStyle, attrs);
        int layoutMargin = ta.getDimensionPixelSize(0, 0);
        //noinspection ResourceType
        int padding = ta.getDimensionPixelSize(1, 0);
        ta.recycle();
        return displayMetrics.widthPixels - layoutMargin - padding * 2;
    }

    private void setCardsColor(int color) {
        if (color != -1) {
            color = ResourcesCompat.getColor(getResources(), color, null);
        } else {
            TypedArray array = requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        mCardHeader.setCardBackgroundColor(color);
        mCardNote.setCardBackgroundColor(color);
        mCardColor.setCardBackgroundColor(color);
    }
}