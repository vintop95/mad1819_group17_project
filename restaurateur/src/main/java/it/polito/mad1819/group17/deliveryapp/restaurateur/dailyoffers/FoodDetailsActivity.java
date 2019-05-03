package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModel;
import it.polito.mad1819.group17.deliveryapp.common.utils.PrefHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.FormAdapter;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.FormAdapter.ListItem;

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
//    private int pos = -1;

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
//            mFields.add(0, new ListItem(LABEL_FOOD_NUMBER, "" + selFood.pos));
            mFields.add(new ListItem(LABEL_FOOD_NAME, selFood.name));
            mFields.add(new ListItem(LABEL_FOOD_DESCRIPTION, selFood.description));
            mFields.add(new ListItem(LABEL_FOOD_PRICE, Double.toString(selFood.price)));
            mFields.add(new ListItem(LABEL_FOOD_AVAILABLE_QTY, Integer.toString(selFood.availableQty)));
        }

        // Load image
        if (!TextUtils.isEmpty(selFood.image_path)) {
            Glide.with(img_food_photo.getContext())
                    .load(selFood.image_path)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ProfileFragment", "Image load failed");
                            return false; // leave false
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.v("ProfileFragment", "Image load OK");
                            return false; // leave false
                        }
                    }).into(img_food_photo);
        }
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
//                pos = b2.getInt("pos");
            }
        }

        if (mFoodLoaded == null) {
            mFoodLoaded = new FoodModel();
        }

//        mFoodLoaded.pos = pos;
        feedViews(mFoodLoaded);

        mFormAdapter = new FormAdapter(this, mFields, mFoodState);
        mFoodForm.setAdapter(mFormAdapter);
    }

    private void uploadImage(Context context, ImageView img_food_photo, FoodModel foodLoaded){
        if(img_food_photo.getDrawable() == null) return;

        Bitmap bitmap = ((BitmapDrawable) img_food_photo.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference firebaseStorageReference = FirebaseStorage.getInstance()
                .getReference().child("daily_offers").child(foodLoaded.id);

        UploadTask uploadTask = firebaseStorageReference
                .child("food_photo.jpg")
                .putBytes(data);

        uploadTask.addOnFailureListener((@NonNull Exception exception) -> {
            Log.e("FIREBASE_LOG", "Picture Upload Fail - EditProfileActivity");
            Toast.makeText(context,
                    "Picture Upload Fail - EditProfileActivity",
                    Toast.LENGTH_LONG).show();
        }).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
            firebaseStorageReference.child("food_photo.jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener((Uri uri) -> {
                        Uri downUri = uri;
                        Log.v("FIREBASE_LOG", "Picture Upload Success - EditProfileActivity");
                        FirebaseDatabase.getInstance().getReference()
                                .child("restaurateurs")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("daily_offers")
                                .child(foodLoaded.id)
                                .child("image_path").setValue(downUri.toString());
                    });
        });
    }

    private FoodModel getUpdatedFood() {
        FoodModel food = new FoodModel();
//        food.pos = pos;
        food.id = mFoodLoaded.id;

        for(ListItem field: mFields){
            switch(field.fieldNameRes){
                case LABEL_FOOD_NAME:
                    food.name = field.fieldValue;
                    break;
                case LABEL_FOOD_DESCRIPTION:
                    food.description = field.fieldValue;
                    break;
                case LABEL_FOOD_PRICE:
                    food.price = Double.valueOf(field.fieldValue);
                    break;
                case LABEL_FOOD_AVAILABLE_QTY:
                    food.availableQty = Integer.valueOf(field.fieldValue);
                    break;
            }
        }

        // GET PHOTO FROM IMAGEVIEW
        if(TextUtils.isEmpty(food.id)){

        }else{
            uploadImage(getApplicationContext(), img_food_photo, mFoodLoaded);
        }

        return food;
    }

    private int getFoodState() {
        return mFoodState.get();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d("DETAILS","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d("DETAILS","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d("DETAILS","onDestroy");
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