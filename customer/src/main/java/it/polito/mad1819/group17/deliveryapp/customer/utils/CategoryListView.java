package it.polito.mad1819.group17.deliveryapp.customer.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad1819.group17.deliveryapp.customer.R;

public class CategoryListView extends ArrayAdapter<String> {

    private String[] categories;
    private Integer[] imgid;
    private Activity context;

    public CategoryListView(Activity context, String[] categories, Integer[] imgid) {
        super(context, R.layout.category_item, categories);
        Log.d("aia", "onCreateView: "+categories.length+"///"+imgid.length);
        this.context=context;
        this.categories=categories;
        this.imgid=imgid;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {

        View r=convertView;
        ViewHolder viewHolder = null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.category_item,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) r.getTag();
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
