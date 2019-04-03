package it.polito.mad1819.group17.lab02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView txt_order_number;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_order_content;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;

    private void locateViews() {
        txt_order_number = findViewById(R.id.txt_order_number);
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_order_content = findViewById(R.id.txt_order_content);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        txt_state_history = findViewById(R.id.txt_state_history);
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

        String state_history = "";
        switch (selctedOrder.getCurrentState()) {
            case Order.STATE1:
                state_history = selctedOrder.getState_stateTime().get(Order.STATE1) + " " + Order.STATE1;
                state_history += System.lineSeparator() + "--:--" + " " + Order.STATE2;
                state_history += System.lineSeparator() + "--:--" + " " + Order.STATE3;
                break;
            case Order.STATE2:
                state_history = selctedOrder.getState_stateTime().get(Order.STATE1) + "  " + Order.STATE1;
                state_history += System.lineSeparator() + selctedOrder.getState_stateTime().get(Order.STATE2) + "  " + Order.STATE2;
                state_history += System.lineSeparator() + "--:--" + "  " + Order.STATE3;
                break;
            case Order.STATE3:
                state_history = selctedOrder.getState_stateTime().get(Order.STATE1) + "  " + Order.STATE1;
                state_history += System.lineSeparator() + selctedOrder.getState_stateTime().get(Order.STATE2) + "  " + Order.STATE2;
                state_history += System.lineSeparator() + selctedOrder.getState_stateTime().get(Order.STATE3) + "  " + Order.STATE3;
                break;
        }
        txt_state_history.setText(state_history);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Order selectedOrder = (Order) getIntent().getExtras().getBundle("bundle_selected_order").getSerializable("selected_order");

        locateViews();


        feedViews(selectedOrder);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
