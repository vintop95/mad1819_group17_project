package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.mad1819.group17.deliveryapp.customer.R;

public class OrderConfirmActivity extends AppCompatActivity {

    private TextView final_results;
    Intent intent;
    Double totalprice;
    Integer itemquantity;
    HashMap<String,Integer> itemsMap;
//
    ArrayList<String> keys;
    ArrayList<Integer> values;

    ListView lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        final_results = (TextView) findViewById(R.id.final_tv);
        intent = getIntent();

        itemsMap = (HashMap<String, Integer>)intent.getSerializableExtra("itemsMap");

        keys = new ArrayList<String>(itemsMap.keySet());
        values = new ArrayList<Integer>(itemsMap.values());

        String[] names = keys.toArray(new String[values.size()]);
        Integer[] quantities = values.toArray(new Integer[keys.size()]);

        Log.d("elem_quantities", Integer.toString(quantities.length));
        Log.d("elem_names", Integer.toString(names.length));

        itemquantity = intent.getIntExtra("items_quantity",0);
        totalprice = intent.getDoubleExtra("items_tot_price",0);

        final_results.setText("Buying "+Integer.toString(itemquantity)+" element(s) for the total price of:  "+Double.toString(totalprice));

        lst = (ListView) findViewById(R.id.listview_items);

        OrderConfirmAdapter orderConfirmAdapter = new OrderConfirmAdapter(names,quantities,this);
        lst.setAdapter(orderConfirmAdapter);
    }

}
