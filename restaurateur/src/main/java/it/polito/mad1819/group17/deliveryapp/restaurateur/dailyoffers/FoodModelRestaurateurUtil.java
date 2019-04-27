package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffers;

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
class FoodModelRestaurateurUtil {
    private final static String FIREBASE_DAILYOFFERS = "daily_offers";
    private static String restaurateur_id = null;

    public static DatabaseReference getDailyOffersRef(){
        if(FirebaseAuth.getInstance().getUid() != null){
            restaurateur_id = FirebaseAuth.getInstance().getUid();
        }

        if(restaurateur_id == null){
            throw new IllegalStateException("restaurateur_id is NULL!!!");
        }

        return FirebaseDatabase.getInstance().getReference()
                .child("restaurateurs")
                .child(restaurateur_id)
                .child(FIREBASE_DAILYOFFERS);
    }

    private static void handleCompletionListener
            (Context context, @Nullable DatabaseError err,
             @NonNull DatabaseReference ref, String msg){
        if(err != null){
            Log.e("FIREBASE_LOG",err.getMessage());
            Toast.makeText(context, err.getMessage(), Toast.LENGTH_LONG).show();
        }else{
            Log.d("FIREBASE_LOG", msg + " " + ref.toString());
        }
    }

    public static void pushToFirebase(Context context, FoodModel food){
        DatabaseReference newFoodRef = getDailyOffersRef().push();
        food.id = newFoodRef.getKey();
        newFoodRef.setValue(food,
                (@Nullable DatabaseError err, @NonNull DatabaseReference ref)
                        -> handleCompletionListener(context, err, ref, "Added")
        );
    }

    public static void modifyInFirebase(Context context, FoodModel food){
        if(food.id.isEmpty())
            throw new IllegalArgumentException("food.id SHOULDN'T BE NULL");

        Map<String, Object> updatedFood = new HashMap<>();
        updatedFood.put(food.id, food);
        getDailyOffersRef().updateChildren(updatedFood,
                (@Nullable DatabaseError err, @NonNull DatabaseReference ref)
                        -> handleCompletionListener(context, err, ref, "Modified")
        );
    }

    public static void removeFromFirebase(Context context, FoodModel food){
        if(food.id.isEmpty())
            throw new IllegalArgumentException("food.id SHOULDN'T BE NULL");

        DatabaseReference foodRef = getDailyOffersRef().child(food.id);
        foodRef.removeValue(
                (@Nullable DatabaseError err, @NonNull DatabaseReference ref)
                        -> handleCompletionListener(context, err, ref, "Removed")
        );
    }

    public static String getPriceFormatted(double price) {
        return CurrencyHelper.getCurrency(price);
    }
}