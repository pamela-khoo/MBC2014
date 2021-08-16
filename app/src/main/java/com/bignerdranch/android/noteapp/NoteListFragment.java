package com.bignerdranch.android.noteapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 *  Controller class to displays the main list view.
 *  Shows the overall view of all the list items
 *
 */

public class NoteListFragment extends Fragment {

    private static final String SAVED_COUNT_VISIBLE = "count";

    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mAdapter;
    private boolean mCountVisible;
    private ConstraintLayout mEmptyView;

    ///////////////////////////////////////
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeSelected(Note note);
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Using a RecylerView for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        mNoteRecyclerView = (RecyclerView) view
                .findViewById(R.id.note_recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mCountVisible = savedInstanceState.getBoolean(SAVED_COUNT_VISIBLE);
        }
        updateUI();

        // Show empty state view if the list is empty
        mEmptyView = (ConstraintLayout) view
                .findViewById(R.id.empty_state_layout);
        NoteLab noteLab = NoteLab.get(getActivity());

        if (noteLab.getNotes().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putBoolean(SAVED_COUNT_VISIBLE, mCountVisible);
    }

    // Inflate menu to show the add and count Notes button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list, menu);

        // Action item that toggles to show or hide the subtitle
        MenuItem countItem = menu.findItem(R.id.show_count);
        if(mCountVisible) {
            countItem.setTitle(R.string.hide_count);
        } else {
            countItem.setTitle(R.string.show_count);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_note:
                Note note = new Note(); // Create a new Note
                NoteLab.get(getActivity()).addNote(note); // Add it to NoteLab

                ////////////////////////////////////
                updateUI();
                mCallbacks.onCrimeSelected(note);
                return true;

                /*
                Intent intent = NotePagerActivity
                        .newIntent(getActivity(), note.getId()); // Edit the new Note
                startActivity(intent);
                */

            case R.id.show_count:
                mCountVisible = !mCountVisible;
                getActivity().invalidateOptionsMenu();
                updateCount();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Set the subtitle of the toolbar to display the number of Notes
    private void updateCount() {
        NoteLab noteLab = NoteLab.get(getActivity());
        int noteCount = noteLab.getNotes().size();
        String count = getString(R.string.count_format, noteCount);

        if (!mCountVisible) {
            count = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(count);
    }

    // Set up NoteListFragment's user interface
    protected void updateUI() {
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();

        // Reload the list
        if (mAdapter == null) {
            mAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }

        updateCount();
    }

    // Create a ViewHolder that inflates layout
    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Note mNote;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mImportantImageView;

        public NoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_note, parent, false));
            itemView.setOnClickListener(this);

            // Pull out views in the constructor
            mTitleTextView = (TextView) itemView.findViewById(R.id.note_title);
            mDateTextView= (TextView) itemView.findViewById(R.id.note_date);
            mImportantImageView = (ImageView) itemView.findViewById(R.id.note_important);
        }

        // Bind list items
        // Called every time a new Note is displayed
        public void bind(Note note) {
            mNote = note;
            mTitleTextView.setText(mNote.getTitle());

            String dateFormat = "EEEE, MMMM dd, HH:mm";
            String dateString = DateFormat.format(dateFormat, mNote.getDate()).toString();
            mDateTextView.setText(dateString);

            mImportantImageView.setVisibility(note.isImportant() ? View.VISIBLE : View.GONE);
        }

        // Click on any list item to start an instance of NotePagerActivity
        @Override
        public void onClick(View view) {
            ////////////////////////////////////////////
            /*
            Intent intent = NotePagerActivity.newIntent(getActivity(), mNote.getId());
            startActivity(intent);
            */
            mCallbacks.onCrimeSelected(mNote);

        }
    }

    // Create an adapter
    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
        }

        // Create necessary ViewHolders
        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NoteHolder(layoutInflater, parent);
        }

        // Bind ViewHolders to data from the model layer
        // Called every time RecyclerView requests a NoteHolder be bound to a particular note
        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            Note note = mNotes.get(position);
            holder.bind(note);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes) {
            mNotes = notes;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

        NoteLab noteLab = NoteLab.get(getActivity());
        if (noteLab.getNotes().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    ///////////////////////////////
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
