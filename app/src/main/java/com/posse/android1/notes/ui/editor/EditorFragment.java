package com.posse.android1.notes.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;

import java.util.Objects;

public class EditorFragment extends Fragment {
    public static final String KEY_NOTE_INDEX = "currentNoteIndex";
    public static final String KEY_LISTENER = "Listener";
    private int mCurrentNoteIndex = -1;
    private EditorListener mListener;
    private Note mNote;
    private TextInputEditText mEditNoteHeader;
    private TextInputEditText mEditNoteBody;

    public EditorFragment() {
    }

    private EditorFragment(EditorListener listener) {
        mListener = listener;
    }

    public static EditorFragment newInstance(int currentNoteIndex, EditorListener listener) {
        EditorFragment fragment = new EditorFragment(listener);
        Bundle args = new Bundle();
        args.putInt(KEY_NOTE_INDEX, currentNoteIndex);
        args.putParcelable(KEY_LISTENER, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteSource noteSource = NoteSourceImpl.getInstance(requireActivity());
        if (getArguments() != null) {
            mCurrentNoteIndex = getArguments().getInt(KEY_NOTE_INDEX, -1);
            mListener = getArguments().getParcelable(KEY_LISTENER);
        }
        mNote = noteSource.getItemAt(mCurrentNoteIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mEditNoteHeader = view.findViewById(R.id.note_heading);
        mEditNoteHeader.setText(mNote.getName());
        mEditNoteBody = view.findViewById(R.id.note_body);
        mEditNoteBody.setText(mNote.getDescription());
        mEditNoteBody.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener((v) -> {
            saveNote();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    public void saveNote() {
        mNote.setName(Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        mNote.setDescription(Objects.requireNonNull(mEditNoteBody.getText()).toString());
        mNote.setCreationDate(DateFormatter.getCurrentDate());
        mListener.noteSaved(mNote);
    }
}