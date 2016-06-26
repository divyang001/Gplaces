package com.devil07.divyang.gplaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by divyang on 26/6/16.
 */
public class Displaymaps extends AppCompatActivity {
    private GoogleMap googleMap;
    ArrayList<MapDetails> mp = new ArrayList<MapDetails>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        mp = (ArrayList<MapDetails>) getIntent().getSerializableExtra("FILES_TO_SEND");
        Toast.makeText(Displaymaps.this, "total" + mp.size(), Toast.LENGTH_SHORT).show();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initilizeMap() {


        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // create marker
            // MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
            // CameraPosition cameraPosition = new CameraPosition.Builder().target(
            //       new LatLng(17.385044, 78.486671)).zoom(7).build();

            //   googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //  MarkerOptions marker = new MarkerOptions().position(new LatLng(17.385044, 78.48667)).title("Hello Maps ");

            // adding marker

            MapDetails obj = mp.get(0);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(obj.getLat(), obj.getLng())).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions marker = new MarkerOptions().position(new LatLng(obj.getLat(), obj.getLng())).title(obj.getName());
            googleMap.addMarker(marker);


            for (int i = 1; i < mp.size(); i++) {
                MapDetails objj = mp.get(i);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(objj.getLat(), objj.getLng()))
                        .title(objj.getName())
                );


            }
            //  googleMap.addMarker(marker);


            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
