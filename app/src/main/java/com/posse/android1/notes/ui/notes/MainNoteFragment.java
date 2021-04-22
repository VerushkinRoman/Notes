package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.posse.android1.notes.CustomSpinnerAdapter;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.confirmation.DeleteFragment;
import com.posse.android1.notes.ui.editor.EditorFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.posse.android1.notes.CustomSpinnerAdapter.PADDING;

public class MainNoteFragment extends Fragment {

    public static final int NOTE_LIST_VIEW = 1;
    public static final int NOTE_VIEW = 2;
    public static final int EMPTY_VIEW = 3;
    public static final String TAG_NOTES_LIST = "ListOfNotes";
    public static final String TAG_NOTE = "Note";
    public static final String KEY_REQUEST_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "_requestClick";
    public static final String KEY_REQUEST_LONG_CLICKED_POSITION = MainNoteFragment.class.getCanonicalName() + "_requestLongClick";
    public static final String KEY_REQUEST_DELETION_CONFIRMATION = MainNoteFragment.class.getCanonicalName() + "_requestDelete";
    public static final String KEY_REQUEST_NOTE_TO_SAVE = MainNoteFragment.class.getCanonicalName() + "_requestNote";
    public static final String KEY_REQUEST_BACK_PRESSED = MainNoteFragment.class.getCanonicalName() + "_requestButtonsView";
    public static final String KEY_DELETE_POSITION = MainNoteFragment.class.getCanonicalName() + "_deletePosition";
    private static final String KEY_LAST_SELECTED = MainNoteFragment.class.getCanonicalName() + "_mLastSelectedPosition";
    private static final String KEY_NEW_NOTE = MainNoteFragment.class.getCanonicalName() + "_mIsNewNote";
    private static final String KEY_GRID_VIEW = MainNoteFragment.class.getCanonicalName() + "_mIsGridView";
    private static final String KEY_CHECKED_CARDS = MainNoteFragment.class.getCanonicalName() + "_mCheckedCards";
    private ArrayList<Integer> mCheckedCards;
    private NoteListFragment mNoteListFragment;
    private NoteFragment mNoteFragment;
    private NoteSource mNoteSource;
    private Spinner mColorSpinner;
    private PreferencesDataWorker mPrefsData;
    private Note mCurrentNote;
    private boolean mIsLandscape;
    private boolean mIsNewNote;
    private int mLastSelectedPosition;
    private int mButtonsView;
    private FragmentManager mFragmentManager;
    private MenuItem mSwitchView;
    private MenuItem mEditBar;
    private MenuItem mDeleteBar;
    private MenuItem mSpinnerView;
    private FloatingActionButton mFloatingButton;
    private boolean mIsEmpty;
    private boolean mIsGridView;
    private long mLastTimePressed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = requireActivity().getSupportFragmentManager();
        mNoteSource = NoteSourceImpl.getInstance(requireActivity());
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mPrefsData = new PreferencesDataWorker(requireActivity());
        mCheckedCards = new ArrayList<>();
        if (savedInstanceState != null) {
            mLastSelectedPosition = savedInstanceState.getInt(KEY_LAST_SELECTED);
            mIsNewNote = savedInstanceState.getBoolean(KEY_NEW_NOTE);
            mIsGridView = savedInstanceState.getBoolean(KEY_GRID_VIEW);
            mCheckedCards = savedInstanceState.getIntegerArrayList(KEY_CHECKED_CARDS);
            restoreCheckedCards();
        } else mIsGridView = mPrefsData.isGridView();
    }

    private void restoreCheckedCards() {
        for (int i = 0; i < mNoteSource.getItemsCount(); i++) {
            if (mCheckedCards.contains(i)) {
                mNoteSource.getItemAt(i).setIsDeleteVisible(true);
            }
        }
    }

    private void changeCheckboxState() {
        final Note note = mNoteSource.getItemAt(mLastSelectedPosition);
        if (note.isDeleteVisible()) {
            final Integer index = mLastSelectedPosition;
            mCheckedCards.remove(index);
            note.setIsDeleteVisible(false);
        } else {
            mCheckedCards.add(mLastSelectedPosition);
            mCheckedCards.sort(Collections.reverseOrder());
            note.setIsDeleteVisible(true);
        }
        showDeleteBar(mCheckedCards.size() > 0);
        mNoteListFragment.onDataChanged(mLastSelectedPosition, false);
        if (mIsLandscape) {
            showNote(note);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_main_notes, container, false);
        mFloatingButton = view.findViewById(R.id.fab);
        mFloatingButton.setOnClickListener(v -> {
            showEditor(-1);
            mIsNewNote = true;
        });
        if (!mIsLandscape) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            final FragmentContainerView fragmentView = view.findViewById(R.id.note_container);
            fragmentView.setLayoutParams(params);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoteListFragment = NoteListFragment.newInstance(mIsGridView, mPrefsData.getHeaderTextSize(), mPrefsData.getNoteTextSize());
        replaceFragments(mNoteListFragment);
        if (savedInstanceState == null && mPrefsData.isEditorOpened()) {
            mLastSelectedPosition = mPrefsData.getLastIndex();
            if (mLastSelectedPosition != -1) {
                showNote(mNoteSource.getItemAt(mLastSelectedPosition));
                showEditor(mLastSelectedPosition);
            } else mPrefsData.setEditorOpened(false, mNoteSource.getItemsCount());
            mLastSelectedPosition = 0;
        }
        if (mIsLandscape) {
            if ((mNoteSource.getItemsCount() == 0)) removeLastNoteFragment();
            else {
                mCurrentNote = mNoteSource.getItemAt(mLastSelectedPosition);
                showNote(mCurrentNote);
            }
        }
        setResultListeners();
    }

    private void setResultListeners() {
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_CLICKED_POSITION, this, (requestKey, bundle) -> {
            if (System.currentTimeMillis() - mLastTimePressed > 500) {
                mLastSelectedPosition = bundle.getInt(NoteListFragment.KEY_POSITION_CLICKED);
                if (!mCheckedCards.isEmpty()) {
                    changeCheckboxState();
                } else showNote(mNoteSource.getItemAt(mLastSelectedPosition));
            }
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_LONG_CLICKED_POSITION, this, (requestKey, bundle) -> {
            mLastSelectedPosition = bundle.getInt(NoteListFragment.KEY_POSITION_LONG_CLICKED);
            changeCheckboxState();
            mLastTimePressed = System.currentTimeMillis();
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_DELETION_CONFIRMATION, this, (requestKey, bundle) -> {
            deleteNotes();
            final Bundle result = new Bundle();
            result.putIntegerArrayList(KEY_DELETE_POSITION, mCheckedCards);
            mFragmentManager.setFragmentResult(NoteListFragment.KEY_REQUEST_DELETE_POSITION, result);
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_NOTE_TO_SAVE, this, (requestKey, bundle) -> {
            final boolean isEditorPaused = bundle.getBoolean(EditorFragment.KEY_PAUSED);
            if (isEditorPaused) {
                final Note note = bundle.getParcelable(EditorFragment.KEY_NOTE);
                saveNote(note);
            }
            final int position = mIsNewNote ? -1 : mLastSelectedPosition;
            mPrefsData.setEditorOpened(isEditorPaused, position);
        });
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_BACK_PRESSED, this, (requestKey, bundle) -> {
            if (!mCheckedCards.isEmpty()) {
                mCheckedCards.clear();
                for (int i = 0; i < mNoteSource.getItemsCount(); i++) {
                    mNoteSource.getItemAt(i).setIsDeleteVisible(false);
                }
                mNoteListFragment.onDataChanged(NoteListFragment.ALL_ITEMS_CHANGED, false);
            }
            final int buttonsView;
            if (mIsEmpty) buttonsView = EMPTY_VIEW;
            else buttonsView = mIsLandscape ? NOTE_VIEW : NOTE_LIST_VIEW;
            changeButtonsLook(buttonsView);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_SELECTED, mLastSelectedPosition);
        outState.putBoolean(KEY_NEW_NOTE, mIsNewNote);
        outState.putBoolean(KEY_GRID_VIEW, mIsGridView);
        outState.putIntegerArrayList(KEY_CHECKED_CARDS, mCheckedCards);
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
        mDeleteBar = menu.findItem(R.id.delete_bar);
        mDeleteBar.setOnMenuItemClickListener(item -> {
            DeleteFragment.newInstance(mCheckedCards.size() < 1).show(mFragmentManager, null);
            return false;
        });
        mSpinnerView = menu.findItem(R.id.spinner);
        mColorSpinner = (Spinner) mSpinnerView.getActionView();
        addItemsToSpinner();

        final Drawable gridView = Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), android.R.drawable.ic_dialog_dialer));
        final Drawable lineView = Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), android.R.drawable.ic_menu_sort_by_size));
        final Drawable menuIcon = mIsGridView ? gridView : lineView;
        mSwitchView = menu.findItem(R.id.action_switch_view);
        mSwitchView.setIcon(menuIcon);
        mSwitchView.setOnMenuItemClickListener(item -> {
            final Drawable icon = mSwitchView.getIcon().getConstantState().equals(lineView.getConstantState()) ? gridView : lineView;
            mSwitchView.setIcon(icon);
            mIsGridView = icon.equals(gridView);
            refreshFragment(mNoteListFragment);
            mPrefsData.setGridView(mIsGridView);
            return false;
        });
        changeButtonsLook(mButtonsView);
    }

    private void addItemsToSpinner() {
        final ArrayList<Integer> list = new ArrayList<>();
        list.add(android.R.color.transparent);
        list.add(android.R.color.holo_purple);
        list.add(android.R.color.holo_blue_dark);
        list.add(android.R.color.holo_blue_bright);
        list.add(android.R.color.holo_green_dark);
        list.add(android.R.color.holo_green_light);
        list.add(android.R.color.holo_orange_light);
        list.add(android.R.color.holo_orange_dark);
        list.add(android.R.color.holo_red_light);
        list.add(android.R.color.holo_red_dark);
        list.add(-1);

        final CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireActivity(), list);
        mColorSpinner.setAdapter(customSpinnerAdapter);
        mColorSpinner.setPadding(0, PADDING * 10, 0, PADDING * 10);
        mColorSpinner.setDropDownVerticalOffset(PADDING * 2);
        mColorSpinner.setPopupBackgroundDrawable(null);
        final Drawable drawable = (customSpinnerAdapter.getCustomView(0)).getDrawable();
        final LayerDrawable background = new LayerDrawable(new Drawable[]{drawable});
        final int multiply = mIsLandscape ? 5 : 3;
        background.setLayerInset(0, 0, PADDING * multiply, PADDING * 3, PADDING * multiply);
        mColorSpinner.setBackground(background);
        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    view.setVisibility(View.GONE);
                    final int noteColor = list.get(position);
                    setNoteColor(noteColor);
                    setColorSpinnerBackground(noteColor);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mIsLandscape && mNoteFragment != null) {
            setColorSpinnerBackground(mCurrentNote.getColor());
        }
    }

    private void setColorSpinnerBackground(int color) {
        if (mColorSpinner != null) {
            final LayerDrawable background = (LayerDrawable) mColorSpinner.getBackground();
            final GradientDrawable layer0 = (GradientDrawable) background.getDrawable(0);
            if (color == -1) {
                final TypedArray array = requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
                color = array.getColor(0, 0xFF00FF);
                array.recycle();
            } else color = ContextCompat.getColor(requireActivity(), color);
            layer0.setColor(color);
        }
    }

    private void setNoteColor(int color) {
        if (mCurrentNote != null) {
            mCurrentNote.setColor(color);
            mPrefsData.setNoteColor(mCurrentNote);
            mNoteFragment.setNote(mCurrentNote);
            mNoteSource.getItemAt(mCurrentNote.getNoteIndex()).setColor(color);
            mNoteListFragment.onDataChanged(mCurrentNote.getNoteIndex(), false);
            refreshFragment(mNoteFragment);
        }
    }

    private void refreshFragment(Fragment fragment) {
        if (fragment != null && fragment.isVisible()) {
            if (fragment instanceof NoteListFragment)
                ((NoteListFragment) fragment).setGridView(mIsGridView);
            final FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.detach(fragment);
            fragmentTransaction.commitNow();
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commitNow();
        }
    }

    private void showNote(Note note) {
        mNoteFragment = NoteFragment.newInstance(note, mPrefsData.getHeaderTextSize(), mPrefsData.getNoteTextSize());
        mCurrentNote = note;
        setColorSpinnerBackground(note.getColor());
        replaceFragments(mNoteFragment);
    }

    private void replaceFragments(Fragment newFragment) {
        final FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mButtonsView = NOTE_VIEW;
        int fragmentId = R.id.note_list_container;
        String backStackTag = null;
        String tag = TAG_NOTE;
        if (newFragment instanceof NoteListFragment) {
            tag = TAG_NOTES_LIST;
            backStackTag = tag;
            mButtonsView = (mNoteSource.getItemsCount() == 0) ? EMPTY_VIEW : NOTE_LIST_VIEW;
        } else if (mIsLandscape) {
            fragmentId = R.id.note_container;
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        if (!mIsLandscape || Objects.equals(backStackTag, TAG_NOTES_LIST))
            transaction.addToBackStack(backStackTag);
        transaction.replace(fragmentId, newFragment, tag);
        transaction.commit();
        changeButtonsLook(mButtonsView);
    }

    private void deleteNotes() {
        changeButtonsLook(NOTE_LIST_VIEW);
        final boolean fromNoteView = mCheckedCards.isEmpty();
        if (mCheckedCards.isEmpty()) mCheckedCards.add(mLastSelectedPosition);
        for (int i = 0; i < mCheckedCards.size(); i++) {
            final int idx = mCheckedCards.get(i);
            mNoteSource.remove(idx);
            mPrefsData.deleteNote(idx, mNoteSource.getItemsCount());
            for (int j = idx; j < mNoteSource.getItemsCount(); j++) {
                mNoteSource.getItemAt(j).setNoteIndex(j);
            }
        }
        mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
        if (mIsLandscape) {
            if (mNoteSource.getItemsCount() > 0) {
                final int idx = (mCheckedCards.size() > 1) ? 0 : mCheckedCards.get(0);
                final int position = Math.max(idx - 1, 0);
                mCurrentNote = mNoteSource.getItemAt(position);
                showNote(mCurrentNote);
            } else removeLastNoteFragment();
        } else {
            if (fromNoteView) mFragmentManager.popBackStack();
            if (mNoteSource.getItemsCount() > 0) {
                showDeleteBar(false);
            } else {
                mLastSelectedPosition = -1;
                mIsEmpty = true;
                changeButtonsLook(EMPTY_VIEW);
            }
        }
        mCheckedCards.clear();
    }

    private void removeLastNoteFragment() {
        final FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        final Fragment noteFragment = mFragmentManager.findFragmentById(R.id.note_container);
        if (noteFragment != null) {
            fragmentTransaction.remove(noteFragment);
            fragmentTransaction.commit();
        }
        mLastSelectedPosition = -1;
        mIsEmpty = true;
        changeButtonsLook(EMPTY_VIEW);
    }

    private void showEditor(int lastSelectedPosition) {
        EditorFragment.newInstance(lastSelectedPosition, mNoteSource.getItemsCount(),
                mPrefsData.getHeaderTextSize(), mPrefsData.getNoteTextSize())
                .show(mFragmentManager, null);
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
            if (!mIsLandscape && !mIsNewNote) mFragmentManager.popBackStack();
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
                showDeleteBar(mCheckedCards.size() > 0);
                showFloatingButton(true);
                showSwitchView(true);
                showEditBar(false);
                showSpinner(false);
                break;
            case NOTE_VIEW:
                showDeleteBar(true);
                showFloatingButton(mIsLandscape);
                showSwitchView(false);
                showEditBar(true);
                showSpinner(true);
                mIsEmpty = false;
                break;
            case EMPTY_VIEW:
                showDeleteBar(false);
                showFloatingButton(true);
                showEditBar(false);
                showSwitchView(!mIsLandscape);
                showSpinner(false);
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

    private void showDeleteBar(boolean isVisible) {
        if (mDeleteBar != null) mDeleteBar.setVisible(isVisible);
    }

    private void showSpinner(boolean isVisible) {
        if (mSpinnerView != null) mSpinnerView.setVisible(isVisible);
    }
}