package it.polito.mad1819.group17.lab02;

/*import android.content.Context;
import android.net.Uri;*/

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;
    private ArrayList<Order> orders = new ArrayList<Order>();

    interface OrdersFragmentObserver {
        void notifyClickOnCardView(Object details);
    }

    public void exampleData() {
        HashMap<String, String> stateMap1 = new HashMap<String, String>();
        stateMap1.put(Order.STATE1, "19:20");
        stateMap1.put(Order.STATE2, "19:40");

        HashMap<String, String> stateMap2 = new HashMap<String, String>();
        stateMap2.put(Order.STATE1, "20:20");

        HashMap<String, String> stateMap3 = new HashMap<String, String>();
        stateMap3.put(Order.STATE1, "21:20");
        stateMap3.put(Order.STATE2, "21:40");
        stateMap3.put(Order.STATE3, "22:20");

        HashMap<String, Integer> orderContent = new HashMap<String, Integer>();
        orderContent.put("pizza margherita", new Integer(2));
        orderContent.put("acqua natuale", new Integer(1));
        orderContent.put("coca cola", new Integer(1));


        orders.add(new Order(1, "aaa", "1112223334", "20:14", "01/05/2019", stateMap2, orderContent));
        orders.add(new Order(2, "bb", "1112223666", "20:00", "02/05/2019", stateMap1, orderContent));
        orders.add(new Order(3, "ccc", "1112223777", "00:14", "03/05/2019", stateMap1, orderContent));
        orders.add(new Order(4, "aaa", "1112223000", "10:14", "02/05/2019", stateMap1, orderContent));
        orders.add(new Order(5, "aaddda", "1112223101", "11:45", "02/05/2019", stateMap2, orderContent));
        orders.add(new Order(6, "jj", "1112223354", "23:25", "02/05/2019", stateMap2, orderContent));
        orders.add(new Order(7, "kk", "1112223351", "21:14", "02/05/2019", stateMap2, orderContent));
        orders.add(new Order(8, "uu", "1112223102", "20:11", "01/05/2019", stateMap3, orderContent));
        orders.add(new Order(9, "auuuaa", "1112223999", "20:18", "01/05/2019", stateMap1, orderContent));
        orders.add(new Order(10, "rht", "1112223367", "20:26", "01/05/2019", stateMap3, orderContent));
        orders.add(new Order(11, "qqq", "1112223834", "22:14", "01/05/2019", stateMap3, orderContent));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // If you know that the size of each element in the recycler view will not change, then you specify this and performance will improve
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Define some data as example
        exampleData();

        OrdersAdapter.RecyclerViewClickListener listener = new OrdersAdapter.RecyclerViewClickListener(

        ) {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), " " + position, Toast.LENGTH_SHORT).show();

                Order selectedOrder = mAdapter.getOrders().get(position);
                //Toast.makeText(getContext(), " " + selectedOrder.getNumber(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_order", selectedOrder);
                intent.putExtra("bundle_selected_order", bundle);

                startActivityForResult(intent, 1);
                Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
            }
        };


        // Define the adapter for our data, then set bind it to the recycle view
        mAdapter = new OrdersAdapter(orders, listener);
        recyclerView.setAdapter(mAdapter);


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Toast.makeText(getContext(), "Position", Toast.LENGTH_SHORT).show();
        }
    }
}
