package com.bignerdranch.android.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.noteapp.database.NoteBaseHelper;
import com.bignerdranch.android.noteapp.database.NoteCursorWrapper;
import com.bignerdranch.android.noteapp.database.NoteDbSchema.NoteTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* Create singleton to store data while application is in memory.
* Will be destroyed when app is stopped and Android removes app from memory
*/

public class NoteLab {
    private static NoteLab sNoteLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context) {
        if (sNoteLab == null) {
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }

    // Opening a SQLiteDatabase
    private NoteLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext)
                .getWritableDatabase();
    }

    // Add a new Note
    public void addNote(Note c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(NoteTable.NAME, null, values);
    }

    // Delete a Note from the database
    public int deleteNote(Note note) {
        String uuidString = note.getId().toString();
        return mDatabase.delete(
                NoteTable.NAME,
                NoteTable.Cols.UUID + " = ?",
                new String[] { uuidString }
        );
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();

        NoteCursorWrapper cursor = queryNotes(null, null);

        // Walk the cursor to read table row by row
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return notes;
    }

    public Note getNote(UUID id) {
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    // Provides complete local filepath for Note's image
    public File getPhotoFile(Note note) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, note.getPhotoFilename());
    }

    public void updateNote(Note note) {
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new NoteCursorWrapper(cursor);
    }

    //Write data to database
    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();

        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteTable.Cols.DESCRIPTION, note.getDescription());
        values.put(NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteTable.Cols.IMPORTANT, note.isImportant() ? 1 : 0);

        return values;
    }
}
