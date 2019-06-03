package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.ProgressBarHandler;

public class DeliveryRequestsAdapter extends FirebaseRecyclerAdapter<DeliveryRequest, DeliveryRequestsAdapter.DeliveryRequestHolder> {

    private Fragment fragment;
    private ProgressBarHandler pbHandler;
    private RecyclerView recyclerView;
    private int animationFlag = 0;

    public DeliveryRequestsAdapter(FirebaseRecyclerOptions<DeliveryRequest> options,
                                   Fragment fragment, ProgressBarHandler pbHandler, RecyclerView recyclerView) {
        super(options);
        this.fragment = fragment;
        this.pbHandler = pbHandler;
        this.recyclerView = recyclerView;
    }


    /* ------------------------------------------------------------------------------------------- */
    public class DeliveryRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View state_background;
        TextView txt_delivery_time;
        TextView txt_delivery_date;
        TextView txt_delivery_address;
        TextView routeDistance;
        //TextView txt_customer_name;
        TextView txt_state;
        TextView txt_restaurant_name;
        TextView txt_restaurant_address;
        ImageView image_state;
        Button mapButton;
        Intent intent;


        public DeliveryRequestHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            state_background = itemView.findViewById(R.id.view_state_background);
            txt_delivery_time = itemView.findViewById(R.id.txt_delivery_time);
            txt_delivery_date = itemView.findViewById(R.id.txt_delivery_date);
            txt_delivery_address = itemView.findViewById(R.id.txt_address);
            //txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_state = itemView.findViewById(R.id.txt_state);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);
            txt_restaurant_address = itemView.findViewById(R.id.txt_restaurant_address);
            image_state = itemView.findViewById(R.id.image_state);
            mapButton = itemView.findViewById(R.id.imageButton_map);
            routeDistance = itemView.findViewById(R.id.RouteDistance);

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DeliveryRequest clickedDeliveryRequest = getItem(getAdapterPosition());
                    Log.d("AAA", ""+getAdapterPosition());
                    //clickedDeliveryRequest.setId(getSnapshots().getSnapshot(getAdapterPosition()).getKey());
                    String restaurant_addr = clickedDeliveryRequest.getRestaurant_address();
                    String customer_addr = clickedDeliveryRequest.getAddress();
                    if(restaurant_addr==null)restaurant_addr="unknown";
                    if(customer_addr==null)customer_addr="unknown";
                    intent = new Intent(fragment.getActivity().getApplicationContext(), LocationMapActivity.class);
                    Log.d("AAAA",restaurant_addr);
                    Log.d("AAAA",customer_addr);
                    intent.putExtra("restaurant_address", restaurant_addr);
                    intent.putExtra("customer_address", customer_addr);
                    fragment.getActivity().getApplicationContext().startActivity(intent);

                }
            });
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

        //Add DISTANCE
        double distance = 0;
        try{
             distance = Double.valueOf(model.getDistance());
             Log.d("AAAAAAAA","daje:"+distance);
        } catch (Exception e){
            Log.e("exception",e.getLocalizedMessage());
            if (distance != 0)
             distance = model.computeDistance(fragment.getActivity().getApplicationContext());
        }

        distance = distance / 1000; //FROM METERS TO KILOMETERS
        DecimalFormat df = new DecimalFormat("#");
        Log.d("computeDistance", "done:"+distance + ","+ df.format(distance));
        holder.routeDistance.setText("≈ "+df.format(distance)+" km");


        holder.txt_state.setText(model.getCurrentStateLocal());
        holder.txt_delivery_address.setText(model.getAddress());
        //holder.txt_customer_name.setText(model.getCustomer_name());
        holder.txt_restaurant_name.setText(model.getRestaurant_name());
        holder.txt_restaurant_address.setText(model.getRestaurant_address());
        switch (model.getCurrentState()) {
            case DeliveryRequest.STATE1:
                holder.state_background.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState1));
                holder.image_state.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp);
                holder.mapButton.setVisibility(View.GONE);
                break;
            case DeliveryRequest.STATE2:
                holder.state_background.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState2));
                holder.image_state.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                holder.mapButton.setVisibility(View.VISIBLE);
                break;
            case DeliveryRequest.STATE3:
                holder.state_background.setBackgroundColor(
                        fragment.getActivity().getResources().getColor(R.color.colorState4));
                holder.image_state.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
                holder.mapButton.setVisibility(View.GONE);

                break;
        }
        if (animationFlag == 0)
            runLayoutAnimation(recyclerView, 0);
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
