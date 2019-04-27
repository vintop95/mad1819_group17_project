package it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.common.dailyoffers.FoodModel;
import it.polito.mad1819.group17.deliveryapp.common.utils.CurrencyHelper;

/**
 * Utilities regarding food that cannot stay in javaBean FoodModel class
 */
class FoodModelCustomerUtil {
    private final static String FIREBASE_DAILYOFFERS = "daily_offers";

    public static DatabaseReference getDailyOffersRef(String restaurateur_id){
        if(restaurateur_id == null){
            throw new IllegalStateException("restaurateur_id is NULL!!!");
        }

        return FirebaseDatabase.getInstance().getReference()
                .child("restaurateurs")
                .child(restaurateur_id)
                .child(FIREBASE_DAILYOFFERS);
    }

    public static String getPriceFormatted(double price) {
        return CurrencyHelper.getCurrency(price);
    }
}