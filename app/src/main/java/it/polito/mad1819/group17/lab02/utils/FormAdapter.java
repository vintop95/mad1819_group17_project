package it.polito.mad1819.group17.lab02.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import it.polito.mad1819.group17.lab02.R;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.FieldHolder> {

    public static class ListItem {
        public int fieldNameRes;
        public String fieldValue;

        public ListItem (int fieldNameRes, String fieldValue){
            this.fieldNameRes = fieldNameRes;
            this.fieldValue = fieldValue;
        }
    }

    private static final String TAG = FormAdapter.class.getName();

    private Context mContext;
    private List<ListItem> mList;
    private LayoutInflater mInflater;

    public void updateList(List<ListItem> updatedData) {
        mList = updatedData;
        notifyDataSetChanged();
    }

    public FormAdapter(Context context, List<ListItem> list) {
        Log.d(TAG, "created");
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    // PHASE 1 OF PROTOCOL: build FieldHolder (ViewHolder)
    // and link rv_field_edittext layout to FormAdapter (Adapter)
    @NonNull
    @Override
    public FieldHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = mInflater.inflate(
                R.layout.rv_field_edittext, viewGroup, false);
        return new FieldHolder(itemView);
    }

    // PHASE 2 OF PROTOCOL: fetch data from model and set data on FieldHolder (ViewHolder)
    @Override
    public void onBindViewHolder(@NonNull FieldHolder holder, int pos) {
        // Log.d(TAG, "onBindViewHolder " + pos);
        ListItem currentField = mList.get(pos);
        holder.setData(currentField, pos);
    }


    @Override
    public int getItemCount() {
        // Log.d(TAG,"itemCount: " + mFoodList.size());
        return mList.size();
    }

    class FieldHolder extends RecyclerView.ViewHolder {
        TextView labelFieldName;
        EditText inputFieldValue;
        int pos;
        ListItem currentItem;

        public FieldHolder(@NonNull View itemView) {
            super(itemView);
            labelFieldName = itemView.findViewById(R.id.label_field_name);
            inputFieldValue = itemView.findViewById(R.id.input_field_value);
        }

        public void setData(ListItem currentItem, int pos) {
            this.labelFieldName.setText(mContext.getString(currentItem.fieldNameRes));
            this.inputFieldValue.setText(currentItem.fieldValue);
            this.pos = pos;
            this.currentItem = currentItem;
        }

        public void modifyItem(int pos, ListItem newItem) {
            Log.d(TAG, "Item in pos " + pos + " modified");
            mList.set(pos, newItem);
            notifyItemChanged(pos);
        }
    }
}