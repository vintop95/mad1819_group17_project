package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import it.polito.mad1819.group17.deliveryapp.common.utils.PopupHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.common.utils.FirebaseRecyclerAdapter;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers.DailyMenuActivity;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart.OrderConfirmActivity;

import static it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers.DailyMenuActivity.RC_ORDER_CONFIRM;

public class RestaurantsActivity extends AppCompatActivity {
    private final int RC_DAILY_MENU = 0;

    private String filterField = null, filterValue = null;
    private String category_selected;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private Intent intent;
    private ProgressBarHandler pbHandler;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        pbHandler.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        pbHandler.hide();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        intent = getIntent();
        category_selected = intent.getStringExtra("category");

        pbHandler = new ProgressBarHandler(this);

        showBackArrowOnToolbar();

        recyclerView = findViewById(R.id.restaurant_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetch(filterField, filterValue);
    }

    public static Bitmap stringToBitMap(String encodedString) throws IllegalArgumentException{
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    private void fetch(String filterField, String filterValue) {

        if(TextUtils.isEmpty(filterField)){
            filterField = "restaurant_type";
            filterValue = category_selected;
        }

        pbHandler.show();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference();
        Query query = ref.child("restaurateurs").orderByChild(filterField).equalTo(filterValue);

        FirebaseRecyclerOptions<RestaurantModel> options =
                new FirebaseRecyclerOptions.Builder<RestaurantModel>()
                        .setQuery(query, new SnapshotParser<RestaurantModel>() {
                            @NonNull
                            @Override
                            public RestaurantModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new RestaurantModel(
                                        snapshot.child("address").getValue(String.class),
                                        snapshot.child("name").getValue(String.class),
                                        snapshot.child("bio").getValue(String.class),
                                        snapshot.child("photo").getValue(String.class),
                                        snapshot.getKey(),
                                        snapshot.child("phone").getValue(String.class),
                                        snapshot.child("orders_count").getValue(Integer.class)
                                );
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<RestaurantModel, ViewHolder>(options, true) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, RestaurantModel model) {
                holder.setAddress(model.getAddress());
                holder.setBio(model.getBio());
                holder.setName(model.getName());
                holder.setPhoto(model.getPhoto());
                holder.setId(model.getKey());
                holder.setPhone(model.phone);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pbHandler.hide();
            }

            @Override
            protected boolean filterCondition(RestaurantModel model, String filterPattern) {
                return model.orders_count > 3;
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView name;
        public TextView bio;
        public ImageView photo;
        public TextView address;
        public TextView avgPrice;
        public String id;
        public String phone;

        public void setId(String id) {
            this.id = id;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.restaurant_root_layout);
            name = itemView.findViewById(R.id.restaurant_name);
            bio = itemView.findViewById(R.id.restaurant_bio);
            photo = itemView.findViewById(R.id.restaurant_image);
            address = itemView.findViewById(R.id.restaurant_address);
            avgPrice = itemView.findViewById(R.id.restaurant_avgprice);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();

                    Context context = v.getContext();
                    Intent intent = new Intent(context, DailyMenuActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name.getText());
                    intent.putExtra("address", address.getText());
                    intent.putExtra("phone", phone);
                    // Toast.makeText(v.getContext(), name.getText(),Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, RC_DAILY_MENU);
                }
            });
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setBio(String bio) {
            this.bio.setText(bio);
        }

        public void setPhoto(String photo) {
            Bitmap bmp;
            if(photo != null) {
                bmp = stringToBitMap(photo);
                this.photo.setImageBitmap(bmp);
            }
        }

        public void setAddress(String address) {
            this.address.setText(address);
        }

        public void setPhone(String phone){
            this.phone = phone;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DAILY_MENU) {
            if (resultCode == RESULT_OK) {
                PopupHelper.showSnackbar(findViewById(android.R.id.content),
                        getString(R.string.order_confirmed));
            }
        }
    }

    ///////////////////// MENU MGMT ////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_restaurants, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_filter) {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a filter");
            // add a list
            String[] filterFields = {"No filter",
                    "5+",
                    // "Free Day" // TODO: re-add
            };
            builder.setItems(filterFields, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0://no filter
                            adapter.stopListening();
                            filterField = null;
                            filterValue = null;
                            fetch(filterField, filterValue);
                            adapter.startListening();
                            break;
                        case 1: // 5+
                            adapter.stopListening();
                            filterField = OrderConfirmActivity.FIREBASE_FILTER_BY_VOTE;
                            filterValue = category_selected + "_5";
                            fetch(filterField, filterValue);
                            adapter.startListening();
                            break;
                        case 2: // Free Day
                            adapter.stopListening();

                            Calendar calendar = Calendar.getInstance();
                            int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
                            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
                            filterField = category_selected + "_" + daysOfWeek[dayIndex];
                            filterValue = null;

                            fetch(filterField, filterValue);
                            adapter.startListening();
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
}
