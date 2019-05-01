package it.polito.mad1819.group17.deliveryapp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.orders.OrdersFragment;
import it.polito.mad1819.group17.deliveryapp.customer.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.customer.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantsFragment;

public class MainActivity extends AppCompatActivity {

    public final static int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase = null;
    private DatabaseReference mCustomerDatabaseReference = null;
    private DatabaseReference mCustomerOrdersRef = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    Fragment restaurantsFragment = new RestaurantsFragment();
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

    private void initBottomNavigation(){
        if(active == null) throw new IllegalStateException("'active' must be initialized");

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
                                        // new AuthUI.IdpConfig.GoogleBuilder().build(), //TODO: google not working
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

        progressBarHandler.hide();

        if(mCustomerOrdersRef != null)
            return;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCustomerDatabaseReference = mFirebaseDatabase.getReference().child("customers");
        mCustomerOrdersRef = mCustomerDatabaseReference.child(userId).child("orders");
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
                    startActivity(editNewProfile);
                    progressBarHandler.hide();
                    if(navigation != null) navigation.setSelectedItemId(R.id.navigation_profile);
                }
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        progressBarHandler.hide();
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


    public boolean isNewSignUp() {
        FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
        return metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
    }

    private void setAuthStateListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.v("FIREBASE_LOG", "AuthListener added - MainActivity");
    }
}
