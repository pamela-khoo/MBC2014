package com.bignerdranch.android.noteapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 *  Controller that hosts NoteFragment.
 *  Hosting is an activity that provides a spot in its
 *  view hierarchy for the fragment to place its view
 *
 */

public class NoteActivity extends SingleFragmentActivity {

    private static final String EXTRA_NOTE_ID =
            "com.bignerdranch.android.noteapp.note_id";

    // Tell NoteFragment which Note to display by passing the noteId as an Intent extra
    public static Intent newIntent(Context packageContext, UUID noteId) {
        Intent intent = new Intent(packageContext, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID noteId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_NOTE_ID);

        return NoteFragment.newInstance(noteId);
    }
}
