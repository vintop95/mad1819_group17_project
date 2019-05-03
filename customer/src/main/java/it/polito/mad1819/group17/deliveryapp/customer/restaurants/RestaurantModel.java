package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

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

    public RestaurantModel(String address, String name, String bio,
                           String image_path,String key, String phone, Integer orders_count,
                           String free_day, String working_time_opening, String working_time_closing) {
        this.address = address;
        this.name = name;
        this.bio = bio;
        this.image_path = image_path;
        this.key = key;
        this.phone = phone;
        this.orders_count = orders_count;
        this.free_day = free_day;
        this.working_time_opening = working_time_opening;
        this.working_time_closing = working_time_closing;
    }

    public RestaurantModel() {
    }
}
