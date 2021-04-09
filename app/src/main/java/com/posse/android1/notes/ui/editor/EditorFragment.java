package com.posse.android1.notes.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.notes.MainNoteFragment;

import java.util.Objects;

public class EditorFragment extends DialogFragment {
    public static final String KEY_NOTE = EditorFragment.class.getCanonicalName() + "_currentNote";
    public static final String KEY_PAUSED = EditorFragment.class.getCanonicalName() + "_isPaused";
    private static final String KEY_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "_currentNoteIndex";
    private static final String KEY_MAX_NOTE_INDEX = EditorFragment.class.getCanonicalName() + "_maxNoteIndex";
    private static final String KEY_HEADER_TEXT = EditorFragment.class.getCanonicalName() + "_headerText";
    private static final String KEY_TEXT = EditorFragment.class.getCanonicalName() + "_text";
    private InputMethodManager mInputMethodManager;
    private int mCurrentNoteIndex = -1;
    private Note mNote;
    private TextInputEditText mEditNoteHeader;
    private TextInputEditText mEditNoteBody;
    private String mNoteHeader;
    private String mNoteBody;
    private int mMaxNoteIndex;
    private boolean mIsBodyFocused;
    private boolean mIsHeadFocused;

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
        mInputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener((v) -> {
            mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mEditNoteBody.requestFocus()) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void saveNote(boolean isPaused) {
        mNote.setName(Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        mNote.setDescription(Objects.requireNonNull(mEditNoteBody.getText()).toString());
        mNote.setCreationDate(DateFormatter.getCurrentDate());
        Bundle result = new Bundle();
        result.putBoolean(KEY_PAUSED, isPaused);
        if (isPaused) result.putParcelable(KEY_NOTE, mNote);
        requireActivity().getSupportFragmentManager().setFragmentResult(MainNoteFragment.KEY_REQUEST_NOTE_TO_SAVE, result);
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsBodyFocused = mEditNoteBody.isFocused();
        mIsHeadFocused = mEditNoteHeader.isFocused();
        saveNote(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        if (mIsBodyFocused) mEditNoteBody.requestFocus();
        else if (mIsHeadFocused) mEditNoteHeader.requestFocus();
        else mInputMethodManager.hideSoftInputFromWindow(
                    getDialog().getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveNote(false);
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