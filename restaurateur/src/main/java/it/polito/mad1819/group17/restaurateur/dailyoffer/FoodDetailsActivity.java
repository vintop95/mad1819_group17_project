package it.polito.mad1819.group17.restaurateur.dailyoffer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.restaurateur.R;
import it.polito.mad1819.group17.restaurateur.utils.FormAdapter;
import it.polito.mad1819.group17.restaurateur.utils.FormAdapter.ListItem;
import it.polito.mad1819.group17.restaurateur.utils.PrefHelper;

public class FoodDetailsActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 0;
    public static final int GALLERY_REQUEST = 1;

    public final static int STATE_CHANGED = 1;
    public final static int STATE_NOT_CHANGED = 0;

    public final static int LABEL_FOOD_NUMBER = R.string.label_food_number;
    public final static int LABEL_FOOD_NAME = R.string.label_food_name;
    public final static int LABEL_FOOD_DESCRIPTION = R.string.label_food_description;
    public final static int LABEL_FOOD_PRICE = R.string.label_food_price;
    public final static int LABEL_FOOD_AVAILABLE_QTY = R.string.label_food_available_qty;

    private RecyclerView mFoodForm;
    private ArrayList<ListItem> mFields = new ArrayList<>();
    private FormAdapter mFormAdapter;
    private FoodModel mFoodLoaded;
    private AtomicInteger mFoodState = new AtomicInteger(STATE_NOT_CHANGED);
    private int pos = -1;

    private ImageView img_food_photo;
    private FloatingActionButton btn_change_img;
    private FloatingActionButton btn_save;

    private void locateViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFoodForm = findViewById(R.id.food_form);
        mFoodForm.setHasFixedSize(true);
        mFoodForm.setLayoutManager(new LinearLayoutManager(this));

        img_food_photo = findViewById(R.id.img_food_photo);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getSimpleName(), "Saving...");

                Intent intent = new Intent();

                intent.putExtra("food", getUpdatedFood());

                setResult(getFoodState(), intent);

                FoodDetailsActivity.this.finish();
            }
        });

        btn_change_img = findViewById(R.id.btn_change_img);
        btn_change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickPictureDialog();
            }
        });
    }

    private void feedViews(FoodModel selFood) {
        if(mFields.isEmpty()) {
            mFields.add(0, new ListItem(LABEL_FOOD_NUMBER, "" + selFood.getIdLong()));
            mFields.add(1, new ListItem(LABEL_FOOD_NAME, selFood.getName()));
            mFields.add(2, new ListItem(LABEL_FOOD_DESCRIPTION, selFood.getDescription()));
            mFields.add(3, new ListItem(LABEL_FOOD_PRICE, Double.toString(selFood.getPriceDouble())));
            mFields.add(4, new ListItem(LABEL_FOOD_AVAILABLE_QTY, selFood.getAvailableQtyString()));
        }

        String photoString = selFood.getPhoto();
        Bitmap photoBmp = PrefHelper.stringToBitMap(photoString);
        img_food_photo.setImageBitmap(photoBmp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        locateViews();

//        Snackbar.make(, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        Bundle b = getIntent().getExtras();
        if(b != null){
            Bundle b2 = b.getBundle("args");
            if(b2 != null){
                mFoodLoaded = (FoodModel) b2.getSerializable("food");
                pos = b2.getInt("pos");
            }
        }

        if (mFoodLoaded == null) {
            mFoodLoaded = new FoodModel();
        }

        mFoodLoaded.setId(pos);
        feedViews(mFoodLoaded);

        mFormAdapter = new FormAdapter(this, mFields, mFoodState);
        mFoodForm.setAdapter(mFormAdapter);
    }

    private FoodModel getUpdatedFood() {
        FoodModel food = new FoodModel();
        food.setId(pos);

        // GET PHOTO FROM IMAGEVIEW
        Bitmap bmp = ((BitmapDrawable)img_food_photo.getDrawable()).getBitmap();
        if(bmp != null){
            String imgStr = PrefHelper.bitMapToStringLossless(bmp);
            food.setPhoto(imgStr);
        }

        for(ListItem field: mFields){
            switch(field.fieldNameRes){
                case LABEL_FOOD_NAME:
                    food.setName(field.fieldValue);
                    break;
                case LABEL_FOOD_DESCRIPTION:
                    food.setDescription(field.fieldValue);
                    break;
                case LABEL_FOOD_PRICE:
                    food.setPrice(Double.valueOf(field.fieldValue));
                    break;
                case LABEL_FOOD_AVAILABLE_QTY:
                    food.setAvailableQty(Integer.valueOf(field.fieldValue));
                    break;
            }
        }

        return food;
    }

    private int getFoodState() {
        return mFoodState.get();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DETAILS","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("DETAILS","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DETAILS","onDestroy");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //////////////////// DIALOG FOR DISCARDING CHANGES //////////////////////////
    @Override
    public void onBackPressed() {
        confirmDiscard();
    }

    private void confirmDiscard() {
        // If no modification happened, then close without showing alert dialog.
        if (getFoodState() == STATE_CHANGED) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(R.string.warning_title);
            alertDialogBuilder.setMessage(R.string.discard_msg);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        super.onBackPressed();
                    }
            );

            alertDialogBuilder.setNegativeButton(android.R.string.no, (dialog, which) -> {
                        dialog.cancel();
                    }
            );

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }
    //////////////////////////////////////////////////////////////////////////

    //----------------------UI IMAGE MANAGEMENT--------------------------------------------
    private void startPickPictureDialog() {
        // TODO: use image chooser?
        // http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setMessage(R.string.pick_picture_message);

        myAlertDialog.setPositiveButton(R.string.gallery,
                (dialog, which) -> {
                    Intent picFromGalleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    try {
                        startActivityForResult(picFromGalleryIntent, GALLERY_REQUEST);
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(FoodDetailsActivity.this, R.string.gallery_not_found,
                                Toast.LENGTH_LONG).show();
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
                        Toast.makeText(FoodDetailsActivity.this, R.string.camera_not_found,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                Toast.makeText(FoodDetailsActivity.this,
                        "FileNotFoundException: Error in setting image!",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (bitmapUserPhoto != null) {
            img_food_photo.setImageBitmap(bitmapUserPhoto);
        }
    }
}