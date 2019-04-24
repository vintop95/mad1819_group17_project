package it.polito.mad1819.group17.deliveryapp.restaurateur;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import com.firebase.ui.auth.util.ui.TextHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer.OffersFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.orders.Order;
import it.polito.mad1819.group17.deliveryapp.restaurateur.orders.OrderDetailsActivity;
import it.polito.mad1819.group17.deliveryapp.restaurateur.orders.OrdersFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.ProgressBarHandler;
import it.polito.mad1819.group17.restaurateur.R;

public class MainActivity extends AppCompatActivity {

    public final static int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase = null;
    private DatabaseReference mRestaurateurDatabaseReference = null;
    private DatabaseReference mOrdersRef = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener onChildAddedListener;
    private int notificationRequestCode = 0;

    private Order notification_order;
    private PendingIntent pendingIntent;

    Fragment offersFragment = new OffersFragment();
    Fragment ordersFragment = new OrdersFragment();
    Fragment profileFragment = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    private Toolbar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private ProgressBarHandler progressBarHandler;
    private BottomNavigationView navigation;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, OffersFragment.class.getName(), offersFragment);
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
            offersFragment = fm.getFragment(inState, OffersFragment.class.getName());
            ordersFragment = fm.getFragment(inState, OrdersFragment.class.getName());
            profileFragment = fm.getFragment(inState, ProfileFragment.class.getName());
            active = fm.getFragment(inState, "active");
        } else {
            offersFragment = new OffersFragment();
            fm.beginTransaction().add(R.id.main_container, offersFragment,
                    OffersFragment.class.getName()).detach(offersFragment).commit();
            ordersFragment = new OrdersFragment();
            fm.beginTransaction().add(R.id.main_container, ordersFragment,
                    OrdersFragment.class.getName()).detach(ordersFragment).commit();
            profileFragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.main_container, profileFragment,
                    ProfileFragment.class.getName()).detach(profileFragment).commit();
            active = ordersFragment;
        }
        fm.beginTransaction().attach(active).commit();
    }

    private void initBottomNavigation(){
        if(active == null) throw new IllegalStateException("'active' must be initalized");

        navigation = findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener
                = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    fm.beginTransaction().detach(active).attach(profileFragment).commit();
                    active = profileFragment;
                    return true;
                case R.id.navigation_dailyoffer:
                    fm.beginTransaction().detach(active).attach(offersFragment).commit();
                    active = offersFragment;
                    return true;
                case R.id.navigation_orders:
                    fm.beginTransaction().detach(active).attach(ordersFragment).commit();
                    active = ordersFragment;
                    return true;
            }
            return false;
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int navSelected = R.id.navigation_orders;
        if (active.equals(offersFragment)) {
            navSelected = R.id.navigation_dailyoffer;
        } else if (active.equals(ordersFragment)) {
            navSelected = R.id.navigation_orders;
        } else if (active.equals(profileFragment)) {
            navSelected = R.id.navigation_profile;
        }
        navigation.setSelectedItemId(navSelected);
    }

    private void initFirebaseAuth(){
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
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    private void initFirebaseDb(String userId){
        if (TextUtils.isEmpty(userId)) {
            throw new IllegalStateException("Log in with FirebaseAuth first");
        }

        if(mOrdersRef != null)
            return;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRestaurateurDatabaseReference = mFirebaseDatabase.getReference().child("restaurateurs");
        mOrdersRef = mRestaurateurDatabaseReference.child(userId).child("orders");

        onChildAddedListener = mOrdersRef
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Order newOrder = dataSnapshot.getValue(Order.class);
                        if (newOrder.getNotified().equals("no")) {
                            sendNotification(newOrder.getId(), newOrder.getDelivery_timestamp());
                            Log.d("WWW", "WWW");
                            mOrdersRef.child(newOrder.getId()).child("notified").setValue("yes");
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
            NotificationChannel channel = new NotificationChannel("new_order_channel_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String order_id, String delivery_timestamp) {
        Intent intent = new Intent(this, OrderDetailsActivity.class)
                .putExtra("id", order_id)
                .setAction(Long.toString(System.currentTimeMillis()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "new_order_channel_id");
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getResources().getString(R.string.new_order))
                .setContentText(delivery_timestamp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationRequestCode++, builder.build());
    }

    private void initUtils(){
        PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        CurrencyHelper.setLocaleCurrency(new Locale(language, country));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarHandler = new ProgressBarHandler(this);
        progressBarHandler.show();

        initFirebaseAuth();
        createNotificationChannel();

        // DONE IN SIGN IN CALLBACK because it needs a reference to the user
        // that could not exist
        // initFirebaseDb();

        // Init toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUtils();

        instantiateFragments(savedInstanceState);
        initBottomNavigation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                initFirebaseDb(mFirebaseAuth.getCurrentUser().getUid());
                if (isNewSignUp()) {
                    Intent editNewProfile = new Intent(MainActivity.this, EditProfileActivity.class);
                    // editNewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(editNewProfile);
                    progressBarHandler.hide();
                    if(navigation != null) navigation.setSelectedItemId(R.id.navigation_profile);
                    // finish();
                }
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOrdersRef.removeEventListener(onChildAddedListener);
    }

    public boolean isNewSignUp() {
        FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
        return metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
    }

    private void setAuthStateListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.v("FIREBASE_LOG", "AuthListener added - MainActivity");
    }
}
