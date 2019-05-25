package it.polito.mad1819.group17.deliveryapp.deliveryman.statistics;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import it.polito.mad1819.group17.deliveryapp.common.orders.DeliveryRequest;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;


public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private double km_accepted;
    private double km_delivered;
    private double km_assigned;
    private double pb_accepted;

    private TextView km_acceptedTv;
    private TextView km_deliveredTv;
    private TextView km_assignedTv;
    private TextView counter;

    private TextView counter_red;
    private TextView counter_yellow;
    private TextView counter_green;

    private TextView numberOfCals;
    private ProgressBar pieChart;

    private ProgressBarHandler progressBarHandler;
    private SwipeRefreshLayout pullToRefresh;


    private DecimalFormat df;
    int count;
    int count_assigned;
    int count_delivered;
    int count_accepted;

    // TODO: Rename and change types of parameters

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();

        progressBarHandler.show();
        updateStats();
        updateChart();
        progressBarHandler.hide();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        progressBarHandler = new ProgressBarHandler(getContext());

        km_accepted =0;
        km_delivered = 0;
        km_assigned = 0;
        count = 0;
        int count_assigned=0;
        int count_delivered=0;
        int count_accepted=0;
        df = new DecimalFormat("#.###");

        locateViews(view);

        retrieveKms(false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStats();
                updateChart();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void locateViews(View view){
        km_acceptedTv = view.findViewById(R.id.kmAccepted);
        km_deliveredTv = view.findViewById(R.id.kmDelivered);
        km_assignedTv = view.findViewById(R.id.kmAssigned);
        counter = view.findViewById(R.id.requestCounter);
        numberOfCals = view.findViewById(R.id.number_of_km);
        pieChart = view.findViewById(R.id.stats_progressbar);

        counter_green = view.findViewById(R.id.requestCounterGreen);
        counter_yellow = view.findViewById(R.id.requestCounterYellow);
        counter_red = view.findViewById(R.id.requestCounterRed);
    }

    public void retrieveKms(boolean isRefreshing){

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("deliverymen")
                .child(FirebaseAuth.getInstance().getUid())
                .child("delivery_requests");


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*km_accepted =0;
                km_delivered = 0;
                km_assigned = 0;
                count = 0;*/

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    count += 1;
                    String state = DeliveryRequest.STATE1;
                    if(ds.child("state_stateTime").child("state3").getValue(String.class)!=null)
                        state = DeliveryRequest.STATE3;
                    else if(ds.child("state_stateTime").child("state2").getValue(String.class)!=null)
                        state = DeliveryRequest.STATE2;

                    switch (state) {
                        case DeliveryRequest.STATE1:
                            //ASSIGNED (red)
                            km_assigned +=  Double.valueOf(ds.child("distance").getValue(String.class))/1000;//FROM METERS TO KILOMETERS
                            count_assigned+=1;

                            break;
                        case DeliveryRequest.STATE2:
                            //ACCEPTED (yellow)
                            km_accepted +=  Double.valueOf(ds.child("distance").getValue(String.class))/1000;//FROM METERS TO KILOMETERS
                            count_accepted +=1;

                            break;
                        case DeliveryRequest.STATE3:
                            //DELIVERED (green)
                            km_delivered +=  Double.valueOf(ds.child("distance").getValue(String.class))/1000;//FROM METERS TO KILOMETERS
                            count_delivered +=1;
                            break;
                    }
                }
                pb_accepted = km_accepted / (km_delivered+km_assigned+km_accepted);

                updateStats();
                updateChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(isRefreshing) pullToRefresh.setRefreshing(false);
    }


    public void updateStats(){

        // Update the text in a center of the chart:

        numberOfCals.setText(df.format(km_delivered) + "km / " + df.format(km_delivered+km_accepted+km_assigned)+"km");

        // Calculate the slice size and update the pie chart:

        //Update stats:
        km_acceptedTv.setText(df.format(km_accepted)+" km");
        km_deliveredTv.setText(df.format(km_delivered)+" km");
        km_assignedTv.setText(df.format(km_assigned)+" km");
        counter.setText(""+count);

        counter_green.setText(""+count_delivered);
        counter_yellow.setText(""+count_accepted);
        counter_red.setText(""+count_assigned);

    }

    public void updateChart(){
        double base = km_delivered+km_assigned+km_accepted;
        pb_accepted = km_delivered / base;
        //Log.d("double_kms",""+km_accepted+","+km_assigned+","+km_delivered);
        //Log.d("double_base",""+base);
        //Log.d("double_d",""+ pb_accepted);
        int progress = 0;
        progress = (int) (pb_accepted * 100);
        Log.d("progress",""+progress);
        synchronized (this) {
            pieChart.setProgress(0);
            pieChart.setProgress(progress);
        };
    }
}
