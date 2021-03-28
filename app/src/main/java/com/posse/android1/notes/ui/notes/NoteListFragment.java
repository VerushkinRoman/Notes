package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.posse.android1.notes.DateFormatter;
import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.PreferencesDataWorker;
import com.posse.android1.notes.R;
import com.posse.android1.notes.adapter.ViewHolderAdapter;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.editor.EditorFragment;

import org.jetbrains.annotations.NotNull;

public class NoteListFragment extends Fragment implements Parcelable {

    public static final Creator<NoteListFragment> CREATOR = new Creator<NoteListFragment>() {
        @Override
        public NoteListFragment createFromParcel(Parcel in) {
            return new NoteListFragment(in);
        }

        @Override
        public NoteListFragment[] newArray(int size) {
            return new NoteListFragment[size];
        }
    };

    private boolean mIsLandscape;
    private Note mCurrentNote;
    private NoteSource mNoteSource;
    private ViewHolderAdapter mViewHolderAdapter;
    private int mLastSelectedPosition = -1;
    private PreferencesDataWorker mPrefsData;

    public NoteListFragment() {
    }

    protected NoteListFragment(Parcel in) {
        mIsLandscape = in.readByte() != 0;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mPrefsData = new PreferencesDataWorker(requireActivity());
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        recyclerView.setHasFixedSize(true);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            mNoteSource.add(new Note(mNoteSource.getItemsCount(), getString(R.string.new_note_caption), "", DateFormatter.getCurrentDate()));
            replaceFragments(EditorFragment.newInstance(mNoteSource.getItemsCount() - 1));
            int position = mNoteSource.getItemsCount() - 1;
            mViewHolderAdapter.notifyItemInserted(position);
            mPrefsData.writeNote(mNoteSource.getItemAt(position));
            mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
            recyclerView.scrollToPosition(position);
        });

        RecyclerView.LayoutManager layoutManager;
        if (((MainActivity) requireActivity()).isGridView() && !mIsLandscape) {
            layoutManager = new GridLayoutManager(requireActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(requireActivity());
        }
        recyclerView.setLayoutManager(layoutManager);

        mNoteSource = NoteSourceImpl.getInstance(requireActivity());
        mViewHolderAdapter = new ViewHolderAdapter(this, mNoteSource);
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            mCurrentNote = mNoteSource.getItemAt(position);
            showNote(mCurrentNote);
        });
        recyclerView.setAdapter(mViewHolderAdapter);

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mCurrentNote == null) {
            try {
                mCurrentNote = mNoteSource.getItemAt(0);
            } catch (IndexOutOfBoundsException ignored) {
                return;
            }
        }
        ((MainActivity) requireActivity()).setMenuEditClickListener((MenuItem.OnMenuItemClickListener) item -> {
            ((MainActivity) requireActivity()).showEditBar(false);
            replaceFragments(EditorFragment.newInstance(mCurrentNote.getNoteIndex()));
            return false;
        });
        if (mIsLandscape) {
            showNote(mCurrentNote);
        }
    }

    private void showNote(Note currentNote) {
        ((MainActivity) requireActivity()).showEditBar(true);
        replaceFragments(NoteFragment.newInstance(currentNote));
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.note_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.note_list_item_menu_edit) {
            if (mLastSelectedPosition != -1) {
                ((MainActivity) requireActivity()).showEditBar(false);
                replaceFragments(EditorFragment.newInstance(mLastSelectedPosition));
            }
        } else if (item.getItemId() == R.id.note_list_item_menu_delete) {
            if (mLastSelectedPosition != -1) {
                mNoteSource.remove(mLastSelectedPosition);
                mViewHolderAdapter.notifyItemRemoved(mLastSelectedPosition);
                mPrefsData.deleteNote(mLastSelectedPosition, mNoteSource.getItemsCount());
                mPrefsData.writeNotesQuantity(mNoteSource.getItemsCount());
            }
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    private void replaceFragments(Fragment newFragment) {
        ((MainActivity) requireActivity()).showSwitchView(false);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        int fragmentId;
        if (mIsLandscape) {
            fragmentId = R.id.note_container;
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (newFragment instanceof EditorFragment)
                ((MainActivity) requireActivity()).showFloatingButton(false);
        } else {
            fragmentId = R.id.note_list_container;
            ((MainActivity) requireActivity()).showFloatingButton(false);
        }
        String tag;
        if (newFragment instanceof EditorFragment) tag = "Editor";
        else tag = "Note";
        transaction.replace(fragmentId, newFragment, tag);
        transaction.commit();
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        mLastSelectedPosition = lastSelectedPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsLandscape ? 1 : 0));
    }
}