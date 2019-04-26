package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

public class RestaurantModel {

    public String address;
    public String name;
    public String bio;
    public String photo;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if(key == null)key="UNKNOWN";
        this.key = key;
    }

    public String key;

    public RestaurantModel(String address, String name, String bio, String photo,String key) {
        this.address = address;
        this.name = name;
        this.bio = bio;
        this.photo = photo;
        this.key = key;
    }

    public RestaurantModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if(address == null) address ="unknown";
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if(name == null)name="UNKNOWN" ;
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if(bio == null)bio="";
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {

        if(photo == null)photo="" ;
        this.photo = photo;
    }




}
