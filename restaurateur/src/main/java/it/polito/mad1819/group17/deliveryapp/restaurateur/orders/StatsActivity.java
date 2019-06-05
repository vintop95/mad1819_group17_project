package it.polito.mad1819.group17.deliveryapp.restaurateur.orders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class StatsActivity extends AppCompatActivity {

    private HashMap<Integer, Integer> orderPerHour;
    private Integer[] orders;
    public int sum;
    public ProgressBarHandler progressBarHandler;
    private ListView listView;
    private CardView popular_hour_card;

    private TextView main_hourInterval_tv;
    private TextView main_numOfOrders_tv;
    public ProgressBar main_progressBar;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        main_hourInterval_tv = findViewById(R.id.mainHoursInterval);
        main_numOfOrders_tv = findViewById(R.id.mainNumberOfOrders);
        main_progressBar = findViewById(R.id.mainProgressBarNumberOfOrder);
        progressBarHandler = new ProgressBarHandler(this);

        showBackArrowOnToolbar();
    }

    private void drawBarchart(Map<Integer, Integer> orderPerHour, int sum) {

        BarChart chart = findViewById(R.id.barchart);
        ArrayList barEntryList = new ArrayList();
        for (int i = 0; i < 24; i++)
            if (orderPerHour.get(i) != null)
                barEntryList.add(new BarEntry(orderPerHour.get(i), i));
            else
                barEntryList.add(new BarEntry(0, i));

        ArrayList hours = new ArrayList();
        for (int i = 0; i < 24; i++)
            hours.add(i + ":00");

        BarDataSet bardataset = new BarDataSet(barEntryList, "Orders per hour");
        bardataset.setColor(getResources().getColor(R.color.colorPrimaryDark));
        chart.setDescription("");
        chart.getAxisRight().setEnabled(false);
        chart.animateY(2000);
        BarData data = new BarData(hours, bardataset);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });
        chart.setData(data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView = findViewById(R.id.listview_stats);

        populateOrdersPerHour();

    }

    public void populateOrdersPerHour() {

        orderPerHour = new HashMap<Integer, Integer>();
        orders = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        sum = 0;

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
                    sum = 0;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        str_hour = ds.child("delivery_time").getValue(String.class); //HOUR ("hh:mm" format)
                        if (str_hour != null && str_hour.contains(":")) {
                            hour = Integer.parseInt(str_hour.split(":")[0]);//HOUR (hh format)
                            if (hour >= 0 && hour < 24) {
                                Log.d("populateOrders...", "hour:" + hour);
                                orders[hour] = orders[hour] + 1;
                                sum = sum + 1;
                                Log.d("populateOrders...", "orders[" + hour + "]=" + orders[hour]);
                            }
                        }
                    }
                    for (int i = 0; i < 24; i++) {
                        orderPerHour.put(i, orders[i]);  //making an HashMap (Hour,#_of_order)
                    }
                    orderPerHour = sortByValue(orderPerHour); //SORTING

                    Iterator<Integer> iterator = orderPerHour.values().iterator();  //REMOVING 0 VALUES
                    while (iterator.hasNext()) {                                    //REMOVING 0 VALUES
                        if (iterator.next() == 0f) {                                //REMOVING 0 VALUES
                            iterator.remove();                                      //REMOVING 0 VALUES
                        }
                    }

                    if (!orderPerHour.isEmpty()) {
                        Map.Entry<Integer, Integer> entry = orderPerHour.entrySet().iterator().next();
                        Log.d("populateOrders_sum", "" + sum);
                        updateListView();
                        drawBarchart(orderPerHour, sum);
                    } else {
                        popular_hour_card = findViewById(R.id.materialCardView);
                        popular_hour_card.setAlpha(0.2f);
                        Toast.makeText(StatsActivity.this, "No Orders Available!", Toast.LENGTH_SHORT).show();
                    }
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
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<Map.Entry<Integer, Integer>>(hm.entrySet());
        Comparator<Integer> cmp = Collections.reverseOrder();

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {

                return -((o1.getValue()).compareTo(o2.getValue()));// - to reverse the order!
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    public void updateListView() {

        Log.d("XYWZ", orderPerHour.toString());
        ArrayList<Hour> hourArrayList = new ArrayList<Hour>();
        for (Map.Entry<Integer, Integer> entry : orderPerHour.entrySet()) {
            hourArrayList.add(new Hour(entry.getKey(), entry.getValue()));
        }
        Log.d("XYWZ", hourArrayList.toString());
        HourAdapter arrayAdapter = new HourAdapter(
                this, R.layout.hour_orders_layout_item, hourArrayList);

        listView.setAdapter(arrayAdapter);
    }


    public class Hour {
        private Integer m_hour;
        private Integer m_numberOfOrders;
        private DecimalFormat format;

        public Hour(Integer m_hour, Integer m_numberOfOrders) {
            this.m_hour = m_hour;
            this.m_numberOfOrders = m_numberOfOrders;
            format = new DecimalFormat("#.##");
        }

        public Hour() {
            format = new DecimalFormat("#.##");
        }

        public Integer getHour() {
            return m_hour;
        }

        public String getHourFormatted() {
            String returnValue = "" + m_hour + ":00/" + m_hour + ":59";
            return returnValue;
        }

        public void setHour(Integer m_hour) {
            this.m_hour = m_hour;
        }

        public Integer getNumberOfOrders() {
            return m_numberOfOrders;
        }

        public String getNumberOfOrdersFormatted() {
            float percentage = (m_numberOfOrders * 100) / sum;
            String returnValue = "" + format.format(percentage) + "% (" + m_numberOfOrders + " ordini)";
            return returnValue;
        }

        public String getNumberOfOrdersonTotalFormatted() {
            float percentage = (m_numberOfOrders * 100) / sum;
            String returnValue = "" + m_numberOfOrders + " ordini su " + sum + " totali (" + format.format(percentage) + "%)";
            return returnValue;
        }

        public void setNumberOfOrders(Integer m_numberOfOrders) {
            this.m_numberOfOrders = m_numberOfOrders;
        }
    }

    public class HourAdapter extends ArrayAdapter<Hour> {
        public HourAdapter(Context context, int resource) {
            super(context, resource);
        }

        public HourAdapter(Context context, int resource, ArrayList<Hour> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hour_orders_layout_item, null);

            TextView hour_tv = convertView.findViewById(R.id.hoursInterval);
            TextView order_tv = convertView.findViewById(R.id.numberOfOrders);
            ProgressBar progressBar = convertView.findViewById(R.id.progressBarNumberOfOrder);

            Hour h = getItem(position);

            order_tv.setText(h.getNumberOfOrdersFormatted());
            hour_tv.setText(h.getHourFormatted());

            int progress = (100 * h.getNumberOfOrders()) / sum;
            progressBar.setProgress(progress);

            //MAIN CARDVIEW FILL-UP
            if (position == 0) {
                main_hourInterval_tv.setText(h.getHourFormatted());
                main_numOfOrders_tv.setText(h.getNumberOfOrdersonTotalFormatted());
                main_progressBar.setProgress(progress);
            }

            return convertView;
        }
    }

    private void showBackArrowOnToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
