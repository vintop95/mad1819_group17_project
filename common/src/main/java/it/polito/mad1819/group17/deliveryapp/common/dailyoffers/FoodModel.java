package it.polito.mad1819.group17.deliveryapp.common.dailyoffers;

import java.io.Serializable;

/**
 * it must follow the javabean convention
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://www.learnhowtoprogram.com/android/data-persistence/firebase-writing-pojos
 */
public class FoodModel implements Serializable {
    public String id = "";
    public String name = "", description = "";
    public String image_path = "";
    public double price = 0.0;
    public int availableQty = 0;
    public int totalOrderedQty = 0;
    public Float total_rate;
    public Integer number_of_rates;

    /////////////////////////////////////////////////////////////////
    public FoodModel() {
    }

    /////////////////////////////////////////////////////////

}
