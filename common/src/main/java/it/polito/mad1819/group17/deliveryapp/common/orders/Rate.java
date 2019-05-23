package it.polito.mad1819.group17.deliveryapp.common.orders;

import android.text.TextUtils;

import java.io.Serializable;

public class Rate implements Serializable {

    private String customer_id;
    private Float restaurant_rate = null;
    private Float service_rate = null;
    private String comment = null;

    public Rate() {

    }

    public Rate(String customer_id, Float restaurant_rate, Float service_rate, String comment) {
        this.customer_id = customer_id;
        if (restaurant_rate!=null && restaurant_rate > 0)
            this.restaurant_rate = restaurant_rate;
        if (service_rate!=null && service_rate > 0)
            this.service_rate = service_rate;
        if (!TextUtils.isEmpty(comment))
            this.comment = comment;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public Float getRestaurant_rate() {
        return restaurant_rate;
    }

    public void setRestaurant_rate(Float restaurant_rate) {
        this.restaurant_rate = restaurant_rate;
    }

    public Float getService_rate() {
        return service_rate;
    }

    public void setService_rate(Float service_rate) {
        this.service_rate = service_rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isEmpty() {
        return (restaurant_rate == null && service_rate == null && comment == null);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "customer_id='" + customer_id + '\'' +
                ", restaurant_rate=" + restaurant_rate +
                ", service_rate=" + service_rate +
                ", comment='" + comment + '\'' +
                '}';
    }
}
