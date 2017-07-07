package com.evelopers.mapviewer.controllers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evelopers.mapviewer.R;
import com.evelopers.mapviewer.fragments.MapsFragment;
import com.evelopers.mapviewer.ui.ProfileImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MapsFragment.OnFragmentInteractionListener,
                   NavigationView.OnNavigationItemSelectedListener{

    private static String TAG = "MainActivity";

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.empty)
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initFragment();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.flContent, MapsFragment.newInstance(), MapsFragment.TAG)
                .commit();
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.menu_map);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            initNavigationView(toolbar);
        }
    }

    /**
     * NavigationView initialization
     */
    private void initNavigationView(Toolbar toolbar) {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(toggle.getDrawerArrowDrawable());
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            DrawerArrowDrawable drawerArrowDrawable = toggle.getDrawerArrowDrawable();
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                // 1 - Display as arrow
                drawerArrowDrawable.setProgress(1);
                toggle.setDrawerIndicatorEnabled(false);

            } else {
                // 2 - Display as arrow menu
                drawerArrowDrawable.setProgress(0);
                toggle.setDrawerIndicatorEnabled(true);
            }
        });

        NavigationView navigationViewTop = (NavigationView) findViewById(R.id.navigation_drawer_top);
        navigationViewTop.setNavigationItemSelectedListener(this);

        NavigationView navigationViewBottom = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);

        setNavHeaderData(navigationViewTop);

    }

    private void setNavHeaderData(NavigationView navigationViewTop) {
        View headerView = navigationViewTop.inflateHeaderView(R.layout.nav_header);
        ProfileImageView profileImage = headerView.findViewById(R.id.profile_image);
        TextView userName = headerView.findViewById(R.id.user_name);
        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_36dp));
        userName.setText("User");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
