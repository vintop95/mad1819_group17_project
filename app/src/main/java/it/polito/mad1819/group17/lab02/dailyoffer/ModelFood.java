package it.polito.mad1819.group17.lab02.dailyoffer;

import android.graphics.Bitmap;

import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicLong;

import it.polito.mad1819.group17.lab02.utils.CurrencyHelper;
import it.polito.mad1819.group17.lab02.utils.IdHelper;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;

public class ModelFood implements java.io.Serializable {
    /////////////////////// STORAGE MGMT ///////////////////////////
    private static final PrefHelper prefHelper = PrefHelper.getInstance();

    private static String getPrefKey(Long id){
        return "PREF_FOOD_" + id;
    }

    public void saveToPref(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        prefHelper.putString(getPrefKey(id), json);
    }

    public static ModelFood loadFromPref(Long id){
        Gson gson = new Gson();
        String json = prefHelper.getString(getPrefKey(id));
        if(json != null){
            return gson.fromJson(json, ModelFood.class);
        }else{
            return null;
        }
    }

    //////////////////////////////////////////////////////////////
    private Long id;
    private String mName, mDescription;
    private String mPhoto;
    private double mPrice;
    private int mAvailableQty;
    /////////////////////////////////////////////////////////////////
    public ModelFood( int id, String name, String description,
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

    public int getAvailableQty() {
        return mAvailableQty;
    }


    public void setAvailableQty(int availableQty) {
        this.mAvailableQty = availableQty;
    }

    /////////////////////////////////////////////////////////
}
