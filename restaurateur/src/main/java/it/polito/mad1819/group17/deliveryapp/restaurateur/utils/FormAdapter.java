package it.polito.mad1819.group17.deliveryapp.restaurateur.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;
import it.polito.mad1819.group17.restaurateur.R;
import it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffers.FoodDetailsActivity;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.FieldHolder> {

    public final static int STATE_CHANGED = 1;
    public final static int STATE_NOT_CHANGED = 0;

    public static class ListItem {
        public int fieldNameRes;
        public String fieldValue;

        public ListItem(int fieldNameRes, String fieldValue) {
            this.fieldNameRes = fieldNameRes;
            this.fieldValue = fieldValue;
        }
    }

    private static final String TAG = FormAdapter.class.getName();

    private Context mContext;
    private List<ListItem> mList;
    private LayoutInflater mInflater;
    private AtomicInteger mFormState;

    public void updateList(List<ListItem> updatedData) {
        mList = updatedData;
        notifyDataSetChanged();
    }

    public FormAdapter(Context context, List<ListItem> list, AtomicInteger formState) {
        Log.d(TAG, "created");
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mFormState = formState;
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
        TextWatcher textWatcher;

        public FieldHolder(@NonNull View itemView) {
            super(itemView);
            textWatcher = new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    mFormState.set(STATE_CHANGED);
                    currentItem.fieldValue = inputFieldValue.getText().toString();
                    modifyItem(pos, currentItem);
                }
            };

            labelFieldName = itemView.findViewById(R.id.label_field_name);
            inputFieldValue = itemView.findViewById(R.id.input_field_value);
        }

        // VALERIO start
        private void addOnFocusChangeListener(EditText editText) {
            editText.setOnFocusChangeListener((v, focus) -> {
                if (focus)
                    ((EditText) v).setSelection(((EditText) v).getText().length(), 0);
            });
        }
        // VALERIO end

        public void setInputFieldValue() {
            this.inputFieldValue.setText(currentItem.fieldValue);
            inputFieldValue.addTextChangedListener(textWatcher);

            // VALERIO start
            addOnFocusChangeListener(inputFieldValue);
            // VALERIO end

            switch (currentItem.fieldNameRes) {
                case FoodDetailsActivity.LABEL_FOOD_NUMBER:
                    inputFieldValue.setEnabled(false);
                    break;
                case FoodDetailsActivity.LABEL_FOOD_NAME:
                    int maxLength1 = 30;
                    inputFieldValue.setHint(mContext.getString(R.string.hint_food_name));
                    inputFieldValue.setFilters(
                            new InputFilter[]{new InputFilter.LengthFilter(maxLength1)});
                    break;
                case FoodDetailsActivity.LABEL_FOOD_DESCRIPTION:
                    int maxLength2 = 100;
                    inputFieldValue.setHint(mContext.getString(R.string.hint_food_description));
                    inputFieldValue.setFilters(
                            new InputFilter[]{new InputFilter.LengthFilter(maxLength2)});
                    break;
                case FoodDetailsActivity.LABEL_FOOD_PRICE:
                    inputFieldValue.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
                case FoodDetailsActivity.LABEL_FOOD_AVAILABLE_QTY:
                    inputFieldValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
        }

        public void setLabelFieldName() {
            String fieldName = mContext.getString(currentItem.fieldNameRes);
            switch (currentItem.fieldNameRes) {
                case FoodDetailsActivity.LABEL_FOOD_PRICE:
                    fieldName = fieldName.replace(":", "") +
                            " (" + CurrencyHelper.getCurrencySymbol() + "):";
                    break;
            }
            this.labelFieldName.setText(fieldName);
        }

        public void setData(ListItem currentItem, int pos) {
            this.pos = pos;
            this.currentItem = currentItem;
            setLabelFieldName();
            setInputFieldValue();
        }

        public void modifyItem(int pos, ListItem newItem) {
            Log.d(TAG, "Item in pos " + pos + " modified");
            mList.set(pos, newItem);
        }
    }
}