package it.polito.mad1819.group17.deliveryapp.customer.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.common.Customer;
import it.polito.mad1819.group17.deliveryapp.customer.R;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCustomerDatabaseReference;
    private ValueEventListener mEditEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;

    public static final int CAMERA_REQUEST = 0;
    public static final int GALLERY_REQUEST = 1;
    public static final int AUTOCOMPLETE_REQUEST = 2;

    private Toolbar toolbar;
    private Boolean image_changed = false;
    private ImageView image_user_photo;
    private EditText input_name;
    private EditText input_phone;
    private EditText input_mail;
    private AutoCompleteTextView input_address;
    private EditText input_bio;

    private String newAddress = null;


    private void locateViews() {
        toolbar = findViewById(R.id.toolbar_edit);

        image_user_photo = findViewById(R.id.image_user_photo_sign_in);
        image_user_photo.setDrawingCacheEnabled(true);
        image_user_photo.buildDrawingCache();

        input_name = findViewById(R.id.input_name_sign_in);
        input_phone = findViewById(R.id.input_phone_sign_in);
        input_mail = findViewById(R.id.input_mail_sign_in);
        input_address = findViewById(R.id.input_address_sign_in);
        input_bio = findViewById(R.id.input_bio_sign_in);
    }

    private void feedViews(Customer customer) {
        if (customer != null) {
            if (!image_changed && customer.getImage_path() != "") {
                Glide.with(image_user_photo.getContext()).load(customer.getImage_path())
                        .into(image_user_photo);
            } else {
                Glide.with(image_user_photo.getContext()).clear(image_user_photo);
            }
            input_name.setText(customer.getName());
            input_phone.setText(customer.getPhone());
            input_mail.setText(customer.getMail());
            input_address.setText(customer.getAddress());
            if (customer.getBio() != "")
                input_bio.setText(customer.getBio());
        } else {
            input_name.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
            input_mail.setText(mFirebaseAuth.getCurrentUser().getEmail());
        }
    }

    private void uploadImage() {
        Bitmap bitmap = ((BitmapDrawable) image_user_photo.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mFirebaseStorageReference
                .child("profile_picture.jpg")
                .putBytes(data);

        uploadTask.addOnFailureListener((@NonNull Exception exception) -> {
            Log.e("FIREBASE_LOG", "Picture Upload Fail - EditProfileActivity");
            Toast.makeText(getApplicationContext(),
                    "Picture Upload Fail - EditProfileActivity",
                    Toast.LENGTH_LONG).show();
        }).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
            mFirebaseStorageReference.child("profile_picture.jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener((Uri uri) -> {
                        Uri downUri = uri;
                        Log.v("FIREBASE_LOG", "Picture Upload Success - EditProfileActivity");
                        mCustomerDatabaseReference.child(mFirebaseAuth.getUid())
                                .child("image_path").setValue(downUri.toString());
                    });
        });
    }

    private int saveProfile() {
        String id = mFirebaseAuth.getUid();
        String name = input_name.getText().toString();
        String phone = input_phone.getText().toString();
        String mail = input_mail.getText().toString();
        String address = input_address.getText().toString();
        String bio = input_bio.getText().toString();

        if (name.isEmpty() ||
                phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() ||
                mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches() ||
                address.isEmpty())
            return -1;

        else {
            if (image_changed) uploadImage();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("id", id);
            childUpdates.put("name", name);
            childUpdates.put("phone", phone);
            childUpdates.put("mail", mail);
            childUpdates.put("address", address);
            childUpdates.put("bio", bio);

            mCustomerDatabaseReference.child(id)
                    .updateChildren(childUpdates);

            if (mFirebaseAuth.getCurrentUser().getDisplayName() != name ||
                    mFirebaseAuth.getCurrentUser().getEmail() != mail) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                mFirebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                mFirebaseAuth.getCurrentUser().updateEmail(mail);
            }

            return 1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
            Bitmap bitmapUserPhoto = null;

            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) bitmapUserPhoto = (Bitmap) b.get("data");
            } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
                try {
                    Uri targetUri = data.getData();
                    if (targetUri != null)
                        bitmapUserPhoto = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(targetUri));

                } catch (FileNotFoundException e) {
                    bitmapUserPhoto = null;
                    Toast.makeText(getApplicationContext(),
                            "FileNotFoundException: Error in setting image!",
                            Toast.LENGTH_LONG).show();
                }
            }

            if (bitmapUserPhoto != null) {
                image_changed = true;
                image_user_photo.setImageBitmap(bitmapUserPhoto);
                image_user_photo.setPadding(8, 8, 8, 8);
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                newAddress = place.getAddress();
                input_address.setText(newAddress);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(this, "Error in retrieving the address :(", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Address has not been selected.", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        locateViews();

        showBackArrowOnToolbar();

        image_user_photo.setOnClickListener(v -> startPickPictureDialog());

        addOnFocusChangeListener(input_name);
        addOnFocusChangeListener(input_phone);
        addOnFocusChangeListener(input_address);
        addOnFocusChangeListener(input_mail);
        addOnFocusChangeListener(input_bio);

        Places.initialize(getApplicationContext(), "AIzaSyB7Tku5m9p0LVYU8k8-G7RB0DQoDXjvdSE");
        input_address.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCustomerDatabaseReference = mFirebaseDatabase.getReference().child("customers");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child(mFirebaseAuth.getUid())
                .child("images");

        input_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 10) {
                    String[] matchingAddresses = searchPossibleMatchingAddresses(s.toString());
                    if (matchingAddresses != null) {
                        ArrayAdapter<String> addressesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, matchingAddresses);
                        input_address.setAdapter(addressesAdapter);
                    }
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        detachValueEventListener(mFirebaseAuth.getUid());
        Log.v("FIREBASE_LOG", "EventListener removed onPause - EditProfileActivity");

    }

    @Override
    public void onResume() {
        super.onResume();
        attachValueEventListener(mFirebaseAuth.getUid());
        Log.v("FIREBASE_LOG", "EventListener added onResume - EditProfileActivity");

    }

    private void attachValueEventListener(String userId) {
        if (userId == null) throw new IllegalArgumentException();

        if (mEditEventListener == null) {
            mEditEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null && newAddress != null)
                        customer.setAddress(newAddress);
                    feedViews(customer);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),
                            "Unable to retrieve customer's information", Toast.LENGTH_LONG).show();
                }
            };
            mCustomerDatabaseReference.child(userId)
                    .addListenerForSingleValueEvent(mEditEventListener);
        }
    }

    private void detachValueEventListener(String userId) {
        if (mEditEventListener != null && userId != null) {
            mCustomerDatabaseReference.child(userId).removeEventListener(mEditEventListener);
            mEditEventListener = null;
        }
    }


    private void showBackArrowOnToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_save) {
            int result = saveProfile();
            if (result == 1) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.settings_changed), Toast.LENGTH_LONG).show();
                // startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                finish();
            } else if (result == -1)
                Toast.makeText(getApplicationContext(),
                        getString(R.string.fill_required_fields), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void startPickPictureDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setMessage(R.string.pick_picture_message);

        myAlertDialog.setPositiveButton(R.string.gallery,
                (dialog, which) -> {
                    Intent picFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    try {
                        startActivityForResult(picFromGalleryIntent, GALLERY_REQUEST);
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.gallery_not_found, Toast.LENGTH_LONG).show();
                    }
                }
        );

        myAlertDialog.setNegativeButton(R.string.camera,
                (dialog, which) -> {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        } else {
                            throw new android.content.ActivityNotFoundException();
                        }
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.camera_not_found,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        myAlertDialog.show();
    }

    @Override
    public void onBackPressed() {
        confirmOnBackPressed();
    }

    // TODO: update dataChange using data from Firebase
    private boolean dataChanged() {
        /*String name = PrefHelper.getInstance().getString(ProfileFragment.NAME, null);
        if ((name == null && input_name.getText().toString() != null) ||
                (name != null && !name.equals(input_name.getText().toString())))
            return true;

        String phone = PrefHelper.getInstance().getString(ProfileFragment.PHONE, null);
        if ((phone == null && input_phone.getText().toString() != null) ||
                (phone != null && !phone.equals(input_phone.getText().toString())))
            return true;

        String mail = PrefHelper.getInstance().getString(ProfileFragment.MAIL, null);
        if ((mail == null && input_mail.getText().toString() != null) ||
                (mail != null && !mail.equals(input_mail.getText().toString())))
            return true;

        String address = PrefHelper.getInstance().getString(ProfileFragment.ADDRESS, null);
        if ((address == null && input_address.getText().toString() != null) ||
                (address != null && !address.equals(input_address.getText().toString())))
            return true;

        String restaurant_type = PrefHelper.getInstance().getString(ProfileFragment.RESTAURANT_TYPE, null);
        if ((restaurant_type == null && !input_restaurant_type.getSelectedItem().toString().isEmpty()) ||
                (restaurant_type != null && !restaurant_type.equals(input_restaurant_type.getSelectedItem().toString())))
            return true;

        String free_day = PrefHelper.getInstance().getString(ProfileFragment.FREE_DAY, null);
        if ((free_day == null && !input_free_day.getSelectedItem().toString().isEmpty()) ||
                (free_day != null && !free_day.equals(input_free_day.getSelectedItem().toString())))
            return true;

        String time_opening = PrefHelper.getInstance().getString(ProfileFragment.TIME_OPENING, null);
        if ((time_opening == null && !input_working_time_opening.getText().toString().equals("--:--")) || (time_opening != null && !time_opening.equals(input_working_time_opening.getText().toString())))
            return true;

        String time_closing = PrefHelper.getInstance().getString(ProfileFragment.TIME_CLOSING, null);
        if ((time_closing == null && !input_working_time_closing.getText().toString().equals("--:--")) ||
                (time_closing != null && !time_closing.equals(input_working_time_closing.getText().toString())))
            return true;

        String bio = PrefHelper.getInstance().getString(ProfileFragment.BIO, null);
        if ((bio == null && input_bio.getText().toString() != null) ||
                (bio != null && !bio.equals(input_bio.getText().toString())))
            return true;*/

        return false;
    }

    private void confirmOnBackPressed() {
        // If no modification happened, then close without showing alert dialog.
        if (dataChanged()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.warning_title)
                    .setMessage(R.string.discard_msg)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                super.onBackPressed();
                            }
                    )
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                dialog.cancel();
                            }
                    )
                    .create()
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addOnFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v, focus) -> {
            if (focus)
                ((EditText) v).setSelection(((EditText) v).getText().length(), 0);
        });
    }

    private String[] searchPossibleMatchingAddresses(String searchString) {
        ArrayList<String> strings = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(searchString, 10);
            for (int i = 0; i < addresses.size(); i++) {
                /*double latitude = addr.getLatitude();
                double longitude = addr.getLongitude();*/
                strings.add(addresses.get(i).getAddressLine(0));
            }

            String[] strings_as_array = new String[strings.size()];
            strings.toArray(strings_as_array);
            return strings_as_array;
        } catch (IOException e) {
            return null;
        }
    }

}