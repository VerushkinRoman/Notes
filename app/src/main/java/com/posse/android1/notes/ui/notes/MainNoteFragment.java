package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.editor.EditorFragment;
import com.posse.android1.notes.ui.editor.EditorListener;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MainNoteFragment extends Fragment implements NoteListFragmentListener, EditorListener {

    private static final String KEY_LAST_SELECTED = MainNoteFragment.class.getCanonicalName() + "mLastSelectedPosition";
    private static final String KEY_NEW_NOTE = MainNoteFragment.class.getCanonicalName() + "mIsNewNote";
    private NoteListFragment mNoteListFragment;
    private NoteSource mNoteSource;
    private PreferencesDataWorker mPrefsData;
    private Note mCurrentNote;
    private boolean mIsLandscape;
    private boolean mIsNewNote;
    private int mLastSelectedPosition = 0;
    private int mButtonsView;
    private FragmentManager mFragmentManager;
    private EditorFragment mEditorFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = requireActivity().getSupportFragmentManager();
        mNoteSource = NoteSourceImpl.getInstance(requireActivity());
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mPrefsData = new PreferencesDataWorker(requireActivity());
        if (savedInstanceState != null) {
            mLastSelectedPosition = savedInstanceState.getInt(KEY_LAST_SELECTED);
            mIsNewNote = savedInstanceState.getBoolean(KEY_NEW_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_main_notes, container, false);
        if (!mIsLandscape) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            FragmentContainerView fragmentView = view.findViewById(R.id.note_container);
            fragmentView.setLayoutParams(params);
        }
        ((MainActivity) requireActivity()).setMenuEditClickListener(item -> {
            mEditorFragment = EditorFragment.newInstance(mLastSelectedPosition, mNoteSource.getItemsCount(), this);
            replaceFragments(mEditorFragment);
            return false;
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment mainFragmentToReplace = null;
        Fragment editorFragment = null;
        if (savedInstanceState != null) {
            mNoteListFragment = (NoteListFragment) mFragmentManager.findFragmentByTag("ListOfNotes");
            List<Fragment> fragments = mFragmentManager.getFragments();
            Fragment fragment = fragments.get(fragments.size() - 1);
            if (fragment instanceof EditorFragment) {
                Fragment.SavedState savedState = mFragmentManager.saveFragmentInstanceState(fragment);
                Fragment newInstance = null;
                try {
                    newInstance = fragment.getClass().newInstance();
                } catch (IllegalAccessException | java.lang.InstantiationException ignored) {
                }
                newInstance.setInitialSavedState(savedState);
                ((EditorFragment) newInstance).setListener(this);
                mEditorFragment = (EditorFragment) newInstance;
                if (!mIsLandscape) mainFragmentToReplace = newInstance;
                else editorFragment = newInstance;
            }
        } else {
            mNoteListFragment = new NoteListFragment();
        }
        mNoteListFragment.setListener(this);
        if (mainFragmentToReplace == null) mainFragmentToReplace = mNoteListFragment;
        replaceFragments(mainFragmentToReplace);
        if (mNoteSource.getItemsCount() == 0) return;
        if (mIsLandscape) {
            if (editorFragment == null) {
                mCurrentNote = mNoteSource.getItemAt(mLastSelectedPosition);
                showNote(mCurrentNote);
            } else replaceFragments(editorFragment);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_SELECTED, mLastSelectedPosition);
        outState.putBoolean(KEY_NEW_NOTE, mIsNewNote);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MainActivity) requireActivity()).showHideButtons(mButtonsView);
    }

    private void showNote(Note note) {
        replaceFragments(NoteFragment.newInstance(note));
    }

    @Override
    public void onAddButtonPressed() {
        mEditorFragment = EditorFragment.newInstance(-1, mNoteSource.getItemsCount(), this);
        replaceFragments(mEditorFragment);
        mIsNewNote = true;
    }

    @Override
    public void onListItemClicked(int idx) {
        onLastSelectedPositionChanged(idx);
        showNote(mNoteSource.getItemAt(idx));
    }

    @Override
    public NoteSource onSourceRequest() {
        return mNoteSource;
    }

    private void replaceFragments(Fragment newFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        mButtonsView = MainActivity.NOTE_VIEW;
        int fragmentId = R.id.note_list_container;
        String tag = "Note";
        if (newFragment instanceof NoteListFragment) {
            tag = "ListOfNotes";
            mButtonsView = (mNoteSource.getItemsCount() == 0) ? MainActivity.EMPTY_VIEW : MainActivity.NOTE_LIST_VIEW;
        } else if (mIsLandscape) {
            fragmentId = R.id.note_container;
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        if (newFragment instanceof EditorFragment) {
            tag = "Editor";
            mButtonsView = MainActivity.EDITOR_VIEW;
        }
        ((MainActivity) requireActivity()).showHideButtons(mButtonsView);
        transaction.replace(fragmentId, newFragment, tag);
        transaction.commit();
    }

    private void deleteNote(int idx) {
        if (idx != -1) {
            mNoteSource.remove(idx);
            mPrefsData.deleteNote(idx, mNoteSource.getItemsCount());
            mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
            if (mIsLandscape) {
                if (mNoteSource.getItemsCount() > 0) {
                    int position = Math.max(idx - 1, 0);
                    mCurrentNote = mNoteSource.getItemAt(position);
                    showNote(mCurrentNote);
                } else {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    Fragment fragment = mFragmentManager.findFragmentByTag("Note");
                    fragmentTransaction.detach(fragment);
                    fragmentTransaction.commit();
                    mLastSelectedPosition = -1;
                    ((MainActivity) requireActivity()).showHideButtons(MainActivity.EMPTY_VIEW);
                }
            }
        }
    }

    @Override
    public void onLastSelectedPositionChanged(int lastSelectedPosition) {
        mLastSelectedPosition = lastSelectedPosition;
    }

    @Override
    public void onContextEditMenuSelected() {
        if (mLastSelectedPosition != -1) {
            mEditorFragment = EditorFragment.newInstance(mLastSelectedPosition, mNoteSource.getItemsCount(), this);
            replaceFragments(mEditorFragment);
        }
    }

    @Override
    public int onContextDeleteMenuSelected() {
        deleteNote(mLastSelectedPosition);
        return mLastSelectedPosition;
    }

    @Override
    public void noteSaved(Note note) {
        hideKeyboard();
        mButtonsView = MainActivity.NOTE_VIEW;
        if (!note.getName().equals("") || !note.getDescription().equals("")) {
            mPrefsData.writeNote(note);
            if (mIsNewNote) {
                mNoteSource.add(note);
                mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
                mFragmentManager.popBackStack();
                showNote(note);
                mLastSelectedPosition = note.getNoteIndex();
            } else {
                mFragmentManager.popBackStack();
            }
            mNoteListFragment.onDataChanged(note.getNoteIndex(), mIsNewNote);
            mIsNewNote = false;
        } else {
            mFragmentManager.popBackStack();
            if (!mIsLandscape) mButtonsView = MainActivity.NOTE_LIST_VIEW;
        }
        if (mNoteSource.getItemsCount() == 0) mButtonsView = MainActivity.EMPTY_VIEW;
        ((MainActivity) requireActivity()).showHideButtons(mButtonsView);
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}