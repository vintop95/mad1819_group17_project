package it.polito.mad1819.group17.deliveryapp.restaurateur;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer.OffersFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.orders.OrdersFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;
import it.polito.mad1819.group17.restaurateur.R;

public class MainActivity extends AppCompatActivity {
    public static boolean DEVELOP_MODE = true;

    private Restaurateur currentRestaurateur;


    Fragment offersFragment = new OffersFragment();
    Fragment ordersFragment = new OrdersFragment();
    Fragment profileFragment = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

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

    private Toolbar toolbar;
    private MenuItem btn_edit;

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

    /*public final static Restaurateur getCurrentRestaurateur() {
        return currentRestaurateur;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Restaurateur restaurateur = (Restaurateur) getIntent().getSerializableExtra("restaurateur");
        if (restaurateur != null && currentRestaurateur == null) {
            currentRestaurateur = restaurateur;
            getIntent().putExtra("restaurateur", currentRestaurateur);
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.fragment_profile);

        btn_edit = toolbar.getMenu().findItem(R.id.btn_edit);
        btn_edit.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == btn_edit.getItemId()) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                return true;
            }
            return false;
        });

        PrefHelper.setMainContext(this);

        // TODO: LET THE USER CHANGE THE CURRENCY FROM SETTINGS?
        String language = Locale.ITALY.getLanguage();
        String country = Locale.ITALY.getCountry();
        CurrencyHelper.setLocaleCurrency(new Locale(language, country));

        instantiateFragments(savedInstanceState);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        if (btn_edit != null)
                            btn_edit.setVisible(true);
                        fm.beginTransaction().detach(active).attach(profileFragment).commit();
                        active = profileFragment;
                        return true;
                    case R.id.navigation_dailyoffer:
                        if (btn_edit != null)
                            btn_edit.setVisible(false);
                        fm.beginTransaction().detach(active).attach(offersFragment).commit();
                        active = offersFragment;
                        return true;
                    case R.id.navigation_orders:
                        if (btn_edit != null)
                            btn_edit.setVisible(false);
                        fm.beginTransaction().detach(active).attach(ordersFragment).commit();
                        active = ordersFragment;
                        return true;
                }
                return false;
            }
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
}
