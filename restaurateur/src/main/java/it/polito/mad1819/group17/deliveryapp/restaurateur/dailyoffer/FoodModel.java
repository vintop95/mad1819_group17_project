package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.support.annotation.Keep;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.CurrencyHelper;

class FoodModelUtil {
    /////////////////////// STORAGE MGMT ///////////////////////////
    private static String getPrefKey(Long id){
        return "PREF_FOOD_" + id;
    }
    public final static String FIREBASE_DAILYOFFERS = "dailyOffers";

    public static void pushToFirebase(FoodModel food){
 /*       Gson gson = new Gson();
        String json = gson.toJson(this);
        PrefHelper.getInstance().putString(getPrefKey(pos), json);
*/
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(FIREBASE_DAILYOFFERS).push();

        databaseReference.setValue(food);
    }

    public static FoodModel loadFromPref(Long id){
    /*    Gson gson = new Gson();
        String json = PrefHelper.getInstance().getString(getPrefKey(pos));
        if(json != null){
            return gson.fromJson(json, FoodModel.class);
        }else{
            return null;
        }
*/
        //Query query = FirebaseDatabase.getInstance().getReference().child("dailyOffers");
        return null;
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
    public String id;
    public int pos = -1;
    public String name = "", description = "";
    public String photo = "";
    public double price = 0.0;
    public int availableQty = 0;
    /////////////////////////////////////////////////////////////////
    public FoodModel() {}

    public FoodModel(int pos, String name, String description,
                     String photo, double price,
                     int availableQty) {
        this.pos = pos;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.price = price;
        this.availableQty = availableQty;
    }

    /////////////////////////////////////////////////////////
}
