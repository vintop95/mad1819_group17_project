package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad1819.group17.deliveryapp.common.Restaurateur;
import it.polito.mad1819.group17.deliveryapp.common.utils.ProgressBarHandler;
import it.polito.mad1819.group17.deliveryapp.customer.R;
import it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers.DailyMenuActivity;

public class RestaurantsActivity extends AppCompatActivity {

    private String category_selected;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private Intent intent;
    private ProgressBarHandler pbHandler;

    private void showBackArrowOnToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        pbHandler.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        pbHandler.hide();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        category_selected = intent.getStringExtra("category");
        Log.d("aaa",category_selected);
        setContentView(R.layout.activity_restaurant);

        pbHandler = new ProgressBarHandler(this);

        showBackArrowOnToolbar();

        recyclerView = findViewById(R.id.restaurant_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetch();
    }

    public static Bitmap stringToBitMap(String encodedString) throws IllegalArgumentException{
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

public class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView name;
    public TextView bio;
    public ImageView photo;
    public TextView address;
    public TextView avgPrice;
    public Restaurateur restaurateur;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.restaurant_root_layout);
        name = itemView.findViewById(R.id.restaurant_name);
        bio = itemView.findViewById(R.id.restaurant_bio);
        photo = itemView.findViewById(R.id.restaurant_image);
        address = itemView.findViewById(R.id.restaurant_address);
        avgPrice = itemView.findViewById(R.id.restaurant_avgprice);
    }

    public void setData(Restaurateur rest){
        restaurateur = rest;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = getAdapterPosition();

                // TODO: pass the ID of the restaurant
                Context context = v.getContext();
                Intent intent = new Intent(context, DailyMenuActivity.class);
                intent.putExtra("restaurateur_id", restaurateur.id);
                // Toast.makeText(v.getContext(), name.getText(),Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });
    }


    public void setName(String name) {
        this.name.setText(name);
    }

    public void setBio(String bio) {
        this.bio.setText(bio);
    }

    public void setPhoto(String photo) {
        Bitmap bmp;
        if(photo != null) {
            bmp = stringToBitMap(photo);
            this.photo.setImageBitmap(bmp);
        }
    }

    public void setAddress(String address) {
        this.address.setText(address);
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice.setText(avgPrice);
    }

}


    private void fetch() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference();
        Query query = ref.child("restaurateurs").orderByChild("restaurant_type").equalTo(category_selected);

        FirebaseRecyclerOptions<Restaurateur> options =
                new FirebaseRecyclerOptions.Builder<Restaurateur>()
                        .setQuery(query, Restaurateur.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Restaurateur, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Restaurateur model) {
                holder.setAddress(model.getAddress());
                holder.setBio(model.getBio());
                holder.setName(model.getName());
                // holder.setPhoto(model.getImage_path());
                holder.setAvgPrice("Pr$");
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pbHandler.hide();
            }
        };
        recyclerView.setAdapter(adapter);

        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Intent intent = new Intent(view.getContext(), DailyMenuActivity.class);
                        Bundle b = new Bundle();
                        b.putString("category",); //Your category selected
                        intent.putExtras(b); //Put your category in the next Intent
                        startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
*/
    }

}
