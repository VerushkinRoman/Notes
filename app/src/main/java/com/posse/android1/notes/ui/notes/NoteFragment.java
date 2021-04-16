package com.posse.android1.notes.ui.notes;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;

public class NoteFragment extends Fragment {

    public static final String KEY_NOTE_INDEX = NoteFragment.class.getCanonicalName() + "_NoteIndex";
    private Note mNote;

    public NoteFragment() {
    }

    public static NoteFragment newInstance(Note note) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_NOTE_INDEX, note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) mNote = savedInstanceState.getParcelable(KEY_NOTE_INDEX);
        else if (getArguments() != null) mNote = getArguments().getParcelable(KEY_NOTE_INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_NOTE_INDEX, mNote);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialTextView noteText = view.findViewById(R.id.note_text);
        noteText.setText(mNote.getDescription());
        MaterialTextView noteHeading = view.findViewById(R.id.note_heading);
        noteHeading.setText(mNote.getName());
        MaterialTextView noteDate = view.findViewById(R.id.note_date_time);
        noteDate.setText(mNote.getCreationDate());
        int color = mNote.getColor();
        CardView card = view.findViewById(R.id.card_note);
        if (color != -1) {
            color = ResourcesCompat.getColor(getResources(), color, null);
        } else {
            TypedArray array = requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        card.setCardBackgroundColor(color);
    }

    public Note getNote() {
        return mNote;
    }

    public void setNote(Note note) {
        mNote = note;
    }
}