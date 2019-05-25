package it.polito.mad1819.group17.deliveryapp.customer.orders;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.customer.R;


public class OrdersAdapter extends FirebaseRecyclerAdapter<Order, OrdersAdapter.OrderHolder> {

    private Fragment fragment;
    private RecyclerView recyclerView;
    private int animationFlag = 0;

    public OrdersAdapter(FirebaseRecyclerOptions<Order> options, Fragment fragment, RecyclerView recyclerView) {
        super(options);
        this.fragment = fragment;
        this.recyclerView = recyclerView;
    }

    /* ------------------------------------------------------------------------------------- */
    public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View state_background;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_restaurant_name;
        TextView txt_total_items;
        TextView txt_order_state;


        public OrderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            state_background = itemView.findViewById(R.id.view_state_background);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);
            txt_total_items = itemView.findViewById(R.id.txt_total_items);
            txt_order_state = itemView.findViewById(R.id.txt_order_state);
        }

        @Override
        public void onClick(View v) {
            Order clickedOrder = getItem(getAdapterPosition());
            fragment.startActivityForResult(
                    new Intent(fragment.getContext(), OrderDetailsActivity.class)
                            .putExtra("order", clickedOrder),
                    OrdersFragment.SHOW_DETAILS_REQUEST);
        }
    }
    /* ------------------------------------------------------------------------------------- */


    @Override
    protected void onBindViewHolder(OrderHolder holder, int position, Order model) {
        holder.txt_delivery_time.setText(model.getDelivery_time());
        holder.txt_delivery_date.setText(model.getDelivery_date());
        holder.txt_restaurant_name.setText(model.getRestaurant_name());
        holder.txt_total_items.setText("" + model.getTotalItemsQuantity());
        holder.txt_order_state.setText(model.getCurrentStateLocal());
        switch (model.getCurrentState()) {
            case Order.STATE1:
                holder.state_background.setBackgroundColor(fragment.getActivity().getResources().getColor(R.color.colorState1));
                break;
            case Order.STATE2:
                holder.state_background.setBackgroundColor(fragment.getActivity().getResources().getColor(R.color.colorState2));
                break;
            case Order.STATE3:
                holder.state_background.setBackgroundColor(fragment.getActivity().getResources().getColor(R.color.colorState3));
                break;
            case Order.STATE4:
                holder.state_background.setBackgroundColor(fragment.getResources().getColor(R.color.colorState4));
        }
        if (animationFlag == 0)
            runLayoutAnimation(recyclerView, 0);
    }


    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_order, viewGroup, false);
        return new OrderHolder(view);
    }

    @Override
    public void onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
        super.onDataChanged();
        if (fragment instanceof OrdersFragment) {
            ((OrdersFragment) fragment).progressBarHandler.hide();
        }
    }

    public static Order getOrderById(String id) {
        return null;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, int type) {
        final Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        animationFlag = 1;
    }
}