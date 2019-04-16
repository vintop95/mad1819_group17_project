package it.polito.mad1819.group17.deliveryapp.restaurateur;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.EditProfileActivity;
import it.polito.mad1819.group17.deliveryapp.restaurateur.profile.ProfileFragment;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;
import it.polito.mad1819.group17.restaurateur.R;

public class SignUpActivity extends AppCompatActivity {
    private ImageView image_user_photo;
    private EditText input_name;
    private EditText input_phone;
    private EditText input_mail;
    private EditText input_password;
    private EditText input_address;
    private Spinner input_restaurant_type;
    private Spinner input_free_day;
    private TextView input_working_time_opening;
    private TextView input_working_time_closing;
    private EditText input_bio;
    private Button btn_sign_up;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String photoBase64;


    private void locateViews() {
        image_user_photo = findViewById(R.id.image_user_photo_sign_up);
        input_name = findViewById(R.id.input_name_sign_up);
        input_phone = findViewById(R.id.input_phone_sign_up);
        input_mail = findViewById(R.id.input_mail_sign_up);
        input_password = findViewById(R.id.input_password_sign_up);
        input_address = findViewById(R.id.input_address_sign_up);
        input_restaurant_type = findViewById(R.id.input_restaurant_type_sign_up);
        input_free_day = findViewById(R.id.input_free_day_sign_up);
        input_working_time_opening = findViewById(R.id.input_working_time_opening_sign_up);
        input_working_time_closing = findViewById(R.id.input_working_time_closing_sign_up);
        input_bio = findViewById(R.id.input_bio_sign_up);
        btn_sign_up = findViewById(R.id.btn_sign_up);
    }

    private void addTimePickerOnClick(View view) {
        view.setOnClickListener(v ->
        {
            TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String timestamp = "";

                        if (hourOfDay < 10)
                            timestamp += "0";
                        timestamp += hourOfDay + ":";

                        if (minute < 10)
                            timestamp += "0";
                        timestamp += minute;

                        ((TextView) view).setText(timestamp);
                    },
                    0,
                    0,
                    false);
            timePickerDialog.show();
        });
    }

    private void startPickPictureDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setMessage(R.string.pick_picture_message);

        myAlertDialog.setPositiveButton(R.string.gallery,
                (dialog, which) -> {
                    Intent picFromGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    try {
                        startActivityForResult(picFromGalleryIntent, EditProfileActivity.GALLERY_REQUEST);
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
                            startActivityForResult(takePictureIntent, EditProfileActivity.CAMERA_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmapUserPhoto = null;

        if (requestCode == EditProfileActivity.CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            if (b != null) bitmapUserPhoto = (Bitmap) b.get("data");
        } else if (requestCode == EditProfileActivity.GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                Uri targetUri = data.getData();
                if (targetUri != null)
                    bitmapUserPhoto = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(targetUri));

            } catch (FileNotFoundException e) {
                bitmapUserPhoto = null;
                Toast.makeText(getApplicationContext(), "FileNotFoundException: Error in setting image!", Toast.LENGTH_LONG).show();
            }
        }

        if (bitmapUserPhoto != null) {
            photoBase64 = PrefHelper.bitMapToStringLossless(bitmapUserPhoto);
            image_user_photo.setImageBitmap(bitmapUserPhoto);
            image_user_photo.setPadding(8, 8, 8, 8);
        }
    }

    private Restaurateur get_restaurateur_from_form() {
        if (TextUtils.isEmpty(photoBase64))
            photoBase64 = "";

        if (TextUtils.isEmpty(input_name.getText().toString())) {
            input_name.setError("Required");
            return null;
        } else
            input_name.setError(null);

        if (TextUtils.isEmpty(input_phone.getText().toString())) {
            input_phone.setError("Required");
            return null;
        } else if (!Patterns.PHONE.matcher(input_phone.getText().toString()).matches()) {
            input_phone.setError("Wrong format");
            return null;
        } else
            input_phone.setError(null);

        if (TextUtils.isEmpty(input_mail.getText().toString())) {
            input_mail.setError("Required");
            return null;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input_mail.getText().toString()).matches()) {
            input_mail.setError("Wrong format");
            return null;
        } else
            input_mail.setError(null);

        if (TextUtils.isEmpty(input_restaurant_type.getSelectedItem().toString()))
            return null;

        if (TextUtils.isEmpty(input_free_day.getSelectedItem().toString()))
            return null;

        if (input_working_time_opening.getText().toString().equals("--:--") || input_working_time_closing.getText().toString().equals("--:--"))
            return null;

        return new Restaurateur(photoBase64,
                input_name.getText().toString(),
                input_phone.getText().toString(),
                input_mail.getText().toString(),
                input_address.getText().toString(),
                input_restaurant_type.getSelectedItem().toString(),
                input_free_day.getSelectedItem().toString(),
                input_working_time_opening.getText().toString(),
                input_working_time_closing.getText().toString(),
                input_bio.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(SignUpActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        locateViews();

        addTimePickerOnClick(input_working_time_opening);
        addTimePickerOnClick(input_working_time_closing);

        image_user_photo.setOnClickListener(v -> startPickPictureDialog());

        btn_sign_up.setOnClickListener(v -> {
            Restaurateur restaurateur = get_restaurateur_from_form();
            if (restaurateur != null) {
                mAuth.createUserWithEmailAndPassword(input_mail.getText().toString(), input_password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // store all data of the restaurateur
                                    mDatabase.child("restaurateurs").child(task.getResult().getUser().getUid()).setValue(restaurateur);
                                    Toast.makeText(SignUpActivity.this, "User created :)", Toast.LENGTH_SHORT).show();

                                    // go to MainActivity of the just created restauratuer
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.putExtra("restaurateur", restaurateur);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                    finish();
                                } else
                                    Toast.makeText(SignUpActivity.this, "Unable to create the new user :(", Toast.LENGTH_SHORT).show();

                            }
                        });
            } else
                Toast.makeText(SignUpActivity.this, "Missing or wrongly formatted input fields :(", Toast.LENGTH_SHORT).show();
        });
    }
}
