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
import android.widget.TextView;
import android.widget.Toast;

import com.evelopers.mapviewer.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gmineev on 06.07.17.
 */

public class EditMarkerDialog extends DialogFragment {

    public static final String EDIT_DIALOG_DLG_TAG = "EditMarkerDialog";
    private static final String MARKER_LATITUDE = "LATITUDE";
    private static final String MARKER_LONGITUDE = "LONGITUDE";
    private static final String MARKER_DESCRIPTION = "DESCRIPTION";
    private static final String UPDATE_GOOGLE_MAP_CALLBACK = "UpdateGoogleMapCallback";
    private String description;
    private double latitude;
    private double longitude;
    private UpdateGoogleMapCallback callback = null;

    public EditMarkerDialog() {}

    public static EditMarkerDialog newInstance(UpdateGoogleMapCallback callback,
                                               double latitude, double longitude,
                                               String description) {
        EditMarkerDialog editMarkerDialog = new EditMarkerDialog();
        Bundle args = new Bundle();
        args.putParcelable(UPDATE_GOOGLE_MAP_CALLBACK, callback);
        args.putString(MARKER_DESCRIPTION, description);
        args.putDouble(MARKER_LATITUDE, latitude);
        args.putDouble(MARKER_LONGITUDE, longitude);
        editMarkerDialog.setArguments(args);
        return editMarkerDialog;
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
        View editDialog = inflater.inflate(R.layout.edit_layout, null);
        builder.setView(editDialog);
        builder.setMessage(R.string.edit_dialog_title);
        final Dialog dlg = builder.create();

        TextView latitudeTextView = editDialog.findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(latitude));
        TextView longitudeTextView = editDialog.findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(longitude));
        TextView descriptionTextView = editDialog.findViewById(R.id.description);
        descriptionTextView.setText(description);
        Button editBtn = editDialog.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(view -> {
            String latitudeTextValue = latitudeTextView.getText().toString();
            String longitudeTextValue = longitudeTextView.getText().toString();
            if (validateData(latitudeTextValue, "Latitude")) {
                dlg.hide();
                String description = descriptionTextView.getText().toString();
                double latitudeValue = Double.valueOf(latitudeTextValue);
                double longitudeValue = Double.valueOf(longitudeTextValue);
                callback.updateMarkerPosition(new LatLng(latitudeValue, longitudeValue), description);
            }
        });

        Button cancelBtn = editDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v -> dlg.hide());

        return dlg;
    }

    private boolean validateData(String latitude, String fieldName) {
        boolean result;
        try{
            Double.parseDouble(latitude);
            result = true;
        } catch(Exception e) {
            tryRecognizeType(latitude, fieldName);
            result = false;
        }
        return result;
    }

    private void tryRecognizeType(String latitude, String fieldName) {
        if(latitude.toCharArray().length == 1) {
            Toast.makeText(getActivity(), fieldName + " is Character", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getActivity(), fieldName + " is String", Toast.LENGTH_LONG);
        }
    }

}
