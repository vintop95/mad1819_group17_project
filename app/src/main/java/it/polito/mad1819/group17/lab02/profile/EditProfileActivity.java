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
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class EditProfileActivity extends AppCompatActivity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;


    private Toolbar toolbar;
    private ImageView image_user_photo;
    private EditText input_name;
    private EditText input_phone;
    private EditText input_mail;
    private EditText input_address;
    private Spinner input_restaurant_type;
    private Spinner input_free_day;
    private TextView input_working_time_opening;
    private TextView input_working_time_closing;
    private TextView input_bio;


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
        input_free_day = findViewById(R.id.txt_free_day);
        input_working_time_opening = findViewById(R.id.input_working_time_opening);
        input_working_time_closing = findViewById(R.id.input_working_time_closing);
        input_bio = findViewById(R.id.input_bio);
    }

    private void feedViews() {
        String stringUserPhoto = PrefHelper.getInstance().getString(ProfileFragment.PHOTO, null);
        if (stringUserPhoto != null) {
            image_user_photo.setImageBitmap(PrefHelper.stringToBitMap(stringUserPhoto));
            image_user_photo.setPadding(8, 8, 8, 8);
        }

        String name = PrefHelper.getInstance().getString(ProfileFragment.NAME, null);
        if (name != null)
            input_name.setText(name);

        String phone = PrefHelper.getInstance().getString(ProfileFragment.PHONE, null);
        if (phone != null)
            input_phone.setText(phone);

        String mail = PrefHelper.getInstance().getString(ProfileFragment.MAIL, null);
        if (mail != null)
            input_mail.setText(mail);

        String address = PrefHelper.getInstance().getString(ProfileFragment.ADDRESS, null);
        if (address != null)
            input_address.setText(address);

        String restaurant_type = PrefHelper.getInstance().getString(ProfileFragment.RESTAURANT_TYPE, null);
        if (restaurant_type != null)
            for (int i = 0; i < getResources().getStringArray(R.array.restaurant_types).length; i++)
                if (getResources().getStringArray(R.array.restaurant_types)[i].equals(restaurant_type)) {
                    input_restaurant_type.setSelection(i);
                    break;
                }
        String free_day = PrefHelper.getInstance().getString(ProfileFragment.FREE_DAY, null);
        if (free_day != null)
            for (int i = 0; i < getResources().getStringArray(R.array.days_of_week).length; i++)
                if (getResources().getStringArray(R.array.days_of_week)[i].equals(free_day)) {
                    input_free_day.setSelection(i);
                    break;
                }

        String time_opening = PrefHelper.getInstance().getString(ProfileFragment.TIME_OPENING, null);
        if (time_opening != null)
            input_working_time_opening.setText(time_opening);

        String time_closing = PrefHelper.getInstance().getString(ProfileFragment.TIME_CLOSING, null);
        if (time_closing != null)
            input_working_time_closing.setText(time_closing);

        String bio = PrefHelper.getInstance().getString(ProfileFragment.BIO, null);
        if (bio != null)
            input_bio.setText(bio);
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

    private int saveViewsInPreferences() {
        String name = input_name.getText().toString();
        String phone = input_phone.getText().toString();
        String mail = input_mail.getText().toString();
        String address = input_address.getText().toString();
        String restaurant_type = input_restaurant_type.getSelectedItem().toString();
        String free_day = input_free_day.getSelectedItem().toString();
        String time_opening = input_working_time_opening.getText().toString();
        String time_closing = input_working_time_closing.getText().toString();
        Date date_timeOpening = null;
        Date date_timeClosing = null;
        String bio = input_bio.getText().toString();


        if (name.isEmpty() ||
                phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() ||
                mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches() ||
                address.isEmpty() ||
                restaurant_type.isEmpty() ||
                free_day.isEmpty() ||
                time_opening.equals("--:--") || time_closing.equals("--:--"))
            return -1;

        else if (!time_opening.equals("--:--") && !time_closing.equals("--:--")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            try {
                date_timeOpening = simpleDateFormat.parse(time_opening);
                date_timeClosing = simpleDateFormat.parse(time_closing);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date_timeOpening.compareTo(date_timeClosing) >= 0)
                return 0;
            else {
                PrefHelper.getInstance().putString(ProfileFragment.NAME, name);
                PrefHelper.getInstance().putString(ProfileFragment.PHONE, phone);
                PrefHelper.getInstance().putString(ProfileFragment.MAIL, mail);
                PrefHelper.getInstance().putString(ProfileFragment.ADDRESS, address);
                PrefHelper.getInstance().putString(ProfileFragment.RESTAURANT_TYPE, restaurant_type);
                PrefHelper.getInstance().putString(ProfileFragment.FREE_DAY, free_day);
                PrefHelper.getInstance().putString(ProfileFragment.TIME_OPENING, time_opening);
                PrefHelper.getInstance().putString(ProfileFragment.TIME_CLOSING, time_closing);
                PrefHelper.getInstance().putString(ProfileFragment.BIO, bio);
                Log.d("XX", "XXXXXXX");
                return 1;
            }

        } else
            return -1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        locateViews();

        toolbar = findViewById(R.id.toolbar_edit);
        showBackArrowOnToolbar();


        image_user_photo.setOnClickListener(v -> startPickPictureDialog());

        addTimePickerOnClick(input_working_time_opening);
        addTimePickerOnClick(input_working_time_closing);
    }

    @Override
    protected void onStart() {
        super.onStart();
        feedViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_save) {
            int result = saveViewsInPreferences();
            if (result == 1) {
                Toast.makeText(getApplicationContext(), getString(R.string.settings_changed), Toast.LENGTH_LONG).show();
                finish();
            } else if (result == -1)
                Toast.makeText(getApplicationContext(), getString(R.string.fill_required_fields), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), getString(R.string.wrong_times), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        confirmOnBackPressed();
    }

    private boolean dataChanged() {
        String name = PrefHelper.getInstance().getString(ProfileFragment.NAME, null);
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
            return true;

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
            PrefHelper.getInstance().putString(ProfileFragment.PHOTO, PrefHelper.getInstance().bitMapToStringLossJpg(bitmapUserPhoto));
            image_user_photo.setImageBitmap(bitmapUserPhoto);
            image_user_photo.setPadding(8, 8, 8, 8);
        }
    }
}
