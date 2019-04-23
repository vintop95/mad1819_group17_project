package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.Locale;

import it.polito.mad1819.group17.restaurateur.R;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;

// https://github.com/firebase/FirebaseUI-Android/tree/master/database#using-firebaseui-to-populate-a-recyclerview
public class FoodAdapter extends FirebaseRecyclerAdapter<FoodModel, FoodAdapter.FoodHolder> {
    private static final String TAG = FoodAdapter.class.getName();

    private OffersFragment mOffersFragment;

    public FoodAdapter(OffersFragment of, FirebaseRecyclerOptions options){
        super(options);
        mOffersFragment = of;
    }

    // PHASE 1 OF PROTOCOL: build FoodHolder (ViewHolder)
    // and link rv_food_item layout to FoodAdapter (Adapter)
    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View foodItemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.rv_food_item, viewGroup, false);
        return new FoodHolder(foodItemView);
    }

    // PHASE 2 OF PROTOCOL: fetch data from model and set data on FoodHolder (ViewHolder)
    @Override
    protected void onBindViewHolder(@NonNull FoodHolder holder, int pos, @NonNull FoodModel model) {
        // Log.d(TAG, "onBindViewHolder " + pos);

        holder.setData(model, pos);
        holder.setListeners();
    }

    @Override
    public void onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
        super.onDataChanged();
        mOffersFragment.progressBarHandler.hide();
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
        TextView itemName, itemPlace, itemPrice, itemAvailableQty;
//        int pos;
        FoodModel currentFoodItem;

        public FoodHolder(@NonNull View itemView){
            super(itemView);
            // Parameters of rv_food_item-layout
            itemPhoto = itemView.findViewById(R.id.img_food_photo);
            itemName = itemView.findViewById(R.id.txt_food_name);
            itemPlace = itemView.findViewById(R.id.txt_food_description);
            itemPrice = itemView.findViewById(R.id.txt_food_price);
            itemAvailableQty = itemView.findViewById(R.id.txt_food_available_qty);
            itemImgModify = itemView.findViewById(R.id.img_food_modify);
            itemImgDelete = itemView.findViewById(R.id.img_food_delete);
            // Log.d(TAG, "Holder " + itemName.getText().toString() + " created");
        }

        public void setData(FoodModel currentFoodItem, int pos){
            if(currentFoodItem.photo != null){
                Bitmap bmp = PrefHelper.stringToBitMap(currentFoodItem.photo);
                itemPhoto.setImageBitmap(bmp);
            }
            itemName.setText(currentFoodItem.name);
            itemPlace.setText(currentFoodItem.description);
            itemPrice.setText(FoodModelUtil.getPriceFormatted(currentFoodItem.price));
            itemAvailableQty.setText(
                    String.format(Locale.getDefault(), "%d", currentFoodItem.availableQty));
//            this.pos = pos;
            this.currentFoodItem = currentFoodItem;
        }


        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.img_food_modify:
                    v.setEnabled(false);
                    mOffersFragment.openFoodDetailsActivityModify(currentFoodItem, v);
                    break;
                case R.id.img_food_delete:
                    deleteItem(currentFoodItem);
                    break;
            }
        }


        public void setListeners(){
            itemImgModify.setOnClickListener(FoodHolder.this);
            itemImgDelete.setOnClickListener(FoodHolder.this);
        }

        public void deleteItem(FoodModel food){
            FoodModelUtil.removeFromFirebase(mOffersFragment.getContext(), food);
            Log.d(TAG, "Item " + food.id + " removed");
        }
    }
}

