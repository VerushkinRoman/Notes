package com.posse.android1.notes.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;

import java.util.Objects;

public class EditorFragment extends Fragment {
    private static final String KEY_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "currentNoteIndex";
    private static final String KEY_MAX_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "maxNoteIndex";
    private static final String KEY_HEADER_TEXT = EditorFragment.class.getCanonicalName() + "headerText";
    private static final String KEY_TEXT = EditorFragment.class.getCanonicalName() + "text";
    private int mCurrentNoteIndex = -1;
    private EditorListener mListener;
    private Note mNote;
    private TextInputEditText mEditNoteHeader;
    private TextInputEditText mEditNoteBody;
    private String mNoteHeader;
    private String mNoteBody;
    private int mMaxNoteIndex;

    public static EditorFragment newInstance(int currentNoteIndex, int maxNoteIndex, EditorListener listener) {
        EditorFragment fragment = new EditorFragment();
        fragment.setListener(listener);
        Bundle args = new Bundle();
        args.putInt(KEY_NOTE_INDEX, currentNoteIndex);
        args.putInt(KEY_MAX_NOTE_INDEX, maxNoteIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mCurrentNoteIndex = getArguments().getInt(KEY_NOTE_INDEX, -1);
            mMaxNoteIndex = getArguments().getInt(KEY_MAX_NOTE_INDEX, -1);
        }
        if (savedInstanceState != null) {
            mMaxNoteIndex = savedInstanceState.getInt(KEY_MAX_NOTE_INDEX);
        }
        if (mCurrentNoteIndex == -1) {
            mNote = new Note(mMaxNoteIndex, "", "", DateFormatter.getCurrentDate());
        } else {
            NoteSource noteSource = NoteSourceImpl.getInstance(requireActivity());
            mNote = noteSource.getItemAt(mCurrentNoteIndex);
        }
        if (savedInstanceState != null) {
            mNoteHeader = savedInstanceState.getString(KEY_HEADER_TEXT, mNote.getName());
            mNoteBody = savedInstanceState.getString(KEY_TEXT, mNote.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mEditNoteHeader = view.findViewById(R.id.note_heading);
        mEditNoteBody = view.findViewById(R.id.note_body);
        if (savedInstanceState == null) {
            mNoteHeader = mNote.getName();
            mNoteBody = mNote.getDescription();
        }
        mEditNoteHeader.setText(mNoteHeader);
        mEditNoteBody.setText(mNoteBody);
        mEditNoteBody.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener((v) -> saveNote());
        return view;
    }

    public void saveNote() {
        mNote.setName(Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        mNote.setDescription(Objects.requireNonNull(mEditNoteBody.getText()).toString());
        mNote.setCreationDate(DateFormatter.getCurrentDate());
        mListener.noteSaved(mNote);
    }

    public void setListener(EditorListener listener) {
        mListener = listener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_HEADER_TEXT, mEditNoteHeader.getText().toString());
        outState.putString(KEY_TEXT, mEditNoteBody.getText().toString());
        outState.putInt(KEY_MAX_NOTE_INDEX, mMaxNoteIndex);
    }
}