/*
package it.polito.mad1819.group17.lab02;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


*/
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OffersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 *//*

public class OffersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FoodAdapter adapter;

    RecyclerView recyclerView;

    ArrayList<ModelFood> foodsList;

    private OnFragmentInteractionListener mListener;

    public OffersFragment() {
        // Required empty public constructor
    }

    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OffersFragment.
     *//*

    // TODO: Rename and change types and number of parameters
    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        Log.d("aa", "offersfragment created ");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("aaa", "onCreate: ");
        foodsList = new ArrayList<>();
        foodsList.add(new ModelFood(R.drawable.food_photo_1,"hamurger","20e", "carne 200g, provola, bacon, insalata" ));
        foodsList.add(new ModelFood(R.drawable.food_photo_1,"spaghetti","10e", "spaghetti, pomodoro" ));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("aa", "onCreateView: ");

        adapter = new FoodAdapter(getContext(),foodsList);
        return inflater.inflate(R.layout.fragment_offers, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("aa", "onActivityCreated: ");
        recyclerView = getActivity().findViewById(R.id.rv);
*/
/*
        foodsList = new ArrayList<>();
        foodsList.add(new ModelFood(R.drawable.food_photo_1,"hamurger","20e", "carne 200g, provola, bacon, insalata" ));
        foodsList.add(new ModelFood(R.drawable.food_photo_1,"spaghetti","10e", "spaghetti, pomodoro" ));
*//*



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager rvLiLayoutManager = layoutManager;
        recyclerView.setLayoutManager(rvLiLayoutManager);


        recyclerView.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    */
/*    if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*//*

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //  mListener = null;
    }

    */
/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}*/

package it.polito.mad1819.group17.lab02.dailyoffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

/**
 * IMPLEMENTING RecyclerView
 * 1. Add RecyclerView dependency to build.gradle if needed
 * 2. Add RecyclerView to layout (fragment_offers.xml)
 * 3. Create XML layout for item (rv_food_item.xml)
 * 4. Extend RecyclerView.Adapter (FoodAdapter)
 * 5. Extend RecyclerView.ViewHolder (FoodAdapter.FoodHolder)
 * 6. In Activity onCreate(), create RecyclerView with adapter
 *    and layout manager (OffersFragment.onViewCreated())
 */

public class OffersFragment extends Fragment {
    private static final String TAG = OffersFragment.class.getName();
    private static final PrefHelper prefHelper = PrefHelper.getInstance();
    public static final String PREF_FOOD_LIST_SIZE = "PREF_FOOD_LIST_SIZE";

    FoodAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton btnAddOffer;
    List<ModelFood> foodList = new ArrayList<>();

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

        // Set your adapter
        adapter = new FoodAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Set add button listener
        btnAddOffer = view.findViewById(R.id.btn_add_offer);

        // TODO: test add object
        Bitmap img1bmp = BitmapFactory.decodeResource(getResources(), R.drawable.food_photo_1);
        String img1 = PrefHelper.bitMapToString(img1bmp);
        ModelFood testFood = new ModelFood(adapter.getItemCount(), "Crispy bacon",
                "carne 500g, provolazza, bacon, insalata", img1,
                55.0, 3);
        btnAddOffer.setOnClickListener(e ->{
            addFoodInList(adapter.getItemCount(), testFood);
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

    public void addFoodInList(int newPos, ModelFood newFood){
        Log.d(TAG, "Item in pos " + newPos + " added");
        try {
            foodList.add(newPos, newFood);
        }catch(IndexOutOfBoundsException e){
            foodList.add(newFood);
        }

        newFood.saveToPref();
        prefHelper.putLong(PREF_FOOD_LIST_SIZE, newPos+1);

        if(adapter != null){
            adapter.notifyItemInserted(newPos);
            adapter.notifyItemRangeChanged(0, newPos+1);
        }
    }

    // Fetching items, passing in the View they will control.
    private List<ModelFood> reloadUpdatedFoodListFromPref(){
        if(foodList != null){
            foodList.clear();
        }else{
            foodList = new ArrayList<>();
        }

        int foodListSize = loadUpdatedFoodListSizeFromPref();
        for(int i = 0; i<foodListSize; i++){
            ModelFood food = ModelFood.loadFromPref(Long.valueOf(i));
            // if we go involuntarily outside the bounds
            if(food == null){
                prefHelper.putLong(PREF_FOOD_LIST_SIZE, i);
                break;
            }
            addFoodInList(i, food);
        }

        return foodList;
    }

    // https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences/28107791
    private static int loadUpdatedFoodListSizeFromPref(){
        // if not found in prefHeper returns 0
        return (int) prefHelper.getLong(PREF_FOOD_LIST_SIZE);
    }

}
