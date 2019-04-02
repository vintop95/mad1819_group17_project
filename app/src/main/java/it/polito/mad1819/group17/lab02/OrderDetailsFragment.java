package it.polito.mad1819.group17.lab02;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrderDetailsFragment extends Fragment {

    private TextView txt_order_number;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_order_content;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;


    private void locateViews(View view) {
        txt_order_number = view.findViewById(R.id.txt_order_number);
        txt_delivery_time = view.findViewById(R.id.txt_delivery_time);
        txt_delivery_date = view.findViewById(R.id.txt_delivery_date);
        txt_order_content = view.findViewById(R.id.txt_order_content);
        txt_customer_name = view.findViewById(R.id.txt_customer_name);
        txt_customer_phone = view.findViewById(R.id.txt_customer_phone);
        txt_state_history = view.findViewById(R.id.txt_state_history);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
//        Order selectedOrder = (Order) savedInstanceState.getSerializable("selected_order");

        Order selectedOrder = (Order) getArguments().getSerializable("selected_order");

        locateViews(view);

        feedViews(selectedOrder);

        return view;
    }

}
