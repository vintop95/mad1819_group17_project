package it.polito.mad1819.group17.lab02.orders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad1819.group17.lab02.R;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> orders;
    private RecyclerViewClickListener mListener;


    public ArrayList<Order> getOrders() {
        return orders;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card_order;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_order_number;
        TextView txt_total_items;
        TextView txt_order_state;

        private RecyclerViewClickListener mListener;
        private Context context;



        public ViewHolder(View itemView, RecyclerViewClickListener listener, Context context) {
            super(itemView);
            card_order = itemView.findViewById(R.id.card_order);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_total_items = itemView.findViewById(R.id.txt_total_items);
            txt_order_state = itemView.findViewById(R.id.txt_order_state);

            mListener = listener;
            itemView.setOnClickListener(this);

            this.context = context;

        }


        public void setData(Order order) {
            this.txt_delivery_time.setText(order.getDelivery_time());
            this.txt_delivery_date.setText(order.getDelivery_date());
            this.txt_order_number.setText("" + order.getNumber());
            this.txt_total_items.setText("" + order.getTotalItemsQuantity());
            this.txt_order_state.setText(order.getCurrentState());
            switch (order.getCurrentState()) {
                case Order.STATE1:
                    this.card_order.setCardBackgroundColor(context.getColor(R.color.colorState1));
                    break;
                case Order.STATE2:
                    this.card_order.setCardBackgroundColor(context.getColor(R.color.colorState2));
                    break;
                case Order.STATE3:
                    this.card_order.setCardBackgroundColor(context.getColor(R.color.colorState3));
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }


    public OrdersAdapter(ArrayList<Order> orders, RecyclerViewClickListener mListener, Context context) {
        this.context = context;
        this.orders = orders;
        this.mListener = mListener;
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order, parent, false);

        ViewHolder viewHolder = new ViewHolder(cardView, mListener, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateList(ArrayList<Order> updatedData) {
        orders = updatedData;
        notifyDataSetChanged();
    }
}
