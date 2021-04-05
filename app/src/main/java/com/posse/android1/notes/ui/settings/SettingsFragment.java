package com.posse.android1.notes.ui.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.R;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).changeButtonsLook(MainActivity.EDITOR_VIEW);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = root.findViewById(R.id.text_settings);
        textView.setText(getString(R.string.settings_fragment_message));
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        int view = (requireActivity().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE)
                ? MainActivity.NOTE_VIEW : MainActivity.NOTE_LIST_VIEW;
        ((MainActivity) requireActivity()).changeButtonsLook(view);
    }
}