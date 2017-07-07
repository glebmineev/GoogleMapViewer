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
import com.google.android.gms.maps.model.Marker;

/**
 * Created by gmineev on 06.07.17.
 */
public class ActionMarkerDialog extends DialogFragment {

    public static final String ACTION_DIALOG_DLG_TAG = "EditMarkerDialog";
    private static final String MARKER_LATITUDE = "LATITUDE";
    private static final String MARKER_LONGITUDE = "LONGITUDE";
    private static final String MARKER_DESCRIPTION = "DESCRIPTION";
    private static final String UPDATE_GOOGLE_MAP_CALLBACK = "UpdateGoogleMapCallback";
    private String description;
    private double latitude;
    private double longitude;
    private UpdateGoogleMapCallback callback = null;

    public ActionMarkerDialog() {
    }

    public static ActionMarkerDialog newInstance(UpdateGoogleMapCallback callback, Marker marker) {
        ActionMarkerDialog actionMarkerDialog = new ActionMarkerDialog();
        Bundle args = new Bundle();
        args.putParcelable(UPDATE_GOOGLE_MAP_CALLBACK, callback);
        args.putString(MARKER_DESCRIPTION, marker.getTitle());
        args.putDouble(MARKER_LATITUDE, marker.getPosition().latitude);
        args.putDouble(MARKER_LONGITUDE, marker.getPosition().longitude);
        actionMarkerDialog.setArguments(args);
        return actionMarkerDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            callback = arguments.getParcelable(UPDATE_GOOGLE_MAP_CALLBACK);
            description = arguments.getString(MARKER_DESCRIPTION);
            latitude = arguments.getDouble(MARKER_LATITUDE);
            longitude = arguments.getDouble(MARKER_LONGITUDE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View actionDialog = inflater.inflate(R.layout.action_dialog, null);
        builder.setView(actionDialog);
        builder.setMessage(R.string.action_dialog);
        final Dialog dlg = builder.create();
        Button editBtn = actionDialog.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(view -> {
            dlg.hide();
            EditMarkerDialog dialog = EditMarkerDialog.newInstance(callback, latitude, longitude, description);
            dialog.show(getFragmentManager(), ACTION_DIALOG_DLG_TAG);
        });

        Button deleteBtn = actionDialog.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(view -> {
            dlg.hide();
            DeleteMarkerDialog dialog = DeleteMarkerDialog.newInstance(callback);
            dialog.show(getFragmentManager(), ACTION_DIALOG_DLG_TAG);
        });

        Button cancelBtn = actionDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v -> {
            dlg.hide();
        });

        return dlg;
    }

}
