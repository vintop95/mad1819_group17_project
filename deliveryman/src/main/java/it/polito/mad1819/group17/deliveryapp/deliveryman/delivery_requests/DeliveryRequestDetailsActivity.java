package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad1819.group17.deliveryapp.deliveryman.R;

public class DeliveryRequestDetailsActivity extends AppCompatActivity {

    private DeliveryRequest inputDeliveryRequest;

    private TextView txt_request_id;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_address;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;
    private TextView txt_notes;
    private Button btn_next_state;

    private void locateViews() {
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        txt_state_history = findViewById(R.id.txt_state_history);
        txt_address = findViewById(R.id.txt_address);
        txt_notes = findViewById(R.id.txt_notes);
        btn_next_state = findViewById(R.id.btn_next_state);
        txt_request_id = findViewById(R.id.txt_request_id);
    }

    private void feedViews(DeliveryRequest selectedDeliveryRequest) {
        txt_delivery_time.setText(selectedDeliveryRequest.getDelivery_time());
        txt_delivery_date.setText(selectedDeliveryRequest.getDelivery_date());
        txt_customer_name.setText(selectedDeliveryRequest.getCustomer_name());
        txt_customer_phone.setText(Html.fromHtml("<u>" + selectedDeliveryRequest.getCustomer_phone() + "<u/>"));
        txt_address.setText(selectedDeliveryRequest.getAddress());
        txt_notes.setText(selectedDeliveryRequest.getNotes());
        txt_state_history.setText(selectedDeliveryRequest.getStateHistoryToString());
        txt_request_id.setText(selectedDeliveryRequest.getId());
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeliveryRequestDetailsActivity.this);
        alertDialog.setTitle(R.string.confirm_next_state_title)
                .setMessage(R.string.confirm_next_state_message)
                .setPositiveButton(R.string.positive_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positiveButtonAction();
                            }
                        })
                .setNegativeButton(R.string.negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //negativeButtonAction();
                            }
                        })
                .show();
    }

    private void positiveButtonAction() {
        if (inputDeliveryRequest.moveToNextState()) {
            txt_state_history.setText(inputDeliveryRequest.getStateHistoryToString());

            if (inputDeliveryRequest.getCurrentState() == inputDeliveryRequest.STATE3) {
                btn_next_state.setTextColor(getResources().getColor(R.color.button_disabled_text));
                btn_next_state.setEnabled(false);
            }
            FirebaseDatabase.getInstance().getReference()
                    .child("deliverymen")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("delivery_requests")
                    .child(inputDeliveryRequest.getId())
                    .child("state_stateTime")
                    .setValue(inputDeliveryRequest.getState_stateTime());
        }

    }

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_request_details);

        showBackArrowOnToolbar();

        locateViews();

        txt_customer_phone.setOnClickListener(v -> {
            String phoneNumber = ((TextView) v).getText().toString();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        });

        if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
            btn_next_state.setOnClickListener(v -> {
                showConfirmationDialog();
            });
            // we came here due to a tap on the notification so let us read the (updated) order from firebase
            FirebaseDatabase.getInstance().getReference()
                    .child("deliverymen")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("delivery_requests")
                    .child(getIntent().getStringExtra("id"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            inputDeliveryRequest = dataSnapshot.getValue(DeliveryRequest.class);
                            inputDeliveryRequest.setId(getIntent().getStringExtra("id"));
                            feedViews(inputDeliveryRequest);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            // we came here tapping on an order in the recycler view
            inputDeliveryRequest = (DeliveryRequest) getIntent().getSerializableExtra("delivery_request");
            if (!inputDeliveryRequest.getCurrentState().equals(DeliveryRequest.STATE3))
                btn_next_state.setOnClickListener(v -> {
                    showConfirmationDialog();
                });
            else {
                btn_next_state.setTextColor(getResources().getColor(R.color.button_disabled_text));
                btn_next_state.setEnabled(false);
            }
            feedViews(inputDeliveryRequest);
        }


    }
}
