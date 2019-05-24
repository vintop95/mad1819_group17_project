package it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Comparator;

import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModel;
import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.MadFirebaseRecyclerAdapter;
import it.polito.mad1819.group17.deliveryapp.common.utils.PopupHelper;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantProfileActivity;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.OrderConfirmActivity;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.ShoppingCart;

public class DailyMenuActivity extends AppCompatActivity {
    private String restaurant_id;
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_phone;
    private Intent intent;

    private TextView tv;
    private FrameLayout frameLayout;
    private ImageView btnFavorite;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private RatingBar rb_mean_rate_daily_menu;
    private Float overallRate;

    private MadFirebaseRecyclerAdapter adapter;
    private boolean somethingAdded;
    private ShoppingCart shoppingCart;
    private boolean isFavorite;
    private String free_day;
    private String opening_time;
    private String closing_time;

    public static int RC_ORDER_CONFIRM = 0;
    public static int RC_RESTAURANT_DETAILS = 1;

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

    private void openRestaurantProfile() {
        Intent intent = new Intent(DailyMenuActivity.this, RestaurantProfileActivity.class);
        intent.putExtra("restaurant_id", restaurant_id);
        startActivityForResult(intent, RC_RESTAURANT_DETAILS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_menu);

        intent = getIntent();

        restaurant_id = intent.getStringExtra("id");
        restaurant_name = intent.getStringExtra("name");
        restaurant_address = intent.getStringExtra("address");
        restaurant_phone = intent.getStringExtra("phone");
        isFavorite = intent.getBooleanExtra("isFavorite", false);
        free_day = intent.getStringExtra("free_day");
        opening_time = intent.getStringExtra("opening_time");
        closing_time = intent.getStringExtra("closing_time");


        rb_mean_rate_daily_menu = findViewById(R.id.rb_mean_rate_daily_menu);
        overallRate = intent.getFloatExtra("overallRate", new Float(0));
        if (overallRate > 0) {
            rb_mean_rate_daily_menu.setRating(overallRate);
            rb_mean_rate_daily_menu.setVisibility(View.VISIBLE);
        } else
            rb_mean_rate_daily_menu.setVisibility(View.GONE);


        frameLayout = findViewById(R.id.frame_layout_restaurant_info);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestaurantProfile();
            }
        });

        // Favorite mgmt
        btnFavorite = findViewById(R.id.btn_favorite);
        if (isFavorite) {
            btnFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFromFavorites();
                } else {
                    addToFavorites();
                }
            }
        });

        tv = findViewById(R.id.subtitle_rn);
        tv.setText(restaurant_name);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestaurantProfile();
            }
        });

        shoppingCart = new ShoppingCart();

        recyclerView = findViewById(R.id.restaurant_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        somethingAdded = false;
        showBackArrowOnToolbar();

        fetch(new Comparator<FoodModel>() {
            @Override
            public int compare(FoodModel lhs, FoodModel rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
    }

    private void addToFavorites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoritesRef = rootRef
                .child("restaurateurs")
                .child(restaurant_id)
                .child("favorites")
                .child(userId);
        favoritesRef.setValue("true");

        isFavorite = true;
        btnFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        PopupHelper.showToast(this, getString(R.string.added_to_favorites));
    }

    private void removeFromFavorites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoritesRef = rootRef
                .child("restaurateurs")
                .child(restaurant_id)
                .child("favorites")
                .child(userId);
        favoritesRef.removeValue();

        isFavorite = false;
        btnFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        PopupHelper.showToast(this, getString(R.string.removed_from_favorites));
    }

    private void resetShoppingCart() {
        shoppingCart.clear();
        updateToolbarText(shoppingCart.getCounter());
        somethingAdded = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView addButton;
        private ImageView subtractButton;
        public TextView title;
        public TextView desc;
        public TextView priceFormatted;
        public ImageView photo;
        public TextView orderedQty;
        public TextView availableQty;
        public RatingBar rb_mean_rate;

        public String id;
        public double priceDouble;
        int countAdded = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.if_name);
            desc = itemView.findViewById(R.id.if_description);
            photo = itemView.findViewById(R.id.if_photo);
            priceFormatted = itemView.findViewById(R.id.if_price);
            addButton = itemView.findViewById(R.id.if_add_button);
            subtractButton = itemView.findViewById(R.id.if_subtract_button);
            availableQty = itemView.findViewById(R.id.if_available_qty);
            orderedQty = itemView.findViewById(R.id.if_ordered_qty);
            rb_mean_rate = itemView.findViewById(R.id.rb_mean_rate);
            setOrderedQty(countAdded);
        }

        public void setData(FoodModel model) {
            this.id = model.id;
            setDesc(model.description);
            setPhoto(model.image_path);
            setTitle(model.name);
            setPrice(model.price);
            setAvailableQty(model.availableQty);

            if (model.number_of_rates == null || model.total_rate == null)
                rb_mean_rate.setVisibility(View.GONE);
            else
                rb_mean_rate.setRating(model.total_rate / model.number_of_rates);


            while (countAdded > model.availableQty && removeItemFromCart()) ;
            if (countAdded < getAvailableQty()) addButton.setVisibility(View.VISIBLE);
            if (countAdded > 0) subtractButton.setVisibility(View.VISIBLE);

            addButton.setOnClickListener((View v) -> {
                if (addItemInCart()) {
//                    Toast.makeText(v.getContext(), "1 " + title.getText() + " "
//                            + getApplicationContext().getString(R.string.added), Toast.LENGTH_SHORT).show();
                }
            });

            subtractButton.setOnClickListener((View v) -> {
                if (removeItemFromCart()) {
//                    Toast.makeText(v.getContext(), "1 " + title.getText() + " "
//                            + getString(R.string.removed), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // false if trying to add more than possible
        private boolean addItemInCart() {
            if (countAdded >= getAvailableQty()) {
                return false;
            }

            shoppingCart.add(new ShoppingItem(id, title.getText().toString(), priceDouble, 1));
            countAdded++;
            setOrderedQty(countAdded);
            if (countAdded > 0) subtractButton.setVisibility(View.VISIBLE);
            if (countAdded >= getAvailableQty()) addButton.setVisibility(View.INVISIBLE);

            updateToolbarText(shoppingCart.getCounter());
            somethingAdded = true;
            return true;
        }

        // false if trying to remove more than possible
        private boolean removeItemFromCart() {
            if (countAdded <= 0) {
                return false;
            }

            shoppingCart.remove(new ShoppingItem(id, title.getText().toString(), priceDouble, 1));
            countAdded--;
            setOrderedQty(countAdded);
            if (countAdded < getAvailableQty()) addButton.setVisibility(View.VISIBLE);
            if (countAdded <= 0) subtractButton.setVisibility(View.GONE);

            updateToolbarText(shoppingCart.getCounter());
            return true;
        }

        private Integer getAvailableQty() {
            return Integer.parseInt(availableQty.getText().toString());
        }

        private void setAvailableQty(Integer availableQty) {
            String avQty = "0";
            if (availableQty != null) avQty = availableQty.toString();
            this.availableQty.setText(avQty);
        }

        private void setOrderedQty(Integer orderedQty) {
            // confronta orderedQty e countadded
            String ordQty = "0";
            if (orderedQty != null) ordQty = orderedQty.toString();
            this.orderedQty.setText("(" + ordQty + ")");
        }

        private void setTitle(String title) {
            this.title.setText(title);
        }

        private void setDesc(String desc) {
            this.desc.setText(desc);
        }

        private void setPrice(double price) {
            this.priceDouble = price;
            this.priceFormatted.setText(CurrencyHelper.getCurrency(priceDouble));
        }

        private void setPhoto(String image_path) {
            if (!TextUtils.isEmpty(image_path)) {
                Glide.with(photo.getContext())
                        .load(image_path)
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
                        }).into(photo);
            } else {
                Glide.with(photo.getContext()).clear(photo);
            }
        }
    }

    private void updateToolbarText(int i) {
        TextView counter = findViewById(R.id.shoppingcart_counter);
        if (counter != null) counter.setText(Integer.toString(i));
    }

    private void fetch(Comparator comparator) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference();
        Log.d("ff", restaurant_id + "..." + restaurant_name);
        Query query = ref.child("restaurateurs").child(restaurant_id).child("daily_offers")
                .orderByChild("totalOrderedQty");

        FirebaseRecyclerOptions<FoodModel> options =
                new FirebaseRecyclerOptions.Builder<FoodModel>()
                        .setQuery(query, new SnapshotParser<FoodModel>() {
                            @NonNull
                            @Override
                            public FoodModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.d("ff", snapshot.getKey());
                                Double priceDbl = snapshot.child("price").getValue(Double.class);
                                Integer availableQtyInt = snapshot.child("availableQty").getValue(Integer.class);
                                Integer totalOrderedQty = snapshot.child("totalOrderedQty").getValue(Integer.class);
                                if (priceDbl == null) priceDbl = 0.0;
                                if (availableQtyInt == null) availableQtyInt = 0;
                                if (totalOrderedQty == null) totalOrderedQty = 0;

                                FoodModel foodModel = new FoodModel();
                                foodModel.id = (String) snapshot.getKey();
                                foodModel.name = snapshot.child("name").getValue(String.class);
                                foodModel.description = snapshot.child("description").getValue(String.class);
                                foodModel.image_path = snapshot.child("image_path").getValue(String.class);
                                foodModel.price = priceDbl;
                                foodModel.availableQty = availableQtyInt;
                                foodModel.totalOrderedQty = totalOrderedQty;
                                foodModel.total_rate = snapshot.child("total_rate").getValue(Float.class);
                                foodModel.number_of_rates = snapshot.child("number_of_rates").getValue(Integer.class);

                                return foodModel;
                            }
                        })
                        .build();

        adapter = new MadFirebaseRecyclerAdapter<FoodModel, ViewHolder>(options, false) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, FoodModel model) {
                Log.d("fff", model.description + "," + model.name + "," + model.price + "," + model.id);
                holder.setData(model);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                adapter.stopListening();
            }
        };
        if (comparator != null) {
            adapter.setSortComparator(comparator);
        }
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        resetShoppingCart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daily_menu, menu);
        final Menu m = menu;
        final MenuItem menuItem = m.findItem(R.id.shoppingcart_itemmenu);
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoppingCart.getCounter() > 0) {
                    Intent intent = new Intent(DailyMenuActivity.this, OrderConfirmActivity.class);
                    Log.d("pedro", Integer.toString(shoppingCart.getItemsMap().size()));
                    intent.putExtra("restaurant_id", restaurant_id);
                    intent.putExtra("restaurant_name", restaurant_name);
                    intent.putExtra("restaurant_address", restaurant_address);
                    intent.putExtra("restaurant_phone", restaurant_phone);
                    intent.putExtra("itemsMap", shoppingCart.getItemsMap());
                    intent.putExtra("items_quantity", shoppingCart.getCounter());
                    intent.putExtra("items_tot_price", shoppingCart.getTotal_price());
                    intent.putExtra("free_day", free_day);
                    intent.putExtra("opening_time", opening_time);
                    intent.putExtra("closing_time", closing_time);
                    startActivityForResult(intent, RC_ORDER_CONFIRM);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.shopping_cart_empty),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.sort_itemmenu) {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choose_sorting_field);
            // add a list
            String[] sortFields = {
                    getString(R.string.label_food_name),
                    getString(R.string.popularity)
            };
            builder.setItems(sortFields, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // by name
                            fetch(new Comparator<FoodModel>() {
                                @Override
                                public int compare(FoodModel lhs, FoodModel rhs) {
                                    return lhs.name.compareTo(rhs.name);
                                }
                            });
                            break;
                        case 1: // by popularity
                            fetch(new Comparator<FoodModel>() {
                                @Override
                                public int compare(FoodModel lhs, FoodModel rhs) {
                                    if (lhs.totalOrderedQty > rhs.totalOrderedQty) {
                                        return -1;
                                    } else if (lhs.totalOrderedQty < rhs.totalOrderedQty) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                            break;
                    }
                }
            });
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ORDER_CONFIRM) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (somethingAdded) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.shopping_cart_exit_warning));
            builder.setPositiveButton(this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DailyMenuActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton(this.getString(android.R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else super.onBackPressed();
    }
}
