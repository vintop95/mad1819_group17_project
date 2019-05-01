package it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.R;

public class OrderConfirmActivity extends AppCompatActivity {

    private TextView final_results;
    private EditText deliveryAddress_edit;
    private EditText deliveryHour_edit;
    private EditText txtOrderNotes_edit;
    private String restaurant_id;
    private String customer_id;
    private String restaurant_name;
    private Intent intent;
    private Double totalprice;
    private Integer itemquantity;
    private HashMap<String, ShoppingItem> itemsMap;
    private Button btnConfirmOrder;
    private String deliveryAddress;
    private String phoneNumber;
    private String name;
    private ProgressBarHandler pbHandler;

    private ArrayList<String> keys;
    private ArrayList<ShoppingItem> values;

    ListView lst;

    private AtomicInteger countFetched = new AtomicInteger(0);
    private static int N_FIELD_TO_FETCH = 2;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar2));
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
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);
        txtOrderNotes_edit = findViewById(R.id.oc_input_order_notes);
        intent = getIntent();

        pbHandler = new ProgressBarHandler(this);
        initFetchHandler();

        customer_id = FirebaseAuth.getInstance().getUid();
        if(TextUtils.isEmpty(customer_id)){
            throw new IllegalStateException("customer_id is NULL!!!");
        }

        restaurant_id = intent.getStringExtra("restaurant_id");
        restaurant_name = intent.getStringExtra("restaurant_name");
        if (restaurant_id == null) throw new IllegalStateException("restaurant_id must not be null!");

        itemsMap = (HashMap<String, ShoppingItem>) intent.getSerializableExtra("itemsMap");
        retrieveData();
        keys = new ArrayList<String>(itemsMap.keySet());
        values = new ArrayList<ShoppingItem>(itemsMap.values());

        ArrayList<Integer> arrQuantities = new ArrayList<>();
        ArrayList<Double> arrPrices = new ArrayList<>();
        for(ShoppingItem details: itemsMap.values()){
            arrQuantities.add(details.getQuantity());
            arrPrices.add(details.getPrice());
        }

        String[] names = keys.toArray(new String[keys.size()]);
        Integer[] quantities = arrQuantities.toArray(new Integer[values.size()]);
        Double[] prices = arrPrices.toArray(new Double[values.size()]);

        Log.d("elem_quantities", Integer.toString(quantities.length));
        Log.d("elem_names", Integer.toString(names.length));
        itemquantity = intent.getIntExtra("items_quantity",0);
        totalprice = intent.getDoubleExtra("items_tot_price",0);

        deliveryAddress_edit = (EditText) findViewById(R.id.deliveryAddress);
        deliveryHour_edit = (EditText) findViewById(R.id.deliveryHour);
        String finalResultString =
                String.format(Locale.getDefault(),
                        "Buying %d element(s) for the total price of: %s",
                        itemquantity, CurrencyHelper.getCurrency(totalprice)
                );
        final_results.setText(finalResultString);

        lst = (ListView) findViewById(R.id.listview_items);
        OrderConfirmAdapter orderConfirmAdapter = new OrderConfirmAdapter(names,quantities,prices,this);
        lst.setAdapter(orderConfirmAdapter);


        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 60);
        Date oneHourFromNow = now.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String oneHourFromNowString = simpleDateFormat.format(oneHourFromNow);
        deliveryHour_edit.setText(oneHourFromNowString);

        btnConfirmOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(itemquantity <= 0){
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.shopping_cart_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: complete filling order
                Order ord = new Order();
                ord.setCustomer_id(customer_id);
                ord.setRestaurant_id(restaurant_id);

                HashMap<String,String> state_stateTime = new HashMap<>();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
                String current_timestamp = simpleDateFormat.format(new Date());
                String day = current_timestamp.split(" ")[0];
                String delivery_timestamp = day+" "+deliveryHour_edit.getText().toString();

                state_stateTime.put("state1",current_timestamp);
                DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference()
                        .child("customers")
                        .child(customer_id);

                //Filling the order:
                ord.setState_stateTime(state_stateTime);
                ord.setSorting_field("state0_"+delivery_timestamp);
                ord.setCustomer_name(name);
                ord.setCustomer_phone(phoneNumber);
                ord.setRestaurant_name(restaurant_name);
                //from query
                ord.setDelivery_timestamp(delivery_timestamp);
                ord.setDelivery_address(deliveryAddress_edit.getText().toString());
                ord.setItem_itemDetails(itemsMap);
                ord.setNotes(txtOrderNotes_edit.getText().toString());

                pushOrderToFirebase(ord);

                Toast.makeText(getApplicationContext(), "Order confirmed",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        showBackArrowOnToolbar();
    }

    private void initFetchHandler(){
        pbHandler.show();
        btnConfirmOrder.setEnabled(false);
    }

    private void handleFetch(){
        if(countFetched.incrementAndGet() >= N_FIELD_TO_FETCH){
            pbHandler.hide();
            btnConfirmOrder.setEnabled(true);
            countFetched.set(0);
        }
    }

    ////////////////// FIREBASE ORDER MGMT ////////////////////////////////////
    private final static String FIREBASE_ORDERS = "orders";

    public DatabaseReference getRestaurateurOrdersRef() {
        if(TextUtils.isEmpty(restaurant_id)){
            throw new IllegalStateException("restaurateur_id is NULL!!!");
        }

        return FirebaseDatabase.getInstance().getReference()
                .child("restaurateurs")
                .child(restaurant_id)
                .child(FIREBASE_ORDERS);
    }

    public String getRestaurateurOrdersPath(@NonNull String orderId) {
        return "restaurateurs/" + restaurant_id + "/" + FIREBASE_ORDERS + "/" + orderId;
    }

    public DatabaseReference getCustomerOrdersRef() {
        if(TextUtils.isEmpty(customer_id)){
            throw new IllegalStateException("customer_id is NULL!!!");
        }

        return FirebaseDatabase.getInstance().getReference()
                .child("customers")
                .child(customer_id)
                .child(FIREBASE_ORDERS);
    }

    public String getCustomerOrdersPath(@NonNull String orderId) {
        return "customers/" + customer_id + "/" + FIREBASE_ORDERS + "/" + orderId;
    }

    private static void handleCompletionListener
            (Context context, @Nullable DatabaseError err,
             @NonNull DatabaseReference ref, String msg){
        if(err != null){
            Log.e("FIREBASE_LOG",err.getMessage());
            Toast.makeText(context, err.getMessage(), Toast.LENGTH_LONG).show();
        }else{
            Log.d("FIREBASE_LOG", msg + " " + ref.toString());
        }
    }

    public void pushOrderToFirebase(Order ord){
        DatabaseReference rootRef =FirebaseDatabase.getInstance().getReference();
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

    public void retrieveData(){
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
                if(deliveryAddress == null) deliveryAddress = "";

                deliveryAddress_edit.setText(deliveryAddress);

                handleFetch();
                Log.d("retrieve", deliveryAddress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        customerReference.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneNumber = dataSnapshot.getValue(String.class);
                if(phoneNumber == null) phoneNumber = "";

                handleFetch();
                Log.d("retrieve", phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
