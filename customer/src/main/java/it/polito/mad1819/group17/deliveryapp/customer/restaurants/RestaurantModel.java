package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

public class RestaurantModel {
    public String key = "";
    public String address = "";
    public String name = "";
    public String bio = "";
    public String photo = "";
    public String phone = "";
    public Integer orders_count = 0;
    public String free_day = "";
    public String working_time_opening = "";
    public String working_time_closing = "";

    public RestaurantModel(String address, String name, String bio,
                           String photo,String key, String phone, Integer orders_count,
                           String free_day, String working_time_opening, String working_time_closing) {
        this.address = address;
        this.name = name;
        this.bio = bio;
        this.photo = photo;
        this.key = key;
        this.phone = phone;
        this.orders_count = orders_count;
        this.free_day = free_day;
        this.working_time_opening = working_time_opening;
        this.working_time_closing = working_time_closing;
    }

    public RestaurantModel() {
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        if(key == null) return;
        this.key = key;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        if(address == null) return;
        this.address = address;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        if(name == null) return;
        this.name = name;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        if(bio == null) return;
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        if(photo == null) return;
        this.photo = photo;
    }
}
