package com.bignerdranch.android.noteapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 *  Create and manage the ViewPager.
 *  The ViewPager allows users to navigate between list items by swiping
 *  across the screen to “page” forward or backward through the notes
 *
 */

public class NotePagerActivity  extends AppCompatActivity implements NoteFragment.Callbacks{

    private static final String EXTRA_NOTE_ID = "com.bignerdranch.android.noteapp.note_id";

    private ViewPager mViewPager;
    private List<Note> mNotes;


    //////////////////////////////////////////
    @Override
    public void onCrimeUpdated(Note note) {

    }

    public static Intent newIntent(Context packageContext, UUID noteId) {
        Intent intent = new Intent(packageContext, NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);

        mViewPager =(ViewPager) findViewById(R.id.note_view_pager);

        mNotes = NoteLab.get(this).getNotes();

        // Set up PagerAdapter to help populate pages inside of ViewPager
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Note note = mNotes.get(position);
                return NoteFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });

        // Loop to find index of the note
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getId().equals(noteId)) {
                // Set the current item to the index of that note
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
