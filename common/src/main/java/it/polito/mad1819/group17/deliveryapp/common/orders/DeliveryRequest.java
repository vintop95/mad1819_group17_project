package it.polito.mad1819.group17.deliveryapp.common.orders;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DeliveryRequest implements Serializable {

    public final static String STATE1 = "Assigned";
    public final static String STATE2 = "Accepted";
    public final static String STATE3 = "Delivered";

    private static String state1Local = STATE1;
    private static String state2Local = STATE2;
    private static String state3Local = STATE3;

    private String id;
    private String restaurant_id;
    private String order_id;
    private String customer_id;

    private String address;
    private String customer_name;
    private String customer_phone;
    private String notes;
    private String sorting_field;
    private String timestamp;
    private HashMap<String, String> state_stateTime;
    private String notified = "no";
    private String restaurant_name;
    private String restaurant_phone;
    private String restaurant_address;



    public DeliveryRequest() {

    }

    public DeliveryRequest(String restaurant_id, String order_id, String customer_id,
                           String address, String customer_name, String customer_phone,
                           String notes, String sorting_field, String timestamp,
                           HashMap<String, String> state_stateTime, String restaurant_name,
                           String restaurant_phone, String restaurant_address) {
        this.restaurant_id = restaurant_id;
        this.order_id = order_id;
        this.customer_id=customer_id;
        this.address = address;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.notes = notes;
        this.sorting_field = sorting_field;
        this.timestamp = timestamp;
        this.state_stateTime = state_stateTime;
        this.restaurant_name = restaurant_name;
        this.restaurant_phone = restaurant_phone;
        this.restaurant_address = restaurant_address;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getOrder_id() {
        return order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_phone() {
        return restaurant_phone;
    }

    public void setRestaurant_phone(String restaurant_phone) {
        this.restaurant_phone = restaurant_phone;
    }

    public String getRestaurant_address() {
        return restaurant_address;
    }

    public void setRestaurant_address(String restaurant_address) {
        this.restaurant_address = restaurant_address;
    }

    public String getNotified() {
        return notified;
    }

    public void setNotified(String notified) {
        this.notified = notified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
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

    public String getDelivery_time() {
        return timestamp.split(" ")[1];
    }

    public String getDelivery_date() {
        return timestamp.split(" ")[0];
    }

    public static void setStateLocal(String s1, String s2, String s3){
        state1Local = s1;
        state2Local = s2;
        state3Local = s3;
    }

    public String getCurrentState() {
        if (state_stateTime.get("state3") != null)
            return STATE3;
        else if (state_stateTime.get("state2") != null)
            return STATE2;
        else
            return STATE1;
    }

    public String getCurrentStateLocal() {
        switch (getCurrentState()) {
            case STATE1:
                return state1Local;
            case STATE2:
                return state2Local;
            case STATE3:
                return state3Local;
            default:
                return null;
        }
    }

    public String getStateHistoryToString() {
        String state_history = "";
        switch (getCurrentState()) {
            case STATE1:
                state_history = getState_stateTime().get("state1") + " " + state1Local;
                state_history += "\n" + "----/--/-- --:--" + " " + state2Local;
                state_history += "\n" + "----/--/-- --:--" + " " + state3Local;
                break;
            case STATE2:
                state_history = getState_stateTime().get("state1") + "  " + state1Local;
                state_history += "\n" + getState_stateTime().get("state2") + "  " + state2Local;
                state_history += "\n" + "----/--/-- --:--" + "  " + state3Local;
                break;
            case STATE3:
                state_history = getState_stateTime().get("state1") + "  " + state1Local;
                state_history += "\n" + getState_stateTime().get("state2") + "  " + state2Local;
                state_history += "\n" + getState_stateTime().get("state3") + "  " + state3Local;
                break;
        }
        return state_history;
    }

    public boolean moveToNextState() {
        if (getCurrentState() == STATE3)
            return false;
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String currentTimestamp = formatter.format(new Date());

            if (getCurrentState() == STATE1)
                this.state_stateTime.put("state2", currentTimestamp);
            else if (getCurrentState() == STATE2) {
                this.state_stateTime.put("state3", currentTimestamp);
                this.sorting_field = "state3_" + this.sorting_field.split("_")[1];
            }
            return true;

        }
    }
}
