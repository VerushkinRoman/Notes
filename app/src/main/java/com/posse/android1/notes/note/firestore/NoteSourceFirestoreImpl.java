package com.posse.android1.notes.note.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.posse.android1.notes.note.BaseNoteSource;
import com.posse.android1.notes.note.Note;

import java.util.LinkedList;

public class NoteSourceFirestoreImpl extends BaseNoteSource {

    private static final String TAG = "NoteSourceFirebase";
    private final static String COLLECTION_NOTES = "com.posse.CollectionNotes";
    private volatile static NoteSourceFirestoreImpl sInstance;

    private final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private final CollectionReference mCollection = mStore.collection(COLLECTION_NOTES);

    private NoteSourceFirestoreImpl() {
        mCollection.get().
                addOnCompleteListener(this::onFetchComplete).
                addOnFailureListener(this::onFetchFailed);
    }

    public static NoteSourceFirestoreImpl getInstance() {
        NoteSourceFirestoreImpl instance = sInstance;
        if (instance == null) {
            synchronized (NoteSourceFirestoreImpl.class) {
                if (sInstance == null) {
                    instance = new NoteSourceFirestoreImpl();
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    private void onFetchComplete(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            LinkedList<Note> data = new LinkedList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                data.add(new NoteFromFirestore(
                        document.getId(), document.getData()));
            }
            mData.clear();
            mData.addAll(data);
            data.clear();
            notifyDataSetChanged();
        }
    }

    private void onFetchFailed(Exception e) {
        Log.e(TAG, "Fetch failed", e);
    }

    @Override
    public void add(@NonNull Note note) {
        final NoteFromFirestore noteData;
        if (note instanceof NoteFromFirestore) {
            noteData = (NoteFromFirestore) note;
        } else {
            noteData = new NoteFromFirestore(note);
        }
        mData.add(noteData);
        mCollection.add(noteData.getFields()).addOnSuccessListener(documentReference -> noteData.setId(documentReference.getId()));
        super.add(noteData);
    }

    @Override
    public void remove(int position) {
        String id = mData.get(position).getId();
        mCollection.document(id).delete();
        super.remove(position);
    }

    @Override
    public void update(@NonNull Note note) {
        String id = note.getId();
        final NoteFromFirestore noteData;
        if (note instanceof NoteFromFirestore) {
            noteData = (NoteFromFirestore) note;
        } else {
            noteData = new NoteFromFirestore(note);
        }
        mCollection.document(id).set(noteData.getFields());
        super.update(note);
    }
}
