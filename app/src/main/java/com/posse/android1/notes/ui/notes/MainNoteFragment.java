package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
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

    public static final int NOTE_LIST_VIEW = 1;
    public static final int NOTE_VIEW = 2;
    public static final int EMPTY_VIEW = 3;
    public static final String KEY_REQUEST_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "_requestClick";
    public static final String KEY_REQUEST_LONG_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "_requestLongClick";
    public static final String KEY_REQUEST_DELETION_CONFIRMATION = MainNoteFragment.class.getCanonicalName() + "_requestDelete";
    public static final String KEY_REQUEST_EDIT_ACTION = MainNoteFragment.class.getCanonicalName() + "_requestEditSelected";
    public static final String KEY_REQUEST_NOTE_TO_SAVE = MainNoteFragment.class.getCanonicalName() + "_requestNote";
    public static final String KEY_REQUEST_BUTTONS_VIEW = MainNoteFragment.class.getCanonicalName() + "_requestButtonsView";
    public static final String KEY_DELETE_POSITION = MainNoteFragment.class.getCanonicalName() + "_deletePosition";
    private static final String KEY_LAST_SELECTED = MainNoteFragment.class.getCanonicalName() + "_mLastSelectedPosition";
    private static final String KEY_NEW_NOTE = MainNoteFragment.class.getCanonicalName() + "_mIsNewNote";
    private static final String KEY_GRID_VIEW = MainNoteFragment.class.getCanonicalName() + "_mIsGridView";
    private NoteListFragment mNoteListFragment;
    private NoteSource mNoteSource;
    private PreferencesDataWorker mPrefsData;
    private Note mCurrentNote;
    private boolean mIsLandscape;
    private boolean mIsNewNote;
    private int mLastSelectedPosition;
    private int mButtonsView;
    private FragmentManager mFragmentManager;
    private MenuItem mSwitchView;
    private MenuItem mEditBar;
    private FloatingActionButton mFloatingButton;
    private boolean mIsEmpty;
    private boolean mIsGridView;

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
            mIsGridView = savedInstanceState.getBoolean(KEY_GRID_VIEW);
        } else mIsGridView = mPrefsData.isGridView();
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
            boolean isEditorPaused = bundle.getBoolean(EditorFragment.KEY_PAUSED);
            if (isEditorPaused) {
                Note note = bundle.getParcelable(EditorFragment.KEY_NOTE);
                saveNote(note);
            }
            int position = mLastSelectedPosition;
            if (mIsNewNote) position = -1;
            mPrefsData.setEditorOpened(isEditorPaused, position);
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_EDIT_ACTION, this, (requestKey, bundle) -> {
            if (mLastSelectedPosition != -1) {
                showEditor(mLastSelectedPosition);
            }
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_BUTTONS_VIEW, this, (requestKey, bundle) -> {
            int view = bundle.getInt(MainActivity.KEY_BUTTONS_VIEW);
            if (mIsEmpty) view = EMPTY_VIEW;
            changeButtonsLook(view);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_main_notes, container, false);
        mFloatingButton = view.findViewById(R.id.fab);
        mFloatingButton.setOnClickListener(v -> {
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
            mNoteListFragment = new NoteListFragment(mIsGridView);
        }
        replaceFragments(mNoteListFragment);
        if (mNoteSource.getItemsCount() == 0) {
            if (mIsLandscape) removeLastNoteFragment();
            return;
        }
        if (savedInstanceState == null && mPrefsData.isEditorOpened()) {
            mLastSelectedPosition = mPrefsData.getLastIndex();
            if (mLastSelectedPosition != -1) {
                mCurrentNote = mNoteSource.getItemAt(mLastSelectedPosition);
                showNote(mCurrentNote);
            }
            showEditor(mLastSelectedPosition);
            mLastSelectedPosition = 0;
        } else if (mIsLandscape) {
            mCurrentNote = mNoteSource.getItemAt(mLastSelectedPosition);
            showNote(mCurrentNote);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_SELECTED, mLastSelectedPosition);
        outState.putBoolean(KEY_NEW_NOTE, mIsNewNote);
        outState.putBoolean(KEY_GRID_VIEW, mIsGridView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        mEditBar = menu.findItem(R.id.edit_bar);
        mEditBar.setOnMenuItemClickListener(item -> {
            showEditor(mLastSelectedPosition);
            return false;
        });
        Drawable gridView = Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), android.R.drawable.ic_dialog_dialer));
        Drawable lineView = Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), android.R.drawable.ic_menu_sort_by_size));
        Drawable menuIcon = mIsGridView ? gridView : lineView;
        mSwitchView = menu.findItem(R.id.action_switch_view);
        mSwitchView.setIcon(menuIcon);
        mSwitchView.setOnMenuItemClickListener(item -> {
            Drawable icon = mSwitchView.getIcon().getConstantState().equals(lineView.getConstantState()) ? gridView : lineView;
            mSwitchView.setIcon(icon);
            mIsGridView = icon.equals(gridView);
            refreshNoteListFragment();
            mPrefsData.setGridView(mIsGridView);
            return false;
        });
        changeButtonsLook(mButtonsView);
    }

    private void refreshNoteListFragment() {
        if (mNoteListFragment != null && mNoteListFragment.isVisible()) {
            mNoteListFragment.setGridView(mIsGridView);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.detach(mNoteListFragment);
            fragmentTransaction.commitNow();
            fragmentTransaction.attach(mNoteListFragment);
            fragmentTransaction.commitNow();
        }
    }

    private void showNote(Note note) {
        replaceFragments(NoteFragment.newInstance(note));
    }

    private void replaceFragments(Fragment newFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mButtonsView = NOTE_VIEW;
        int fragmentId = R.id.note_list_container;
        String backStackTag = null;
        String tag = "Note";
        if (newFragment instanceof NoteListFragment) {
            tag = "ListOfNotes";
            backStackTag = tag;
            mButtonsView = (mNoteSource.getItemsCount() == 0) ? EMPTY_VIEW : NOTE_LIST_VIEW;
        } else if (mIsLandscape) {
            fragmentId = R.id.note_container;
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        if (!mIsLandscape || Objects.equals(backStackTag, "ListOfNotes"))
            transaction.addToBackStack(backStackTag);
        transaction.replace(fragmentId, newFragment, tag);
        transaction.commit();
        changeButtonsLook(mButtonsView);
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
                } else removeLastNoteFragment();
            }
        }
    }

    private void removeLastNoteFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.note_container);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        mLastSelectedPosition = -1;
        mIsEmpty = true;
        changeButtonsLook(EMPTY_VIEW);
    }

    private void showEditor(int lastSelectedPosition) {
        new EditorFragment(lastSelectedPosition, mNoteSource.getItemsCount()).show(mFragmentManager, "Editor");
    }

    private void saveNote(Note note) {
        mButtonsView = NOTE_LIST_VIEW;
        if (!note.getName().equals("") || !note.getDescription().equals("")) {
            mPrefsData.writeNote(note);
            if (mIsNewNote) {
                mNoteSource.add(note);
                mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
            } else mButtonsView = NOTE_VIEW;
            mNoteListFragment.onDataChanged(note.getNoteIndex(), mIsNewNote);
            mIsNewNote = false;
            mLastSelectedPosition = note.getNoteIndex();
            showNote(note);
        }
        if (mIsLandscape) {
            mButtonsView = NOTE_VIEW;
        }
        if (mNoteSource.getItemsCount() == 0) mButtonsView = EMPTY_VIEW;
        changeButtonsLook(mButtonsView);
    }

    private void changeButtonsLook(int lookVariant) {
        switch (lookVariant) {
            case NOTE_LIST_VIEW:
                showFloatingButton(true);
                showSwitchView(true);
                showEditBar(false);
                break;
            case NOTE_VIEW:
                showFloatingButton(mIsLandscape);
                showSwitchView(false);
                showEditBar(true);
                mIsEmpty = false;
                break;
            case EMPTY_VIEW:
                showFloatingButton(true);
                showEditBar(false);
                showSwitchView(!mIsLandscape);
                mIsEmpty = true;
                break;
            default:
                throw new RuntimeException("Unexpected view: " + lookVariant);
        }
    }

    private void showFloatingButton(boolean isVisible) {
        if (mFloatingButton != null) {
            if (isVisible) mFloatingButton.show();
            else mFloatingButton.hide();
        }
    }

    private void showSwitchView(boolean isVisible) {
        if (mSwitchView != null) mSwitchView.setVisible(isVisible);
    }

    private void showEditBar(boolean isVisible) {
        if (mEditBar != null) mEditBar.setVisible(isVisible);
    }
}