package it.polito.mad1819.group17.deliveryapp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.orders.OrdersFragment;
import it.polito.mad1819.group17.deliveryapp.customer.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantsFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    Fragment restaurantsFragment = new RestaurantsFragment();
    Fragment ordersFragment = new OrdersFragment();
    Fragment profileFragment = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

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

    private Toolbar toolbar;
    private MenuItem btn_edit;

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
            active = ordersFragment;
        }
        fm.beginTransaction().attach(active).commit();
    }

    /*public final static Restaurateur getCurrentRestaurateur() {
        return currentRestaurateur;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        //PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        //CurrencyHelper.setLocaleCurrency(new Locale(language, country));

        instantiateFragments(savedInstanceState);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


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
            }
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int navSelected = R.id.navigation_orders;
        if (active.equals(restaurantsFragment)) {
            navSelected = R.id.navigation_restaurants;
        } else if (active.equals(ordersFragment)) {
            navSelected = R.id.navigation_orders;
        } else if (active.equals(profileFragment)) {
            navSelected = R.id.navigation_profile;
        }

        navigation.setSelectedItemId(navSelected);
    }
}
