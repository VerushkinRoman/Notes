package com.posse.android1.notes.ui.confirmation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.posse.android1.notes.R;
import com.posse.android1.notes.ui.notes.MainNoteFragment;

import java.util.Objects;

public class DeleteFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        TextView text = view.findViewById(R.id.dialog_caption);
        text.setText(R.string.delete_confirmation);
        MaterialButton btnYes = view.findViewById(R.id.confirm_button);
        btnYes.setOnClickListener(v -> {
            dismiss();
            requireActivity().getSupportFragmentManager().setFragmentResult(MainNoteFragment.KEY_REQUEST_DELETION_CONFIRMATION, new Bundle());
        });
        MaterialButton btnNo = view.findViewById(R.id.cancel_button);
        btnNo.setOnClickListener(v -> dismiss());
        setCancelable(false);
        return view;
    }

    @Override
    public void onResume() {
        try {
            Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                //noinspection SuspiciousNameCombination
                width = displayMetrics.heightPixels;
            }
            dialogWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception ignored) {
        }
        super.onResume();
    }
}