package com.posse.android1.notes.ui.confirmation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.posse.android1.notes.R;

import java.util.Objects;

public class DeleteFragment extends DialogFragment {

    private final DialogListener mListener;

    public DeleteFragment(DialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        TextView text = view.findViewById(R.id.dialog_caption);
        text.setText(R.string.delete_confirmation);
        MaterialButton btnYes = view.findViewById(R.id.confirm_button);
        btnYes.setOnClickListener(v -> {
            dismiss();
            mListener.onOkClicked();
        });
        MaterialButton btnNo = view.findViewById(R.id.cancel_button);
        btnNo.setOnClickListener(v -> {
            dismiss();
            mListener.onCancelClicked();
        });
        setCancelable(false);
        return view;
    }

    @Override
    public void onResume() {
        Window dialogWindow = null;
        try {
            dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        } catch (Exception ignored) {
        }
        if (dialogWindow != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                //noinspection SuspiciousNameCombination
                width = displayMetrics.heightPixels;
            }
            dialogWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        }
        super.onResume();
    }
}