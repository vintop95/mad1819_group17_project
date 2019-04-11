package it.polito.mad1819.group17.lab02.orders;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad1819.group17.lab02.R;

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

    private ArrayList<Order> inputOrders;
    private int inputPosition;

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
    }

    private void feedViews(Order selctedOrder) {
        txt_order_number.setText("" + selctedOrder.getNumber());
        txt_delivery_time.setText(selctedOrder.getDelivery_time());
        txt_delivery_date.setText(selctedOrder.getDelivery_date());
        txt_customer_name.setText(selctedOrder.getCustomer_name());
        txt_customer_phone.setText(Html.fromHtml("<u>" + selctedOrder.getCustomer_phone() + "<u/>"));
        txt_delivery_address.setText(selctedOrder.getDelivery_address());
        txt_order_notes.setText(selctedOrder.getNotes());

        String order_content = "";
        for (String item : selctedOrder.getItem_itemQuantity().keySet()) {
            if (!order_content.equals(""))
                order_content += System.lineSeparator();
            order_content += "x" + selctedOrder.getItem_itemQuantity().get(item) + " " + item;
        }
        txt_order_content.setText(order_content);

        txt_state_history.setText(selctedOrder.getStateHistoryToString());

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
        if (inputOrders.get(inputPosition).moveToNextState()) {
            Intent intent = new Intent();

            ArrayList<Order> updatedOrders = new ArrayList<Order>();
            updatedOrders.addAll(inputOrders);

            Collections.sort(updatedOrders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    if (o1.getCurrentState() == Order.STATE3)
                        return 1;
                    else
                        return -o1.getDelivery_timestamp().compareTo(o2.getDelivery_timestamp());
                }
            });

            intent.putExtra("orders", updatedOrders);
            intent.putExtra("position", inputPosition);

            setResult(STATE_CHANGED, intent);
            txt_state_history.setText(inputOrders.get(inputPosition).getStateHistoryToString());

        } else
            setResult(STATE_NOT_CHANGED);

        if (inputOrders.get(inputPosition).getCurrentState() == Order.STATE3) {
            btn_next_state.setTextColor(getColor(R.color.button_disabled_text));
            btn_next_state.setEnabled(false);
        }
    }

    private void negativeButtonAction() {
        setResult(STATE_NOT_CHANGED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        showBackArrowOnToolbar();

        locateViews();

        txt_customer_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = ((TextView) v).getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            }
        });

        inputOrders = (ArrayList<Order>) getIntent().getExtras().getBundle("args").getSerializable("orders");
        inputPosition = getIntent().getExtras().getBundle("args").getInt("position");

        if (inputOrders.get(inputPosition).getCurrentState() != Order.STATE3)
            btn_next_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog();
                }
            });
        else {
            btn_next_state.setTextColor(getColor(R.color.button_disabled_text));
            btn_next_state.setEnabled(false);
        }

        feedViews(inputOrders.get(inputPosition));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}