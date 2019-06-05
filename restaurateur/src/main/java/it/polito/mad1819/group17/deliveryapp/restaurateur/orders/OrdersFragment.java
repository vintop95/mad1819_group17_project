package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;


public class OrdersFragment extends Fragment {

    public final static int SHOW_DETAILS_REQUEST = 1;
    private OrdersAdapter mAdapter;
    public ProgressBarHandler progressBarHandler;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        progressBarHandler = new ProgressBarHandler(getContext());
        recyclerView = view.findViewById(R.id.rv_orders);
        fab = view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getUid() != null) {
            progressBarHandler.show();

            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("restaurateurs")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("orders")
                    .orderByChild("sorting_field");

            FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                    .setQuery(query, Order.class)
                    .build();

            mAdapter = new OrdersAdapter(options, getFragmentManager().findFragmentByTag(OrdersFragment.class.getName()), recyclerView);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mAdapter);
            mAdapter.startListening();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Statistics Activity
                Intent intent = new Intent(getContext(),StatsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (FirebaseAuth.getInstance().getUid() != null)
            mAdapter.stopListening();

        progressBarHandler.hide();
    }
}