package com.posse.android1.notes;

import android.content.Intent;
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

import java.util.Objects;

public class NotesList extends Fragment {

    private Note mCurrentNote;
    private boolean mIsLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_notes_list, container, false);
        String[] notes = getResources().getStringArray(R.array.notes_list);
        for (int i = 0; i < notes.length; i++) {
            String note = notes[i];
            MaterialTextView tv = new MaterialTextView(Objects.requireNonNull(getContext()));
            tv.setText(note);
            tv.setTextSize(30);
            final int finalI = i;
            tv.setOnClickListener(v -> {
                mCurrentNote = new Note(finalI,
                        getResources().getStringArray(R.array.notes_list)[finalI],
                        getResources().getStringArray(R.array.notes)[finalI],
                        getResources().getStringArray(R.array.dates)[finalI]);
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
            mCurrentNote = savedInstanceState.getParcelable(NotesFragment.NOTE_INDEX);
        } else {
            mCurrentNote = new Note(0,
                    getResources().getStringArray(R.array.notes_list)[0],
                    getResources().getStringArray(R.array.notes)[0],
                    getResources().getStringArray(R.array.dates)[0]);
        }
        if (mIsLandscape) {
            showLandNote(mCurrentNote);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(NotesFragment.NOTE_INDEX, mCurrentNote);
    }

    private void showNote(Note currentNote) {
        if (mIsLandscape) {
            showLandNote(currentNote);
        } else {
            showPortNote(currentNote);
        }
    }

    private void showPortNote(Note currentNote) {
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        intent.putExtra(NoteActivity.CURRENT_NOTE, currentNote);
        startActivity(intent);
    }

    private void showLandNote(Note currentNote) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note_container, NotesFragment.newInstance(currentNote));
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}