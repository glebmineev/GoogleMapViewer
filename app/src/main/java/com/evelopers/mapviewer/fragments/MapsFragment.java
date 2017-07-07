package com.evelopers.mapviewer.fragments;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.evelopers.mapviewer.R;
import com.evelopers.mapviewer.ui.ActionMarkerDialog;
import com.evelopers.mapviewer.ui.UpdateGoogleMapCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapsFragment extends Fragment
        implements OnMapReadyCallback,
                   GoogleMap.OnMarkerClickListener,
                   GoogleMap.OnMapLongClickListener {

    public final static String TAG = "MapsFragment";

    @BindView(R.id.google_maps_view)
    MapView googleMapsView;
    private GoogleMap googleMap;
    private OnFragmentInteractionListener mListener;

    public MapsFragment() {
    }

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        googleMapsView.onCreate(savedInstanceState);
        googleMapsView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: ", e);
        }
        googleMapsView.getMapAsync(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng TutorialsPoint = new LatLng(21, 57);
        MarkerOptions marker = new
                MarkerOptions().position(TutorialsPoint).title("Some marker");
//        marker.draggable(true);
//        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            // Use default InfoWindow frame
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            // Defines the contents of the InfoWindow
//            @Override
//            public View getInfoContents(Marker marker) {
//
//                // Getting view from the layout file info_window_layout
//                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_action_layout, null);
//                Button editBtn = v.findViewById(R.id.edit_btn);
//                editBtn.setOnClickListener(view -> {
//                    Toast.makeText(getActivity(), "edit!", Toast.LENGTH_LONG);
//                });
//                Button deleteBtn = v.findViewById(R.id.delete_btn);
//                deleteBtn.setOnClickListener(view -> Toast.makeText(getActivity(), "delete!", Toast.LENGTH_LONG));
//                return v;
//
//            }
//
//        });
        this.googleMap.addMarker(marker);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        ActionMarkerDialog dialog = ActionMarkerDialog.newInstance(new UpdateGoogleMapCallback() {

            @Override
            public void updateMarkerPosition(LatLng toPosition, String description) {
                animateMarker(marker, toPosition, description, false);
            }

            @Override
            public void hideMarker() {
                marker.setVisible(false);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

        }, marker);
        dialog.show(getFragmentManager(), ActionMarkerDialog.ACTION_DIALOG_DLG_TAG);
        return false;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        MarkerOptions marker = new MarkerOptions().position(point).title(point.toString());
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);

    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final String description,
                              final boolean hideMarker) {
        marker.setTitle(description);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;

                LatLng point = new LatLng(lat, lng);
                marker.setPosition(point);

                marker.hideInfoWindow();
                marker.showInfoWindow();

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));

            }
        });
    }


}
