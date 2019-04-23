package it.polito.mad1819.group17.deliveryapp.deliveryman;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests.DeliveryRequestsFragment;
import it.polito.mad1819.group17.deliveryapp.deliveryman.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    Fragment profileFragment = new ProfileFragment();
    Fragment deliveryRequestsFragment = new DeliveryRequestsFragment();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, DeliveryRequestsFragment.class.getName(), deliveryRequestsFragment);
        fm.putFragment(outState, ProfileFragment.class.getName(), profileFragment);
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
            active = fm.getFragment(inState, "active");
        } else {
            profileFragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.main_container, profileFragment,
                    ProfileFragment.class.getName()).detach(profileFragment).commit();
            deliveryRequestsFragment = new DeliveryRequestsFragment();
            fm.beginTransaction().add(R.id.main_container, deliveryRequestsFragment,
                    DeliveryRequestsFragment.class.getName()).detach(deliveryRequestsFragment).commit();
            active = deliveryRequestsFragment;
        }
        fm.beginTransaction().attach(active).commit();
    }

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_delivery_requests:

                    return true;
                case R.id.navigation_profile:

                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        CurrencyHelper.setLocaleCurrency(new Locale(language, country));*/

        instantiateFragments(savedInstanceState);

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
        navigation.setSelectedItemId(navSelected);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
