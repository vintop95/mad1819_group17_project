package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad1819.group17.restaurateur.R;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;

/**
 * IMPLEMENTING RecyclerView
 * 1. Add RecyclerView dependency to build.gradle if needed
 * 2. Add RecyclerView to layout (fragment_offers.xml)
 * 3. Create XML layout for item (rv_food_item.xml)
 * 4. Extend RecyclerView.Adapter (FoodAdapter)
 * 5. Extend RecyclerView.ViewHolder (FoodAdapter.FoodHolder)
 * 6. In Activity onCreate(), create RecyclerView with mAdapter
 *    and layout manager (OffersFragment.onViewCreated())
 */

public class OffersFragment extends Fragment {
    private static final String TAG = OffersFragment.class.getName();
    public static final String PREF_FOOD_LIST_SIZE = "PREF_FOOD_LIST_SIZE";
    public final static int ADD_FOOD_REQUEST = 0;
    public final static int MODIFY_FOOD_REQUEST = 1;


    private FirebaseRecyclerAdapter adapter;

    FoodAdapter mAdapter;
    RecyclerView recyclerView;
    FloatingActionButton btnAddOffer;
    // TODO: make private?
    public List<FoodModel> foodList = new ArrayList<>();

    // @Nullable: It makes it clear that the method accepts null values,
    // and that if you override the method, you should also accept null values.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    // https://stackoverflow.com/questions/45827981/android-recyclerview-not-showing-list-items-in-a-fragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Log.d(TAG, "onViewCreated");

        // Bind your views
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        // Create your layout manager
        // from Linear/Grid/StaggeredLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch your items
        reloadUpdatedFoodListFromPref();

        // Set your mAdapter
        mAdapter = new FoodAdapter(getActivity(), this, foodList);
        recyclerView.setAdapter(mAdapter);

        // Set add button listener
        btnAddOffer = view.findViewById(R.id.btn_add_offer);

        ////////////// TODO: remove test add object
//        Bitmap img1bmp = BitmapFactory.decodeResource(getResources(), R.drawable.food_photo_1);
//        String img1 = PrefHelper.bitMapToStringLossJpg(img1bmp);
//        FoodModel testFood = new FoodModel(mAdapter.getItemCount(), "Crispy bacon",
//                "carne 500g, provolazza, bacon, insalata", img1,
//                55.0, 3);
        ////////////////////////////////////////////////////////////
        btnAddOffer.setOnClickListener(e -> {
            // TO PREVENT DOUBLE CLICK -> DOUBLE OPEN ACTIVITY
            btnAddOffer.setEnabled(false);
            openFoodDetailsActivityAdd();
        });

