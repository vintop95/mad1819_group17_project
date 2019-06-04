package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import java.util.Locale;

import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModel;
import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModelUtil;
import it.polito.mad1819.group17.deliveryapp.common.utils.MadFirebaseRecyclerAdapter;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

// https://github.com/firebase/FirebaseUI-Android/tree/master/database#using-firebaseui-to-populate-a-recyclerview
public class FoodAdapter extends MadFirebaseRecyclerAdapter<FoodModel, FoodAdapter.FoodHolder> {
    private static final String TAG = FoodAdapter.class.getName();

    private OffersFragment mOffersFragment;
    private RecyclerView recyclerView;
    private int animationFlag = 0;

    public FoodAdapter(OffersFragment of, FirebaseRecyclerOptions<FoodModel> options, RecyclerView recyclerView) {
        super(options, false);
        mOffersFragment = of;
        this.recyclerView = recyclerView;
    }

    // PHASE 1 OF PROTOCOL: build FoodHolder (AvailableDeliverymanHolder)
    // and link rv_food_item layout to FoodAdapter (Adapter)
    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View foodItemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.rv_food_item, viewGroup, false);
        return new FoodHolder(foodItemView);
    }

    // PHASE 2 OF PROTOCOL: fetch data from model and set data on FoodHolder (AvailableDeliverymanHolder)
    @Override
    protected void onBindViewHolder(@NonNull FoodHolder holder, int pos, @NonNull FoodModel model) {
        // Log.d(TAG, "onBindViewHolder " + pos);

        holder.setData(model, pos);
        holder.setListeners();
        if (animationFlag == 0)
            runLayoutAnimation(recyclerView, 0);
    }

    @Override
    public void onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
        super.onDataChanged();
        mOffersFragment.progressBarHandler.hide();
        stopListening();
    }

    @Override
    public void onError(DatabaseError e) {
        // Called when there is an error getting data. You may want to update
        // your UI to display an error message to the user.
        // ...
        Toast.makeText(mOffersFragment.getContext(),
                e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // NOT COMPATIBLE WITH FIREBASE RECYCLER ADAPTER
//    @Override
//    public int getItemCount() {
//        // Log.d(TAG,"itemCount: " + mFoodList.size());
//        return mFoodList.size();
//    }

    public class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemPhoto, itemImgModify, itemImgDelete;
        TextView itemName, itemPlace, itemPrice, itemAvailableQty, itemTotalOrderedQty;
        RatingBar rb_mean_rate;
        FoodModel currentFoodItem;

        public FoodHolder(@NonNull View itemView) {
            super(itemView);
            // Parameters of rv_food_item-layout
            itemPhoto = itemView.findViewById(R.id.img_food_photo);
            itemName = itemView.findViewById(R.id.txt_food_name);
            itemPlace = itemView.findViewById(R.id.txt_food_description);
            itemPrice = itemView.findViewById(R.id.txt_food_price);
            itemAvailableQty = itemView.findViewById(R.id.txt_food_available_qty);
            itemTotalOrderedQty = itemView.findViewById(R.id.txt_food_total_ordered_qty);
            itemImgModify = itemView.findViewById(R.id.img_food_modify);
            itemImgDelete = itemView.findViewById(R.id.img_food_delete);
            rb_mean_rate = itemView.findViewById(R.id.rb_mean_rate);
        }

        public void setData(FoodModel currentFoodItem, int pos) {

            // Load image
            if (!TextUtils.isEmpty(currentFoodItem.image_path)) {
                Glide.with(itemPhoto.getContext())
                        .load(currentFoodItem.image_path)
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
                                Log.v("ProfileFragment", "Image load FOOD_OK");
                                return false; // leave false
                            }
                        }).into(itemPhoto);
            } else {
                Glide.with(itemPhoto.getContext()).clear(itemPhoto);
            }

            itemName.setText(currentFoodItem.name);
            itemPlace.setText(currentFoodItem.description);
            itemPrice.setText(FoodModelRestaurateurUtil.getPriceFormatted(currentFoodItem.price));
            itemAvailableQty.setText(
                    String.format(Locale.getDefault(), "%d", currentFoodItem.availableQty));
            itemTotalOrderedQty.setText(
                    String.format(Locale.getDefault(), "%d", currentFoodItem.totalOrderedQty));
            if (currentFoodItem.total_rate != null && currentFoodItem.number_of_rates != null)
                rb_mean_rate.setRating(currentFoodItem.total_rate / currentFoodItem.number_of_rates);
            else
                rb_mean_rate.setRating(0);
            this.currentFoodItem = currentFoodItem;

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_food_modify:
                    v.setEnabled(false);
                    mOffersFragment.openFoodDetailsActivityModify(currentFoodItem, v, false);
                    break;
                case R.id.img_food_delete:
                    confirmDelete(currentFoodItem);
                    break;
            }
        }


        public void setListeners() {
            itemImgModify.setOnClickListener(FoodHolder.this);
            itemImgDelete.setOnClickListener(FoodHolder.this);
        }


        private void confirmDelete(FoodModel foodToDelete) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mOffersFragment.getContext());

            alertDialogBuilder.setTitle(R.string.warning_title);
            alertDialogBuilder.setMessage(R.string.delete_msg);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        deleteItem(foodToDelete);
                    }
            );

            alertDialogBuilder.setNegativeButton(android.R.string.no, (dialog, which) -> {
                        dialog.cancel();
                    }
            );

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        public void deleteItem(FoodModel food) {
            FoodModelRestaurateurUtil.removeFromFirebase(mOffersFragment.getContext(), food);
            Log.d(TAG, "Item " + food.id + " removed");
        }
    }
    private void runLayoutAnimation(final RecyclerView recyclerView, int type) {
        final Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_up);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        animationFlag = 1;
    }
}

