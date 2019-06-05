package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad1819.group17.deliveryapp.deliveryman.R;

import static it.polito.mad1819.group17.deliveryapp.deliveryman.MainActivity.ACCESS_FINE_LOCATION_REQUEST;


public class LocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static int ASSIGNED_STATUS = 0;
    public static int ACCEPTED_STATUS = 1;

    public GoogleMap mMap;
    Intent intent;
    GeoApiContext context;
    String restaurantAddress;
    String customerAddress;
    String meantime, origin, destination, current;
    DateTime now;
    DirectionsResult result;
    Polyline line;
    BitmapDescriptor trasparent;
    Button goToGoogleMaps;
    double[] restaurantCoordinates;
    double[] customerCoordinates;
    double[] currentCoordinates;
    int orderState;
    LocationManager locationManager = null;
    LocationListener locationListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        restaurantAddress = intent.getStringExtra("restaurant_address");
        customerAddress = intent.getStringExtra("customer_address");
        orderState = intent.getIntExtra("order_status", 0);


        Log.d("AAA", restaurantAddress);
        Log.d("AAA", customerAddress);



        restaurantCoordinates = getLatitudeAndLongitudeFromLocation(restaurantAddress);
        customerCoordinates = getLatitudeAndLongitudeFromLocation(customerAddress);
        currentCoordinates = getLatitudeAndLongitudeFromLocation(customerAddress);




        try {
            Log.d("AAA", Double.toString(restaurantCoordinates[0]) + " " + Double.toString(restaurantCoordinates[1]));
            Log.d("AAA", Double.toString(customerCoordinates[0]) + " " + Double.toString(customerCoordinates[1]));

            setContentView(R.layout.activity_location_map);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.one_px);
            trasparent = BitmapDescriptorFactory.fromBitmap(img);


            goToGoogleMaps = findViewById(R.id.goToGoogleMapsButton);
        } catch (Exception e) {
            Log.e("exception", e.getLocalizedMessage());
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {


            origin = Double.toString(restaurantCoordinates[0]) + "," + Double.toString(restaurantCoordinates[1]);
            destination = Double.toString(customerCoordinates[0]) + "," + Double.toString(customerCoordinates[1]);

            Log.d("orderstate",orderState+"");


        } catch (Exception e) {
            Log.e("exception", e.getLocalizedMessage());
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
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
        if(orderState == ASSIGNED_STATUS) {
            setLocationManagerListener();
        }
        else {
            plotRoutes(origin,destination);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurantCoordinates[0], restaurantCoordinates[1]), 13));
        }

    }


    private double[] getLatitudeAndLongitudeFromLocation(String location) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            if (addresses.size() != 0)
                return new double[]{addresses.get(0).getLatitude(), addresses.get(0).getLongitude()};
            else
                return null;
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


    private void plotRoutes(String origin, String destination){
        LatLng latLng_restaurant = new LatLng(restaurantCoordinates[0], restaurantCoordinates[1]);
        LatLng latLng_customer = new LatLng(customerCoordinates[0], customerCoordinates[1]);

        mMap.addMarker(new MarkerOptions().position(latLng_customer).title("CUSTOMER"));
        mMap.addMarker(new MarkerOptions().position(latLng_restaurant).title("RESTAURATEUR")).showInfoWindow();

        try {
            mMap.setMyLocationEnabled(true);

            goToGoogleMaps.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    String url = "http://maps.google.com/maps?saddr=" + origin + "&daddr=" + destination;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        } catch (SecurityException se) {
            Log.e("security_exception", se.getLocalizedMessage());
        }


        ///////////////////
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        context = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, origin, destination);


        //ROUTE RESTAURANT -> CUSTOMER
        try {
            now = new DateTime();
            DirectionsResult res = req.departureTime(now).await();
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
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

        } catch (Exception ex) {
            Log.e("exception_catched", ex.getLocalizedMessage());
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10).clickable(true);
            line = mMap.addPolyline(opts);
            //line.setTag("" + path.size());
            Log.d("polyline", Integer.toString(path.size()));
        }

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                try {
                    now = new DateTime();
                    result = DirectionsApi.newRequest(context)
                            .origin(origin)
                            .destination(destination)
                            .departureTime(now).await();

                    meantime = "Time: " + result.routes[0].legs[0].duration.humanReadable + ", Distance: " + result.routes[0].legs[0].distance.humanReadable;
                    mMap.addMarker(new MarkerOptions().position(getPolylineCentroid(line)).title(meantime).icon(trasparent)).showInfoWindow();
                } catch (Exception ex) {
                    Log.e("exception_catched_2", ex.getLocalizedMessage());
                }
                Log.e("Polyline position", " -- " + polyline.getTag());

            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng_restaurant, 13));
        //////////////////
    }


    private void setLocationManagerListener(){
        Log.d("setLocationManager", currentCoordinates[0]+ " "+currentCoordinates[1]);

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentCoordinates[0] = location.getLatitude();
                currentCoordinates[1] = location.getLongitude();
                Log.d("setLocationManager", currentCoordinates[0]+ " "+currentCoordinates[1]);
                current = Double.toString(currentCoordinates[0]) + "," + Double.toString(currentCoordinates[1]);

                MarkerOptions mo = new MarkerOptions();
                mo.position(new LatLng(location.getLatitude(),location.getLongitude()));
                //currentPosMarker = mMap.addMarker(mo);
                plotRoutes(current,origin);
                if(locationManager!=null && locationListener!=null)locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, locationListener);
        }
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);

    }
}

