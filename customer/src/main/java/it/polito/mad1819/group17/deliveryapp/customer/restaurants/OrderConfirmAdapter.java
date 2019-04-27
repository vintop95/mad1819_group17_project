package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.polito.mad1819.group17.deliveryapp.customer.R;

public class OrderConfirmAdapter extends ArrayAdapter<String> {
    private String[] names;
    private Integer[] quantities;
    private Activity context;


    public OrderConfirmAdapter(String[] names, Integer[] quantities, Activity context) {
        super(context,R.layout.order_confirm_item,names);
        this.names = names;
        this.quantities = quantities;
        this.context = context;
    }

    class ViewHolder {
        TextView name_tv;
        TextView quantity_tv;


        ViewHolder(View v){
            name_tv=v.findViewById(R.id.item_name);
            quantity_tv=v.findViewById(R.id.item_quantity);
        }

    }

    public View getView(int position, View convertView,  ViewGroup parent) {
        View r = convertView;

        ViewHolder viewHolder = null;

        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.order_confirm_item,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) r.getTag();
        }
        viewHolder.name_tv.setText(names[position]);
        viewHolder.quantity_tv.setText(Integer.toString(quantities[position]));

        return r;
    }
}

/*


public class CategoryListView extends ArrayAdapter<String> {

    private String[] categories;
    private Integer[] imgid;
    private Activity context;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View r=convertView;
        it.polito.mad1819.group17.deliveryapp.customer.utils.CategoryListView.ViewHolder viewHolder = null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.category_item,null,true);
            viewHolder = new it.polito.mad1819.group17.deliveryapp.customer.utils.CategoryListView.ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder=(it.polito.mad1819.group17.deliveryapp.customer.utils.CategoryListView.ViewHolder) r.getTag();
        }

        Log.d("aia", "onCreateView: "+categories.length+"///"+imgid.length);
        viewHolder.ivw.setImageResource((Integer)imgid[position]);
        viewHolder.tv1.setText(categories[position]);

        return r;
    }

    class ViewHolder {
        TextView tv1;
        ImageView ivw;

        ViewHolder(View v){
            tv1=v.findViewById(R.id.category_name);
            ivw=v.findViewById(R.id.category_image);
        }
    }
}
*/
