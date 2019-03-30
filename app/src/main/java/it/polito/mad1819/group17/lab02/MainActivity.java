package it.polito.mad1819.group17.lab02;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    final Fragment offersFragment = new OffersFragment();
    final Fragment ordersFragment = new OrdersFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = offersFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    return true;
                case R.id.navigation_dailyoffer:
                    fm.beginTransaction().hide(active).show(offersFragment).commit();
                    active = offersFragment;
                    return true;
                case R.id.navigation_orders:
                    fm.beginTransaction().hide(active).show(ordersFragment).commit();
                    active = ordersFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.main_container, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_container, ordersFragment, "2").hide(ordersFragment).commit();
        fm.beginTransaction().add(R.id.main_container, offersFragment, "1").commit();
//
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_orders);
    }

}
