package com.posse.android1.notes.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
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

import com.posse.android1.notes.MainActivity;
import com.posse.android1.notes.R;
import com.posse.android1.notes.adapter.ViewHolderAdapter;
import com.posse.android1.notes.note.NoteSourceImpl;

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
    private int mCurrentNote;
    private NoteListFragmentListener mListener;

    public NoteListFragment() {
    }

    public NoteListFragment(NoteListFragmentListener listener, int currentNote) {
        mListener = listener;
        mCurrentNote = currentNote;
    }

    protected NoteListFragment(Parcel in) {
        mIsLandscape = in.readByte() != 0;
        mCurrentNote = in.readInt();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_note_list, container, false);
        recyclerView.setHasFixedSize(true);

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
            mListener.onNoteClick(mCurrentNote);
            showNote(mCurrentNote);
        });
        recyclerView.setAdapter(viewHolderAdapter);

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mCurrentNote < 1) mCurrentNote = 0;
        if (mIsLandscape) showNote(mCurrentNote);
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
            ((MainActivity) getActivity()).getSwitchView().setVisible(false);
        }
        fragmentTransaction.commit();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsLandscape ? 1 : 0));
        dest.writeInt(mCurrentNote);
    }
}