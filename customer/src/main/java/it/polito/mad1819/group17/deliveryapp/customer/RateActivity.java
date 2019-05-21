package it.polito.mad1819.group17.deliveryapp.customer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;

public class RateActivity extends AppCompatActivity {

    private RatingBar rb_restaurant;
    private RatingBar rb_service;
    private RatingBar rb_food;
    private EditText input_comment;
    private Button btn_rate;

    private String inputOrderId;
    private Order inputOrder;

    private ProgressBarHandler progressBar;

    private void locateViews() {
        rb_restaurant = findViewById(R.id.rb_restaurant);
        rb_service = findViewById(R.id.rb_service);
        rb_food = findViewById(R.id.rb_food);
        input_comment = findViewById(R.id.input_comment);
        btn_rate = findViewById(R.id.btn_rate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        locateViews();
        progressBar = new ProgressBarHandler(this);

        progressBar.show();
        inputOrderId = getIntent().getStringExtra("id");
        FirebaseDatabase.getInstance().getReference().child("customers").child(FirebaseAuth.getInstance().getUid()).child("orders").child(inputOrderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        inputOrder = dataSnapshot.getValue(Order.class);
                        progressBar.hide();
                        btn_rate.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        btn_rate.setOnClickListener(v -> storeRatesToFirebase());

    }

    private void storeRatesToFirebase() {
        if (inputOrder != null) {
            DatabaseReference restaurantRatesRef = FirebaseDatabase.getInstance().getReference().child("restaurant_rates").child(inputOrder.getRestaurant_id());
            String newPushedKey = restaurantRatesRef.push().getKey();

            HashMap<String, Object> ratesMap = new HashMap<>();
            if (rb_restaurant.getRating() != 0)
                ratesMap.put("restaurant_rate", rb_restaurant.getRating());
            if (rb_service.getRating() != 0)
                ratesMap.put("service_rate", rb_service.getRating());
            if (!TextUtils.isEmpty(input_comment.getText().toString()))
                ratesMap.put("comment", input_comment.getText().toString());

            if (!ratesMap.isEmpty()) {
                restaurantRatesRef.child(newPushedKey).updateChildren(ratesMap);
                Toast.makeText(this, getString(R.string.feedback_sent), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, getString(R.string.feedback_empty_fields), Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, getString(R.string.feedback_error), Toast.LENGTH_SHORT).show();

        finish();
    }
   
}
