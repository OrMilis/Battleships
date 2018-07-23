package com.example.ormil.battleships;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ormil.battleships.Logic.LeaderboardRepository;
import com.example.ormil.battleships.Logic.User;
import com.example.ormil.battleships.Logic.eDifficulty;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LeaderboardScreen extends AppCompatActivity
        implements OnMapReadyCallback, LeaderboardListFragment.OnLeaderboardActionListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    ArrayList<Marker> markerList = new ArrayList<Marker>();

    eDifficulty currentDifficulty;

    ListView listViewReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnMarkerClickListener(this);

        updateMarkersData(currentDifficulty);
    }

    @Override
    public void updateMarkersData(eDifficulty difficulty) {

        currentDifficulty = difficulty;

        ArrayList<User> userList = new ArrayList<User> (LeaderboardRepository.getInstance().getTopUsersByDifficulty(difficulty));

        if(mGoogleMap != null) {
            mGoogleMap.clear();
            markerList.clear();
            for (User user : userList) {
                LatLng location = new LatLng(user.getLat(), user.getLon());
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(location).title(user.getName() + " " + user.getScore()));
                markerList.add(marker);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(markerList.contains(marker)){
            ((LeaderboardAdapter)listViewReference.getAdapter()).setActiveView(markerList.indexOf(marker));
            ((LeaderboardAdapter)listViewReference.getAdapter()).notifyDataSetChanged();
            listViewReference.smoothScrollToPosition(markerList.indexOf(marker));
            marker.showInfoWindow();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            return true;
        }
        return false;
    }

    @Override
    public void moveToMarker(int position) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(position).getPosition()));
    }

    @Override
    public void setListViewReference(ListView listViewReference) {
        this.listViewReference = listViewReference;
    }

}
