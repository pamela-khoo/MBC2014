package com.bignerdranch.android.noteapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 *  Controller class to display the detail view.
 *  Shows the details of the selected list items
 *
 */

public class NoteFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PICTURE = "DialogPicture";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;

    private Note mNote;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mImportantCheckBox;
    private Button mSummaryButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    ///////////////////////////////////
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Note note);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }





    // Use fragment arguments to access data in the activity’s intent:
    public static NoteFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteLab.get(getActivity()).getNote(noteId);
        mPhotoFile = NoteLab.get(getActivity()).getPhotoFile(mNote);
        setHasOptionsMenu(true);
    }

    //Inflate menu to show delete button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_note:
                UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
                NoteLab noteLab = NoteLab.get(getActivity());
                mNote = noteLab.getNote(noteId);
                noteLab.deleteNote(mNote);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        NoteLab.get(getActivity())
                .updateNote(mNote);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);

        //Title field
        mTitleField = (EditText) v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    mTitleField.setError("Title field cannot be blank");
                }
                mNote.setTitle(s.toString());
                ///////////////////////////////////////////////
                updateNote();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
            }
        });

        //Description field
        mDescriptionField = (EditText) v.findViewById(R.id.note_description);
        mDescriptionField.setText(mNote.getDescription());
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setDescription(s.toString());

                ////////////////////////////////////
                updateNote();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
            }
        });

        // Date button
        mDateButton = (Button) v.findViewById(R.id.note_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mNote.getDate());

                // Receive the date back from DatePickerFragment
                dialog.setTargetFragment(NoteFragment.this, REQUEST_DATE);

                dialog.show(manager, DIALOG_DATE);
            }
        });

        // Time Button
        mTimeButton = (Button)v.findViewById(R.id.note_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mNote.getDate());
                dialog.setTargetFragment(NoteFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        updateDate();

        // Checkbox
        mImportantCheckBox = (CheckBox)v.findViewById(R.id.note_important);
        mImportantCheckBox.setChecked(mNote.isImportant());
        mImportantCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNote.setImportant(isChecked);

                ////////////////////////////////////
                updateNote();
            }
        });

        // Summary button - send summary of note
        mSummaryButton = (Button) v.findViewById(R.id.note_summary);
        mSummaryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getNoteSummary());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.note_summary_subject));
                i = Intent.createChooser(i, getString(R.string.send_summary));
                startActivity(i);
            }
        });

        // Photo Button - take a new photo
        mPhotoButton = (ImageButton) v.findViewById(R.id.note_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.noteapp.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        //Photo view - show photos taken
        mPhotoView = (ImageView) v.findViewById(R.id.note_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = NoteLab.get(getActivity()).getPhotoFile(mNote).getPath();
                PictureFragment.newInstance(filePath).show(getFragmentManager(), DIALOG_PICTURE);
            }
        });

        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean hasLoadedPhoto = false;
            @Override
            public void onGlobalLayout() {
                if (!hasLoadedPhoto) {
                    updatePhotoView();
                    hasLoadedPhoto = true;
                }
            }
        });

        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mNote.setDate(date);
            updateDate();

            ////////////////////////////////////
            updateNote();

        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mNote.setDate(date);
            updateDate();

            ////////////////////////////////////
            updateNote();

        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.noteapp.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();

            ////////////////////////////////////
            updateNote();
        }
    }

    private void updateDate() {
        Date d = mNote.getDate();
        CharSequence date = DateFormat.format("EEEE, MMM dd, yyyy", d);
        CharSequence time = DateFormat.format("HH:mm", d);
        mDateButton.setText(date);
        mTimeButton.setText(time);
    }

    /////////////////////////////////////////////////////////////
    private void updateNote() {
        NoteLab.get(getActivity()).updateNote(mNote);
        mCallbacks.onCrimeUpdated(mNote);
    }

    public static NoteFragment newIntent (UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, crimeId);
        NoteFragment crimeFragment = new NoteFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }





    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    // Create 4 strings and piece them together to return a complete summary
    private String getNoteSummary() {
        String importantString = null;
        if (mNote.isImportant()) {
            importantString = getString(R.string.note_summary_important);
        } else {
            importantString = getString(R.string.note_summary_unimportant);
        }

        String dateFormat = "EEEE, MMMM dd";
        String dateString = DateFormat.format(dateFormat, mNote.getDate()).toString();

        String timeFormat = "HH:mm";
        String timeString = DateFormat.format(timeFormat, mNote.getDate()).toString();

        String summary = getString(R.string.note_summary,
                mNote.getTitle(), dateString, timeString, importantString);

        return summary;
    }


    ///////////////////////////////////
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
