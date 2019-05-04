package it.polito.mad1819.group17.deliveryapp.restaurateur.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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

import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.common.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;


public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRestaurateurDatabaseReference;
    private ValueEventListener mProfileEventListener;
    private FirebaseAuth mFirebaseAuth;

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

    private void locateViews(View view) {
        image_user_photo = view.findViewById(R.id.image_user_photo_sign_in);
        txt_name = view.findViewById(R.id.txt_name);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_mail = view.findViewById(R.id.txt_mail);
        txt_address = view.findViewById(R.id.txt_address);
        txt_restaurant_type = view.findViewById(R.id.txt_restaurant_type);
        txt_free_day = view.findViewById(R.id.input_free_day_sign_in);
        txt_working_time = view.findViewById(R.id.txt_working_time);
        txt_bio = view.findViewById(R.id.txt_bio);
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
            } else{
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBarHandler = new ProgressBarHandler(getContext());
        setHasOptionsMenu(true);
        locateViews(view);
        progressBarHandler.show();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

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
        detachValueEventListener(mFirebaseAuth.getUid());
        Log.v("FIREBASE_LOG", "EventListener removed onPause - ProfileFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        attachValueEventListener(mFirebaseAuth.getUid());
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
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable to retrieve restaurateur's information",
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}
