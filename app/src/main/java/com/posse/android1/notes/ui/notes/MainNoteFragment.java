package com.posse.android1.notes.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.posse.android1.notes.R;

public class MainNoteFragment extends Fragment {

    private static final String KEY_NOTE_FRAGMENT = MainNoteFragment.class.getCanonicalName() + "mNoteListFragment";
    private NoteListFragment mNoteListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_notes, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_NOTE_FRAGMENT, mNoteListFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        if (savedInstanceState != null) {
            mNoteListFragment = savedInstanceState.getParcelable(KEY_NOTE_FRAGMENT);
            fragmentManager.popBackStack();
        } else mNoteListFragment = new NoteListFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note_list_container, mNoteListFragment, "ListOfNotes");
        fragmentTransaction.commit();
    }
}