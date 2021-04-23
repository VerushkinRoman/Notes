package com.posse.android1.notes.ui.editor;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
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
    private static final String KEY_HEADER_SIZE = EditorFragment.class.getCanonicalName() + "_mHeaderTextSize";
    private static final String KEY_NOTE_SIZE = EditorFragment.class.getCanonicalName() + "_mNoteTextSize";
    private static final String KEY_TEXT = EditorFragment.class.getCanonicalName() + "_text";
    private static final String KEY_COLOR = EditorFragment.class.getCanonicalName() + "_mDefaultColor";
    private static final String KEY_OPENED_KEYBOARD = EditorFragment.class.getCanonicalName() + "_mIsKeyboardOpened";
    private InputMethodManager mInputMethodManager;
    private int mCurrentNoteIndex = -1;
    private Note mNote;
    private TextInputEditText mEditNoteHeader;
    private TextInputEditText mEditNoteBody;
    private String mNoteHeader;
    private String mNoteBody;
    private int mMaxNoteIndex;
    private int mDefaultColor;
    private boolean mIsBodyFocused;
    private boolean mIsHeadFocused;
    private float mHeaderTextSize;
    private float mNoteTextSize;
    private boolean mIsKeyboardOpened;

    public EditorFragment() {
    }

    public static EditorFragment newInstance(int currentNoteIndex, int maxNoteIndex, float headerTextSize, float noteTextSize, int color) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_NOTE_INDEX, currentNoteIndex);
        args.putInt(KEY_MAX_NOTE_INDEX, maxNoteIndex);
        args.putInt(KEY_COLOR, color);
        args.putFloat(KEY_HEADER_SIZE, headerTextSize);
        args.putFloat(KEY_NOTE_SIZE, noteTextSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentNoteIndex = getArguments().getInt(KEY_NOTE_INDEX);
            mMaxNoteIndex = getArguments().getInt(KEY_MAX_NOTE_INDEX);
            mHeaderTextSize = getArguments().getFloat(KEY_HEADER_SIZE);
            mNoteTextSize = getArguments().getFloat(KEY_NOTE_SIZE);
            mDefaultColor = getArguments().getInt(KEY_COLOR);
        }
        if (mCurrentNoteIndex == -1) {
            mNote = new Note(mMaxNoteIndex, "", "", DateFormatter.getCurrentDate(), -1);
        } else {
            NoteSource noteSource = NoteSourceImpl.getInstance(requireActivity());
            mNote = noteSource.getItemAt(mCurrentNoteIndex);
        }
        if (savedInstanceState != null) {
            mNoteHeader = savedInstanceState.getString(KEY_HEADER_TEXT, mNote.getName());
            mNoteBody = savedInstanceState.getString(KEY_TEXT, mNote.getDescription());
            mIsKeyboardOpened = savedInstanceState.getBoolean(KEY_OPENED_KEYBOARD);
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
        mEditNoteHeader.setTextSize(mHeaderTextSize);
        mEditNoteBody.setText(mNoteBody);
        mEditNoteBody.setTextSize(mNoteTextSize);
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
        mEditNoteBody.requestFocus();
        setBackgroundColor(view);
        setWindowSize();
        showKeyboard();
    }

    private void setBackgroundColor(@NonNull View view) {
        int color = mNote.getColor();
        CardView card = view.findViewById(R.id.card_editor);
        if (mCurrentNoteIndex == -1) color = mDefaultColor;
        if (color != -1) {
            color = ResourcesCompat.getColor(getResources(), color, null);
        } else {
            final TypedArray array = requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        card.setCardBackgroundColor(color);
    }

    private void setWindowSize() {
        Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int height = WindowManager.LayoutParams.MATCH_PARENT;
        if (!isLandscape) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels / 2;
        }
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = height;
        params.gravity = Gravity.TOP;
        dialogWindow.setAttributes(params);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void showKeyboard() {
        View rootView = requireActivity().getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (!mIsKeyboardOpened) mIsKeyboardOpened = keypadHeight > screenHeight * 0.2;
        });
        if (!mIsKeyboardOpened)
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void saveNote(boolean isPaused) {
        mNote.setName(Objects.requireNonNull(mEditNoteHeader.getText()).toString());
        mNote.setDescription(Objects.requireNonNull(mEditNoteBody.getText()).toString());
        mNote.setCreationDate(DateFormatter.getCurrentDate());
        if (mCurrentNoteIndex == -1) mNote.setColor(mDefaultColor);
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
        if (mIsBodyFocused) mEditNoteBody.requestFocus();
        else if (mIsHeadFocused) mEditNoteHeader.requestFocus();
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
        outState.putBoolean(KEY_OPENED_KEYBOARD, mIsKeyboardOpened);
    }
}