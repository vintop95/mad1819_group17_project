package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.common.utils.MadFirebaseRecyclerAdapter;
import it.polito.mad1819.group17.deliveryapp.common.utils.PopupHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.common.utils.TimeHelper;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers.DailyMenuActivity;

public class RestaurantsActivity extends AppCompatActivity {
    private final int RC_DAILY_MENU = 0;
    public static String FILTER_ORDERS_COUNT = "filter_orders_count";
    public static String FILTER_SEARCH = "filter_search";
    public static String FILTER_OPEN_NOW = "filter_open_now";

    private String firebasePath = null;
    private String filterField = null, filterValue = null;
    private String category_selected;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MadFirebaseRecyclerAdapter adapter;
    private String userId;
    private Comparator<RestaurantModel> currentSorting =
            new Comparator<RestaurantModel>() {
                @Override
                public int compare(RestaurantModel lhs, RestaurantModel rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            };

    private SearchView input_search;
    private TextView label_subtitle;

    private Intent intent;
    private ProgressBarHandler pbHandler;
    private int animationFlag = 0;


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
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        label_subtitle = findViewById(R.id.label_subtitle);
        String[] restTypes = getResources().getStringArray(R.array.restaurant_types);
        Integer index;
        try {
            index = Integer.valueOf(category_selected);
        } catch (NumberFormatException e) {
            index = 0;
        }

        firebasePath = "restaurateurs";
        filterField = "restaurant_type";
        filterValue = category_selected;

        // Only favourite restaurants\
        if (index == 0) {
            filterField = "favorites/" + userId;
            filterValue = "true";
        }
        label_subtitle.setText(getString(R.string.restaurants) + ": " + restTypes[index]);

        input_search = findViewById(R.id.input_search);
        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(FILTER_SEARCH + "=" + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(FILTER_SEARCH + "=" + newText);
                return true;
            }
        });
        recyclerView = findViewById(R.id.restaurant_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        fetch(currentSorting);
        adapter.startListening();
    }

    private void fetch(Comparator comparator) {
        if (firebasePath == null) throw new IllegalStateException();

        pbHandler.show();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child(firebasePath);

        Query query = ref;

        if (!TextUtils.isEmpty(filterField) && !TextUtils.isEmpty(filterValue)) {
            query = ref.orderByChild(filterField).equalTo(filterValue);
        }

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
                                        snapshot.child("image_path").getValue(String.class),
                                        snapshot.getKey(),
                                        snapshot.child("phone").getValue(String.class),
                                        snapshot.child("orders_count").getValue(Integer.class),
                                        snapshot.child("free_day").getValue(String.class),
                                        snapshot.child("working_time_opening").getValue(String.class),
                                        snapshot.child("working_time_closing").getValue(String.class),
                                        (Map) snapshot.child("favorites").getValue(),
                                        snapshot.child("number_of_restaurant_rates").getValue(Integer.class),
                                        snapshot.child("total_restaurant_rate").getValue(Float.class),
                                        snapshot.child("number_of_service_rates").getValue(Integer.class),
                                        snapshot.child("total_service_rate").getValue(Float.class));
                            }
                        })
                        .build();

        adapter = new MadFirebaseRecyclerAdapter<RestaurantModel, ViewHolder>(options, true) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, RestaurantModel model) {
                holder.setData(model);
                if (animationFlag == 0)
                    runLayoutAnimation(recyclerView, 0);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pbHandler.hide();
                adapter.stopListening();
            }

            @Override
            protected boolean filterCondition(RestaurantModel model, String filterPattern) {
                if (filterPattern.equals(FILTER_ORDERS_COUNT)) {
                    Integer count = model.orders_count;
                    if (count == null) count = 0;
                    return count >= 3;
                } else if (filterPattern.startsWith(FILTER_SEARCH + "=")) {
                    String search = filterPattern.replace(FILTER_SEARCH + "=", "");
//                    Log.d("[FILTER]",model.name.toLowerCase() + " contains " + search + " ?");
                    return model.name.toLowerCase().contains(search);
                } else if (filterPattern.startsWith(FILTER_OPEN_NOW)) {
                    Integer freeDay;
                    try {
                        freeDay = Integer.valueOf(model.free_day);
                    } catch (NumberFormatException e) {
                        freeDay = 0;
                    }


                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    // If today is not closed
                    if (!freeDay.equals(day)) {
                        // Check the time
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        String currentTime = TimeHelper.getTimeAsString(hour, minute);
                        String openingTime = model.working_time_opening;
                        String closingTime = model.working_time_closing;


                        if (currentTime.compareTo(openingTime) > 0 &&
                                currentTime.compareTo(closingTime) < 0) {
                            return true;
                        } else return false;
                    } else return false;
                } else {
                    return true;
                }
            }
        };

        adapter.setSortComparator(new Comparator<RestaurantModel>() {
            @Override
            public int compare(RestaurantModel lhs, RestaurantModel rhs) {
                if (lhs.orders_count > rhs.orders_count) {
                    return -1;
                } else if (lhs.orders_count < rhs.orders_count) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        if (comparator != null) {
            adapter.setSortComparator(comparator);
        }
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView bio;
        public ImageView photo;
        public TextView address;
        public TextView avgPrice;
        public TextView label_closed;
        public TextView txt_closed;

        public String id;
        public String phone;
        public String image_path;
        public boolean isFavorite = false;
        public RatingBar rb_mean_rate_restaurant;
        public Float overallRate;

        public String free_day;
        public String opening_time;
        public String closing_time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurant_name);
            bio = itemView.findViewById(R.id.restaurant_bio);
            photo = itemView.findViewById(R.id.restaurant_image);
            address = itemView.findViewById(R.id.restaurant_address);
            avgPrice = itemView.findViewById(R.id.restaurant_avgprice);
            rb_mean_rate_restaurant = itemView.findViewById(R.id.rb_mean_rate_restaurant);
            label_closed = itemView.findViewById(R.id.label_closed);
            txt_closed = itemView.findViewById(R.id.txt_closed);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Context context = v.getContext();
                    Intent intent = new Intent(context, DailyMenuActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name.getText());
                    intent.putExtra("address", address.getText());
                    intent.putExtra("phone", phone);
                    intent.putExtra("bio", bio.getText());
                    intent.putExtra("photo", image_path);
                    intent.putExtra("isFavorite", isFavorite);
                    intent.putExtra("overallRate", overallRate);
                    intent.putExtra("free_day", free_day);
                    intent.putExtra("opening_time", opening_time);
                    intent.putExtra("closing_time", closing_time);

                    // Toast.makeText(v.getContext(), name.getText(),Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, RC_DAILY_MENU);
                }
            });
        }

        public void setData(RestaurantModel model) {
            setAddress(model.address);
            setBio(model.bio);
            setName(model.name);
            setPhoto(model.image_path);
            setId(model.key);
            setPhone(model.phone);
            if (model.favorites != null && model.favorites.get(userId) != null) {
                isFavorite = true;
            }
            free_day = model.free_day;
            opening_time = model.working_time_opening;
            closing_time = model.working_time_closing;


            // compute overall rate of the restaurant (mean value between mean restaurant rate and service rate)
            overallRate = new Float(0);
            if (model.total_restaurant_rate != null && model.total_service_rate != null)
                overallRate = (model.total_restaurant_rate / model.number_of_restaurant_rates + model.total_service_rate / model.number_of_service_rates) / 2;
            else if (model.total_restaurant_rate != null && model.total_service_rate == null)
                overallRate = model.total_restaurant_rate / model.number_of_restaurant_rates;
            else if (model.total_restaurant_rate == null && model.total_service_rate != null)
                overallRate = model.total_service_rate / model.number_of_service_rates;
            rb_mean_rate_restaurant.setRating(overallRate);

            int switch_closed = restaurantClosed(free_day, opening_time, closing_time);
            if (switch_closed != 0)
            {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                photo.setColorFilter(filter);

                label_closed.setVisibility(View.VISIBLE);
                txt_closed.setVisibility(View.VISIBLE);

                if (switch_closed == 1)
                    txt_closed.setText(R.string.closed_tomorrow);

                if (switch_closed == 2)
                    txt_closed.setText(opening_time);
            } else {
                photo.clearColorFilter();
                label_closed.setVisibility(View.GONE);
                txt_closed.setVisibility(View.GONE);
            }
        }

        private void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        private void setBio(String bio) {
            this.bio.setText(bio);
        }

        private void setPhoto(String image_path) {
            if (!TextUtils.isEmpty(image_path)) {
                Glide.with(photo.getContext())
                        .load(image_path)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e("GlideLog", "Image load failed");
                                return false; // leave false
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false; // leave false
                            }
                        }).into(photo);
            } else {
                Glide.with(photo.getContext()).load(getResources().getIdentifier("logo1", "drawable", getPackageName())).fitCenter().into(photo);
            }
            this.image_path = image_path;
        }

        private void setAddress(String address) {
            String address_short = address.split(",", 2)[0];
            this.address.setText(address_short);
        }

        private void setPhone(String phone) {
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
            builder.setTitle(R.string.choose_filter);
            // add a list
            String[] filterFields = {getString(R.string.no_filter),
                    getString(R.string.popular),
                    getString(R.string.open_now)
            };
            builder.setItems(filterFields, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0://no filter
                            // fetch(currentSorting);
                            adapter.stopListening();
                            adapter.getFilter().filter("");
                            break;
                        case 1: // popular
                            // fetch(currentSorting);
                            adapter.stopListening();
                            adapter.getFilter().filter(FILTER_ORDERS_COUNT);
                            break;
                        case 2: // Free Day
                            // fetch(currentSorting);
                            adapter.stopListening();
                            adapter.getFilter().filter(FILTER_OPEN_NOW);
                            break;
                    }
                }
            });
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

        if (item.getItemId() == R.id.btn_sort) {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choose_sorting_field);
            // add a list
            String[] sortFields = {
                    getString(R.string.name_restaurant),
                    getString(R.string.popularity)
            };
            builder.setItems(sortFields, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // by name
                            currentSorting = new Comparator<RestaurantModel>() {
                                @Override
                                public int compare(RestaurantModel lhs, RestaurantModel rhs) {
                                    return lhs.name.compareTo(rhs.name);
                                }
                            };
                            fetch(currentSorting);
                            break;
                        case 1: // by popularity
                            currentSorting = new Comparator<RestaurantModel>() {
                                @Override
                                public int compare(RestaurantModel lhs, RestaurantModel rhs) {
                                    if (lhs.orders_count > rhs.orders_count) {
                                        return -1;
                                    } else if (lhs.orders_count < rhs.orders_count) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            };
                            fetch(currentSorting);
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

    private int restaurantClosed(String free_day, String opening_time, String closing_time) {
        String current_timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());

        String current_date = current_timestamp.split(" ")[0];
        String current_time = current_timestamp.split(" ")[1];

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy/MM/dd").parse(current_date));
            if (Integer.parseInt(free_day) == calendar.get(Calendar.DAY_OF_WEEK))
                return 1;

            if (current_time.compareTo(closing_time) > 0)
                return 1;

            if (current_time.compareTo(opening_time) < 0)
                return 2;

            else
                return 0;

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.restaurant_closed), Toast.LENGTH_SHORT).show();
            return 1;
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, int type) {
        final Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_up);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        animationFlag = 1;
    }

}
