package com.evelopers.mapviewer.ui;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gmineev on 06.07.17.
 */
public interface UpdateGoogleMapCallback extends Parcelable {

    void updateMarkerPosition(LatLng toPosition, String description);

    void hideMarker();

}
