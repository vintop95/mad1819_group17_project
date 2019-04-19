package it.polito.mad1819.group17.deliveryapp.restaurateur;

import java.io.Serializable;

public class Restaurateur implements Serializable {
    private String photo = "";
    private String name = "";
    private String phone = "";
    private String mail = "";
    private String address = "";
    private String restaurant_type = "";
    private String free_day = "";
    private String working_time_opening = "";
    private String working_time_closing = "";
    private String bio = "";

    public Restaurateur() {

    }

    public Restaurateur(String photo, String name, String phone, String mail, String address, String restaurant_type, String free_day, String working_time_opening, String working_time_closing, String bio) {
        this.photo = photo;
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.address = address;
        this.restaurant_type = restaurant_type;
        this.free_day = free_day;
        this.working_time_opening = working_time_opening;
        this.working_time_closing = working_time_closing;
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRestaurant_type() {
        return restaurant_type;
    }

    public void setRestaurant_type(String restaurant_type) {
        this.restaurant_type = restaurant_type;
    }

    public String getFree_day() {
        return free_day;
    }

    public void setFree_day(String free_day) {
        this.free_day = free_day;
    }

    public String getWorking_time_opening() {
        return working_time_opening;
    }

    public void setWorking_time_opening(String working_time_opening) {
        this.working_time_opening = working_time_opening;
    }

    public String getWorking_time_closing() {
        return working_time_closing;
    }

    public void setWorking_time_closing(String working_time_closing) {
        this.working_time_closing = working_time_closing;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
