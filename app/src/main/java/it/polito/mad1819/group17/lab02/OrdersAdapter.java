package it.polito.mad1819.group17.lab02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private ArrayList<Order> orders;
    private RecyclerViewClickListener mListener;

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card_order;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_order_number;
        TextView txt_customer_name;
        TextView txt_customer_phone;
        TextView txt_total_items;
        TextView txt_order_state;

        private RecyclerViewClickListener mListener;


        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            card_order = itemView.findViewById(R.id.card_order);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_total_items = itemView.findViewById(R.id.txt_total_items);
            txt_order_state = itemView.findViewById(R.id.txt_order_state);


            mListener = listener;
            itemView.setOnClickListener(this);

        }


        public void setData(Order order) {
            this.txt_delivery_time.setText(order.getDelivery_time());
            this.txt_delivery_date.setText(order.getDelivery_date());
            this.txt_order_number.setText("" + order.getNumber());
            this.txt_total_items.setText("" + order.getTotalItemsQuantity());
            this.txt_order_state.setText(order.getCurrentState());
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }


    public OrdersAdapter(ArrayList<Order> orders, RecyclerViewClickListener mListener) {
        this.orders = orders;
        this.mListener = mListener;
        Collections.sort(this.orders);
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order, parent, false);

        ViewHolder viewHolder = new ViewHolder(cardView, mListener);

        /*viewHolder.card_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order selectedOrder = orders.get(viewHolder.getAdapterPosition());
                Intent intent = new Intent(v.getContext(), OrderDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_order", selectedOrder);

                v.getContext().startActivity(intent);
            }
        });*/

        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(orders.get(position));

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return orders.size();
    }
}
