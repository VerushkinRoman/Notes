package com.posse.android1.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

public class NotesFragment extends Fragment {

    public static final String NOTE_INDEX = "NoteIndex";
    private Note mNote;

    public static NotesFragment newInstance(Note note) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_INDEX, note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNote = getArguments().getParcelable(NOTE_INDEX);
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
        MaterialTextView noteText = view.findViewById(R.id.note_text);
        String[] notes = getResources().getStringArray(R.array.notes);
        noteText.setTextSize(30);
        noteText.setText(notes[mNote.getNoteIndex()]);
        MaterialTextView noteHeading = view.findViewById(R.id.note_heading);
        noteHeading.setText(mNote.getName());
    }
}