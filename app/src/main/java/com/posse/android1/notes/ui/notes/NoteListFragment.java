package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.posse.android1.notes.R;
import com.posse.android1.notes.adapter.ViewHolderAdapter;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.confirmation.DeleteFragment;

import org.jetbrains.annotations.NotNull;

public class NoteListFragment extends Fragment {

    public static final String KEY_POSITION_CLICKED = NoteListFragment.class.getCanonicalName() + "_position";
    public static final String KEY_REQUEST_DELETE_POSITION = NoteListFragment.class.getCanonicalName() + "_deletePosition";
    public static final String KEY_POSITION_LONG_CLICKED = NoteListFragment.class.getCanonicalName() + "_positionLongClick";
    private static final String KEY_GRID_VIEW = NoteListFragment.class.getCanonicalName() + "_mIsGridView";
    private ViewHolderAdapter mViewHolderAdapter;
    private RecyclerView mRecyclerView;
    private FragmentManager mFragmentManager;
    private boolean mIsGridView;

    public NoteListFragment() {
    }

    public NoteListFragment(boolean isGridView) {
        mIsGridView = isGridView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsGridView = savedInstanceState.getBoolean(KEY_GRID_VIEW);
        }
        mFragmentManager = requireActivity().getSupportFragmentManager();
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_DELETE_POSITION, this, (requestKey, result) -> {
            int deletePosition = result.getInt(MainNoteFragment.KEY_DELETE_POSITION);
            mViewHolderAdapter.notifyItemRemoved(deletePosition);
            mViewHolderAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager;
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (mIsGridView && !isLandscape) {
            layoutManager = new GridLayoutManager(requireActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(requireActivity());
        }
        mRecyclerView.setLayoutManager(layoutManager);

        mViewHolderAdapter = new ViewHolderAdapter(this, NoteSourceImpl.getInstance(requireActivity()));
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            Bundle result = new Bundle();
            result.putInt(KEY_POSITION_CLICKED, position);
            mFragmentManager.setFragmentResult(MainNoteFragment.KEY_REQUEST_CLICKED_POSITION, result);
        });
        mRecyclerView.setAdapter(mViewHolderAdapter);

        return mRecyclerView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.note_list_menu, menu);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_GRID_VIEW, mIsGridView);
    }

    public void onDataChanged(int idx, boolean isNewNote) {
        if (isNewNote) {
            mViewHolderAdapter.notifyItemInserted(idx);
        } else mViewHolderAdapter.notifyItemChanged(idx);
        mRecyclerView.scrollToPosition(idx);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.note_list_item_menu_edit) {
            mFragmentManager.setFragmentResult(MainNoteFragment.KEY_REQUEST_EDIT_ACTION, new Bundle());
        } else if (item.getItemId() == R.id.note_list_item_menu_delete) {
            new DeleteFragment().show(mFragmentManager, null);
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    public void onViewHolderLongClick(int position) {
        Bundle result = new Bundle();
        result.putInt(KEY_POSITION_LONG_CLICKED, position);
        mFragmentManager.setFragmentResult(MainNoteFragment.KEY_REQUEST_LONG_CLICKED_POSITION, result);
    }

    public void setGridView(boolean isGridView) {
        mIsGridView = isGridView;
    }
}