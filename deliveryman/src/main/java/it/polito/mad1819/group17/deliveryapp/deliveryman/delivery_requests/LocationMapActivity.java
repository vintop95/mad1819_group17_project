package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.DataParser;


public class LocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    LocationRequest mLocationRequest;
    Intent intent;
    GeoApiContext context;
    String restaurantAddress;
    String customerAddress;
    String meantime, origin, destination;
    GoogleApiClient mGoogleApiClient;
    DateTime now, arrive;
    DirectionsResult result;
    Polyline line;
    BitmapDescriptor trasparent;
    double[] restaurantCoordinates;
    double[] customerCoordinates;

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        restaurantAddress = intent.getStringExtra("restaurant_address");
        customerAddress = intent.getStringExtra("customer_address");

        Log.d("AAA", restaurantAddress);
        Log.d("AAA", customerAddress);


        restaurantCoordinates = getLatitudeAndLongitudeFromLocation(restaurantAddress);
        customerCoordinates = getLatitudeAndLongitudeFromLocation(customerAddress);

        Log.d("AAA", Double.toString(restaurantCoordinates[0]) + " " + Double.toString(restaurantCoordinates[1]));
        Log.d("AAA", Double.toString(customerCoordinates[0]) + " " + Double.toString(customerCoordinates[1]));

        setContentView(R.layout.activity_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.one_px);
        trasparent = BitmapDescriptorFactory.fromBitmap(img);

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
        LatLng latLng_restaurant = new LatLng(restaurantCoordinates[0], restaurantCoordinates[1]);
        LatLng latLng_customer = new LatLng(customerCoordinates[0], customerCoordinates[1]);

        mMap.addMarker(new MarkerOptions().position(latLng_customer).title("CUSTOMER"));
        mMap.addMarker(new MarkerOptions().position(latLng_restaurant).title("RESTAURATEUR")).showInfoWindow();

        try{
        mMap.setMyLocationEnabled(true);
        }catch (SecurityException se){
            Log.e("security_exception",se.getLocalizedMessage());
        }


            ///////////////////
            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();

            origin = Double.toString(restaurantCoordinates[0]) + "," + Double.toString(restaurantCoordinates[1]);
            destination = Double.toString(customerCoordinates[0]) + "," + Double.toString(customerCoordinates[1]);

            //Execute Directions API request
             context = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
           // DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
            DirectionsApiRequest req = DirectionsApi.getDirections(context, origin,destination);

            try {
                now = new DateTime();
                DirectionsResult res = req.departureTime(now).await();
                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs !=null) {
                        for(int i=0; i<route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j=0; j<leg.steps.length;j++){
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length >0) {
                                        for (int k=0; k<step.steps.length;k++){
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;
                                            if (points1 != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for (com.google.maps.model.LatLng coord1 : coords1) {
                                                    path.add(new LatLng(coord1.lat, coord1.lng));
                                                }
                                            }
                                            //meantime = "Time :"+ result.routes[0].legs[0].duration.humanReadable + " Distance :" + result.routes[0].legs[0].distance.humanReadable;
                                            //Log.d("meantime",meantime);
                                        }
                                    } else {
                                        EncodedPolyline points = step.polyline;
                                        if (points != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                path.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch(Exception ex) {
                Log.e("exception_catched", ex.getLocalizedMessage());
            }





        //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10).clickable(true);
                line = mMap.addPolyline(opts);
                //line.setTag("" + path.size());
                Log.d("polyline",Integer.toString(path.size()));
            }

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                try{
                    now = new DateTime();
                    result = DirectionsApi.newRequest(context)
                            .origin(origin)
                            .destination(destination)
                            .departureTime(now).await();

                    meantime = "Time: "+ result.routes[0].legs[0].duration.humanReadable + ", Distance: " + result.routes[0].legs[0].distance.humanReadable;
                    mMap.addMarker(new MarkerOptions().position(getPolylineCentroid(line)).title(meantime).icon(trasparent)).showInfoWindow();
                }catch (Exception ex){
                    Log.e("exception_catched_2", ex.getLocalizedMessage());
                }
                Log.e("Polyline position", " -- " + polyline.getTag());

            }
        });

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng_restaurant, 13));
            //////////////////

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


    public LatLng getPolylineCentroid(@NonNull Polyline p) {

        LatLng return_points = p.getPoints().get(0);
        if(p.getPoints().size()>0){
            int middle_length = (int) p.getPoints().size()/2;
            return_points = p.getPoints().get(middle_length);
        }
        return return_points;
        }

}




