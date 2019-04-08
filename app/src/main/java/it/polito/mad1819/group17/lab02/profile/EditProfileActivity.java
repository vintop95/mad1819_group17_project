package it.polito.mad1819.group17.lab02.profile;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class EditProfileActivity extends AppCompatActivity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;

    private Toolbar toolbar;
    private MenuItem btn_save;
    private ImageView image_user_photo;
    private EditText input_name;
    private EditText input_phone;
    private EditText input_mail;
    private EditText input_address;
    private Spinner input_restaurant_type;
    private Spinner input_free_day;
    private TextView input_working_time_opening;
    private TextView input_working_time_closing;


    private void showBackArrowOnToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void locateViews() {
        toolbar = findViewById(R.id.toolbar_edit);



        image_user_photo = findViewById(R.id.image_user_photo);
        input_name = findViewById(R.id.input_name);
        input_phone = findViewById(R.id.input_phone);
        input_mail = findViewById(R.id.input_mail);
        input_address = findViewById(R.id.input_address);
        input_restaurant_type = findViewById(R.id.input_restaurant_type);
        input_free_day = findViewById(R.id.input_free_day);
        input_working_time_opening = findViewById(R.id.input_working_time_opening);
        input_working_time_closing = findViewById(R.id.input_working_time_closing);
    }

    private void addTimePickerOnClick(View view) {
        view.setOnClickListener(v ->
        {
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditProfileActivity.this,
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        locateViews();

        toolbar = findViewById(R.id.toolbar_edit);
        showBackArrowOnToolbar();



        image_user_photo.setOnClickListener(v -> startPickPictureDialog());

        /*btn_save.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == btn_save.getItemId()) {
                Log.d("AA", "BB");
                return true;
            }
            return false;
        });*/

        addTimePickerOnClick(input_working_time_opening);
        addTimePickerOnClick(input_working_time_closing);
        Log.d("XX", "XX");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_save) {
            Log.d("AA", "BB");
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                Toast.makeText(getApplicationContext(), "FileNotFoundException: Error in setting image!", Toast.LENGTH_LONG).show();
            }
        }

        if (bitmapUserPhoto != null) {
            PrefHelper.getInstance().putString("image_user_photo", PrefHelper.getInstance().bitMapToStringLossJpg(bitmapUserPhoto));
            image_user_photo.setImageBitmap(bitmapUserPhoto);
            image_user_photo.setPadding(8, 8, 8, 8);
        }
    }
}
