package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.R;
import com.posse.android1.notes.adapter.ViewHolderAdapter;
import com.posse.android1.notes.note.NoteSourceImpl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NoteListFragment extends Fragment {

    private boolean mIsLandscape;
    private int mCurrentNote = -1;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration decorator = new DividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL);
        decorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.decoration)));
        recyclerView.addItemDecoration(decorator);

        RecyclerView.LayoutManager layoutManager;
        if (MainActivity.isGridView() && !mIsLandscape) {
            layoutManager = new GridLayoutManager(requireActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(requireActivity());
        }
        recyclerView.setLayoutManager(layoutManager);

        ViewHolderAdapter viewHolderAdapter = new ViewHolderAdapter(inflater,
                new NoteSourceImpl(getResources()));
        viewHolderAdapter.setOnClickListener((v, position) -> {
            mCurrentNote = position;
            showNote(mCurrentNote);
        });
        recyclerView.setAdapter(viewHolderAdapter);

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentNote = savedInstanceState.getInt(NoteFragment.NOTE_INDEX, -1);
        } else mCurrentNote = 0;
        if (mIsLandscape) {
            showNote(mCurrentNote);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(NoteFragment.NOTE_INDEX, mCurrentNote);
    }

    private void showNote(int currentNote) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (mIsLandscape) {
            fragmentTransaction.replace(R.id.note_container, NoteFragment.newInstance(currentNote));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        } else {
            fragmentTransaction.replace(R.id.note_list_container, NoteFragment.newInstance(currentNote));
        }
        fragmentTransaction.commit();
    }
}