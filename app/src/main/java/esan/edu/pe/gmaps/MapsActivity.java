package esan.edu.pe.gmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private static final LatLng LIMA = new LatLng(-12.04318, -77.0282400);
    private static final LatLng BOGOTA = new LatLng(4.60971, -74.08175);
    private static final LatLng RIO = new LatLng(-22.970722, -43.182365);
    private Marker mLima;
    private Marker mBogota;
    private Marker mRio;
    private Button btnUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        // Move the camera instantly to Sydney with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LIMA, 2));

// Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 6000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(LIMA)      // Sets the center of the map to Mountain View
                .zoom(4)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Add some markers to the map, and add a data object to each marker.
        mLima = mMap.addMarker(new MarkerOptions()
                .position(LIMA)
                .title("Lima").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mLima.setTag(0);

        mBogota = mMap.addMarker(new MarkerOptions()
                .position(BOGOTA)
                .title("Bogota"));
        mBogota.setTag(0);

        mRio = mMap.addMarker(new MarkerOptions()
                .position(RIO).snippet("Población: 209'300,000")
                .title("Río de Janeiro").icon(BitmapDescriptorFactory.fromResource(R.drawable.brasil)));
        mRio.setTag(0);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        mMap.addPolygon(new PolygonOptions()
                .add(LIMA, BOGOTA, RIO)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

        float[] results = new float[1];

        Location.distanceBetween(LIMA.latitude, LIMA.longitude,
                BOGOTA.latitude, BOGOTA.longitude, results);

        showDistance();

        btnUbicacion = findViewById(R.id.btnUbicacion);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

        }


        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermisoUbicacion()) {
                    Toast.makeText(getApplicationContext(), "Brindó permisos", Toast.LENGTH_SHORT).show();


                } else {
                    requestStoragePermission();

                }
            }
        });

    }

    private boolean PermisoUbicacion() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean exito = false;
        if (result == PackageManager.PERMISSION_GRANTED) {
            exito = true;

            //locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //mLastLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Toast.makeText(getApplicationContext(), "lat: " +mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();


        }

        return exito;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            //Si la respuesta fue cancelada el param "grantResults" es vacío
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permiso ACEPTADO, ahora usted puede acceder al GPS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permiso DENEGADO, usted no puede acceder al GPS", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showDistance() {
        double distance = SphericalUtil.computeDistanceBetween(LIMA, BOGOTA);
        Toast.makeText(getApplicationContext(), "La distancia de LIMA a BOGOTA es de " + formatNumber(distance), Toast.LENGTH_SHORT).show();
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " ha sido clickeado " + clickCount + " veces.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

}
