package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.common.Deliveryman;
import it.polito.mad1819.group17.deliveryapp.common.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class OrderDetailsActivity extends AppCompatActivity {

    //public final static int STATE_CHANGED = 1;
    public final static int STATE_NOT_CHANGED = 0;
    public final static int SELECT_DELIVERYMEN = 2;

    private Restaurateur currentRestaurateur;

    private TextView txt_order_id;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_order_content;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;
    private TextView txt_delivery_address;
    private TextView txt_order_notes;
    private Button btn_next_state;
    private CardView card_deliveryman;
    private TextView txt_deliveryman_name;
    private TextView txt_deliveryman_phone;

    private Order inputOrder;

    private void showBackArrowOnToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void locateViews() {
        txt_order_id = findViewById(R.id.txt_order_id);
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_order_content = findViewById(R.id.txt_order_content);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        txt_state_history = findViewById(R.id.txt_state_history);
        txt_delivery_address = findViewById(R.id.txt_delivery_address);
        txt_order_notes = findViewById(R.id.txt_order_notes);
        btn_next_state = findViewById(R.id.btn_next_state);
        card_deliveryman = findViewById(R.id.card_deliveryman);
        txt_deliveryman_name = findViewById(R.id.txt_deliveryman_name);
        txt_deliveryman_phone = findViewById(R.id.txt_deliveryman_phone);
    }

    private void feedViews(Order selectedOrder) {
        txt_order_id.setText(selectedOrder.getId());
        txt_delivery_time.setText(selectedOrder.getDelivery_timestamp().split(" ")[1]);
        txt_delivery_date.setText(selectedOrder.getDelivery_timestamp().split(" ")[0]);
        txt_customer_name.setText(selectedOrder.getCustomer_name());
        txt_customer_phone.setText(Html.fromHtml("<u>" + selectedOrder.getCustomer_phone() + "<u/>"));
        txt_delivery_address.setText(selectedOrder.getDelivery_address());
        txt_order_notes.setText(selectedOrder.getNotes());

        String order_content = "";
        for (String itemId : selectedOrder.getItem_itemDetails().keySet()) {
            if (!order_content.equals(""))
                order_content += "\n";

            ShoppingItem shoppingItem = selectedOrder.getItem_itemDetails().get(itemId);
            order_content += "x" + shoppingItem.getQuantity()
                    + " " + shoppingItem.getName()
                    + " - " + CurrencyHelper.getCurrency(shoppingItem.getPrice() * shoppingItem.getQuantity());
        }
        txt_order_content.setText(order_content);


        if (!TextUtils.isEmpty(selectedOrder.getDeliveryman_id())) {
            txt_deliveryman_name.setText(selectedOrder.getDeliveryman_name());
            txt_deliveryman_phone.setText(Html.fromHtml("<u>" + selectedOrder.getDeliveryman_phone() + "<u/>"));
            txt_deliveryman_phone.setOnClickListener(v -> {
                String phoneNumber = ((TextView) v).getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            });
        }

        txt_state_history.setText(selectedOrder.getStateHistoryToString());

        if (!TextUtils.isEmpty(inputOrder.getDeliveryman_name()) && !TextUtils.isEmpty(inputOrder.getDeliveryman_phone())) {
            txt_deliveryman_name.setText(inputOrder.getDeliveryman_name());
            txt_deliveryman_phone.setText(Html.fromHtml("<u>" + inputOrder.getDeliveryman_phone() + "<u/>"));
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailsActivity.this);
        alertDialog.setTitle(R.string.confirm_next_state_title)
                .setMessage(R.string.confirm_next_state_message)
                .setPositiveButton(R.string.positive_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positiveButtonAction();
                            }
                        })
                .setNegativeButton(R.string.negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                negativeButtonAction();
                            }
                        })
                .show();
    }

    ////////////////// FIREBASE ORDER MGMT ////////////////////////////////////
    private final static String FIREBASE_ORDERS = "orders";

    private String getRestaurateurOrdersPath(String restaurateurId, String orderId) {
        return "restaurateurs/" + restaurateurId + "/" + FIREBASE_ORDERS + "/" + orderId;
    }

    private String getCustomerOrdersPath(String customerId, String orderId) {
        return "customers/" + customerId + "/" + FIREBASE_ORDERS + "/" + orderId;
    }

    private void handleCompletionListener
            (Context context, @Nullable DatabaseError err,
             @NonNull DatabaseReference ref, String msg) {
        if (err != null) {
            Log.e("FIREBASE_LOG", err.getMessage());
            Toast.makeText(context, err.getMessage(), Toast.LENGTH_LONG).show();
            btn_next_state.setEnabled(true);
        } else {
            Log.d("FIREBASE_LOG", msg + " " + ref.toString());
            adjustLayoutProgrammatically();
        }
    }

    private void updateOrderInFirebase(Order ord) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) throw new IllegalArgumentException("user id is not set");

        if (!TextUtils.isEmpty(ord.getCustomer_id())) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            Map<String, Object> insertedOrderData = new HashMap<>();
            insertedOrderData.put(getRestaurateurOrdersPath(userId, ord.getId()), ord);
            insertedOrderData.put(getCustomerOrdersPath(ord.getCustomer_id(), ord.getId()), ord);

            rootRef.updateChildren(insertedOrderData,
                    (@Nullable DatabaseError err, @NonNull DatabaseReference ref)
                            -> handleCompletionListener(getApplicationContext(),
                            err, ref, "Updated order " + ord.getId() + " in customer and restaurateur of ")
            );

        } else {
            Toast.makeText(getApplicationContext(),
                    "Customer id was not set, customer order not updated",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void positiveButtonAction() {
        btn_next_state.setEnabled(false);
        if (inputOrder.moveToNextState()) {
            txt_state_history.setText(inputOrder.getStateHistoryToString());
            updateOrderInFirebase(inputOrder);
        }
    }

    private void negativeButtonAction() {
        setResult(STATE_NOT_CHANGED);
    }

    private void retrieveCurrentRestaurateurAndSendDeliveryRequest(Deliveryman selectedDeliveryman) {
        FirebaseDatabase.getInstance().getReference()
                .child("restaurateurs").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentRestaurateur = dataSnapshot.getValue(Restaurateur.class);

                        // create the new delivery request
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String currentTimestamp = formatter.format(new Date());
                        HashMap<String, String> state_stateTime = new HashMap<String, String>();
                        state_stateTime.put("state1", currentTimestamp);
                        DeliveryRequest newDeliveryRequest = new DeliveryRequest(
                                inputOrder.getRestaurant_id(),
                                inputOrder.getId(),
                                inputOrder.getCustomer_id(),
                                inputOrder.getDelivery_address(),
                                inputOrder.getCustomer_name(),
                                inputOrder.getCustomer_phone(),
                                inputOrder.getNotes(),
                                "state0_" + inputOrder.getDelivery_timestamp(),
                                inputOrder.getDelivery_timestamp(),
                                state_stateTime,
                                currentRestaurateur.getName(),
                                currentRestaurateur.getPhone(),
                                currentRestaurateur.getAddress()
                        );
                        newDeliveryRequest.setDistanceFromContext(getApplicationContext());

                        // send delivery request to the rider
                        String newDeliveryRequestKey = FirebaseDatabase.getInstance().getReference()
                                .child("deliverymen").child(selectedDeliveryman.getId()).child("delivery_requests")
                                .push().getKey();
                        FirebaseDatabase.getInstance().getReference()
                                .child("deliverymen").child(selectedDeliveryman.getId())
                                .child("delivery_requests").child(newDeliveryRequestKey)
                                .setValue(newDeliveryRequest);

                        // update locally the order:
                        // - move it to the STATE3
                        // - add deliveryman's information
                        inputOrder.moveToNextState();
                        inputOrder.setDeliveryman_id(selectedDeliveryman.getId());
                        inputOrder.setDeliveryman_name(selectedDeliveryman.getName());
                        inputOrder.setDeliveryman_phone(selectedDeliveryman.getPhone());

                        // update the order remotely
                        updateOrderInFirebase(inputOrder);

                        // update UI
                        feedViews(inputOrder);
                        adjustLayoutProgrammatically();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void selectDeliveryman(String selectedDeliverymanId) {
        FirebaseDatabase.getInstance().getReference()
                .child("deliverymen").child(selectedDeliverymanId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Deliveryman selectedDeliveryman = dataSnapshot.getValue(Deliveryman.class);
                        retrieveCurrentRestaurateurAndSendDeliveryRequest(selectedDeliveryman);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void adjustLayoutProgrammatically() {
        switch (inputOrder.getCurrentState()) {
            case Order.STATE1:
                card_deliveryman.setVisibility(View.GONE);
                btn_next_state.setOnClickListener(v -> showConfirmationDialog());
                break;
            case Order.STATE2:
                card_deliveryman.setVisibility(View.GONE);
                btn_next_state.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), AvailableDeliverymenActivity.class), SELECT_DELIVERYMEN));
                btn_next_state.setEnabled(true);
                break;
            case Order.STATE3:
            case Order.STATE4:
                card_deliveryman.setVisibility(View.VISIBLE);
                btn_next_state.setTextColor(getResources().getColor(R.color.button_disabled_text));
                btn_next_state.setEnabled(false);
                break;
        }
        feedViews(inputOrder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        showBackArrowOnToolbar();

        locateViews();

        txt_customer_phone.setOnClickListener(v -> {
            String phoneNumber = ((TextView) v).getText().toString();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        });

        if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
            // we came here due to a tap on the notification so let us read the (updated) order from firebase
            FirebaseDatabase.getInstance().getReference("/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders/" + getIntent().getStringExtra("id"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            inputOrder = dataSnapshot.getValue(Order.class);
                            adjustLayoutProgrammatically();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            // we came here tapping on an order in the recycler view
            inputOrder = (Order) getIntent().getSerializableExtra("order");
            adjustLayoutProgrammatically();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String selectedDeliverymanId = null;
        if (requestCode == SELECT_DELIVERYMEN && resultCode == AvailableDeliverymenActivity.RESULT_OK) {
            selectedDeliverymanId = data.getStringExtra("selected_deliveryman_id");
            selectDeliveryman(selectedDeliverymanId);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}