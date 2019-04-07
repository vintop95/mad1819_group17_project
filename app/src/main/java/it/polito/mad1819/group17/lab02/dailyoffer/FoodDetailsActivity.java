package it.polito.mad1819.group17.lab02.dailyoffer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.FormAdapter;
import it.polito.mad1819.group17.lab02.utils.FormAdapter.ListItem;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class FoodDetailsActivity extends AppCompatActivity {

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

    // TODO: change
    private ImageButton img_food_photo;
    private FloatingActionButton btn_save;

    private void locateViews() {
        mFoodForm = findViewById(R.id.food_form);
        mFoodForm.setHasFixedSize(true);
        mFoodForm.setLayoutManager(new LinearLayoutManager(this));

        img_food_photo = findViewById(R.id.img_food_photo);
        btn_save = findViewById(R.id.btn_save);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    private FoodModel getUpdatedFood() {
        FoodModel food = new FoodModel();
        food.setId(pos);

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
}