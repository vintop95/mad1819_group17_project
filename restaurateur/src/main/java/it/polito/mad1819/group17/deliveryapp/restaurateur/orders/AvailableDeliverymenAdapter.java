package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad1819.group17.deliveryapp.common.AvailableDeliveryman;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class AvailableDeliverymenAdapter extends RecyclerView.Adapter<AvailableDeliverymenAdapter.AvailableDeliverymanHolder> {

    private ArrayList<AvailableDeliveryman> availableDeliverymen;
    private Context context;

    public AvailableDeliverymenAdapter(ArrayList<AvailableDeliveryman> availableDeliverymen, Context context) {
        this.availableDeliverymen = new ArrayList<AvailableDeliveryman>(availableDeliverymen);
        this.context = context;
    }

    /* ------------------------------------------------------------------------------------------------------------- */
    public class AvailableDeliverymanHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_deliveryman_id;
        TextView txt_distance;

        public AvailableDeliverymanHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_deliveryman_id = itemView.findViewById(R.id.txt_deliveryman_id);
            txt_distance = itemView.findViewById(R.id.txt_distance);

        }

        @Override
        public void onClick(View v) {
            Log.d("CLICK", getAdapterPosition() + "");
        }
    }
    /* ------------------------------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public AvailableDeliverymanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_available_deliveryman, viewGroup, false);
        return new AvailableDeliverymanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableDeliverymanHolder availableDeliverymanHolder, int i) {
        AvailableDeliveryman availableDeliveryman = availableDeliverymen.get(i);
        availableDeliverymanHolder.txt_deliveryman_id.setText(availableDeliveryman.getId());
        availableDeliverymanHolder.txt_distance.setText(availableDeliveryman.getHaversineDistanceFromReference().toString());
    }

    @Override
    public int getItemCount() {
        return availableDeliverymen.size();
    }


}
