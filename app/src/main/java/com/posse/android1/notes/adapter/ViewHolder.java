package com.posse.android1.notes.adapter;

import android.content.res.TypedArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.ui.notes.NoteListFragment;

class ViewHolder extends RecyclerView.ViewHolder {

    private final AppCompatTextView mHeader;
    private final AppCompatTextView mDescription;
    private final MaterialTextView mTimestamp;
    private final AppCompatImageView mCheckbox;
    private final CardView mCard;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mHeader = itemView.findViewById(R.id.list_note_header);
        mDescription = itemView.findViewById(R.id.list_note_description);
        mTimestamp = itemView.findViewById(R.id.list_note_date_time);
        mCheckbox = itemView.findViewById(R.id.delete_checkbox);
        mCard = itemView.findViewById(R.id.card_list_notes);
    }

    public void fillCard(NoteListFragment fragment, Note note) {
        mHeader.setText(note.getName());
        mDescription.setText(note.getDescription());
        mTimestamp.setText(note.getCreationDate());
        int color = note.getColor();
        if (color != -1) {
            color = ResourcesCompat.getColor(fragment.getResources(), color, null);
        } else {
            TypedArray array = fragment.requireActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackgroundFloating});
            color = array.getColor(0, 0xFF00FF);
            array.recycle();
        }
        mCard.setCardBackgroundColor(color);
        mCheckbox.setVisibility(note.isDeleteVisible() ? View.VISIBLE : View.INVISIBLE);
        itemView.setOnLongClickListener((v) -> {
            fragment.onViewHolderLongClick(getLayoutPosition());
            return false;
        });
        fragment.registerForContextMenu(itemView);
    }

    public void clear(Fragment fragment) {
        itemView.setOnLongClickListener(null);
        fragment.unregisterForContextMenu(itemView);
    }
}