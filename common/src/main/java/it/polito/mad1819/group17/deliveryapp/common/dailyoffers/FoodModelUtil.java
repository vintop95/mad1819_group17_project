package it.polito.mad1819.group17.deliveryapp.common.dailyoffers;

import android.content.Context;
import android.text.TextUtils;

import it.polito.mad1819.group17.deliveryapp.common.R;

public class FoodModelUtil {
    public static boolean isValid(FoodModel food) {
        return !(TextUtils.isEmpty(food.name) || food.price <= 0);
    }

    public static String notValidMessage(Context context) {
        return context.getString(R.string.food_not_valid);
    }
}