package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import it.polito.mad1819.group17.deliveryapp.deliveryman.R;

public class StatsActivity extends AppCompatActivity {

    private double km_accepted;
    private double km_delivered;
    private double km_assigned;

    private TextView km_acceptedTv;
    private TextView km_deliveredTv;
    private TextView km_assignedTv;
    private TextView counter;

    private DecimalFormat df;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        km_accepted =0;
        km_delivered = 0;
        km_assigned = 0;
        count = 0;
        df = new DecimalFormat("#.###");

          km_acceptedTv = findViewById(R.id.kmAccepted);
          km_deliveredTv = findViewById(R.id.kmDelivered);
          km_assignedTv = findViewById(R.id.kmAssigned);
          counter = findViewById(R.id.requestCounter);


        retrieveKms();
        showBackArrowOnToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateChart();
    }

    public void updateChart(){

        Log.d("Valuesss",""+count+":"+km_delivered+","+km_accepted+","+km_assigned);
        // Update the text in a center of the chart:
        TextView numberOfCals = findViewById(R.id.number_of_km);
        numberOfCals.setText(df.format(km_delivered) + "km / " + df.format(km_delivered+km_accepted+km_assigned)+"km");

        // Calculate the slice size and update the pie chart:
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double d =  km_accepted / (km_accepted+km_delivered+km_assigned);
        int progress = (int) (d * 100);
        pieChart.setProgress(progress);

        //Update stats:
        km_acceptedTv.setText(df.format(km_accepted)+"km");
        km_deliveredTv.setText(df.format(km_delivered)+"km");
        km_assignedTv.setText(df.format(km_assigned)+"km");
        counter.setText(""+count);

    }

    public void retrieveKms(){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("deliverymen")
                .child(FirebaseAuth.getInstance().getUid())
                .child("delivery_requests");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                            km_assigned +=  100*Double.valueOf(ds.child("distance").getValue(String.class));
                            Log.d("VALUESSS","assigned,"+km_assigned);

                            break;
                        case DeliveryRequest.STATE2:
                            //ACCEPTED (yellow)
                            km_accepted +=  100*Double.valueOf(ds.child("distance").getValue(String.class));
                            Log.d("VALUESSS","accepted"+km_accepted);

                            break;
                        case DeliveryRequest.STATE3:
                            //DELIVERED (green)
                            km_delivered +=  100*Double.valueOf(ds.child("distance").getValue(String.class));
                            Log.d("VALUESSS","delivered"+km_delivered);
                            break;
                    }
                    updateChart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*public String getCurrentState(HashMap state_stateTime) {
        if (state_stateTime.get("state3") != null)
            return STATE3;
        else if (state_stateTime.get("state2") != null)
            return STATE2;
        else
            return STATE1;
    }*/

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_stats));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