        // Hide floating button on scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(TAG, "view scrolled");
                if (dy > 0 && btnAddOffer.getVisibility() == View.VISIBLE) {
                    // Log.d(TAG, "btn hidden");
                    btnAddOffer.hide();
                } else if (dy <= 0 ) {
                    // Log.d(TAG, "btn shown");
                    btnAddOffer.show();
                }
            }
        });
    }

    public void addFoodInList(int newPos, FoodModel newFood){
        Log.d(TAG, "Item in pos " + newPos + " added");
        try {
            foodList.add(newPos, newFood);
        }catch(IndexOutOfBoundsException e){
            foodList.add(newFood);
        }

        newFood.saveToPref();
        PrefHelper.getInstance().putLong(PREF_FOOD_LIST_SIZE, newPos+1);

        if(mAdapter != null){
            mAdapter.notifyItemInserted(newPos);
            mAdapter.notifyItemRangeChanged(0, newPos+1);
        }
    }

    public void modifyItem(int pos, FoodModel newFood){
        Log.d(TAG, "Item in pos " + pos + " modified");
        foodList.set(pos, newFood);
        newFood.saveToPref();

        if(mAdapter != null){
            mAdapter.notifyItemChanged(pos);
        }
    }

    // Fetching items, passing in the View they will control.
    private List<FoodModel> reloadUpdatedFoodListFromPref(){

        Query query = FirebaseDatabase.getInstance().getReference().child("dailyOffer");

        FirebaseRecyclerOptions<ColorSpace.Model> options =
                new FirebaseRecyclerOptions.Builder<FoodModel>()
                        .setQuery(query, new SnapshotParser<FoodModel>() {
                            @NonNull
                            @Override
                            public FoodModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new FoodModel(snapshot.child("position").getValue().toInteger(),
                                        snapshot.child("title").getValue().toString(),
                                        snapshot.child("desc").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<FoodModel, FoodAdapter.FoodHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Model model) {
                holder.setTxtTitle(model.getmTitle());
                holder.setTxtDesc(model.getmDesc());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);

/*        if(foodList != null){
            foodList.clear();
        }else{
            foodList = new ArrayList<>();
        }

        int foodListSize = loadUpdatedFoodListSizeFromPref();
        for(int i = 0; i<foodListSize; i++){
            FoodModel food = FoodModel.loadFromPref(Long.valueOf(i));
            // if we go involuntarily outside the bounds
            if(food == null){
                PrefHelper.getInstance().putLong(PREF_FOOD_LIST_SIZE, i);
                break;
            }
            addFoodInList(i, food);
        }
        return foodList;*/
    }

    // https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences/28107791
    private int loadUpdatedFoodListSizeFromPref(){
        // if not found in prefHeper returns 0
        return (int) PrefHelper.getInstance().getLong(PREF_FOOD_LIST_SIZE);
    }

    ////////////////////// OPEN ACTIVITY //////////////////////////////////////////
    private void openFoodDetailsActivityAdd(){
        Intent intent = new Intent(getContext(), FoodDetailsActivity.class);

//        Bitmap img1bmp = BitmapFactory.decodeResource(getResources(), R.drawable.food_photo_1);
//        String img1 = PrefHelper.bitMapToStringLossJpg(img1bmp);
//        FoodModel testFood = new FoodModel(mAdapter.getItemCount(), "Crispy bacon",
//                "carne 500g, provolazza, bacon, insalata", img1,
//                55.0, 3);
        Bundle bundle = new Bundle();
        bundle.putInt("pos", mAdapter.getItemCount());
        intent.putExtra("args", bundle);

        startActivityForResult(intent, ADD_FOOD_REQUEST);
    }

    private View btnEditItem;

    public void openFoodDetailsActivityModify(FoodModel foodToModify, View v){
        btnEditItem = v;

        Intent intent = new Intent(getContext(), FoodDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("pos", (int) foodToModify.getIdLong());
        bundle.putSerializable("food", foodToModify);
        intent.putExtra("args", bundle);

        startActivityForResult(intent, MODIFY_FOOD_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FOOD_REQUEST) {
            if (data != null){
                FoodModel addedFood = (FoodModel) data.getSerializableExtra("food");
                int newPos = (int) addedFood.getIdLong();
                if(addedFood != null) addFoodInList(newPos, addedFood);
            }
            btnAddOffer.setEnabled(true);
        }else if (requestCode == MODIFY_FOOD_REQUEST) {
            if (data != null){
                FoodModel modifiedFood = (FoodModel) data.getSerializableExtra("food");
                if(modifiedFood != null){
                    modifyItem((int) modifiedFood.getIdLong(), modifiedFood);
                }
            }
            btnEditItem.setEnabled(true);
        }
    }
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
                //////
                //UNCOMMENT LINE BELOW AND FIND A WAY TO REFERS TO IT
                //////
               // mOffersFragment.openFoodDetailsActivityModify(currentFoodItem, v);

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

    //////
    //UNCOMMENT LINE BELOW AND FIND A WAY TO REFERS TO IT
    //////
   /* public void deleteItem(int pos){
        Log.d(TAG, "Item in pos " + pos + " removed");
        mFoodList.remove(pos);

        PrefHelper.getInstance().putLong(
                OffersFragment.PREF_FOOD_LIST_SIZE, getItemCount());

        for(int i = pos; i<getItemCount();i++){
            mFoodList.get(i).setId(i);
        }

        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }*/
}