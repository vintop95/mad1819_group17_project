package it.polito.mad1819.group17.lab02;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Order[] orders;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public OrdersAdapter(Order[] orders) {
        this.orders = orders;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        ((TextView) holder.cardView.findViewById(R.id.txt_delivery_time)).setText(orders[position].getDelivery_time());
        ((TextView) holder.cardView.findViewById(R.id.txt_order_number)).setText("" + orders[position].getNumber());
        ((TextView) holder.cardView.findViewById(R.id.txt_customer_name)).setText(orders[position].getCustomer_name());
        ((TextView) holder.cardView.findViewById(R.id.txt_customer_phone)).setText(orders[position].getCustomer_phone());
        ((TextView) holder.cardView.findViewById(R.id.txt_total_items)).setText(""+5);
        ((TextView) holder.cardView.findViewById(R.id.txt_order_state)).setText(orders[position].getState());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return orders.length;
    }
}
