package it.polito.mad1819.group17.deliveryapp.common.dailyoffers;

import java.io.Serializable;

/**
 * it must follow the javabean convention
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://www.learnhowtoprogram.com/android/data-persistence/firebase-writing-pojos
 */
public class FoodModel implements Serializable{
    public String id = "";
//    public int pos = -1; // not useful now and hard to update
    public String name = "", description = "";
    public String image_path = "";
    public double price = 0.0;
    public int availableQty = 0;

    /////////////////////////////////////////////////////////////////
    public FoodModel() {}

    public FoodModel(String name, String description,
                     String image_path, double price,
                     int availableQty) {
//        this.pos = pos;
        this.name = name;
        this.description = description;
        this.image_path = image_path;
        this.price = price;
        this.availableQty = availableQty;
    }

    /////////////////////////////////////////////////////////
}
