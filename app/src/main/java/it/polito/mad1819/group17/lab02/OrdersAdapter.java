package it.polito.mad1819.group17.lab02;

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

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card_order;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_order_number;
        TextView txt_customer_name;
        TextView txt_customer_phone;
        TextView txt_total_items;
        TextView txt_order_state;

        public ViewHolder(View itemView) {
            super(itemView);
            card_order = itemView.findViewById(R.id.card_order);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_customer_phone = itemView.findViewById(R.id.txt_customer_phone);
            txt_total_items = itemView.findViewById(R.id.txt_total_items);
            txt_order_state = itemView.findViewById(R.id.txt_order_state);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment orderDetailsFragment = new OrderDetailsFragment();
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, orderDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });*/
        }

        public void setData(Order order) {
            this.txt_delivery_time.setText(order.getDelivery_time());
            this.txt_delivery_date.setText(order.getDelivery_date());
            this.txt_order_number.setText("" + order.getNumber());
            this.txt_customer_name.setText(order.getCustomer_name());
            this.txt_customer_phone.setText(order.getCustomer_phone());
            this.txt_total_items.setText("" + order.getTotalItemsQuantity());
            this.txt_order_state.setText(order.getCurrentState());
        }
    }


    public OrdersAdapter(ArrayList<Order> orders) {
        this.orders = orders;
        Collections.sort(this.orders);
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order, parent, false);

        final ViewHolder viewHolder = new ViewHolder(cardView);

        viewHolder.card_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order selectedOrder = orders.get(viewHolder.getAdapterPosition());
                Bundle orderDetailsFragmentArgs = new Bundle();
                orderDetailsFragmentArgs.putSerializable("selected_order", selectedOrder);
                OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
                orderDetailsFragment.setArguments(orderDetailsFragmentArgs);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, orderDetailsFragment, "order_detail_fagment")
                        .addToBackStack("orders_fragment")
                        .commit();
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.setData(orders.get(position));
        /*viewHolder.txt_delivery_time.setText(orders.get(position).getDelivery_time());
        viewHolder.txt_order_number.setText("" + orders.get(position).getNumber());
        viewHolder.txt_customer_name.setText(orders.get(position).getCustomer_name());
        viewHolder.txt_customer_phone.setText(orders.get(position).getCustomer_phone());
        viewHolder.txt_total_items.setText("" + orders.get(position).getTotalItemsQuantity());
        viewHolder.txt_order_state.setText(orders.get(position).getCurrentState());*/

        /*((TextView) holder.cardView.findViewById(R.id.txt_delivery_time)).setText(orders.get(position).getDelivery_time());
        ((TextView) holder.cardView.findViewById(R.id.txt_order_number)).setText("" + orders.get(position).getNumber());
        ((TextView) holder.cardView.findViewById(R.id.txt_customer_name)).setText(orders.get(position).getCustomer_name());
        ((TextView) holder.cardView.findViewById(R.id.txt_customer_phone)).setText(orders.get(position).getCustomer_phone());
        ((TextView) holder.cardView.findViewById(R.id.txt_total_items)).setText("" + orders.get(position).getTotalItemsQuantity());
        ((TextView) holder.cardView.findViewById(R.id.txt_order_state)).setText(orders.get(position).getCurrentState());
        */

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return orders.size();
    }
}
