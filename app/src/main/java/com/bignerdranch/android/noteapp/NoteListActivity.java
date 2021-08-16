package com.bignerdranch.android.noteapp;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 *  Controller class
 *
 */

public class NoteListActivity extends SingleFragmentActivity implements NoteListFragment.Callbacks, NoteFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }

    ////////////////////////////////////////////
    @Override
    protected  int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Note note) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = NotePagerActivity.newIntent(this, note.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = NoteFragment.newIntent(note.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Note note) {
        NoteListFragment listFragment = (NoteListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
