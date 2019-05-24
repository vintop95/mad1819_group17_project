package it.polito.mad1819.group17.deliveryapp.deliveryman;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests.DeliveryRequestDetailsActivity;
import it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests.DeliveryRequestsFragment;
import it.polito.mad1819.group17.deliveryapp.deliveryman.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.deliveryman.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.deliveryman.statistics.StatsFragment;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.ProgressBarHandler;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    public final static int RC_SIGN_IN = 1;
    public final static String CHANNEL_ID = "new_delivery_request_channel_id";
    public final static int ACCESS_FINE_LOCATION_REQUEST = 200;
    public final static String FIREBASE_APP_NAME = "deliverymen";

    private FirebaseDatabase mFirebaseDatabase = null;
    private DatabaseReference mDeliverymenDatabaseRef = null;
    private DatabaseReference mDeliveryRequestsRef = null;
    private DatabaseReference mDeliverymenAvailableDatabaseRef = null;
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener onChildAddedListener;
    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    private int notificationRequestCode = 0;

    private Toolbar toolbar;

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    Fragment statsFragment = new StatsFragment();
    Fragment profileFragment = new ProfileFragment();
    Fragment deliveryRequestsFragment = new DeliveryRequestsFragment();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private ProgressBarHandler progressBarHandler;
    private BottomNavigationView navigation;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, DeliveryRequestsFragment.class.getName(), deliveryRequestsFragment);
        fm.putFragment(outState, ProfileFragment.class.getName(), profileFragment);
        fm.putFragment(outState, ProfileFragment.class.getName(), statsFragment);
        fm.putFragment(outState, "active", active);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        instantiateFragments(inState);
    }


    private void instantiateFragments(Bundle inState) {
        if (inState != null) {
            deliveryRequestsFragment = fm.getFragment(inState, DeliveryRequestsFragment.class.getName());
            profileFragment = fm.getFragment(inState, ProfileFragment.class.getName());
            statsFragment = fm.getFragment(inState, StatsFragment.class.getName());
            active = fm.getFragment(inState, "active");
        } else {
            profileFragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.main_container, profileFragment,
                    ProfileFragment.class.getName()).detach(profileFragment).commit();
            deliveryRequestsFragment = new DeliveryRequestsFragment();
            fm.beginTransaction().add(R.id.main_container, deliveryRequestsFragment,
                    DeliveryRequestsFragment.class.getName()).detach(deliveryRequestsFragment).commit();
            statsFragment = new StatsFragment();
            fm.beginTransaction().add(R.id.main_container, statsFragment,
                    StatsFragment.class.getName()).detach(statsFragment).commit();
            active = deliveryRequestsFragment;
        }
        fm.beginTransaction().attach(active).commit();
    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // Toast.makeText(MainActivity.this, "Signed In!", Toast.LENGTH_SHORT).show();
                initFirebaseDb(mFirebaseAuth.getCurrentUser().getUid());
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        // new AuthUI.IdpConfig.GoogleBuilder().build(), //TODO: google not working
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    private void initFirebaseDb(String userId) {
        if (TextUtils.isEmpty(userId)) {
            throw new IllegalStateException("Log in with FirebaseAuth first");
        }

        if (mDeliveryRequestsRef != null)
            return;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDeliverymenDatabaseRef = mFirebaseDatabase.getReference().child("deliverymen");
        mDeliveryRequestsRef = mDeliverymenDatabaseRef.child(userId).child("delivery_requests");
        mDeliverymenAvailableDatabaseRef = mFirebaseDatabase.getReference().child("deliverymen_available");

        onChildAddedListener = mDeliveryRequestsRef
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        DeliveryRequest newDeliveryRequest = dataSnapshot.getValue(DeliveryRequest.class);
                        newDeliveryRequest.setId(dataSnapshot.getKey());
                        if (newDeliveryRequest.getNotified().equals("no")) {
                            sendNotification(newDeliveryRequest.getId(), newDeliveryRequest.getTimestamp());
                            mDeliveryRequestsRef.child(newDeliveryRequest.getId()).child("notified").setValue("yes");
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        progressBarHandler.hide();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "new_order_channel";
            String description = "new_order_channel_description";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String delivery_request_id, String delivery_timestamp) {
        Intent intent = new Intent(this, DeliveryRequestDetailsActivity.class)
                .putExtra("id", delivery_request_id)
                .setAction(Long.toString(System.currentTimeMillis()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getResources().getString(R.string.new_delivery_request))
                .setContentText(delivery_timestamp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationRequestCode++, builder.build());

    }

    private void initUtils() {
        PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        CurrencyHelper.setLocaleCurrency(new Locale(language, country));

        Order.setStateLocal(getString(R.string.state_accepted),
                getString(R.string.state_in_preparation),
                getString(R.string.state_delivering),
                getString(R.string.state_delivered)
        );

        DeliveryRequest.setStateLocal(getString(R.string.state_assigned),
                getString(R.string.state_accepted),
                getString(R.string.state_delivered)
        );
    }

    private void initBottomNavigation() {
        if (active == null) throw new IllegalStateException("'active' must be initialized");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        fm.beginTransaction().detach(active).attach(profileFragment).commit();
                        active = profileFragment;
                        return true;
                    case R.id.navigation_delivery_requests:
                        fm.beginTransaction().detach(active).attach(deliveryRequestsFragment).commit();
                        active = deliveryRequestsFragment;
                        return true;
                    case R.id.navigation_stats:
                        fm.beginTransaction().detach(active).attach(statsFragment).commit();
                        active = statsFragment;
                        return true;
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int navSelected = R.id.navigation_delivery_requests;
        if (active.equals(deliveryRequestsFragment))
            navSelected = R.id.navigation_delivery_requests;
        else if (active.equals(profileFragment))
            navSelected = R.id.navigation_profile;
        else if (active.equals(statsFragment))
            navSelected = R.id.navigation_stats;

        navigation.setSelectedItemId(navSelected);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarHandler = new ProgressBarHandler(this);
        // progressBarHandler.show();

        initFirebaseAuth();

        createNotificationChannel();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initUtils();

        instantiateFragments(savedInstanceState);

        initBottomNavigation();
    }

    // Check if user exists in the db even if it's authenticated because
    // same account for different app
    public void checkNewSignUp(String uid, String userPath) {
        Query q = FirebaseDatabase.getInstance().getReference().child(userPath).child(uid);
        Log.d("QUERY NEW_SIGN_UP", q.getPath().toString());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String key = dataSnapshot.getKey();
//                if (key == null) key = "NONO";
//                Log.d("SNAPSHOT NEW_SIGN_UP", key);

                if (dataSnapshot.getValue() == null) {
                    Intent editNewProfile = new Intent(MainActivity.this, EditProfileActivity.class);
                    startActivity(editNewProfile);
                    if (navigation != null) navigation.setSelectedItemId(R.id.navigation_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.sign_in_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBarHandler.hide();
            if (resultCode == RESULT_OK) {
                String uid = mFirebaseAuth.getCurrentUser().getUid();
                initFirebaseDb(uid);
                checkNewSignUp(uid, FIREBASE_APP_NAME);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_signout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Log.v("FIREBASE_LOG", "Sign Out - MainActivity");
                return true;

            case R.id.btn_edit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            Log.v("FIREBASE_LOG", "AuthListener removed onPause - MainActivity");
        }
        setDeliverymanUnaivable(FirebaseAuth.getInstance().getUid());
    }

    private void handleLocationUpdates() {
        if (mFirebaseAuth.getUid() != null) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        String deliveryman_id = FirebaseAuth.getInstance().getUid();
                        GeoFire geoFire = new GeoFire(mDeliverymenAvailableDatabaseRef);
                        geoFire.setLocation(deliveryman_id, new GeoLocation(location.getLatitude(), location.getLongitude()),
                                new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        // workaround: needed to make setLocation(..) to work
                                        Log.d("OK", "Location set to the server");
                                    }
                                });
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(), "Turn " + provider.toUpperCase() + " on !", Toast.LENGTH_LONG).show();
                    }
                };
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);

            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);
        }
    }

    private void setDeliverymanUnaivable(String deliveryman_id) {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;
        }
        if (deliveryman_id != null) {
            GeoFire geoFire = new GeoFire(mDeliverymenAvailableDatabaseRef);
            geoFire.removeLocation(deliveryman_id, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    // workaround: needed to make removeLocation(..) to work
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAuthStateListener();
        handleLocationUpdates();
    }

    private void setAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.v("FIREBASE_LOG", "AuthListener added - MainActivity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.d("PERMISSION_LOG", "ACCESS_FINE_LOCATION permission granted");
                break;
            }
        }
    }
}
