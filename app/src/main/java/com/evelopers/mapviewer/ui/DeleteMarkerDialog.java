package com.evelopers.mapviewer.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.evelopers.mapviewer.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by gmineev on 06.07.17.
 */

public class DeleteMarkerDialog extends DialogFragment {

    public static final String DELETE_DIALOG_DLG_TAG = "EditMarkerDialog";
    private static final String UPDATE_GOOGLE_MAP_CALLBACK = "UpdateGoogleMapCallback";
    private UpdateGoogleMapCallback callback = null;

    public DeleteMarkerDialog() {}

    public static DeleteMarkerDialog newInstance(UpdateGoogleMapCallback callback) {
        DeleteMarkerDialog deleteMarkerDialog = new DeleteMarkerDialog();
        Bundle args = new Bundle();
        args.putParcelable(UPDATE_GOOGLE_MAP_CALLBACK, callback);
        deleteMarkerDialog.setArguments(args);
        return deleteMarkerDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            callback = arguments.getParcelable(UPDATE_GOOGLE_MAP_CALLBACK);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View deleteDialog = inflater.inflate(R.layout.delete_layout, null);
        builder.setView(deleteDialog);
        builder.setMessage(R.string.delete_dialog_title);
        final Dialog dlg = builder.create();
        Button editBtn = deleteDialog.findViewById(R.id.delete_btn);
        editBtn.setOnClickListener(view -> {
            dlg.hide();
            callback.hideMarker();
        });

        Button cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v -> {
            dlg.hide();
        });

        return dlg;
    }

}
