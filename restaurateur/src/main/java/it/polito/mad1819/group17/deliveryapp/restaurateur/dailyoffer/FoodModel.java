package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.CurrencyHelper;

/**
 * Utilities regarding food that cannot stay in javaBean FoodModel class
 */
class FoodModelUtil {
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

/**
 * it must follow the javabean convention
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://www.learnhowtoprogram.com/android/data-persistence/firebase-writing-pojos
 */
@IgnoreExtraProperties
public class FoodModel implements Serializable{
    public String id = "";
//    public int pos = -1; // not useful now and hard to update
    public String name = "", description = "";
    public String photo = "";
    public double price = 0.0;
    public int availableQty = 0;

    /////////////////////////////////////////////////////////////////
    public FoodModel() {}

    public FoodModel(String name, String description,
                     String photo, double price,
                     int availableQty) {
//        this.pos = pos;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.price = price;
        this.availableQty = availableQty;
    }

    /////////////////////////////////////////////////////////
}
