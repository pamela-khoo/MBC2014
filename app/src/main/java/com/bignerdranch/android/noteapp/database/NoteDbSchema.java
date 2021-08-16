package com.bignerdranch.android.noteapp.database;

/**
 *  Define a simplified  database schema
 *  that describes what your table is named and what its columns are
 *
 */

public class NoteDbSchema {

    // Define the String constants
    public static final class NoteTable {

        // Define name of table in the database
        public static final String NAME = "notes";

        // Describe the columns
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "description";
            public static final String DATE = "date";
            public static final String IMPORTANT = "important";
        }
    }
}
