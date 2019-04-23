package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;

import it.polito.mad1819.group17.restaurateur.R;


public class OrdersFragment extends Fragment {

    public final static int SHOW_DETAILS_REQUEST = 1;
    private OrdersAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        /*Query query = FirebaseDatabase.getInstance()
                .getReference("/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders")
                .orderByChild("sorting_field")
                .startAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .endAt(FirebaseAuth.getInstance().getCurrentUser().getUid()+"\uf8ff");

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        mAdapter = new OrdersAdapter(options, getFragmentManager().findFragmentByTag(OrdersFragment.class.getName()));
        RecyclerView recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);*/

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("XXXX", "/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders");
        Toast.makeText(getContext(), "/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders", Toast.LENGTH_LONG).show();
        Query query = FirebaseDatabase.getInstance()
                .getReference("/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders")
                .orderByChild("sorting_field")
                /*.startAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .endAt(FirebaseAuth.getInstance().getCurrentUser().getUid()+"\uf8ff")*/;

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        mAdapter = new OrdersAdapter(options, getFragmentManager().findFragmentByTag(OrdersFragment.class.getName())/*getActivity(), listener*/);
        RecyclerView recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        /*Collections.sort(orders, new Comparator<Order>() {
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
        mAdapter.updateList(orders);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_DETAILS_REQUEST && resultCode == OrderDetailsActivity.STATE_CHANGED) {
            ArrayList<Order> updatedOrders = (ArrayList<Order>) data.getSerializableExtra("orders");
            this.orders = updatedOrders;
        }
        Log.d("XX", "ORDERS FRAGMENT onActivityResult");
    }*/
}