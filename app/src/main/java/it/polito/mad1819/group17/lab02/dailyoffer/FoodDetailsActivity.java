package it.polito.mad1819.group17.lab02.dailyoffer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.FormAdapter;
import it.polito.mad1819.group17.lab02.utils.FormAdapter.ListItem;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class FoodDetailsActivity extends AppCompatActivity {

    public final static int STATE_CHANGED = 1;
    public final static int STATE_NOT_CHANGED = 0;

    private final static int LABEL_FOOD_NUMBER = R.string.app_name;
    private final static int LABEL_FOOD_NAME = R.string.app_name;
    private final static int LABEL_FOOD_DESCRIPTION = R.string.app_name;
    private final static int LABEL_FOOD_PRICE = R.string.app_name;
    private final static int LABEL_FOOD_AVAILABLE_QTY = R.string.app_name;

    private RecyclerView foodForm;
    private ArrayList<ListItem> fields = new ArrayList<>();
    private FormAdapter formAdapter;
    private FoodModel foodLoaded;
    private int foodState = STATE_NOT_CHANGED;

    // TODO: change
    private ImageButton img_food_photo;
    private FloatingActionButton btn_save;

    private void locateViews() {
        foodForm = findViewById(R.id.food_form);
        foodForm.setHasFixedSize(true);
        foodForm.setLayoutManager(new LinearLayoutManager(this));

        img_food_photo = findViewById(R.id.img_food_photo);
        btn_save = findViewById(R.id.btn_save);
    }

    private void feedViews(FoodModel selFood) {
        if(fields.isEmpty()) {
            fields.add(0, new ListItem(LABEL_FOOD_NUMBER, "" + selFood.getIdLong()));
            fields.add(1, new ListItem(LABEL_FOOD_NAME, selFood.getName()));
            fields.add(2, new ListItem(LABEL_FOOD_DESCRIPTION, selFood.getDescription()));
            fields.add(new ListItem(LABEL_FOOD_PRICE, Double.toString(selFood.getPriceDouble())));
            fields.add(new ListItem(LABEL_FOOD_AVAILABLE_QTY, Integer.toString(selFood.getAvailableQty())));
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
            foodLoaded = (FoodModel) b.getBundle("args").getSerializable("food");
        }

        if (foodLoaded != null){
            //if it's a modify operation
            feedViews(foodLoaded);
        }

        formAdapter = new FormAdapter(this, fields);
        foodForm.setAdapter(formAdapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getSimpleName(), "Saving...");

                Intent intent = new Intent();

                if(getFoodState() == STATE_CHANGED){
                    intent.putExtra("food", foodLoaded);
                }

                setResult(getFoodState(), intent);

                finish();
            }
        });
    }

    private int getFoodState() {
        return foodState;
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

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //final Order selectedOrder = (Order) getIntent().getExtras().getBundle("bundle_selected_order").getSerializable("selected_order");

        final ArrayList<Order> orders = (ArrayList<Order>) getIntent()
                .getExtras()
                .getBundle("args")
                .getSerializable("orders");

        final int position = getIntent().getExtras().getBundle("args").getInt("position");


        locateViews();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orders.get(position).moveToNextState()) {
                    Intent intent = new Intent();

                    ArrayList<Order> updatedOrders = new ArrayList<Order>();
                    updatedOrders.addAll(orders);

                    Collections.sort(updatedOrders, new Comparator<Order>() {
                        @Override
                        public int compare(Order o1, Order o2) {
                            if (o1.getCurrentState() == Order.STATE3)
                                return 1;
                            else
                                return -o1.getDelivery_timestamp().compareTo(o2.getDelivery_timestamp());
                        }
                    });
                    Log.d("AAA", "XX " + updatedOrders.get(0).getNumber() + " " + updatedOrders.get(1).getNumber());

                    intent.putExtra("orders", updatedOrders);
                    intent.putExtra("position", position);


                    //Log.d("AAA", "" + orders.get(0).getNumber());

                    setResult(STATE_CHANGED, intent);
                    txt_state_history.setText(orders.get(position).getStateHistoryToString());

                } else
                    setResult(STATE_NOT_CHANGED);

                //txt_state_history.setText(orders.get(position).getStateHistoryToString());
            }
        });

        feedViews(orders.get(position));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
     */