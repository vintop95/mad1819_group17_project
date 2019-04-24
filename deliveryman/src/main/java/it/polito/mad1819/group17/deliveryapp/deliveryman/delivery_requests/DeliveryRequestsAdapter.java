package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
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
            clickedDeliveryRequest.setId(getSnapshots().getSnapshot(getAdapterPosition()).getKey());
            fragment.startActivityForResult(
                    new Intent(fragment.getActivity().getApplicationContext(), DeliveryRequestDetailsActivity.class)
                            .putExtra("delivery_request", clickedDeliveryRequest),
                    DeliveryRequestsFragment.SHOW_DETAILS_REQUEST);
        }
    }
    /* ------------------------------------------------------------------------------------------- */

    @Override
    protected void onBindViewHolder(DeliveryRequestHolder holder, int position, DeliveryRequest model) {
        model.setId(getRef(position).getKey());
        holder.txt_delivery_time.setText(model.getDelivery_time());
        holder.txt_delivery_date.setText(model.getDelivery_date());
        holder.txt_state.setText(model.getCurrentState());
        holder.txt_customer_name.setText(model.getCustomer_name());
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
