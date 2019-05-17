package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
        ImageView image_user_photo;
        TextView txt_deliveryman_id, txt_distance, txt_name, txt_phone;
        String selectedDeliverymanId;

        public AvailableDeliverymanHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            image_user_photo = itemView.findViewById(R.id.image_deliveryman);
            txt_deliveryman_id = itemView.findViewById(R.id.txt_deliveryman_id);
            txt_distance = itemView.findViewById(R.id.txt_distance);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_phone = itemView.findViewById(R.id.txt_phone);


        }

        @Override
        public void onClick(View v) {
            selectedDeliverymanId = availableDeliverymen.get(getAdapterPosition()).getId();
            showAlertDialog();
        }

        private void showAlertDialog() {
            new AlertDialog.Builder(context).setTitle(context.getString(R.string.alert_dialog_available_deliveryman_title))
                    .setMessage(context.getString(R.string.alert_dialog_available_deliveryman_text))
                    .setNegativeButton(context.getString(R.string.negative_button), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(context.getString(R.string.positive_button), (dialog, which) -> returnSelectedDeliverymanAndFinish())
                    .show();
        }

        private void returnSelectedDeliverymanAndFinish() {
            ((AppCompatActivity)context).setResult(AvailableDeliverymenActivity.RESULT_OK, new Intent().putExtra("selected_deliveryman_id", selectedDeliverymanId));
            ((AppCompatActivity)context).finish();
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
                            if (!TextUtils.isEmpty(deliveryman.getImage_path())) {
                                Glide.with(availableDeliverymanHolder.image_user_photo.getContext())
                                        .load(deliveryman.getImage_path())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                        Target<Drawable> target, boolean isFirstResource) {
                                                Log.e("ProfileFragment", "Image load failed");
                                                return false; // leave false
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model,
                                                                           Target<Drawable> target, DataSource dataSource,
                                                                           boolean isFirstResource) {
                                                Log.v("ProfileFragment", "Image load OK");
                                                return false; // leave false
                                            }
                                        }).into(availableDeliverymanHolder.image_user_photo);
                            }else{
                                Glide.with(availableDeliverymanHolder.image_user_photo.getContext()).clear(availableDeliverymanHolder.image_user_photo);
                            }
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
