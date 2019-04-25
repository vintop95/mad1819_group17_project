package it.polito.mad1819.group17.deliveryapp.common;

import java.io.Serializable;

public class Deliveryman implements Serializable {

    private String id;
    private String name;
    private String phone;
    private String busy;

    public Deliveryman (){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBusy() {
        return busy;
    }

    public void setBusy(String busy) {
        this.busy = busy;
    }
}
