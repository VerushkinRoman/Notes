package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.editor.EditorFragment;

import java.util.Objects;

public class MainNoteFragment extends Fragment {

    public static final String KEY_REQUEST_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "requestClick";
    public static final String KEY_REQUEST_LONG_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "requestLongClick";
    public static final String KEY_REQUEST_DELETION_CONFIRMATION = MainNoteFragment.class.getCanonicalName() + "requestDelete";
    public static final String KEY_REQUEST_EDIT_ACTION = MainNoteFragment.class.getCanonicalName() + "editSelected";
    public static final String KEY_REQUEST_NOTE_TO_SAVE = MainNoteFragment.class.getCanonicalName() + "requestNote";
    public static final String KEY_BUTTONS_LOOK = MainNoteFragment.class.getCanonicalName() + "buttonsLook";
    public static final String KEY_DELETE_POSITION = MainNoteFragment.class.getCanonicalName() + "deletePosition";
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
        setResultListeners();
    }

    private void setResultListeners() {
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_CLICKED_POSITION, this, (requestKey, bundle) -> {
            mLastSelectedPosition = bundle.getInt(NoteListFragment.KEY_POSITION_CLICKED);
            showNote(mNoteSource.getItemAt(mLastSelectedPosition));
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_LONG_CLICKED_POSITION, this, (requestKey, bundle)
                -> mLastSelectedPosition = bundle.getInt(NoteListFragment.KEY_POSITION_LONG_CLICKED));
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_DELETION_CONFIRMATION, this, (requestKey, bundle) -> {
            deleteNote(mLastSelectedPosition);
            Bundle result = new Bundle();
            result.putInt(KEY_DELETE_POSITION, mLastSelectedPosition);
            mFragmentManager.setFragmentResult(NoteListFragment.KEY_REQUEST_DELETE_POSITION, result);
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_NOTE_TO_SAVE, this, (requestKey, bundle) -> {
            Note note = bundle.getParcelable(EditorFragment.KEY_NOTE);
            saveNote(note);
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_EDIT_ACTION, this, (requestKey, bundle) -> {
            if (mLastSelectedPosition != -1) {
                showEditor(mLastSelectedPosition);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_main_notes, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            showEditor(-1);
            mIsNewNote = true;
        });
        if (!mIsLandscape) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            FragmentContainerView fragmentView = view.findViewById(R.id.note_container);
            fragmentView.setLayoutParams(params);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mNoteListFragment = (NoteListFragment) mFragmentManager.findFragmentByTag("ListOfNotes");
        } else {
            mNoteListFragment = new NoteListFragment();
        }
        replaceFragments(mNoteListFragment);
        if (mNoteSource.getItemsCount() == 0) return;
        if (mIsLandscape) {
            mCurrentNote = mNoteSource.getItemAt(mLastSelectedPosition);
            showNote(mCurrentNote);
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
        MenuItem editBar = menu.findItem(R.id.edit_bar);
        editBar.setOnMenuItemClickListener(item -> {
            showEditor(mLastSelectedPosition);
            return false;
        });
        setButtonsView(mButtonsView);
    }

    private void setButtonsView(int view) {
        Bundle result = new Bundle();
        result.putInt(KEY_BUTTONS_LOOK, view);
        mFragmentManager.setFragmentResult(MainActivity.KEY_REQUEST_BUTTONS_VIEW, result);
    }

    private void showNote(Note note) {
        replaceFragments(NoteFragment.newInstance(note));
    }

    private void replaceFragments(Fragment newFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mButtonsView = MainActivity.NOTE_VIEW;
        int fragmentId = R.id.note_list_container;
        String backStackTag = null;
        String tag = "Note";
        if (newFragment instanceof NoteListFragment) {
            tag = "ListOfNotes";
            backStackTag = tag;
            mButtonsView = (mNoteSource.getItemsCount() == 0) ? MainActivity.EMPTY_VIEW : MainActivity.NOTE_LIST_VIEW;
        } else if (mIsLandscape) {
            fragmentId = R.id.note_container;
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        transaction.addToBackStack(backStackTag);
        transaction.replace(fragmentId, newFragment, tag);
        transaction.commit();
        setButtonsView(mButtonsView);
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
                    fragmentTransaction.remove(Objects.requireNonNull(fragment));
                    fragmentTransaction.commit();
                    mLastSelectedPosition = -1;
                    setButtonsView(MainActivity.EMPTY_VIEW);
                }
            }
        }
    }

    private void showEditor(int lastSelectedPosition) {
        new EditorFragment(lastSelectedPosition, mNoteSource.getItemsCount()).show(mFragmentManager, "Editor");
    }

    public void saveNote(Note note) {
        mButtonsView = MainActivity.NOTE_LIST_VIEW;
        if (!note.getName().equals("") || !note.getDescription().equals("")) {
            mPrefsData.writeNote(note);
            if (mIsNewNote) {
                mNoteSource.add(note);
                mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
                mLastSelectedPosition = note.getNoteIndex();
            } else mButtonsView = MainActivity.NOTE_VIEW;
            mNoteListFragment.onDataChanged(note.getNoteIndex(), mIsNewNote);
            mIsNewNote = false;
            showNote(note);
        }
        if (mIsLandscape) {
            mButtonsView = MainActivity.NOTE_VIEW;
        }
        if (mNoteSource.getItemsCount() == 0) mButtonsView = MainActivity.EMPTY_VIEW;
        setButtonsView(mButtonsView);
    }
}