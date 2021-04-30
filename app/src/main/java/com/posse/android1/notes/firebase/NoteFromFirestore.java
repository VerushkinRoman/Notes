package com.posse.android1.notes.firebase;

import com.posse.android1.notes.note.Note;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NoteFromFirestore extends Note {

    public static final String FIELD_INDEX = "index";
    public static final String FIELD_AUTHOR = "author";
    private static final String FIELD_NOTE = "note";
    private static final String FIELD_HEADER = "header";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_COLOR = "color";

    public NoteFromFirestore(long noteIndex, String name, String description, String creationDate, int color, String author) {
        super(noteIndex, name, description, creationDate, color, author);
    }

    public NoteFromFirestore(String id, long noteIndex, String name, String description, String creationDate, int color, String author) {
        this(noteIndex, name, description, creationDate, color, author);
        setId(id);
    }

    public NoteFromFirestore(String id, Map<String, Object> fields) {
        this(id, ((Long) Objects.requireNonNull(fields.get(FIELD_INDEX))).intValue(),
                (String) fields.get(FIELD_HEADER),
                (String) fields.get(FIELD_NOTE),
                (String) fields.get(FIELD_DATE),
                ((Long) Objects.requireNonNull(fields.get(FIELD_COLOR))).intValue(),
                (String) fields.get(FIELD_AUTHOR));
    }

    public NoteFromFirestore(Note note) {
        this(note.getId(), note.getNoteIndex(), note.getName(), note.getDescription(), note.getCreationDate(), note.getColor(), note.getAuthor());
    }

    public final Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put(FIELD_INDEX, getNoteIndex());
        fields.put(FIELD_HEADER, getName());
        fields.put(FIELD_NOTE, getDescription());
        fields.put(FIELD_DATE, getCreationDate());
        fields.put(FIELD_COLOR, getColor());
        fields.put(FIELD_AUTHOR, getAuthor());
        return Collections.unmodifiableMap(fields);
    }
}
