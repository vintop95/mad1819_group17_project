package it.polito.mad1819.group17.lab02.dailyoffer;

import android.content.Context;
import android.graphics.Bitmap;
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
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {
    private static final String TAG = FoodAdapter.class.getName();
    // NAME CONVENTION: private fields are named "m<fieldName>"
    private Context mContext;
    private OffersFragment mOffersFragment;
    private List<FoodModel> mFoodList;
    private LayoutInflater mInflater;

    public void updateList(List<FoodModel> updatedData) {
        mFoodList = updatedData;
        notifyDataSetChanged();
    }

    FoodAdapter(Context context, OffersFragment of, List<FoodModel> list){
        Log.d(TAG, "created");
        mContext = context;
        mFoodList = list;
        mInflater = LayoutInflater.from(context);
        mOffersFragment = of;
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
        // Log.d(TAG, "onBindViewHolder " + pos);

        FoodModel currentFoodItem = mFoodList.get(pos);
        holder.setData(currentFoodItem, pos);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        // Log.d(TAG,"itemCount: " + mFoodList.size());
        return mFoodList.size();
    }

    class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemPhoto, itemImgModify, itemImgDelete;
        TextView itemName, itemPlace, itemPrice, itemAvailableQty;
        int pos;
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
            if(currentFoodItem.getPhoto() != null){
                Bitmap bmp = PrefHelper.stringToBitMap(currentFoodItem.getPhoto());
                itemPhoto.setImageBitmap(bmp);
            }
            itemName.setText(currentFoodItem.getName());
            itemPlace.setText(currentFoodItem.getDescription());
            itemPrice.setText(currentFoodItem.getPriceString());
            itemAvailableQty.setText(currentFoodItem.getAvailableQtyString());
            this.pos = pos;
            this.currentFoodItem = currentFoodItem;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.img_food_modify:
//                    Bitmap img1bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.food_photo_1);
//                    String img1 = PrefHelper.bitMapToStringLossJpg(img1bmp);
//                    FoodModel testFood = new FoodModel(pos, "MODIFIED",
//                            "carne 500g, provolazza, bacon, insalata", img1,
//                            55.0, 3);
                    v.setEnabled(false);
                    mOffersFragment.openFoodDetailsActivityModify(currentFoodItem, v);

                    break;
                case R.id.img_food_delete:
                    deleteItem(pos);
                    break;
            }
        }


        public void setListeners(){
            itemImgModify.setOnClickListener(FoodHolder.this);
            itemImgDelete.setOnClickListener(FoodHolder.this);
        }

        // You can use notifyDataSetChanged() instead of notifyItemRemoved
        // and notifyItemRangeChanged but you lose the animation
        public void deleteItem(int pos){
            Log.d(TAG, "Item in pos " + pos + " removed");
            mFoodList.remove(pos);

            PrefHelper.getInstance().putLong(
                    OffersFragment.PREF_FOOD_LIST_SIZE, getItemCount());

            for(int i = pos; i<getItemCount();i++){
                mFoodList.get(i).setId(i);
            }

            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, getItemCount());
        }
    }
}
