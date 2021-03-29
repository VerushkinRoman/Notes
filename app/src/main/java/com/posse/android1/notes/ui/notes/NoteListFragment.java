package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;

public class NoteListFragment extends Fragment {

    private boolean mIsLandscape;
    private LinearLayout mLinearLayout;
    private int mCurrentNote = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLinearLayout = new LinearLayout(requireContext());
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
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
            mLinearLayout.addView(tv);
        }
        return mLinearLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
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
        } else {
            fragmentTransaction.replace(R.id.note_list_container, NoteFragment.newInstance(currentNote));
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}