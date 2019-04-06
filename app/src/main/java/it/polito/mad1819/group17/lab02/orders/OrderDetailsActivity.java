package it.polito.mad1819.group17.lab02.orders;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private Button btn_next_state;

    private void locateViews() {
        txt_order_number = findViewById(R.id.txt_order_number);
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_order_content = findViewById(R.id.txt_order_content);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        txt_state_history = findViewById(R.id.txt_state_history);
        btn_next_state = findViewById(R.id.btn_save);
    }

    private void feedViews(Order selctedOrder) {
        txt_order_number.setText("" + selctedOrder.getNumber());
        txt_delivery_time.setText(selctedOrder.getDelivery_time());
        txt_delivery_date.setText(selctedOrder.getDelivery_date());
        txt_customer_name.setText(selctedOrder.getCustomer_name());
        txt_customer_phone.setText(selctedOrder.getCustomer_phone());
        String order_content = "";
        for (String item : selctedOrder.getItem_itemQuantity().keySet()) {
            if (!order_content.equals(""))
                order_content += System.lineSeparator();
            order_content += "x" + selctedOrder.getItem_itemQuantity().get(item) + " " + item;
        }
        txt_order_content.setText(order_content);

        txt_state_history.setText(selctedOrder.getStateHistoryToString());

    }

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

        btn_next_state.setOnClickListener(new View.OnClickListener() {
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


}
