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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.R;
import com.posse.android1.notes.adapter.ViewHolderAdapter;
import com.posse.android1.notes.note.NoteSourceImpl;
import com.posse.android1.notes.ui.confirmation.DeleteFragment;

import org.jetbrains.annotations.NotNull;


public class NoteListFragment extends Fragment {

    public static final String KEY_POSITION_CLICKED = NoteListFragment.class.getCanonicalName() + "position";
    public static final String KEY_REQUEST_DELETE_POSITION = NoteListFragment.class.getCanonicalName() + "deletePosition";
    public static final String KEY_POSITION_LONG_CLICKED = NoteListFragment.class.getCanonicalName() + "positionLongClick";
    private NoteListFragmentListener mListener;

    private ViewHolderAdapter mViewHolderAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getSupportFragmentManager().setFragmentResultListener(KEY_REQUEST_DELETE_POSITION, this, (requestKey, result) -> {
            int deletePosition = result.getInt(MainNoteFragment.KEY_DELETE_POSITION);
            mViewHolderAdapter.notifyItemRemoved(deletePosition);
            mViewHolderAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> mListener.onAddButtonPressed());

        RecyclerView.LayoutManager layoutManager;
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (((MainActivity) requireActivity()).isGridView() && !isLandscape) {
            layoutManager = new GridLayoutManager(requireActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(requireActivity());

        }
        mRecyclerView.setLayoutManager(layoutManager);

        mViewHolderAdapter = new ViewHolderAdapter(this, NoteSourceImpl.getInstance(requireActivity()));
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            Bundle result = new Bundle();
            result.putInt(KEY_POSITION_CLICKED, position);
            requireActivity().getSupportFragmentManager().setFragmentResult(MainNoteFragment.KEY_REQUEST_CLICKED_POSITION, result);
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

    public void onDataChanged(int idx, boolean isNewNote) {
        if (isNewNote) {
            mViewHolderAdapter.notifyItemInserted(idx);
            mRecyclerView.scrollToPosition(idx);
        } else mViewHolderAdapter.notifyItemChanged(idx);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.note_list_item_menu_edit) {
            mListener.onContextEditMenuSelected();
        } else if (item.getItemId() == R.id.note_list_item_menu_delete) {
            new DeleteFragment().show(requireActivity().getSupportFragmentManager(), null);
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    public void setListener(NoteListFragmentListener listener) {
        mListener = listener;
    }

    public void onViewHolderLongClick(int position) {
        Bundle result = new Bundle();
        result.putInt(KEY_POSITION_LONG_CLICKED, position);
        getParentFragmentManager().setFragmentResult(MainNoteFragment.KEY_REQUEST_LONG_CLICKED_POSITION, result);
    }
}