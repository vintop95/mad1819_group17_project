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
   /* private HashMap<Integer,Integer> orderPerHour;
    private Integer[] orders;
    boolean click = true;
    private PopupWindow popUp;*/


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


    /*public void populateOrdersPerHour (){

        orderPerHour = new HashMap <Integer,Integer> ();
        orders = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        if (FirebaseAuth.getInstance().getUid() != null) {
            progressBarHandler.show();

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("restaurateurs")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("orders");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String str_hour;
                    int hour;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        str_hour = ds.child("delivery_time").getValue(String.class); //HOUR ("hh:mm" format)
                        if(str_hour != null && str_hour.contains(":")) {
                            hour = Integer.parseInt(str_hour.split(":")[0]);//HOUR (hh format)
                            if (hour >= 0 && hour < 24){
                                Log.d("populateOrders...","hour:"+hour);
                                orders[hour]=orders[hour]+1;
                                Log.d("populateOrders...","orders[" + hour + "]=" + orders[hour]);
                            }
                        }
                    }
                    for (int i=0;i<24;i++){
                        orderPerHour.put(i,orders[i]);  //making an HashMap (Hour,#_of_order)
                    }
                    orderPerHour = sortByValue(orderPerHour); //SORTING
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            progressBarHandler.hide();
        }
    }

    // function to sort hashmap by values
    public HashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Integer> > list =
                new LinkedList<Map.Entry<Integer, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
*/
}