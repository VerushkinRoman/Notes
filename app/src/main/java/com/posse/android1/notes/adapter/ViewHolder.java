package com.posse.android1.notes.adapter;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
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

    public void fillCard(NoteListFragment fragment, Note note, float headerSize, float noteSize) {
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(mHeader, (int) headerSize,
                (int) (fragment.getResources().getDimension(R.dimen.max_header_size)
                        / fragment.getResources().getDisplayMetrics().scaledDensity),
                2, TypedValue.COMPLEX_UNIT_PX);
        String[] caption = note.getName().split("\\R", 2);
        mHeader.setText(caption[0]);
        mDescription.setText(note.getDescription());
        mDescription.setTextSize(noteSize);
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