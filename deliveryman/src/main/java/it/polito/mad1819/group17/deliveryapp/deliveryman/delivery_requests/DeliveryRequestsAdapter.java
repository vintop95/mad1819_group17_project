package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.Serializable;

import it.polito.mad1819.group17.deliveryapp.deliveryman.R;

public class DeliveryRequestsAdapter extends FirebaseRecyclerAdapter<DeliveryRequest, DeliveryRequestsAdapter.DeliveryRequestHolder> {

    private Fragment fragment;

    public DeliveryRequestsAdapter(FirebaseRecyclerOptions<DeliveryRequest> options, Fragment fragment) {
        super(options);
        this.fragment = fragment;
    }


    /* ------------------------------------------------------------------------------------------- */
    public class DeliveryRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_customer_name;
        TextView txt_state;


        public DeliveryRequestHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = itemView.findViewById(R.id.card_delivery_request);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_state = itemView.findViewById(R.id.txt_state);

        }

        @Override
        public void onClick(View v) {
            DeliveryRequest clickedDeliveryRequest = getItem(getAdapterPosition());
            fragment.startActivityForResult(
                    new Intent(fragment.getActivity().getApplicationContext(), DeliveryRequestDetailsActivity.class)
                            .putExtra("delivery_request", clickedDeliveryRequest),
                    DeliveryRequestsFragment.SHOW_DETAILS_REQUEST);
        }
    }
    /* ------------------------------------------------------------------------------------------- */

    @Override
    protected void onBindViewHolder(DeliveryRequestHolder holder, int position, DeliveryRequest model) {
        holder.txt_delivery_time.setText(model.getTimestamp().split(" ")[1]);
        holder.txt_delivery_date.setText(model.getTimestamp().split(" ")[0]);
    }


    @NonNull
    @Override
    public DeliveryRequestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_delivery_request, viewGroup, false);
        return new DeliveryRequestHolder(view);
    }

    public static DeliveryRequest getOrderById(String id){

        return null;
    }
}
