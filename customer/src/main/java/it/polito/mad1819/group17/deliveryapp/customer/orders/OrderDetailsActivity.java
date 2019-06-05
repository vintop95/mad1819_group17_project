package it.polito.mad1819.group17.deliveryapp.customer.orders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad1819.group17.deliveryapp.common.orders.Order;
import it.polito.mad1819.group17.deliveryapp.common.orders.Rate;
import it.polito.mad1819.group17.deliveryapp.common.orders.ShoppingItem;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.RateActivity;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.RestaurantProfileActivity;

public class OrderDetailsActivity extends AppCompatActivity {

    public final static int RATE_SENT = 1;
    public final static int RATE_NOT_SENT = -1;
    public final static int RATE_REQUEST = 0;

    private TextView txt_restaurant_name;
    private TextView txt_delivery_time;
    private TextView txt_delivery_date;
    private TextView txt_order_content;
    private TextView txt_customer_name;
    private TextView txt_customer_phone;
    private TextView txt_state_history;
    private TextView txt_delivery_address;
    private TextView txt_order_notes;
    private CardView card_deliveryman;
    private TextView txt_deliveryman_name;
    private TextView txt_deliveryman_phone;
    private FrameLayout frame_layout;

    private ImageView image_restaurant_info;

    private Order inputOrder;

    private Menu menu;

    private CardView card_rate;
    private RatingBar rb_restaurant;
    private RatingBar rb_service;
    private TextView txt_comment;

    private void showBackArrowOnToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void locateViews() {
        txt_restaurant_name = findViewById(R.id.txt_restaurant_name);
        txt_delivery_time = findViewById(R.id.txt_delivery_time);
        txt_delivery_date = findViewById(R.id.txt_delivery_date);
        txt_order_content = findViewById(R.id.txt_order_content);
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_state_history = findViewById(R.id.txt_state_history);
        txt_delivery_address = findViewById(R.id.txt_delivery_address);
        txt_order_notes = findViewById(R.id.txt_order_notes);
        card_deliveryman = findViewById(R.id.card_deliveryman);
        txt_deliveryman_name = findViewById(R.id.txt_deliveryman_name);
        txt_deliveryman_phone = findViewById(R.id.txt_deliveryman_phone);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        image_restaurant_info = findViewById(R.id.image_restaurant_info);
        frame_layout = findViewById(R.id.frame_layout_restaurant_info);
        card_rate = findViewById(R.id.card_rate);
        rb_restaurant = findViewById(R.id.rb_restaurant);
        rb_service = findViewById(R.id.rb_service);
        txt_comment = findViewById(R.id.txt_comment);

    }

    private void feedViews(Order selectedOrder) {
        txt_restaurant_name.setText(selectedOrder.getRestaurant_name());
        txt_delivery_time.setText(selectedOrder.getDelivery_timestamp().split(" ")[1]);
        txt_delivery_date.setText(selectedOrder.getDelivery_timestamp().split(" ")[0]);
        txt_customer_name.setText(selectedOrder.getCustomer_name());
        txt_customer_phone.setText(Html.fromHtml("<u>" + selectedOrder.getCustomer_phone() + "<u/>"));
        txt_delivery_address.setText(selectedOrder.getDelivery_address());
        txt_order_notes.setText(selectedOrder.getNotes());

        String order_content = "";
        for (String item : selectedOrder.getItem_itemDetails().keySet()) {
            if (!order_content.equals(""))
                order_content += "\n";

            ShoppingItem shoppingItem = selectedOrder.getItem_itemDetails().get(item);
            order_content += "x" + shoppingItem.getQuantity()
                    + " " + shoppingItem.getName()
                    + " - " + CurrencyHelper.getCurrency(shoppingItem.getPrice() * shoppingItem.getQuantity());
        }
        txt_order_content.setText(order_content);


        if (!TextUtils.isEmpty(selectedOrder.getDeliveryman_id())) {
            txt_deliveryman_name.setText(selectedOrder.getDeliveryman_name());
            txt_deliveryman_phone.setText(Html.fromHtml("<u>" + selectedOrder.getDeliveryman_phone() + "<u/>"));
            txt_deliveryman_phone.setOnClickListener(v -> {
                String phoneNumber = ((TextView) v).getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            });
        }

        txt_state_history.setText(selectedOrder.getStateHistoryToString());

        if (inputOrder.getRestaurant_rate() != null)
            rb_restaurant.setRating(inputOrder.getRestaurant_rate());
        if (inputOrder.getService_rate() != null)
            rb_service.setRating(inputOrder.getService_rate());
        if (!TextUtils.isEmpty(inputOrder.getComment()))
            txt_comment.setText(inputOrder.getComment());
    }


    public void adjustLayoutProgrammatically() {
        if (!inputOrder.getCurrentState().equals(Order.STATE3)) {
            card_deliveryman.setVisibility(View.GONE);
        } else {
            card_deliveryman.setVisibility(View.VISIBLE);
        }

        Rate rate = new Rate(FirebaseAuth.getInstance().getUid(), inputOrder.getRestaurant_rate(), inputOrder.getService_rate(), inputOrder.getComment());
        if (rate.isEmpty() || inputOrder.getRated().equals("no"))
            card_rate.setVisibility(View.GONE);
        else
            card_rate.setVisibility(View.VISIBLE);

        feedViews(inputOrder);
    }

    private void openRestaurantProfile() {
        Intent intent = new Intent(OrderDetailsActivity.this, RestaurantProfileActivity.class);
        intent.putExtra("restaurant_id", inputOrder.getRestaurant_id());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        showBackArrowOnToolbar();

        locateViews();

        txt_customer_phone.setOnClickListener(v -> {
            String phoneNumber = ((TextView) v).getText().toString();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        });
        frame_layout.setOnClickListener(v -> openRestaurantProfile());


        if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
            // we came here due to a tap on the notification so let us read the (updated) order from firebase
            FirebaseDatabase.getInstance().getReference("/customers/" + FirebaseAuth.getInstance().getUid() + "/orders/" + getIntent().getStringExtra("id"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            inputOrder = dataSnapshot.getValue(Order.class);
                            adjustLayoutProgrammatically();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            // we came here tapping on an order in the recycler view
            inputOrder = (Order) getIntent().getSerializableExtra("order");
            adjustLayoutProgrammatically();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_details, menu);
        this.menu = menu;
        if (inputOrder.getRated().equals("no") && inputOrder.getCurrentState().equals(Order.STATE4))
            menu.findItem(R.id.rate_itemmenu).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.rate_itemmenu) {
            startActivityForResult(new Intent(this, RateActivity.class).putExtra("id", inputOrder.getId()), RATE_REQUEST);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RATE_REQUEST && resultCode != RATE_NOT_SENT) {
            menu.findItem(R.id.rate_itemmenu).setVisible(false);
            Rate rate = (Rate) data.getSerializableExtra("rate");
            if (rate != null) {
                card_rate.setVisibility(View.VISIBLE);
                if (rate.getRestaurant_rate() != null)
                    rb_restaurant.setRating(rate.getRestaurant_rate());
                if (rate.getService_rate() != null)
                    rb_service.setRating(rate.getService_rate());
                if (!TextUtils.isEmpty(rate.getComment()))
                    txt_comment.setText(rate.getComment());
            }
        }
    }
}