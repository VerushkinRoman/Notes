package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.posse.android1.notes.R;
import com.posse.android1.notes.viewHolder.ViewHolderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    public static final String KEY_POSITION_CLICKED = NoteListFragment.class.getCanonicalName() + "_position";
    public static final String KEY_REQUEST_DELETE_POSITION = NoteListFragment.class.getCanonicalName() + "_deletePosition";
    public static final String KEY_POSITION_LONG_CLICKED = NoteListFragment.class.getCanonicalName() + "_positionLongClick";
    public static final int ALL_ITEMS_CHANGED = -1;
    private static final String KEY_GRID_VIEW = NoteListFragment.class.getCanonicalName() + "_mIsGridView";
    private static final String KEY_HEADER_SIZE = NoteListFragment.class.getCanonicalName() + "_mHeaderTextSize";
    private static final String KEY_NOTE_SIZE = NoteListFragment.class.getCanonicalName() + "_mNoteTextSize";
    private ViewHolderAdapter mViewHolderAdapter;
    private RecyclerView mRecyclerView;
    private FragmentManager mFragmentManager;
    private boolean mIsGridView;
    private float mHeaderTextSize;
    private float mNoteTextSize;

    public NoteListFragment() {
    }

    public static NoteListFragment newInstance(boolean isGridView, float headerTextSize, float noteTextSize) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_GRID_VIEW, isGridView);
        args.putFloat(KEY_HEADER_SIZE, headerTextSize);
        args.putFloat(KEY_NOTE_SIZE, noteTextSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsGridView = getArguments().getBoolean(KEY_GRID_VIEW);
            mHeaderTextSize = getArguments().getFloat(KEY_HEADER_SIZE);
            mNoteTextSize = getArguments().getFloat(KEY_NOTE_SIZE);
        }
        mFragmentManager = requireActivity().getSupportFragmentManager();
        mFragmentManager.setFragmentResultListener(KEY_REQUEST_DELETE_POSITION, this, (requestKey, result) -> {
            ArrayList<Integer> positionsToDelete = result.getIntegerArrayList(MainNoteFragment.KEY_DELETE_POSITION);
            for (int i = 0; i < positionsToDelete.size(); i++) {
                mViewHolderAdapter.notifyItemRemoved(positionsToDelete.get(i));
            }
            mViewHolderAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        mRecyclerView.setHasFixedSize(true);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        RecyclerView.LayoutManager layoutManager = (mIsGridView && !isLandscape) ?
                new GridLayoutManager(requireActivity(), 2) : new LinearLayoutManager(requireActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mViewHolderAdapter = new ViewHolderAdapter(this, mHeaderTextSize, mNoteTextSize);
//        mViewHolderAdapter = new ViewHolderAdapter(this, NoteSourceFirestoreImpl.getInstance(requireActivity()), mHeaderTextSize, mNoteTextSize);
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            Bundle result = new Bundle();
            result.putInt(KEY_POSITION_CLICKED, position);
            mFragmentManager.setFragmentResult(MainNoteFragment.KEY_REQUEST_CLICKED_POSITION, result);
        });
        mRecyclerView.setAdapter(mViewHolderAdapter);

        return mRecyclerView;
    }

    public void onDataChanged(int idx, boolean isNewNote) {
        if (idx == ALL_ITEMS_CHANGED) {
            mViewHolderAdapter.notifyDataSetChanged();
        } else {
            if (isNewNote) {
                mViewHolderAdapter.notifyItemInserted(idx);
            } else mViewHolderAdapter.notifyItemChanged(idx);
            mRecyclerView.scrollToPosition(idx);
        }
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