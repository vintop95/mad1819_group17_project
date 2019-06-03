package it.polito.mad1819.group17.deliveryapp.restaurateur.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class AnalyticsActivity extends AppCompatActivity {

    private RatingBar rb_summary_restaurant;
    private RatingBar rb_summary_service;
    private TextView tv_summary_comments;
    private RecyclerView rv_reviews;
    private ProgressBarHandler progressBar;
    private ArrayList<Review> reviews;
    private Context context = this;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        rb_summary_restaurant = findViewById(R.id.rb_summary_restaurant);
        rb_summary_service = findViewById(R.id.rb_summary_service);
        tv_summary_comments = findViewById(R.id.tv_summary_comments);
        rv_reviews = findViewById(R.id.rv_reviews);
        rv_reviews.setLayoutManager(new LinearLayoutManager(this));

        progressBar = new ProgressBarHandler(this);
        progressBar.show();

        showBackArrowOnToolbar();

        if (FirebaseAuth.getInstance().getUid() != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("restaurant_rates").child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() != 0) {
                                Float totalRestaurantRate = new Float(0);
                                Float totalServiceRate = new Float(0);
                                int restaurantRates = 0;
                                int serviceRates = 0;
                                int comments = 0;
                                reviews = new ArrayList<Review>();
                                for (DataSnapshot reviewDataSnapshot : dataSnapshot.getChildren()) {
                                    Review review = reviewDataSnapshot.getValue(Review.class);
                                    if (review.getRestaurant_rate() != null) {
                                        totalRestaurantRate += review.getRestaurant_rate();
                                        restaurantRates++;
                                    }
                                    if (review.getService_rate() != null) {
                                        totalServiceRate += review.getService_rate();
                                        serviceRates++;
                                    }
                                    if (!TextUtils.isEmpty(review.getComment()))
                                        comments++;
                                    reviews.add(review);

                                }
                                rb_summary_restaurant.setRating(totalRestaurantRate / restaurantRates);
                                rb_summary_service.setRating(totalServiceRate / serviceRates);
                                tv_summary_comments.setText("" + comments);

                                rv_reviews.setAdapter(new ReviewsAdapter(reviews, context, rv_reviews));
                            } else
                                Toast.makeText(context, getString(R.string.no_reviews), Toast.LENGTH_LONG).show();
                            //rv_reviews.setAdapter(new ReviewsAdapter(reviews, context, rv_reviews));
                            progressBar.hide();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void showBackArrowOnToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
