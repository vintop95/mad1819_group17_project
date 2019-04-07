package it.polito.mad1819.group17.lab02;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class OrdersFragment extends Fragment {

    private final static int SHOW_DETAILS_REQUEST = 1;

    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;
    private ArrayList<Order> orders = new ArrayList<Order>();



    private OrdersAdapter.RecyclerViewClickListener listener;


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


        orders.add(new Order(1, "aaa", "1112223334", "20:14", "01/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap2, orderContent, "pizza tagliata"));
        orders.add(new Order(2, "bb", "1112223666", "20:00", "02/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap1, orderContent, "tovaglioli"));
        orders.add(new Order(3, "ccc", "1112223777", "00:14", "03/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap1, orderContent, ""));
        orders.add(new Order(4, "aaa", "1112223000", "10:14", "02/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap1, orderContent, ""));
        orders.add(new Order(5, "aaddda", "1112223101", "11:45", "02/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap2, orderContent, ""));
        orders.add(new Order(6, "jj", "1112223354", "23:25", "02/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap2, orderContent, "ahahbaauaaiubajbibauboanonoaunonoau"));
        orders.add(new Order(7, "kk", "1112223351", "21:14", "02/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap2, orderContent, ""));
        orders.add(new Order(8, "uu", "1112223102", "20:11", "01/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap3, orderContent, ""));
        orders.add(new Order(9, "auuuaa", "1112223999", "20:18", "01/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap1, orderContent, "aivnaorivnairvnaoig   rvnanraòrnvajnvan"));
        orders.add(new Order(10, "rht", "1112223367", "20:26", "01/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap3, orderContent, ""));
        orders.add(new Order(11, "qqq", "1112223834", "22:14", "01/05/2019", "Via Federico Pesce, 6, 10138 Torino, TO", stateMap3, orderContent, ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);



        recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Define some data as example
        exampleData();

        listener = new OrdersAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Order selectedOrder = mAdapter.getOrders().get(position);
                Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable("orders", mAdapter.getOrders());
                bundle.putInt("position", position);

                intent.putExtra("args", bundle);

                startActivityForResult(intent, SHOW_DETAILS_REQUEST);
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getCurrentState() == Order.STATE3)
                    return 1;
                else
                    return o1.getDelivery_timestamp().compareTo(o2.getDelivery_timestamp());
            }
        });
        mAdapter = new OrdersAdapter(orders, listener, getContext());
        recyclerView.setAdapter(mAdapter);
        mAdapter.updateList(orders);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_DETAILS_REQUEST && resultCode == OrderDetailsActivity.STATE_CHANGED) {
            ArrayList<Order> updatedOrders = (ArrayList<Order>) data.getSerializableExtra("orders");
            this.orders = updatedOrders;
        }
    }
}
