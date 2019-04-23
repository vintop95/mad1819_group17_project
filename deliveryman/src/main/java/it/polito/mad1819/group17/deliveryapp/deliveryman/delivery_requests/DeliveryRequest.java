package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import java.io.Serializable;
import java.util.HashMap;

public class DeliveryRequest implements Serializable {

    public final static String STATE1 = "Assigned";
    public final static String STATE2 = "Accepted";
    public final static String STATE3 = "Delivered";

    private String address;
    private String customer_name;
    private String customer_phone;
    private String notes;
    private String sorting_field;
    private String timestamp;
    private HashMap<String, String> state_stateTime;

    public DeliveryRequest() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSorting_field() {
        return sorting_field;
    }

    public void setSorting_field(String sorting_field) {
        this.sorting_field = sorting_field;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, String> getState_stateTime() {
        return state_stateTime;
    }

    public void setState_stateTime(HashMap<String, String> state_stateTime) {
        this.state_stateTime = state_stateTime;
    }
}
