package com.posse.android1.notes.ui.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;

public class SettingsFragment extends Fragment {

    private PreferencesDataWorker mPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new PreferencesDataWorker(requireActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        final float headerTextSize = mPreferences.getHeaderTextSize();
        final TextView headerText = view.findViewById(R.id.settings_header_caption);
        int headerSize = (int) requireActivity().getResources().getDimension(R.dimen.max_header_size);
        if (!isLandscape) headerSize *= 2;
        headerText.setMinimumHeight(headerSize);
        headerText.setTextSize(headerTextSize);
        final Slider headerSlider = view.findViewById(R.id.settings_header_size_slider);
        headerSlider.setValue(headerTextSize);
        headerSlider.addOnChangeListener((slider, value, fromUser) -> {
            headerText.setTextSize(value);
            mPreferences.setHeaderTextSize(value);
        });
        final float noteTextSize = mPreferences.getNoteTextSize();
        final TextView noteText = view.findViewById(R.id.settings_note_caption);
        int noteSize = (int) requireActivity().getResources().getDimension(R.dimen.max_note_size);
        if (!isLandscape) noteSize *= 2;
        noteText.setMinimumHeight(noteSize);
        noteText.setTextSize(noteTextSize);
        final Slider noteSlider = view.findViewById(R.id.settings_note_size_slider);
        noteSlider.setValue(noteTextSize);
        noteSlider.addOnChangeListener((slider, value, fromUser) -> {
            noteText.setTextSize(value);
            mPreferences.setNoteTextSize(value);
        });
        return view;
    }
}