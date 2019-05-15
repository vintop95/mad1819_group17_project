package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.utils.CategoryListView;

public class RestaurantsFragment extends Fragment {

    String[] categories;
    Integer[] imgid = {
            R.drawable.pizza,       //pizza
            R.drawable.hamburger,    //hamburger
            R.drawable.chicken,       //chicken
            R.drawable.vegan,        //vegan
            R.drawable.icecream,       //icecream
            R.drawable.dessert,       //dessert
            R.drawable.healthy,       //healthy
            R.drawable.japanese,       //japanese
            R.drawable.mexican,       //mexican
    };
    ListView lst;


    public RestaurantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = getResources().getStringArray(R.array.restaurant_types);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);


        lst = view.findViewById(R.id.list_view_category);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(getContext(),""+categories[i], Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), RestaurantsActivity.class);
                Bundle b = new Bundle();
                b.putString("category", Integer.toString(i)); //Your category selected
                intent.putExtras(b); //Put your category in the next Intent
                startActivity(intent);
            }
        });


        Log.d("aia", "onCreateView: "+categories.length+"///"+imgid.length);
        CategoryListView categoryListView = new CategoryListView(this.getActivity(),categories,imgid);
        lst.setAdapter(categoryListView);
        return view;
    }
}
