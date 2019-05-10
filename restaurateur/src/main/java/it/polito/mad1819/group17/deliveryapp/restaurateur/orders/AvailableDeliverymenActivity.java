package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.common.AvailableDeliveryman;
import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class AvailableDeliverymenActivity extends AppCompatActivity {
    public final static double RADIUS_KM = 10000.0;
    public final static int RESULT_OK = 1;

    private Context context = this;

    private Toolbar toolbar;

    private DatabaseReference mDatabaseReference;
    private String restaurateur_id;
    private GeoFire geoFire;

    private Double restaurantLatitude = null;
    private Double restaurantLongitude = null;

    private ArrayList<AvailableDeliveryman> availableDeliverymen;
    private RecyclerView recyclerView;

    private void initUtils() {
        PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        CurrencyHelper.setLocaleCurrency(new Locale(language, country));

        Order.setStateLocal(getString(R.string.state_accepted),
                getString(R.string.state_in_preparation),
                getString(R.string.state_delivering)
        );

        DeliveryRequest.setStateLocal(getString(R.string.state_assigned),
                getString(R.string.state_accepted),
                getString(R.string.state_delivered)
        );
    }

    private void initFirebaseStuff() {
        restaurateur_id = FirebaseAuth.getInstance().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(mDatabaseReference.child("deliverymen_available"));
    }

    private void computeRestaurantLatitudeAndLongitude(String location) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            restaurantLatitude = addresses.get(0).getLatitude();
            restaurantLongitude = addresses.get(0).getLongitude();
        } catch (
                IOException e) {
            restaurantLatitude = null;
            restaurantLongitude = null;
        }

    }

    private void showBackArrowOnToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_deliverymen);

        showBackArrowOnToolbar();

        recyclerView = findViewById(R.id.rv_available_deliverymen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initUtils();

        initFirebaseStuff();


        mDatabaseReference.child("restaurateurs").child(restaurateur_id).child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        computeRestaurantLatitudeAndLongitude(dataSnapshot.getValue().toString());

                        //Log.d("RESTAURANT_COORDINATES", restaurantLatitude + "_" + restaurantLongitude);

                        availableDeliverymen = new ArrayList<AvailableDeliveryman>();


                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(restaurantLatitude, restaurantLongitude), RADIUS_KM);
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                //Log.d("XX", (new AvailableDeliveryman(key, location.latitude, location.longitude, restaurantLatitude, restaurantLongitude)).toString());
                                availableDeliverymen.add(new AvailableDeliveryman(key, location, new GeoLocation(restaurantLatitude, restaurantLongitude)));
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (availableDeliverymen.size() == 0)
                                    Toast.makeText(getApplicationContext(), "No currently deliverymen available. Try again in a while!", Toast.LENGTH_LONG).show();

                                /*for (AvailableDeliveryman p : availableDeliverymen)
                                    Log.d("BEFORE_SORT", p.toString());*/

                                else {
                                    Collections.sort(availableDeliverymen);

                                /*for (AvailableDeliveryman p : availableDeliverymen)
                                    Log.d("AFTER_SORT", p.toString());*/

                                    AvailableDeliverymenAdapter availableDeliverymenAdapter = new AvailableDeliverymenAdapter(availableDeliverymen, context);
                                    recyclerView.setAdapter(availableDeliverymenAdapter);
                                }
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
