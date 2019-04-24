package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad1819.group17.deliveryapp.deliveryman.R;


public class DeliveryRequestsFragment extends Fragment {

    public final static int SHOW_DETAILS_REQUEST = 1;

    private DeliveryRequestsAdapter mAdapter;

    //private OnFragmentInteractionListener mListener;

    public DeliveryRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(getContext(), "/restaurateurs/" + FirebaseAuth.getInstance().getUid() + "/orders",
                Toast.LENGTH_LONG).show();

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
                .findFragmentByTag(DeliveryRequestsFragment.class.getName()));
        RecyclerView recyclerView = view.findViewById(R.id.rv_delivery_requests);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
