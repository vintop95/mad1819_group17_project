package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

import java.util.HashMap;
import java.util.Map;

public class RestaurantModel {
    public String key = "";
    public String address = "";
    public String name = "";
    public String bio = "";
    public String image_path = "";
    public String phone = "";
    public Integer orders_count = 0;
    public String free_day = "";
    public String working_time_opening = "";
    public String working_time_closing = "";
    public Map<String, String> favorites = new HashMap<>();

    public Integer number_of_restaurant_rates;
    public Float total_restaurant_rate;
    public Integer number_of_service_rates;
    public Float total_service_rate;

    public RestaurantModel(String address, String name, String bio, String image_path, String key, String phone, Integer orders_count,
                           String free_day, String working_time_opening, String working_time_closing, Map<String, String> favorites,
                           Integer number_of_restaurant_rates, Float total_restaurant_rate, Integer number_of_service_rates, Float total_service_rate) {
        this.address = address;
        this.name = name;
        this.bio = bio;
        this.image_path = image_path;
        this.key = key;
        this.phone = phone;
        this.orders_count = orders_count == null ? 0 : orders_count;
        this.free_day = free_day;
        this.working_time_opening = working_time_opening;
        this.working_time_closing = working_time_closing;
        this.favorites = favorites;
        this.number_of_restaurant_rates = number_of_restaurant_rates;
        this.total_restaurant_rate = total_restaurant_rate;
        this.number_of_service_rates = number_of_service_rates;
        this.total_service_rate = total_service_rate;
    }

    public RestaurantModel() {
    }

    @Override
    public String toString() {
        return "\n\nRestaurantModel{" +
                "key='" + key + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", image_path='" + image_path + '\'' +
                ", phone='" + phone + '\'' +
                ", orders_count=" + orders_count +
                ", free_day='" + free_day + '\'' +
                ", working_time_opening='" + working_time_opening + '\'' +
                ", working_time_closing='" + working_time_closing + '\'' +
                ", favorites=" + favorites +
                ", number_of_restaurant_rates=" + number_of_restaurant_rates +
                ", total_restaurant_rate=" + total_restaurant_rate +
                ", number_of_service_rates=" + number_of_service_rates +
                ", total_service_rate=" + total_service_rate +
                '}';
    }
}
