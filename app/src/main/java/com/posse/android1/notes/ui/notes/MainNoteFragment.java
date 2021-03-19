package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;

import java.util.Objects;

public class MainNoteFragment extends Fragment {

    private int mCurrentNote = -1;
    private boolean mIsLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_notes, container, false);
        String[] notes = getResources().getStringArray(R.array.notes_list);
        for (int i = 0; i < notes.length; i++) {
            String note = notes[i];
            MaterialTextView tv = new MaterialTextView(requireContext());
            tv.setText(note);
            tv.setTextSize(30);
            final int finalI = i;
            tv.setOnClickListener(v -> {
                mCurrentNote = finalI;
                showNote(mCurrentNote);
            });
            view.addView(tv);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (savedInstanceState != null) {
            mCurrentNote = savedInstanceState.getInt(NoteFragment.NOTE_INDEX, -1);
        } else mCurrentNote = 0;
//        if (mIsLandscape) {
//            showLandNote(mCurrentNote);
//        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(NoteFragment.NOTE_INDEX, mCurrentNote);
    }

    private void showNote(int currentNote) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mIsLandscape) {
            fragmentTransaction.replace(R.id.note_list, NoteFragment.newInstance(currentNote));
            fragmentTransaction.remove(this);
        } else {
            fragmentTransaction.replace(R.id.note_list, NoteFragment.newInstance(currentNote));
            fragmentTransaction.remove(this);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

//    private void showLandNote(int currentNote) {
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.note_container, NotesFragment.newInstance(currentNote));
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fragmentTransaction.commit();
//    }
}