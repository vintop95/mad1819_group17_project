package it.polito.mad1819.group17.lab02;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    final Fragment offersFragment = new OffersFragment();
    final Fragment ordersFragment = new OrdersFragment();
    final Fragment profileFragment = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = offersFragment;

    private Toolbar toolbar;
    private MenuItem btn_edit;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    if (btn_edit != null)
                        btn_edit.setVisible(true);
                    fm.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    return true;
                case R.id.navigation_dailyoffer:
                    if (btn_edit != null)
                        btn_edit.setVisible(false);
                    fm.beginTransaction().hide(active).show(offersFragment).commit();
                    active = offersFragment;
                    return true;
                case R.id.navigation_orders:
                    if (btn_edit != null)
                        btn_edit.setVisible(false);
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

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.fragment_profile);

        btn_edit = toolbar.getMenu().findItem(R.id.btn_edit);
        btn_edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == btn_edit.getItemId()) {
                    /*
                    start EDIT ACTIVITY here
                     */
                    Log.d("AA", "bb");
                    return true;
                }
                return false;
            }
        });

        fm.beginTransaction().add(R.id.main_container, profileFragment, "profile_fragment").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_container, ordersFragment, "orders_fragment").hide(ordersFragment).commit();
        fm.beginTransaction().add(R.id.main_container, offersFragment, "offers_fragment").commit();
//
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_orders);
    }

}
