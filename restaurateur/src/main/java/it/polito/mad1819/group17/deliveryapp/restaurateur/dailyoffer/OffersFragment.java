package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.content.Intent;
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
import android.widget.ProgressBar;

import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.io.Serializable;

import it.polito.mad1819.group17.deliveryapp.restaurateur.MainActivity;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.ProgressBarHandler;
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
    public final static int ADD_FOOD_REQUEST = 0;
    public final static int MODIFY_FOOD_REQUEST = 1;

    private FoodAdapter mAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddOffer;
    public ProgressBarHandler progressBarHandler;

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        progressBarHandler.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
        progressBarHandler.hide();
    }

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
        recyclerView.setHasFixedSize(false);

        // Create your layout manager
        // from Linear/Grid/StaggeredLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set the firebase adapter (automatically updates)
        setFirebaseRecycler();

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

        progressBarHandler = new ProgressBarHandler(getContext());
        progressBarHandler.show();
    }

    /////// FIREBASE MGMT ////////
    public void addFoodInList(FoodModel newFood){
        Log.d(TAG, "Item " + newFood.id + " added");
        FoodModelUtil.pushToFirebase(newFood);
    }

    public void modifyItem(FoodModel newFood){
        Log.d(TAG, "Item " + newFood.id + " modified");
        FoodModelUtil.modifyInFirebase(newFood);
    }

    private void setFirebaseRecycler(){
        Query query = FoodModelUtil.getDailyOffersRef();

        FirebaseRecyclerOptions<FoodModel> options =
                new FirebaseRecyclerOptions.Builder<FoodModel>()
                        .setQuery(query, FoodModel.class)
                        .build();

        mAdapter = new FoodAdapter(this, options);
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }
    ////////////////////////////

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
//        bundle.putInt("pos", foodToModify.pos);
        bundle.putSerializable("food", foodToModify);
        intent.putExtra("args", bundle);

        startActivityForResult(intent, MODIFY_FOOD_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FOOD_REQUEST) {
            if (data != null){
                FoodModel addedFood = (FoodModel) data.getSerializableExtra("food");
                if(addedFood != null) addFoodInList(addedFood);
            }
            btnAddOffer.setEnabled(true);
        }else if (requestCode == MODIFY_FOOD_REQUEST) {
            if (data != null){
                FoodModel modifiedFood = (FoodModel) data.getSerializableExtra("food");
                if(modifiedFood != null){
                    modifyItem(modifiedFood);
                }
            }
            btnEditItem.setEnabled(true);
        }
    }
}
