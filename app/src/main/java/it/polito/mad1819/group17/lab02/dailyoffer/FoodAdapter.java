package it.polito.mad1819.group17.lab02.dailyoffer;

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

import it.polito.mad1819.group17.lab02.R;

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
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        // Log.d(TAG,"itemCount: " + mFoodList.size());
        return mFoodList.size();
    }

    class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImage, itemImgModify, itemImgDelete;
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
            itemImgModify = itemView.findViewById(R.id.food_img_modify);
            itemImgDelete = itemView.findViewById(R.id.food_img_delete);
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

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.food_img_modify:
                    //TODO: test
                    ModelFood testFood = new ModelFood(R.drawable.food_photo_1,"MODIFIED",
                            "55e", "carne 500g, provolazza, bacon, insalata");
                    modifyItem(pos, testFood);
                    break;
                case R.id.food_img_delete:
                    deleteItem(pos);
                    break;
            }
        }

        public void setListeners(){
            itemImgModify.setOnClickListener(FoodHolder.this);
            itemImgDelete.setOnClickListener(FoodHolder.this);
        }

        public void modifyItem(int pos, ModelFood newFood){
            Log.d(TAG, "Item in pos " + pos + " modified");
            mFoodList.set(pos, newFood);
            notifyItemChanged(pos);
        }

        // You can use notifyDataSetChanged() instead of notifyItemRemoved
        // and notifyItemRangeChanged but you lose the animation
        public void deleteItem(int pos){
            Log.d(TAG, "Item in pos " + pos + " removed");
            mFoodList.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, getItemCount());
        }
    }


}
