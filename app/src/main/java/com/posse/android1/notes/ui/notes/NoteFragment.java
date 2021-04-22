package com.posse.android1.notes.ui.notes;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public static final String KEY_HEADER_SIZE = NoteFragment.class.getCanonicalName() + "mHeaderTextSize";
    public static final String KEY_NOTE_SIZE = NoteFragment.class.getCanonicalName() + "mNoteTextSize";
    private Note mNote;
    private float mHeaderTextSize;
    private float mNoteTextSize;

    public NoteFragment() {
    }

    public static NoteFragment newInstance(Note note, float headerTextSize, float noteTextSize) {
        final NoteFragment fragment = new NoteFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_NOTE_INDEX, note);
        args.putFloat(KEY_HEADER_SIZE, headerTextSize);
        args.putFloat(KEY_NOTE_SIZE, noteTextSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNote = savedInstanceState.getParcelable(KEY_NOTE_INDEX);
            mHeaderTextSize = savedInstanceState.getFloat(KEY_HEADER_SIZE);
            mNoteTextSize = savedInstanceState.getFloat(KEY_NOTE_SIZE);
        } else if (getArguments() != null) {
            mNote = getArguments().getParcelable(KEY_NOTE_INDEX);
            mHeaderTextSize = getArguments().getFloat(KEY_HEADER_SIZE);
            mNoteTextSize = getArguments().getFloat(KEY_NOTE_SIZE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_NOTE_INDEX, mNote);
        outState.putFloat(KEY_HEADER_SIZE, mHeaderTextSize);
        outState.putFloat(KEY_NOTE_SIZE, mNoteTextSize);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MaterialTextView noteText = view.findViewById(R.id.note_text);
        noteText.setText(mNote.getDescription());
        noteText.setTextSize(mNoteTextSize);
        final MaterialTextView noteHeading = view.findViewById(R.id.note_heading);
        noteHeading.setText(mNote.getName());
        noteHeading.setTextSize(mHeaderTextSize);
        final MaterialTextView noteDate = view.findViewById(R.id.note_date_time);
        noteDate.setText(mNote.getCreationDate());
        int color = mNote.getColor();
        final CardView card = view.findViewById(R.id.card_note);
        if (color != -1) {
            color = ResourcesCompat.getColor(getResources(), color, null);
        } else {
            final TypedArray array = requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
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