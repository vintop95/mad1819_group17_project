package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;

public class LocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent intent;
    String restaurantAddress;
    String customerAddress;
    double[] restaurantCoordinates;
    double[] customerCoordinates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        restaurantAddress = intent.getStringExtra("restaurant_address");
        customerAddress = intent.getStringExtra("customer_address");

        restaurantCoordinates = getLatitudeAndLongitudeFromLocation(restaurantAddress);
        customerCoordinates = getLatitudeAndLongitudeFromLocation(customerAddress);

        Log.d("AAA",Double.toString(restaurantCoordinates[0])+" "+Double.toString(restaurantCoordinates[1]));
        Log.d("AAA",Double.toString(customerCoordinates[0])+" "+Double.toString(customerCoordinates[1]));

        setContentView(R.layout.activity_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(39.233956, -77.484703)));

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

        // Add a marker in Sydney and move the camera
        LatLng latLng_restaurant = new LatLng(restaurantCoordinates[0],restaurantCoordinates[1]);
        LatLng latLng_customer = new LatLng(customerCoordinates[0],customerCoordinates[1]);

        mMap.addMarker(new MarkerOptions().position(latLng_customer).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(latLng_restaurant).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_restaurant));
    }

    private double[] getLatitudeAndLongitudeFromLocation(String location) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            return new double[]{addresses.get(0).getLatitude(), addresses.get(0).getLongitude()};
        } catch (
                IOException e) {
            return null;
        }
    }
}
