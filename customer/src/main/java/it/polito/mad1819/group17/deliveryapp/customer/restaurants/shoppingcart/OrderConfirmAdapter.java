package it.polito.mad1819.group17.deliveryapp.customer.restaurants.shoppingcart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.customer.R;

public class OrderConfirmAdapter extends RecyclerView.Adapter<OrderConfirmAdapter.OrderConfirmHolder> {

    private ArrayList<ShoppingItem> shoppingItem;
    private Context context;
    private RecyclerView recyclerView;
    private int animationFlag = 0;

    public OrderConfirmAdapter(ArrayList<ShoppingItem> shoppingItem, Context context, RecyclerView recyclerView) {
        this.shoppingItem = new ArrayList<ShoppingItem>(shoppingItem);
        this.context = context;
        this.recyclerView = recyclerView;
    }

    /* ------------------------------------------------------------------------------------------------------------- */
    public class OrderConfirmHolder extends RecyclerView.ViewHolder {
        TextView name_tv, quantity_tv, price_tv;

        public OrderConfirmHolder(@NonNull View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.item_name);
            quantity_tv = itemView.findViewById(R.id.item_quantity);
            price_tv = itemView.findViewById(R.id.item_price);
        }
    }
    /* ------------------------------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public OrderConfirmHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_confirm_item, viewGroup, false);
        return new OrderConfirmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderConfirmHolder holder, int position) {
        holder.name_tv.setText(shoppingItem.get(position).getName());
        holder.quantity_tv.setText(Integer.toString(shoppingItem.get(position).getQuantity()));
        holder.price_tv.setText(CurrencyHelper.getCurrency(shoppingItem.get(position).getPrice()));
        if (animationFlag == 0)
           runLayoutAnimation(recyclerView, 0);
    }

    @Override
    public int getItemCount() {
        return shoppingItem.size();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, int type) {
        final Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_left_to_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        animationFlag = 1;
    }
}