package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;
import it.polito.mad1819.group17.deliveryapp.deliveryman.utils.ProgressBarHandler;


public class DeliveryRequestsFragment extends Fragment {

    public final static int SHOW_DETAILS_REQUEST = 1;

    private DeliveryRequestsAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBarHandler progressBarHandler;
    private CardView button_stats;

    public DeliveryRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_requests, container, false);
        recyclerView=view.findViewById(R.id.rv_delivery_requests);

        progressBarHandler = new ProgressBarHandler(getContext());

        button_stats = view.findViewById(R.id.statsButton);
        button_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StatsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getUid() != null) {
            progressBarHandler.show();

            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("deliverymen")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("delivery_requests")
                    .orderByChild("sorting_field");

            FirebaseRecyclerOptions<DeliveryRequest> options = new FirebaseRecyclerOptions.Builder<DeliveryRequest>()
                    .setQuery(query, DeliveryRequest.class)
                    .build();

            mAdapter = new DeliveryRequestsAdapter(options, getFragmentManager()
                    .findFragmentByTag(DeliveryRequestsFragment.class.getName()),progressBarHandler);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mAdapter);
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (FirebaseAuth.getInstance().getUid() != null)
            mAdapter.stopListening();
    }


}
