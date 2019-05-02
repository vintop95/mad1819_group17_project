package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.media.tv.TvContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.ProgressBarHandler;

public class DeliveryRequestsAdapter extends FirebaseRecyclerAdapter<DeliveryRequest, DeliveryRequestsAdapter.DeliveryRequestHolder> {

    private Fragment fragment;
    private ProgressBarHandler pbHandler;

    public DeliveryRequestsAdapter(FirebaseRecyclerOptions<DeliveryRequest> options,
                                   Fragment fragment, ProgressBarHandler pbHandler) {
        super(options);
        this.fragment = fragment;
        this.pbHandler = pbHandler;
    }


    /* ------------------------------------------------------------------------------------------- */
    public class DeliveryRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_delivery_address;
        //TextView txt_customer_name;
        TextView txt_state;
        TextView txt_restaurant_name;
        TextView txt_restaurant_address;


        public DeliveryRequestHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = itemView.findViewById(R.id.card_delivery_request);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_delivery_address = itemView.findViewById(R.id.txt_address);
            //txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_state = itemView.findViewById(R.id.txt_state);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);
            txt_restaurant_address = itemView.findViewById(R.id.txt_restaurant_address);

        }

        @Override
        public void onClick(View v) {
            DeliveryRequest clickedDeliveryRequest = getItem(getAdapterPosition());
            clickedDeliveryRequest.setId(getSnapshots().getSnapshot(getAdapterPosition()).getKey());
            fragment.startActivityForResult(
                    new Intent(fragment.getActivity().getApplicationContext(), DeliveryRequestDetailsActivity.class)
                            .putExtra("delivery_request", clickedDeliveryRequest),
                    DeliveryRequestsFragment.SHOW_DETAILS_REQUEST);
        }
    }
    /* ------------------------------------------------------------------------------------------- */

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        pbHandler.hide();
    }

    @Override
    protected void onBindViewHolder(DeliveryRequestHolder holder, int position, DeliveryRequest model) {
        model.setId(getRef(position).getKey());
        holder.txt_delivery_time.setText(model.getDelivery_time());
        holder.txt_delivery_date.setText(model.getDelivery_date());
        holder.txt_state.setText(model.getCurrentState());
        holder.txt_delivery_address.setText(model.getAddress());
        //holder.txt_customer_name.setText(model.getCustomer_name());
        holder.txt_restaurant_name.setText(model.getRestaurant_name());
        holder.txt_restaurant_address.setText(model.getRestaurant_address());
        switch (model.getCurrentState()) {
            case DeliveryRequest.STATE1:
                holder.cardView.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState1));
                break;
            case DeliveryRequest.STATE2:
                holder.cardView.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState2));
                break;
            case DeliveryRequest.STATE3:
                holder.cardView.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState3));
                break;
        }
    }


    @NonNull
    @Override
    public DeliveryRequestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_delivery_request, viewGroup, false);
        return new DeliveryRequestHolder(view);
    }

    public static DeliveryRequest getOrderById(String id) {
        return null;
    }

}
