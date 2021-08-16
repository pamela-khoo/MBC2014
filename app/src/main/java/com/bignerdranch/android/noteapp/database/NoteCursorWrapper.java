package com.bignerdranch.android.noteapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.noteapp.Note;

import java.util.Date;
import java.util.UUID;

import static com.bignerdranch.android.noteapp.database.NoteDbSchema.NoteTable.Cols;

/**
 *  Read data from the database by
 *  pulling out relevant column data
 *
 */

public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String title = getString(getColumnIndex(Cols.TITLE));
        String description = getString(getColumnIndex(Cols.DESCRIPTION));
        long date = getLong(getColumnIndex(Cols.DATE));
        int isImportant = getInt(getColumnIndex(Cols.IMPORTANT));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDescription(description);
        note.setDate(new Date(date));
        note.setImportant(isImportant != 0);

        return note;
    }
}
