package it.polito.mad1819.group17.deliveryapp.restaurateur.dailyoffer;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.CurrencyHelper;
import it.polito.mad1819.group17.deliveryapp.restaurateur.utils.PrefHelper;

public class FoodModel implements Serializable {
    /////////////////////// STORAGE MGMT ///////////////////////////

    private static String getPrefKey(Long id){
        return "PREF_FOOD_" + id;
    }
    DatabaseReference databaseReference;


    public void saveToPref(){


 /*       Gson gson = new Gson();
        String json = gson.toJson(this);
        PrefHelper.getInstance().putString(getPrefKey(id), json);
*/

        databaseReference = FirebaseDatabase.getInstance().getReference().child("dailyOffers").push();
        Map<String, Object> map = new HashMap<>();

        map.put("id", databaseReference.getKey());//retrieve a key for our entry
        map.put("position", this.getIdLong());//retrieve a key for our entry
        map.put("name", this.getName());
        map.put("description", this.getDescription());
        map.put("availableQuantity", this.getAvailableQtyInt());
        map.put("price", this.getPriceDouble());
        map.put("photo", this.getPhoto());

        databaseReference.setValue(map);

    }

    public static FoodModel loadFromPref(Long id){
    /*    Gson gson = new Gson();
        String json = PrefHelper.getInstance().getString(getPrefKey(id));
        if(json != null){
            return gson.fromJson(json, FoodModel.class);
        }else{
            return null;
        }
*/
        //Query query = FirebaseDatabase.getInstance().getReference().child("dailyOffers");
    return null;

    }

    //////////////////////////////////////////////////////////////
    private Long id = Long.valueOf(-1);
    private String mName = "", mDescription = "";
    private String mPhoto = "";
    private double mPrice = 0.0;
    private int mAvailableQty = 0;
    /////////////////////////////////////////////////////////////////
    public FoodModel() {

    }

    public FoodModel(int id, String name, String description,
                     String photo, double price,
                     int availableQty) {
        setId(id);
        setName(name);
        setDescription(description);
        setPhoto(photo);
        setPrice(price);
        setAvailableQty(availableQty);
    }
    /////////////////////////////////////////////////////////

    public long getIdLong(){
        return id;
    }

    public String getIdString(){
        return id.toString();
    }

    public void setId(long id){
        this.id = id;
    }

    ////////////////////////////////////////////////////////////

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    /////////////////////////////////////////////////////////

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    /////////////////////////////////////////////////////////

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }

    /////////////////////////////////////////////////////////

    public String getPriceString() {
        return CurrencyHelper.getCurrency(getPriceDouble());
    }

    public double getPriceDouble() {
        return mPrice;
    }

    public void setPrice(double price) {
        this.mPrice = price;
    }


    /////////////////////////////////////////////////////////

    public int getAvailableQtyInt() {
        return mAvailableQty;
    }

    public String getAvailableQtyString() {
        return Integer.toString(mAvailableQty);
    }

    public void setAvailableQty(int availableQty) {
        this.mAvailableQty = availableQty;
    }

    /////////////////////////////////////////////////////////
}
