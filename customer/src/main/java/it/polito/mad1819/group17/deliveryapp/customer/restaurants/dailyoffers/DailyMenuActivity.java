package it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.OrderConfirmActivity;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.ShoppingCart;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.ShoppingItem;

import static it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantsActivity.stringToBitMap;

public class DailyMenuActivity extends AppCompatActivity {
    private String restaurant_name;
    public String restaurant_id;
    private Intent intent;
    private TextView tv;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private boolean somethingAdded;
    private ShoppingCart shoppingCart;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_menu);
        intent = getIntent();
        restaurant_name = intent.getStringExtra("name");
        restaurant_id = intent.getStringExtra("id");
        tv = (TextView) findViewById(R.id.subtitle_rn);
        tv.setText(restaurant_name);

        shoppingCart = new ShoppingCart();

        recyclerView = findViewById(R.id.restaurant_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        somethingAdded=false;
        showBackArrowOnToolbar();

        fetch();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView priceFormatted;
        public ImageView photo;
        public double priceDouble;

        int countAdded = 0;
        private ImageView addButton;
        private ImageView subtractButton;

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDesc(String desc) {
            Log.d("ffff", desc);
            this.desc.setText(desc);
        }

        // public void setPrice(String price) {
        //     this.priceFormatted.setText(price);
        // }

        public void setPrice(double price){
            this.priceDouble = price;
            this.priceFormatted.setText(CurrencyHelper.getCurrency(priceDouble));
        }

        public void setPhoto(String photo) {
            Bitmap bmp;
            if (photo != null) {
                bmp = stringToBitMap(photo);
                this.photo.setImageBitmap(bmp);
            }
        }

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.if_name);
            desc = itemView.findViewById(R.id.if_description);
            photo = itemView.findViewById(R.id.if_photo);
            priceFormatted = itemView.findViewById(R.id.if_price);
            addButton = itemView.findViewById(R.id.if_add_button);
            subtractButton = itemView.findViewById(R.id.if_subtract_button);
            addButton.setOnClickListener((View v) -> {
                Toast.makeText(v.getContext(), "1 " + title.getText()+" added", Toast.LENGTH_SHORT).show();
                shoppingCart.add(new ShoppingItem(title.getText().toString(),priceDouble,1));
                countAdded++;
                subtractButton.setVisibility(View.VISIBLE);

                updateToolbarText(shoppingCart.getCounter());
                somethingAdded=true;
            });
            subtractButton.setOnClickListener((View v) -> {
                Toast.makeText(v.getContext(), "1 " + title.getText()+" removed", Toast.LENGTH_SHORT).show();
                shoppingCart.remove(new ShoppingItem(title.getText().toString(),priceDouble,1));
                countAdded--;
                if(countAdded <= 0) subtractButton.setVisibility(View.GONE);

                updateToolbarText(shoppingCart.getCounter());
            });
        }

    }

    private void updateToolbarText(int i) {
        TextView counter = findViewById(R.id.shoppingcart_counter);
        counter.setText(Integer.toString(i));
    }


    private void fetch() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference();
        Log.d("ff", restaurant_id + "..." + restaurant_name);
        Query query = ref.child("restaurateurs").child(restaurant_id).child("daily_offers");

        FirebaseRecyclerOptions<FoodItemModel> options =
                new FirebaseRecyclerOptions.Builder<FoodItemModel>()
                        .setQuery(query, new SnapshotParser<FoodItemModel>() {
                            @NonNull
                            @Override
                            public FoodItemModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.d("ff", snapshot.getKey());
                                return new FoodItemModel(
                                        (String) snapshot.getKey(),
                                        snapshot.child("name").getValue(String.class),
                                        snapshot.child("description").getValue(String.class),
                                        snapshot.child("photo").getValue(String.class),
                                        snapshot.child("price").getValue(Double.class).toString()
                                );
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<FoodItemModel, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, FoodItemModel model) {
                Log.d("fff", model.getDescription() + "," + model.getTitle() + "," + model.getPrice() + "," + model.getId());
                holder.setDesc(model.getDescription());
                holder.setPhoto(model.getPhoto());
                holder.setTitle(model.getTitle());
                holder.setPrice(Double.valueOf(model.getPrice()));
            }

        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_daily_menu,menu);
        final Menu m = menu;
        final MenuItem menuItem = m.findItem(R.id.shoppingcart_itemmenu);
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyMenuActivity.this, OrderConfirmActivity.class);
                HashMap<String,Integer> itemsMap = shoppingCart.getItemsMap();
                Log.d("pedro",Integer.toString(itemsMap.size()));
                intent.putExtra("restaurant_id", restaurant_id);
                intent.putExtra("itemsMap",itemsMap);
                intent.putExtra("items_quantity",shoppingCart.getCounter());
                intent.putExtra("items_tot_price",shoppingCart.getTotal_price());
                startActivity(intent);
            }
        });



        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if(somethingAdded) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sure to quit? All shopping cart items will be lost");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DailyMenuActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else super.onBackPressed();
    }
}