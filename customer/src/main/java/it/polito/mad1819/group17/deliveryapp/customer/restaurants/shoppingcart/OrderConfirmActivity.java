package it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.deliveryapp.common.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.PopupHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.MainActivity;
import it.polito.mad1819.group17.deliveryapp.customer.R;

public class OrderConfirmActivity extends AppCompatActivity {

    private TextView final_results;
    private EditText deliveryAddress_edit;
    private EditText deliveryDate_edit;
    private EditText deliveryHour_edit;
    private EditText txtOrderNotes_edit;
    private TextView item_tot_price;

    private String restaurant_id;
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_phone;
    private String customer_id;
    private String free_day;
    private String opening_time;
    private String closing_time;

    private Intent intent;
    private Double totalprice;
    private Integer itemquantity;
    private HashMap<String, ShoppingItem> itemsMap;
    private Button btnConfirmOrder;
    private String deliveryAddress;
    private String phoneNumber;
    private String name;
    private ProgressBarHandler pbHandler;

    private ArrayList<String> itemIds;
    private ArrayList<ShoppingItem> itemValues;

    private RecyclerView recyclerView;

    private AtomicInteger countFetched = new AtomicInteger(0);
    private static int N_FIELD_TO_FETCH = 2;

    private static int AUTOCOMPLETE_REQUEST = 2;
    private String newAddress = null;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_confirm));
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
        setContentView(R.layout.activity_order_confirm);

        final_results = findViewById(R.id.final_tv);
        item_tot_price = findViewById(R.id.item_tot_price);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);
        txtOrderNotes_edit = findViewById(R.id.oc_input_order_notes);
        intent = getIntent();

        pbHandler = new ProgressBarHandler(this);
        initFetchHandler();

        customer_id = FirebaseAuth.getInstance().getUid();
        if (TextUtils.isEmpty(customer_id)) {
            throw new IllegalStateException("customer_id is NULL!!!");
        }

        restaurant_id = intent.getStringExtra("restaurant_id");
        restaurant_name = intent.getStringExtra("restaurant_name");
        restaurant_address = intent.getStringExtra("restaurant_address");
        restaurant_phone = intent.getStringExtra("restaurant_phone");
        free_day = intent.getStringExtra("free_day");
        opening_time = intent.getStringExtra("opening_time");
        closing_time = intent.getStringExtra("closing_time");

        if (restaurant_id == null)
            throw new IllegalStateException("restaurant_id must not be null!");

        itemsMap = (HashMap<String, ShoppingItem>) intent.getSerializableExtra("itemsMap");
        retrieveCustomerInfo();
        itemIds = new ArrayList<String>(itemsMap.keySet());
        itemValues = new ArrayList<ShoppingItem>(itemsMap.values());

        itemquantity = intent.getIntExtra("items_quantity", 0);
        totalprice = intent.getDoubleExtra("items_tot_price", 0);

        deliveryAddress_edit = findViewById(R.id.deliveryAddress);
        deliveryAddress_edit.setKeyListener(null);
        deliveryHour_edit = (EditText) findViewById(R.id.deliveryHour);
        addTimePickerOnClick(deliveryHour_edit);
        deliveryDate_edit = findViewById(R.id.deliveryDate);
        addDatePickerOnClick(deliveryDate_edit);
        String finalResultString =
                String.format(Locale.getDefault(),
                        getApplicationContext().getString(R.string.buying_elements_summary),
                        itemquantity, CurrencyHelper.getCurrency(totalprice)
                );
        final_results.setText(finalResultString);
        item_tot_price.setText(CurrencyHelper.getCurrency(totalprice));

        recyclerView = (RecyclerView) findViewById(R.id.listview_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderConfirmAdapter orderConfirmAdapter = new OrderConfirmAdapter(itemValues, this, recyclerView);
        recyclerView.setAdapter(orderConfirmAdapter);

        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String dateNowString = df.format(dateNow);
        deliveryDate_edit.setText(dateNowString);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 60);
        Date oneHourFromNow = now.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String oneHourFromNowString = simpleDateFormat.format(oneHourFromNow);
        deliveryHour_edit.setText(oneHourFromNowString);

        Places.initialize(getApplicationContext(), "AIzaSyB7Tku5m9p0LVYU8k8-G7RB0DQoDXjvdSE");
        deliveryAddress_edit.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder(v);
            }
        });

        showBackArrowOnToolbar();
    }


    private void addTimePickerOnClick(View view) {
        view.setOnClickListener(v ->
        {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
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

    private void addDatePickerOnClick(View view) {
        view.setOnClickListener(v ->
        {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month++;
                    String month_string;
                    String day_string;
                    if (month < 10)
                        month_string = "0" + month;
                    else
                        month_string = "" + month;
                    if (dayOfMonth < 10)
                        day_string = "0" + dayOfMonth;
                    else
                        day_string = "" + dayOfMonth;
                    deliveryDate_edit.setText(year + "/" + month_string + "/" + day_string);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }


    private Order getOrderToPush() {
        Order ord = new Order();
        ord.setCustomer_id(customer_id);
        ord.setRestaurant_id(restaurant_id);

        HashMap<String, String> state_stateTime = new HashMap<>();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        String current_timestamp = simpleDateFormat.format(new Date());
        /*String day = current_timestamp.split(" ")[0];
        String delivery_timestamp = day + " " + deliveryHour_edit.getText().toString();*/
        String delivery_timestamp = deliveryDate_edit.getText().toString() + " " + deliveryHour_edit.getText();
        state_stateTime.put("state1", current_timestamp);

        //Filling the order:
        ord.setState_stateTime(state_stateTime);
        ord.setSorting_field("state0_" + delivery_timestamp);
        ord.setCustomer_name(name);
        ord.setCustomer_phone(phoneNumber);
        ord.setRestaurant_name(restaurant_name);
        ord.setRestaurant_address(restaurant_address);
        ord.setRestaurant_phone(restaurant_phone);
        //from query
        ord.setDelivery_timestamp(delivery_timestamp);
        ord.setDelivery_address(deliveryAddress_edit.getText().toString());
        ord.setItem_itemDetails(itemsMap);
        ord.setNotes(txtOrderNotes_edit.getText().toString());

        return ord;
    }

    private void confirmOrder(View v) {
        // If shopping cart is empty don't send the order
        if (itemquantity <= 0) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.shopping_cart_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(deliveryHour_edit.getText().toString()) || TextUtils.isEmpty(deliveryDate_edit.getText().toString())) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.empty_delivery_timestamp),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (restaurantClosed(free_day, opening_time, closing_time, deliveryDate_edit.getText().toString() + " " + deliveryHour_edit.getText().toString())) {
            return;
        }

        pbHandler.show();

        // Must be done first in order to have a lighter transaction
        Order ord = getOrderToPush();

        // Check if there is enough available qty for the order
        if (TextUtils.isEmpty(restaurant_id)) throw new IllegalStateException("rest_id null!");
        DatabaseReference restaurateurRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurateurs")
                .child(restaurant_id);

        restaurateurRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                // Check and update availableQty for each item
                for (String itemId : itemsMap.keySet()) {
                    MutableData currentItemRef = mutableData.child(FIREBASE_DAILY_OFFERS).child(itemId);
                    MutableData currentQtyRef = currentItemRef.child("availableQty");
                    MutableData totalOrderedQtyRef = currentItemRef.child("totalOrderedQty");

                    Integer availableQty = currentQtyRef.getValue(Integer.class);
                    Integer orderedQty = itemsMap.get(itemId).getQuantity();

                    // may be null but doTransaction will be called more than once
                    if (availableQty == null) return Transaction.success(mutableData);

                    if (availableQty < orderedQty) {
                        return Transaction.abort();
                    } else {
                        int newQty = availableQty - orderedQty;
                        if (newQty >= 0) {
                            // Update the number of items already ordered
                            Integer totalOrderedQty = totalOrderedQtyRef.getValue(Integer.class);
                            if (totalOrderedQty == null) totalOrderedQty = 0;
                            totalOrderedQtyRef.setValue(totalOrderedQty + 1);

                            // Update the current available quantity
                            currentQtyRef.setValue(newQty);
                        } else return Transaction.abort();
                    }
                }

                MutableData ordersCountRef = mutableData.child(FIREBASE_ORDERS_COUNT);

                Integer ordersCount = ordersCountRef.getValue(Integer.class);
                if (ordersCount == null) ordersCount = 0;
                ordersCountRef.setValue(ordersCount + 1);

                // Set a field that will be used in filtering
                if (ordersCount >= Restaurateur.VOTE_5_THRESHOLD) {
                    String restaurant_type = mutableData.child("restaurant_type").getValue(String.class);
                    mutableData.child(FIREBASE_FILTER_BY_VOTE).setValue(restaurant_type + "_" + "5");
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d("ORDER_CONFIRM",
                        "postTransaction:onComplete:" + databaseError);

                // Push the order to get an id if quantities were reduced in a correct way
                if (committed) {
                    pushOrderToFirebase(ord);
                    setResult(RESULT_OK);
                    Toast.makeText(getApplicationContext(), getString(R.string.order_confirmed), Toast.LENGTH_LONG).show();
                    finish();
                    Intent orders = new Intent(OrderConfirmActivity.this, MainActivity.class);
                    startActivity(orders);
                } else {
                    PopupHelper.showToast(
                            OrderConfirmActivity.this.getApplicationContext(),
                            getString(R.string.error_transaction_push_order));
                    setResult(RESULT_CANCELED);
                    finish();
                }

                pbHandler.hide();
            }
        });
    }

    private void initFetchHandler() {
        pbHandler.show();
        btnConfirmOrder.setEnabled(false);
    }

    private void handleFetch() {
        if (countFetched.incrementAndGet() >= N_FIELD_TO_FETCH) {
            pbHandler.hide();
            btnConfirmOrder.setEnabled(true);
            countFetched.set(0);
        }
    }

    ////////////////// FIREBASE ORDER MGMT ////////////////////////////////////
    private final static String FIREBASE_ORDERS = "orders";
    public final static String FIREBASE_DAILY_OFFERS = "daily_offers";
    public final static String FIREBASE_ORDERS_COUNT = "orders_count";
    public final static String FIREBASE_FILTER_BY_VOTE = "filter_by_vote";
    public final static String FIREBASE_FILTER_BY_FREE_DAY = "filter_by_free_day";

    public String getRestaurateurOrdersPath(@Nullable String orderId) {
        if (TextUtils.isEmpty(restaurant_id)) {
            throw new IllegalStateException("restaurateur_id is NULL!");
        }

        String path = "restaurateurs/" + restaurant_id + "/" + FIREBASE_ORDERS;
        if (!TextUtils.isEmpty(orderId)) path = path + "/" + orderId;
        return path;
    }

    public String getCustomerOrdersPath(@Nullable String orderId) {
        if (TextUtils.isEmpty(customer_id)) {
            throw new IllegalStateException("customer_id is NULL!");
        }
        String path = "customers/" + customer_id + "/" + FIREBASE_ORDERS;
        if (!TextUtils.isEmpty(orderId)) path = path + "/" + orderId;

        return path;
    }

    public DatabaseReference getCustomerOrdersRef() {
        if (TextUtils.isEmpty(customer_id)) {
            throw new IllegalStateException("customer_id is NULL!");
        }

        return FirebaseDatabase.getInstance().getReference()
                .child("customers")
                .child(customer_id)
                .child(FIREBASE_ORDERS);
    }

    private static void handleCompletionListener
            (Context context, @Nullable DatabaseError err,
             @NonNull DatabaseReference ref, String msg) {
        if (err != null) {
            Log.e("FIREBASE_LOG", err.getMessage());
            Toast.makeText(context, err.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Log.d("FIREBASE_LOG", msg + " " + ref.toString());
        }
    }

    public void pushOrderToFirebase(Order ord) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newCustomerOrderRef = getCustomerOrdersRef().push();
        ord.setId(newCustomerOrderRef.getKey());

        Map<String, Object> insertedOrderData = new HashMap<>();
        insertedOrderData.put(getRestaurateurOrdersPath(ord.getId()), ord);
        insertedOrderData.put(getCustomerOrdersPath(ord.getId()), ord);

        rootRef.updateChildren(insertedOrderData,
                (@Nullable DatabaseError err, @NonNull DatabaseReference ref)
                        -> handleCompletionListener(getApplicationContext(),
                        err, ref, "Added in customer and restaurateur of ")
        );
    }

    public void retrieveCustomerInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
        } else {
            name = "";
        }

        DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference()
                .child("customers")
                .child(customer_id);

        customerReference.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryAddress = dataSnapshot.getValue(String.class);
                if (deliveryAddress == null) deliveryAddress = "";

                deliveryAddress_edit.setText(deliveryAddress);

                handleFetch();
                Log.d("retrieve", deliveryAddress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.cannot_retrieve_your_address),
                        Toast.LENGTH_SHORT).show();
            }
        });

        customerReference.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneNumber = dataSnapshot.getValue(String.class);
                if (phoneNumber == null) phoneNumber = "";

                handleFetch();
                Log.d("retrieve", phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.cannot_retrieve_your_phone),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                newAddress = place.getAddress();
                deliveryAddress_edit.setText(newAddress);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(this, "Error in retrieving the address :(", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Address has not been selected.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean restaurantClosed(String free_day, String opening_time, String closing_time, String delivery_timestamp) {
        // first of all check that the delivery timestamp is not in the past
        String current_timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());

        if (current_timestamp.compareTo(delivery_timestamp) > 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.invalid_delivery_timestamp), Toast.LENGTH_SHORT).show();
            return true;
        }

        String delivery_date = delivery_timestamp.split(" ")[0];
        String delivery_time = delivery_timestamp.split(" ")[1];

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy/MM/dd").parse(delivery_date));
            // check that free day (of week) and delivery day (of week) are the same
            if (Integer.parseInt(free_day)  == calendar.get(Calendar.DAY_OF_WEEK)) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.restaurant_closed), Toast.LENGTH_SHORT).show();
                return true;
            }

            // if we get here than the delivery day is not the free day of the restaurant
            // given that, check that opening_time < delivery time < closing_time
            if (delivery_time.compareTo(opening_time) > 0 && delivery_time.compareTo(closing_time) < 0)
                return false; // restaurant open
            else {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.restaurant_closed), Toast.LENGTH_SHORT).show();
                Log.d("XYZW", "B");
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.restaurant_closed), Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
