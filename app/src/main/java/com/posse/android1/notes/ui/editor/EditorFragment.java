package com.posse.android1.notes.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditorFragment extends Fragment {
    private static final String KEY_NOTE_INDEX = "currentNoteIndex";
    private int mCurrentNoteIndex = -1;

    public EditorFragment() {
    }

    public static EditorFragment newInstance(int currentNoteIndex) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_NOTE_INDEX, currentNoteIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentNoteIndex = getArguments().getInt(KEY_NOTE_INDEX, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        NoteSource noteSource = NoteSourceImpl.getInstance(requireActivity());
        Note note = noteSource.getItemAt(mCurrentNoteIndex);

        TextInputEditText editNoteHeader = view.findViewById(R.id.note_heading);
        editNoteHeader.setText(note.getName());
        TextInputEditText editNoteBody = view.findViewById(R.id.note_body);
        editNoteBody.setText(note.getDescription());
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener((v) -> {
            saveNote(note, editNoteHeader, editNoteBody);
            hideKeyboard();
            ((MainActivity) requireActivity()).showFloatingButton(true);
            ((MainActivity) requireActivity()).showSwitchView(true);
            ((MainActivity) requireActivity()).showEditBar(false);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    private void saveNote(Note note, TextInputEditText editNoteHeader, TextInputEditText editNoteBody) {
        note.setName(Objects.requireNonNull(editNoteHeader.getText()).toString());
        note.setDescription(Objects.requireNonNull(editNoteBody.getText()).toString());
        note.setCreationDate(DateFormatter.getCurrentDate());
        PreferencesDataWorker prefsData = new PreferencesDataWorker(requireActivity());
        prefsData.writeNote(note);
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}