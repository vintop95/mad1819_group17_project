package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

import it.polito.mad1819.group17.restaurateur.R;

public class OrderDetailsActivity extends AppCompatActivity {

    public final static int STATE_CHANGED = 1;
    public final static int STATE_NOT_CHANGED = 0;

    private TextView txt_order_number;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_order_content;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;
    private TextView txt_delivery_address;
    private TextView txt_order_notes;
    private Button btn_next_state;
    private CardView card_rider;
    private TextView txt_rider_name;
    private TextView txt_rider_phone;

    private Order inputOrder;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void locateViews() {
        txt_order_number = findViewById(R.id.txt_order_number);
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_order_content = findViewById(R.id.txt_order_content);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        txt_state_history = findViewById(R.id.txt_state_history);
        txt_delivery_address = findViewById(R.id.txt_delivery_address);
        txt_order_notes = findViewById(R.id.txt_order_notes);
        btn_next_state = findViewById(R.id.btn_next_state);
        card_rider = findViewById(R.id.card_rider);
        txt_rider_name = findViewById(R.id.txt_rider_name);
        txt_rider_phone = findViewById(R.id.txt_rider_phone);
    }

    private void feedViews(Order selectedOrder) {
        txt_order_number.setText("" + selectedOrder.getId());
        txt_delivery_time.setText(selectedOrder.getDelivery_timestamp().split(" ")[1]);
        txt_delivery_date.setText(selectedOrder.getDelivery_timestamp().split(" ")[0]);
        txt_customer_name.setText(selectedOrder.getCustomer_name());
        txt_customer_phone.setText(Html.fromHtml("<u>" + selectedOrder.getCustomer_phone() + "<u/>"));
        txt_delivery_address.setText(selectedOrder.getDelivery_address());
        txt_order_notes.setText(selectedOrder.getNotes());

        String order_content = "";
        for (String item : selectedOrder.getItem_itemQuantity().keySet()) {
            if (!order_content.equals(""))
                order_content += "\n";
            order_content += "x" + selectedOrder.getItem_itemQuantity().get(item) + " " + item;
        }
        txt_order_content.setText(order_content);


        if (!TextUtils.isEmpty(selectedOrder.getRider_id())) {
            txt_rider_name.setText(selectedOrder.getRider_name());
            txt_rider_phone.setText(Html.fromHtml("<u>" + selectedOrder.getRider_phone() + "<u/>"));
            txt_rider_phone.setOnClickListener(v -> {
                String phoneNumber = ((TextView) v).getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            });
        }

        txt_state_history.setText(selectedOrder.getStateHistoryToString());
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

    private void positiveButtonAction() {
        if (inputOrder.moveToNextState()) {

            txt_state_history.setText(inputOrder.getStateHistoryToString());

            if (inputOrder.getCurrentState() == Order.STATE3) {
                btn_next_state.setTextColor(getResources().getColor(R.color.button_disabled_text));
                btn_next_state.setEnabled(false);
                selectRider();
                card_rider.setVisibility(View.VISIBLE);
            }
            FirebaseDatabase.getInstance()
                    .getReference("/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders/" + inputOrder.getId())
                    .setValue(inputOrder);
        }

    }

    private void negativeButtonAction() {
        setResult(STATE_NOT_CHANGED);
    }

    private void selectRider() {

        //choose rider from firebase
        String rider_id = "trial_rider_id";
        String rider_name = "trial_rider_name";
        String rider_phone = "3319898789";

        //update the order
        inputOrder.setRider_id(rider_id);
        inputOrder.setRider_name(rider_name);
        inputOrder.setRider_phone(rider_phone);
        FirebaseDatabase.getInstance().getReference().child("orders").child(inputOrder.getId()).setValue(inputOrder);

        //notify rider

        txt_rider_name.setText(inputOrder.getRider_name());
        txt_rider_phone.setText(Html.fromHtml("<u>" + inputOrder.getRider_phone() + "<u/>"));
        txt_rider_phone.setOnClickListener(v -> {
            String phoneNumber = ((TextView) v).getText().toString();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        });
    }

    public void adjustLayoutProgrammatically() {
        if (inputOrder.getCurrentState() != Order.STATE3) {
            btn_next_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog();
                }
            });
            card_rider.setVisibility(View.GONE);
        } else {
            card_rider.setVisibility(View.VISIBLE);
            btn_next_state.setTextColor(getResources().getColor(R.color.button_disabled_text));
            btn_next_state.setEnabled(false);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}