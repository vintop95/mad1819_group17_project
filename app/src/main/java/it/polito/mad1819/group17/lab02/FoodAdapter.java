package it.polito.mad1819.group17.lab02;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ModelFood> mList;

    public void updateList(ArrayList<ModelFood> updatedData) {
        mList = updatedData;
        notifyDataSetChanged();
    }

    FoodAdapter(Context context, ArrayList<ModelFood> list){
        mContext = context;
        mList = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from((mContext));

        View view = layoutInflater.inflate(R.layout.rv_food_items,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {

        ModelFood fooditem = mList.get(pos);

        ImageView image = viewHolder.item_image;
        TextView name,place,price;

        name = viewHolder.item_name;
        place=viewHolder.item_place;
        price=viewHolder.item_price;

        image.setImageResource(fooditem.getImage());
        name.setText(fooditem.getName());
        place.setText(fooditem.getPlace());
        price.setText(fooditem.getPrice());

    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView item_image;
        TextView item_name, item_place, item_price;

        public ViewHolder(View itemView){
            super(itemView);

            item_image = itemView.findViewById(R.id.food_item_image);
            item_name = itemView.findViewById(R.id.food_item_name);
            item_price = itemView.findViewById(R.id.food_item_price);
            item_place = itemView.findViewById(R.id.food_item_place);
        }
    }
}
