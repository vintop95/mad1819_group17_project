package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.polito.mad1819.group17.deliveryapp.common.AvailableDeliveryman;
import it.polito.mad1819.group17.deliveryapp.common.Deliveryman;
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
        TextView txt_deliveryman_id, txt_distance, txt_name, txt_phone;

        public AvailableDeliverymanHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_deliveryman_id = itemView.findViewById(R.id.txt_deliveryman_id);
            txt_distance = itemView.findViewById(R.id.txt_distance);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_phone = itemView.findViewById(R.id.txt_phone);


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

        // retrieve for firebase name and phone of the current deliveryman, then feed the whole card view
        FirebaseDatabase.getInstance().getReference()
                .child("deliverymen").child(availableDeliveryman.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Deliveryman deliveryman = dataSnapshot.getValue(Deliveryman.class);
                        if (deliveryman != null) {
                            availableDeliverymanHolder.txt_deliveryman_id.setText(availableDeliveryman.getId());
                            availableDeliverymanHolder.txt_distance.setText(new DecimalFormat("####0.00").format(availableDeliveryman.getHaversineDistanceFromReference()));
                            availableDeliverymanHolder.txt_name.setText(deliveryman.getName());
                            availableDeliverymanHolder.txt_phone.setText(Html.fromHtml("<u>" + deliveryman.getPhone() + "<u/>"));

                            availableDeliverymanHolder.txt_phone.setOnClickListener(v -> context.startActivity(
                                    new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ((TextView) v).getText().toString(), null)))
                            );
                        } else
                            Toast.makeText(context, "Unable to retrieve information for some deliverymen", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return availableDeliverymen.size();
    }


}
