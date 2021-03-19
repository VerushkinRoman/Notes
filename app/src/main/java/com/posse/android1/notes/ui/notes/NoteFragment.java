package com.posse.android1.notes.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;

public class NoteFragment extends Fragment {

    public static final String NOTE_INDEX = "NoteIndex";
    private int mNoteIndex;

    public static NoteFragment newInstance(int index) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(NOTE_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteIndex = getArguments().getInt(NOTE_INDEX, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] notes = getResources().getStringArray(R.array.notes);
        String[] header = getResources().getStringArray(R.array.notes_list);
        MaterialTextView noteText = view.findViewById(R.id.note_text);
        noteText.setTextSize(30);
        noteText.setText(notes[mNoteIndex]);
        MaterialTextView noteHeading = view.findViewById(R.id.note_heading);
        noteHeading.setText(header[mNoteIndex]);
    }
}