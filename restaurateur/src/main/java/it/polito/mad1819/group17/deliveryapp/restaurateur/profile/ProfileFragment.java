package it.polito.mad1819.group17.deliveryapp.restaurateur.profile;

import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad1819.group17.deliveryapp.restaurateur.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;
import it.polito.mad1819.group17.restaurateur.R;


public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRestaurateurDatabaseReference;
    private ValueEventListener mProfileEventListener;
    private FirebaseAuth mFirebaseAuth;

    // TODO: implement Firebase Storage to remove shared preference for photo
    public static final String PHOTO = "restaurant_photo";
    /*public static final String NAME = "restaurant_name";
    public static final String PHONE = "restaurant_phone";
    public static final String MAIL = "restaurant_mail";
    public static final String ADDRESS = "restaurant_address";
    public static final String RESTAURANT_TYPE = "restaurant_type";
    public static final String FREE_DAY = "restaurant_free_day";
    public static final String TIME_OPENING = "restaurant_time_opening";
    public static final String TIME_CLOSING = "restaurant_time_closing";
    public static final String BIO = "restaurant_bio";*/

    private ImageView image_user_photo;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_mail;
    private TextView txt_address;
    private TextView txt_restaurant_type;
    private TextView txt_free_day;
    private TextView txt_working_time;
    private TextView txt_bio;

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
            if (restaurateur.getPhoto() != "") {
                image_user_photo.setImageBitmap(PrefHelper.stringToBitMap(restaurateur.getPhoto()));
                image_user_photo.setPadding(8, 8, 8, 8);
            }
            txt_name.setText(restaurateur.getName());
            txt_phone.setText(restaurateur.getPhone());
            txt_mail.setText(restaurateur.getMail());
            txt_address.setText(restaurateur.getAddress());
            txt_restaurant_type.setText(restaurateur.getRestaurant_type());
            txt_free_day.setText(restaurateur.getFree_day());
            txt_working_time.setText(getString(R.string.from) + " " + restaurateur.getWorking_time_opening() + " " + getString(R.string.to) + " " + restaurateur.getWorking_time_closing());
            if (restaurateur.getBio() != "")
                txt_bio.setText(restaurateur.getBio());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        locateViews(view);
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
        if(userId == null) throw new AssertionError();

        if (mProfileEventListener == null) {
            mProfileEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurateur restaurateur = dataSnapshot.getValue(Restaurateur.class);
                    feedViews(restaurateur);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity().getApplicationContext(), "Unable to retrieve restaurateur's information", Toast.LENGTH_LONG).show();
                }
            };
            mRestaurateurDatabaseReference.child(userId).addListenerForSingleValueEvent(mProfileEventListener);
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
