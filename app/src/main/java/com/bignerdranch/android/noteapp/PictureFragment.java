package com.bignerdranch.android.noteapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class PictureFragment extends DialogFragment {

    private final static String ARG_FILE_PATH = "file_path";

    public static PictureFragment newInstance(String filePath) {
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, filePath);

        PictureFragment pictureFragment = new PictureFragment();
        pictureFragment.setArguments(args);
        return pictureFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String filePath = getArguments().getString(ARG_FILE_PATH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture, null);

        Bitmap bitmap = PictureUtils.getScaledBitmap(filePath, getActivity());
        ImageView photoImageView = (ImageView)v.findViewById(R.id.note_picture_zoom);
        photoImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.picture_dialog_title)
                .create();
    }
}