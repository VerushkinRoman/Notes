package com.posse.android1.notes.ui.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.notes.MainNoteFragment;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditorFragment extends DialogFragment {
    public static final String KEY_NOTE = EditorFragment.class.getCanonicalName() + "currentNote";
    private static final String KEY_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "currentNoteIndex";
    private static final String KEY_MAX_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "maxNoteIndex";
    private static final String KEY_HEADER_TEXT = EditorFragment.class.getCanonicalName() + "headerText";
    private static final String KEY_TEXT = EditorFragment.class.getCanonicalName() + "text";
    private int mCurrentNoteIndex = -1;
    private Note mNote;
    private TextInputEditText mEditNoteHeader;
    private TextInputEditText mEditNoteBody;
    private String mNoteHeader;
    private String mNoteBody;
    private int mMaxNoteIndex;

    public EditorFragment() {
    }

    public EditorFragment(int currentNoteIndex, int maxNoteIndex) {
        mCurrentNoteIndex = currentNoteIndex;
        mMaxNoteIndex = maxNoteIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentNoteIndex = savedInstanceState.getInt(KEY_NOTE_INDEX);
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
        btnSave.setOnClickListener((v) -> dismiss());
        return view;
    }

    public void saveNote() {
        mNote.setName(Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        mNote.setDescription(Objects.requireNonNull(mEditNoteBody.getText()).toString());
        mNote.setCreationDate(DateFormatter.getCurrentDate());
        Bundle result = new Bundle();
        result.putParcelable(KEY_NOTE, mNote);
        requireActivity().getSupportFragmentManager().setFragmentResult(MainNoteFragment.KEY_REQUEST_NOTE_TO_SAVE, result);
    }

    @Override
    public void onResume() {
        try {
            Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception ignored) {
        }
        super.onResume();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        saveNote();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_HEADER_TEXT, Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        outState.putString(KEY_TEXT, Objects.requireNonNull(mEditNoteBody.getText()).toString());
        outState.putInt(KEY_NOTE_INDEX, mCurrentNoteIndex);
        outState.putInt(KEY_MAX_NOTE_INDEX, mMaxNoteIndex);
    }
}