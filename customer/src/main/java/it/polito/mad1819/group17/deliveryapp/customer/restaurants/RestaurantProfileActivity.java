package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad1819.group17.deliveryapp.common.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.R;


public class RestaurantProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRestaurateurDatabaseReference;
    private ValueEventListener mProfileEventListener;
    private FirebaseAuth mFirebaseAuth;

    private Toolbar toolbar;
    private ImageView image_user_photo;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_mail;
    private TextView txt_address;
    private TextView txt_restaurant_type;
    private TextView txt_free_day;
    private TextView txt_working_time;
    private TextView txt_bio;

    private ProgressBarHandler progressBarHandler;
    private String restaurateur_id;

    private void locateViews() {
        toolbar = findViewById(R.id.toolbar_edit);

        image_user_photo = findViewById(R.id.image_user_photo_sign_in);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_mail = findViewById(R.id.txt_mail);
        txt_address = findViewById(R.id.txt_address);
        txt_restaurant_type = findViewById(R.id.txt_restaurant_type);
        txt_free_day = findViewById(R.id.input_free_day_sign_in);
        txt_working_time = findViewById(R.id.txt_working_time);
        txt_bio = findViewById(R.id.txt_bio);
    }

    private void feedViews(Restaurateur restaurateur) {
        if (restaurateur != null) {
            if (!restaurateur.getImage_path().isEmpty()) {
                Glide.with(image_user_photo.getContext())
                        .load(restaurateur.getImage_path())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                progressBarHandler.hide();
                                Log.e("ProfileFragment", "Image load failed");
                                return false; // leave false
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                Log.v("ProfileFragment", "Image load OK");
                                progressBarHandler.hide();
                                return false; // leave false
                            }
                        }).into(image_user_photo);
            }else{
                Glide.with(image_user_photo.getContext()).clear(image_user_photo);
                progressBarHandler.hide();
            }
            txt_name.setText(restaurateur.getName());
            txt_phone.setText(restaurateur.getPhone());
            txt_mail.setText(restaurateur.getMail());
            txt_address.setText(restaurateur.getAddress());
            txt_working_time.setText(getString(R.string.from) + " " +
                    restaurateur.getWorking_time_opening() + " " +
                    getString(R.string.to) + " " + restaurateur.getWorking_time_closing());
            if (!restaurateur.getBio().isEmpty())
                txt_bio.setText(restaurateur.getBio());

            String[] restTypes = getResources().getStringArray(R.array.restaurant_types);
            Integer index;
            try {
                index = Integer.valueOf(restaurateur.getRestaurant_type());
            }catch(NumberFormatException e){
                index=0;
            }
            txt_restaurant_type.setText(restTypes[index]);

            String[] days = getResources().getStringArray(R.array.days_of_week);
            Integer dayIndex;
            try {
                dayIndex = Integer.valueOf(restaurateur.getFree_day());
            }catch(NumberFormatException e){
                dayIndex=0;
            }
            txt_free_day.setText(days[dayIndex]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        Intent inputIntent = getIntent();
        restaurateur_id = inputIntent.getStringExtra("restaurant_id");

        locateViews();
        progressBarHandler = new ProgressBarHandler(this);

        showBackArrowOnToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBarHandler.show();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRestaurateurDatabaseReference = mFirebaseDatabase.getReference().child("restaurateurs");
    }

    @Override
    public void onStop() {
        super.onStop();
        progressBarHandler.hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachValueEventListener(restaurateur_id);
        Log.v("FIREBASE_LOG", "EventListener removed onPause - ProfileFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        attachValueEventListener(restaurateur_id);
        Log.v("FIREBASE_LOG", "EventListener added onResume - ProfileFragment");
    }

    private void attachValueEventListener(String userId) {
        if (userId == null) throw new IllegalArgumentException();

        if (mProfileEventListener == null) {
            mProfileEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurateur restaurateur = dataSnapshot.getValue(Restaurateur.class);
                    feedViews(restaurateur);

                    // already done for the image (it loads slower)
                    // progressBarHandler.hide();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.cannot_retrieve_restaurateur_info),
                            Toast.LENGTH_LONG).show();
                }
            };
            mRestaurateurDatabaseReference.child(userId).addValueEventListener(mProfileEventListener);
        }
    }

    private void detachValueEventListener(String userId) {
        if (mProfileEventListener != null && userId != null) {
            mRestaurateurDatabaseReference.child(userId).removeEventListener(mProfileEventListener);
            mProfileEventListener = null;
        }
    }

    private void showBackArrowOnToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }
}
