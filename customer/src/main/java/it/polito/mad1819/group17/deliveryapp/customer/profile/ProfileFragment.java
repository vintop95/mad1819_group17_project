package it.polito.mad1819.group17.deliveryapp.customer.profile;

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
import it.polito.mad1819.group17.deliveryapp.common.Customer;
import it.polito.mad1819.group17.deliveryapp.customer.R;


public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCustomerDatabaseReference;
    private ValueEventListener mProfileEventListener;
    private FirebaseAuth mFirebaseAuth;

    private ImageView image_user_photo;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_mail;
    private TextView txt_address;
    private TextView txt_bio;

    private ProgressBarHandler progressBarHandler;

    private void locateViews(View view) {
        image_user_photo = view.findViewById(R.id.image_user_photo_sign_in);
        txt_name = view.findViewById(R.id.txt_name);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_mail = view.findViewById(R.id.txt_mail);
        txt_address = view.findViewById(R.id.txt_address);
        txt_bio = view.findViewById(R.id.txt_bio);
    }

    private void feedViews(Customer customer) {
        if (customer != null) {
            if (!customer.getImage_path().isEmpty()) {
                Glide.with(image_user_photo.getContext())
                        .load(customer.getImage_path())
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
            } else
                progressBarHandler.hide();
            txt_name.setText(customer.getName());
            txt_phone.setText(customer.getPhone());
            txt_mail.setText(customer.getMail());
            txt_address.setText(customer.getAddress());
            if (!customer.getBio().isEmpty())
                txt_bio.setText(customer.getBio());
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
        mCustomerDatabaseReference = mFirebaseDatabase.getReference().child("customers");
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
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    feedViews(customer);

                    // already done for the image (it loads slower)
                    // progressBarHandler.hide();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable to retrieve customer's information",
                            Toast.LENGTH_LONG).show();
                }
            };
            mCustomerDatabaseReference.child(userId).addValueEventListener(mProfileEventListener);
        }
    }

    private void detachValueEventListener(String userId) {
        if (mProfileEventListener != null && userId != null) {
            mCustomerDatabaseReference.child(userId).removeEventListener(mProfileEventListener);
            mProfileEventListener = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}
