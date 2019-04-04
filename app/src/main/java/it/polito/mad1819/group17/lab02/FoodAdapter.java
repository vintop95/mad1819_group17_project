package it.polito.mad1819.group17.lab02;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {
    private static final String TAG = FoodAdapter.class.getName();
    // NAME CONVENTION: private fields are named "m<fieldName>"
    private List<ModelFood> mFoodList;
    private LayoutInflater mInflater;

    public void updateList(List<ModelFood> updatedData) {
        mFoodList = updatedData;
        notifyDataSetChanged();
    }

    FoodAdapter(Context context, List<ModelFood> list){
        Log.d(TAG, "created");
        mFoodList = list;
        mInflater = LayoutInflater.from(context);
    }

    // PHASE 1 OF PROTOCOL: build FoodHolder (ViewHolder)
    // and link rv_food_item layout to FoodAdapter (Adapter)
    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View foodItemView = mInflater.inflate(
                R.layout.rv_food_item, viewGroup, false);
        return new FoodHolder(foodItemView);
    }

    // PHASE 2 OF PROTOCOL: fetch data from model and set data on FoodHolder (ViewHolder)
    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int pos) {
        Log.d(TAG, "onBindViewHolder " + pos);

        ModelFood currentFoodItem = mFoodList.get(pos);
        holder.setData(currentFoodItem, pos);
    }

    @Override
    public int getItemCount() {
        // Log.d(TAG,"itemCount: " + mFoodList.size());
        return mFoodList.size();
    }

    class FoodHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemName, itemPlace, itemPrice;
        int pos;
        ModelFood currentFoodItem;

        public FoodHolder(@NonNull View itemView){
            super(itemView);
            // Parameters of rv_food_item-layout
            itemImage = itemView.findViewById(R.id.food_item_image);
            itemName = itemView.findViewById(R.id.food_item_name);
            itemPlace = itemView.findViewById(R.id.food_item_place);
            itemPrice = itemView.findViewById(R.id.food_item_price);
            Log.d(TAG, "Holder " + itemName.getText().toString() + " created");
        }

        public void setData(ModelFood currentFoodItem, int pos){
            itemImage.setImageResource(currentFoodItem.getImage());
            itemName.setText(currentFoodItem.getName());
            itemPlace.setText(currentFoodItem.getPlace());
            itemPrice.setText(currentFoodItem.getPrice());
            this.pos = pos;
            this.currentFoodItem = currentFoodItem;
        }
    }
}
