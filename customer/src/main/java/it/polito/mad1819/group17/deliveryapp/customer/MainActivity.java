package it.polito.mad1819.group17.deliveryapp.customer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
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

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.orders.OrdersFragment;
import it.polito.mad1819.group17.deliveryapp.customer.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.customer.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantsFragment;

public class MainActivity extends AppCompatActivity {
    public final static String FIREBASE_APP_NAME = "customers";
    public final static int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase = null;
    private DatabaseReference mCustomerDatabaseReference = null;
    private DatabaseReference mCustomerOrdersRef = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private int notificationRequestCode = 0;

    Fragment restaurantsFragment = new RestaurantsFragment();
    Fragment ordersFragment = new OrdersFragment();
    Fragment profileFragment = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    private Toolbar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private ProgressBarHandler progressBarHandler;
    private BottomNavigationView navigation;

    private Boolean firstAccess = true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, RestaurantsFragment.class.getName(), restaurantsFragment);
        fm.putFragment(outState, OrdersFragment.class.getName(), ordersFragment);
        fm.putFragment(outState, ProfileFragment.class.getName(), profileFragment);
        fm.putFragment(outState, "active", active);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        instantiateFragments(inState);
    }

    private void instantiateFragments(Bundle inState) {
        if (inState != null) {
            restaurantsFragment = fm.getFragment(inState, RestaurantsFragment.class.getName());
            ordersFragment = fm.getFragment(inState, OrdersFragment.class.getName());
            profileFragment = fm.getFragment(inState, ProfileFragment.class.getName());
            active = fm.getFragment(inState, "active");
        } else {
            restaurantsFragment = new RestaurantsFragment();
            fm.beginTransaction().add(R.id.main_container, restaurantsFragment,
                    RestaurantsFragment.class.getName()).detach(restaurantsFragment).commit();
            ordersFragment = new OrdersFragment();
            fm.beginTransaction().add(R.id.main_container, ordersFragment,
                    OrdersFragment.class.getName()).detach(ordersFragment).commit();
            profileFragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.main_container, profileFragment,
                    ProfileFragment.class.getName()).detach(profileFragment).commit();
            active = restaurantsFragment;
        }
        fm.beginTransaction().attach(active).commit();
    }

    private void initBottomNavigation() {
        if (active == null) throw new IllegalStateException("'active' must be initialized");

        navigation = findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener
                = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    fm.beginTransaction().detach(active).attach(profileFragment).commit();
                    active = profileFragment;
                    return true;
                case R.id.navigation_restaurants:
                    fm.beginTransaction().detach(active).attach(restaurantsFragment).commit();
                    active = restaurantsFragment;
                    return true;
                case R.id.navigation_orders:
                    fm.beginTransaction().detach(active).attach(ordersFragment).commit();
                    active = ordersFragment;
                    return true;
            }
            return false;
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int navSelected = R.id.navigation_restaurants;
        if (active.equals(restaurantsFragment)) {
            navSelected = R.id.navigation_restaurants;
        } else if (active.equals(ordersFragment)) {
            navSelected = R.id.navigation_orders;
        } else if (active.equals(profileFragment)) {
            navSelected = R.id.navigation_profile;
        }

        navigation.setSelectedItemId(navSelected);
    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // Toast.makeText(MainActivity.this, "Signed In!", Toast.LENGTH_SHORT).show();
                initFirebaseDb(mFirebaseAuth.getCurrentUser().getUid());
            } else {
                progressBarHandler.show();
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

        progressBarHandler.hide();

        if (mCustomerOrdersRef != null)
            return;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCustomerDatabaseReference = mFirebaseDatabase.getReference().child("customers");
        mCustomerOrdersRef = mCustomerDatabaseReference.child(userId).child("orders");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarHandler = new ProgressBarHandler(this);

        initFirebaseAuth();

        // Init toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initUtils();

        instantiateFragments(savedInstanceState);
        initBottomNavigation();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null && firstAccess) {
            checkNewSignUp(user.getUid(), FIREBASE_APP_NAME);
        }

        createNotificationChannel();
        fetchDeliveredOrdersWithoutRate();
    }

    // Check if user exists in the db even if it's authenticated because
    // same account for different app
    public void checkNewSignUp(String uid, String userPath) {
        Query q = FirebaseDatabase.getInstance().getReference().child(userPath).child(uid);
        Log.d("QUERY NEW_SIGN_UP", q.getPath().toString());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    Intent editNewProfile = new Intent(MainActivity.this, EditProfileActivity.class);
                    firstAccess = true;
                    editNewProfile.putExtra("firstAccess", firstAccess);
                    startActivity(editNewProfile);
                    if (navigation != null) navigation.setSelectedItemId(R.id.navigation_profile);
                    Toast.makeText(MainActivity.this, "Please, complete your profile first!", Toast.LENGTH_LONG).show();
                }
                else
                    firstAccess = false;
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
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            Log.v("FIREBASE_LOG", "AuthListener removed onPause - MainActivity");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAuthStateListener();
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

    private void setAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.v("FIREBASE_LOG", "AuthListener added - MainActivity");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "rate_channel";
            String description = "rate_channel_description";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("rate_channel_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String order_id) {
        Intent intent = new Intent(this, RateActivity.class)
                .putExtra("id", order_id)
                .setAction(Long.toString(System.currentTimeMillis()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "new_order_channel_id");
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getResources().getString(R.string.rate_title_notification).toUpperCase())
                .setContentText(getString(R.string.rate_content_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationRequestCode++, builder.build());
    }

    private void fetchDeliveredOrdersWithoutRate() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            FirebaseDatabase.getInstance().getReference().child("customers").child(FirebaseAuth.getInstance().getUid()).child("orders")
                    .orderByChild("rate_notified").equalTo("no").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot orderDataSnapshot : dataSnapshot.getChildren()) {
                            Order order = orderDataSnapshot.getValue(Order.class);
                            if (order.getCurrentState() == Order.STATE4) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("customers")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("orders")
                                        .child(order.getId())
                                        .child("rate_notified").setValue("yes");
                                sendNotification(order.getId());
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
