package it.polito.mad1819.group17.deliveryapp.deliveryman.profile;

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

import it.polito.mad1819.group17.deliveryapp.common.Deliveryman;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;


public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDeliverymanReference;
    private ValueEventListener mProfileEventListener;
    private FirebaseAuth mFirebaseAuth;

    private ImageView image_user_photo;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_mail;
    private TextView txt_city;
    private TextView txt_bio;

    private ProgressBarHandler progressBarHandler;

    private void locateViews(View view) {
        image_user_photo = view.findViewById(R.id.image_user_photo_sign_in);
        txt_name = view.findViewById(R.id.txt_name);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_mail = view.findViewById(R.id.txt_mail);
        txt_city = view.findViewById(R.id.txt_city);
        txt_bio = view.findViewById(R.id.txt_bio);
    }

    private void feedViews(Deliveryman deliveryman) {
        if (deliveryman != null) {
            if (!deliveryman.getImage_path().isEmpty()) {
                Glide.with(image_user_photo.getContext())
                        .load(deliveryman.getImage_path())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                progressBarHandler.hide();
                                Log.e("ProfileFragment","Image load failed");
                                return false; // leave false
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                Log.v("ProfileFragment","Image load OK");
                                progressBarHandler.hide();
                                return false; // leave false
                            }
                        }).into(image_user_photo);
            }
            txt_name.setText(deliveryman.getName());
            txt_phone.setText(deliveryman.getPhone());
            txt_mail.setText(deliveryman.getMail());
            txt_city.setText(deliveryman.getCity());
            if (!deliveryman.getBio().isEmpty())
                txt_bio.setText(deliveryman.getBio());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBarHandler = new ProgressBarHandler(getContext());
        setHasOptionsMenu(true);
        locateViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBarHandler.show();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDeliverymanReference = mFirebaseDatabase.getReference().child("deliverymen");
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
                    Deliveryman deliveryman = dataSnapshot.getValue(Deliveryman.class);
                    feedViews(deliveryman);

                    // already done for the image (it loads slower)
                    // progressBarHandler.hide();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable to retrieve deliveryman's information",
                            Toast.LENGTH_LONG).show();
                }
            };
            mDeliverymanReference.child(userId).addValueEventListener(mProfileEventListener);
        }
    }

    private void detachValueEventListener(String userId) {
        if (mProfileEventListener != null && userId != null) {
            mDeliverymanReference.child(userId).removeEventListener(mProfileEventListener);
            mProfileEventListener = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}
