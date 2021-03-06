package net.xshivan.excercise4;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CoordinateManager.initialize(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null)
            return;

        CoordinateManager.initialize(getApplicationContext());
        addMarkers();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkers();
    }

    public void handleBtnAddPlaceClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddPlaceActivity.class);
        startActivity(intent);
    }

    public void handleBtnPlacesClick(View view) {
        Intent intent = new Intent(getApplicationContext(), PlacesActivity.class);
        startActivity(intent);
    }

    private void addMarkers() {
        mMap.clear();
        for (Coordinates coords : CoordinateManager.getCoordinates()) {
            LatLng mapCoords = new LatLng(coords.latitude, coords.longitude);
            mMap.addMarker((new MarkerOptions().position(mapCoords)).title(coords.name));
        }
    }
}
