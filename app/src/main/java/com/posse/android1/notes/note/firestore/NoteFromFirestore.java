package com.posse.android1.notes.note.firestore;

import com.posse.android1.notes.note.Note;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NoteFromFirestore extends Note {

    public static final String FIELD_TEXT = "text";
    public static final String FIELD_HEADER = "header";
    public static final String FIELD_DATE = "date";

    public NoteFromFirestore(String name, String description, String creationDate) {
        super(name, description, creationDate);
    }

    public NoteFromFirestore(String id, String name, String description, String creationDate) {
        this(name, description, creationDate);
        setId(id);
    }

    public NoteFromFirestore(String id, Map<String, Object> fields) {
        this(id, (String) fields.get(FIELD_HEADER), (String) fields.get(FIELD_TEXT), (String) fields.get(FIELD_DATE));
    }

    public NoteFromFirestore(Note note) {
        this(note.getId(), note.getName(), note.getDescription(), note.getCreationDate());
    }

    public final Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put(FIELD_HEADER, getName());
        fields.put(FIELD_TEXT, getDescription());
        fields.put(FIELD_DATE, getCreationDate());
        return Collections.unmodifiableMap(fields);
    }
}
