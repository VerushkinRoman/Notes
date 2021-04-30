package com.posse.android1.notes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.posse.android1.notes.note.Note;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

public class NoteSourceFirestoreImpl implements NoteSource {

    public static final String COLLECTION = "Notes";
    private volatile static NoteSourceFirestoreImpl sInstance;
    private final CollectionReference mCollection;
    private final HashSet<NoteSourceListener> mListeners = new HashSet<>();
    private final LinkedList<Note> mData = new LinkedList<>();
    private final FirebaseFirestore mFirestore;
    private volatile String mAuthor;
//    private volatile boolean isOfflineCompleted;

    private NoteSourceFirestoreImpl() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build();
        mFirestore.setFirestoreSettings(settings);
        mCollection = mFirestore.collection(COLLECTION);
        if (mAuthor != null) updateDB();
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

    @Override
    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String oldAuthor, String newAuthor) {
        mAuthor = newAuthor;
        if (oldAuthor != null) {
            mCollection.whereEqualTo(NoteFromFirestore.FIELD_AUTHOR, oldAuthor)
                    .orderBy(NoteFromFirestore.FIELD_INDEX, Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(task -> onFetchComplete1(task, newAuthor))
                    .addOnFailureListener(this::onFetchFailed);
        } else updateDB();
    }

    private void onFetchComplete1(Task<QuerySnapshot> task, String newAuthor) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                mCollection.document(document.getId()).update(NoteFromFirestore.FIELD_AUTHOR, newAuthor);
            }
        }

        mCollection.whereEqualTo(NoteFromFirestore.FIELD_AUTHOR, newAuthor)
                .orderBy(NoteFromFirestore.FIELD_INDEX, Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task1 -> onFetchComplete2(task1, newAuthor))
                .addOnFailureListener(this::onFetchFailed);
    }

    private void onFetchComplete2(Task<QuerySnapshot> task, String newAuthor) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                mCollection.document(document.getId());
            }
        }
    }


    private void updateDB() {
        mFirestore.disableNetwork().addOnCompleteListener(getOnCompleteListener());
    }

    @NotNull
    private OnCompleteListener<Void> getOnCompleteListener() {
        return task -> mCollection.whereEqualTo(NoteFromFirestore.FIELD_AUTHOR, mAuthor)
                .orderBy(NoteFromFirestore.FIELD_INDEX, Query.Direction.ASCENDING).get()
                .addOnCompleteListener(this::onFetchComplete).addOnFailureListener(this::onFetchFailed);
    }

    private void onFetchComplete(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            LinkedList<Note> data = new LinkedList<>();
            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                data.add(new NoteFromFirestore(document.getId(), document.getData()));
            }
//            if (!mData.isEmpty()) {
//                if (data.hashCode() != mData.hashCode()) {
//                    Log.e("TAG", "not matched");
//                } else Log.e("TAG", "matched");
//            }
            mData.clear();
            mData.addAll(data);
            data.clear();
            for (NoteSourceListener listener : mListeners) {
                listener.onFetchComplete();
            }
//            if (!isOfflineCompleted) {
//                isOfflineCompleted = true;
            mFirestore.enableNetwork();
//                mFirestore.enableNetwork().addOnCompleteListener(getOnCompleteListener());
//            }
        }
    }

    private void onFetchFailed(Exception e) {
        Log.e("TAG", "Fetch failed", e);
    }

    @Override
    public Note getItemAt(int idx) {
        return mData.get(idx);
    }

    @Override
    public int getItemsCount() {
        return mData.size();
    }

    @Override
    public void add(@NonNull Note note) {
        NoteFromFirestore noteData = getNoteFromFirestore(note);
        mData.add(noteData);
        mCollection.add(noteData.getFields()).addOnSuccessListener(documentReference -> noteData.setId(documentReference.getId()));
    }

    @Override
    public void update(@NonNull Note note) {
        String id = note.getId();
        NoteFromFirestore noteData = getNoteFromFirestore(note);
        if (id != null) mCollection.document(id).set(noteData.getFields());
    }

    @NotNull
    private NoteFromFirestore getNoteFromFirestore(@NonNull Note note) {
        NoteFromFirestore noteData;
        if (note instanceof NoteFromFirestore) {
            noteData = (NoteFromFirestore) note;
        } else {
            noteData = new NoteFromFirestore(note);
        }
        return noteData;
    }

    @Override
    public void remove(@NotNull Note note) {
        mData.remove(note);
        String id = note.getId();
        if (id != null) mCollection.document(id).delete();
//        for (long i = note.getNoteIndex(); i < getItemsCount(); i++) {
//            getItemAt(i).setNoteIndex(i);
//            update(getItemAt(i));
//        }
    }

    @Override
    public void setColor(@NonNull Note note) {
        update(note);
        for (int i = 0; i < mData.size(); i++) {
            Note tempNote = mData.get(i);
            if (Objects.equals(tempNote.getId(), note.getId())) tempNote.setColor(note.getColor());
        }
    }

    @Override
    public void addNoteSourceListener(NoteSourceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeNoteSourceListener(NoteSourceListener listener) {
        mListeners.remove(listener);
    }
}
