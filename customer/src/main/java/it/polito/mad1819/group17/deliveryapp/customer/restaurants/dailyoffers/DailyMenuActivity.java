package it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModel;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.R;

public class DailyMenuActivity extends AppCompatActivity {

    private String restaurateur_id = null;

    private String getRestaurateurIdFromIntent(){
        // TODO: change
        return "GNEII2JrDEXimmTEdv6McBZc5fV2"; //wewe@wewewewe
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_menu);

        // Bind your views
        recyclerView = findViewById(R.id.rv_daily_offers);
        recyclerView.setHasFixedSize(false);

        // Create your layout manager
        // from Linear/Grid/StaggeredLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the firebase adapter (automatically updates)
        setFirebaseRecycler();

        progressBarHandler = new ProgressBarHandler(this);
        progressBarHandler.show();
    }

    private FoodAdapter mAdapter;
    private RecyclerView recyclerView;
    public ProgressBarHandler progressBarHandler;

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        progressBarHandler.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
        progressBarHandler.hide();
    }

    /////// FIREBASE MGMT ////////
    private void setFirebaseRecycler(){
        restaurateur_id = getRestaurateurIdFromIntent();
        if (TextUtils.isEmpty(restaurateur_id)) throw new IllegalStateException("Should not be empty");

        Query query = FoodModelCustomerUtil.getDailyOffersRef(restaurateur_id);

        FirebaseRecyclerOptions<FoodModel> options =
                new FirebaseRecyclerOptions.Builder<FoodModel>()
                        .setQuery(query, FoodModel.class)
                        .build();

        mAdapter = new FoodAdapter(this, options);
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }
    ////////////////////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
