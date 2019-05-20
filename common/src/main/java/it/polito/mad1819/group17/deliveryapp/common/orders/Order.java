package it.polito.mad1819.group17.deliveryapp.common.orders;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Order implements Serializable {

    public final static String STATE1 = "Accepted";
    public final static String STATE2 = "In preparation";
    public final static String STATE3 = "Delivering";

    private static String state1Local = STATE1;
    private static String state2Local = STATE2;
    private static String state3Local = STATE3;

    private String id = "";
    private String restaurant_id = "";
    private String customer_id = "";

    private String restaurant_name = "";
    private String restaurant_phone = "";
    private String restaurant_address = "";

    private String customer_name = "";
    private String customer_phone = "";
    private String delivery_timestamp = "";
    private String delivery_address = "";
    private HashMap<String, String> state_stateTime = new HashMap<>();
    private HashMap<String, ShoppingItem> item_itemDetails = new HashMap<>();

    private String notes = "";
    private String deliveryman_id = "";
    private String deliveryman_name = "";
    private String deliveryman_phone = "";
    private String sorting_field = "";
    private String notified = "no";

    public Order() {

    }

    public String getRestaurant_id() {
        return restaurant_id;
    }
    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }
    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
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

    public String getDeliveryman_id() {
        return deliveryman_id;
    }
    public void setDeliveryman_id(String deliveryman_id) {
        this.deliveryman_id = deliveryman_id;
    }

    public String getDeliveryman_name() {
        return deliveryman_name;
    }
    public void setDeliveryman_name(String deliveryman_name) {
        this.deliveryman_name = deliveryman_name;
    }

    public String getDeliveryman_phone() {
        return deliveryman_phone;
    }
    public void setDeliveryman_phone(String deliveryman_phone) {
        this.deliveryman_phone = deliveryman_phone;
    }

    public String getNotified() {
        return notified;
    }
    public void setNotified(String notified) {
        this.notified = notified;
    }


    public String getSorting_field() {
        return sorting_field;
    }
    public void setSorting_field(String sorting_field) {
        this.sorting_field = sorting_field;
    }

    public String getDelivery_timestamp() {
        return delivery_timestamp;
    }
    public void setDelivery_timestamp(String delivery_timestamp) {
        this.delivery_timestamp = delivery_timestamp;
    }

    public HashMap<String, ShoppingItem> getItem_itemDetails() {
        return item_itemDetails;
    }
    public void setItem_itemDetails(HashMap<String, ShoppingItem> item_itemDetails) {
        this.item_itemDetails = item_itemDetails;
    }

    public String getDelivery_address() {
        return delivery_address;
    }
    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
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

    public HashMap<String, String> getState_stateTime() {
        return state_stateTime;
    }
    public void setState_stateTime(HashMap<String, String> state_stateTime) {
        this.state_stateTime = state_stateTime;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public String getDelivery_date() {
        return this.delivery_timestamp.split(" ")[0];
    }

    public String getDelivery_time() {
        if(delivery_timestamp.length() <= 1) return "";
        else return this.delivery_timestamp.split(" ")[1];
    }

    public int getTotalItemsQuantity() {
        int totOrderedItems = 0;
        for (String orderedItem : item_itemDetails.keySet()){
            totOrderedItems += item_itemDetails.get(orderedItem).getQuantity();
        }

        return totOrderedItems;
    }

    public static void setStateLocal(String s1, String s2, String s3){
        state1Local = s1;
        state2Local = s2;
        state3Local = s3;
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

    public String getCurrentState() {
        if (state_stateTime.get("state3") != null)
            return STATE3;
        else if (state_stateTime.get("state2") != null)
            return STATE2;
        else
            return STATE1;
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

            if (getCurrentState() == STATE1) {
                this.state_stateTime.put("state2", currentTimestamp);
                //this.sorting_field = this.sorting_field.split("_")[0] + "_state0_" + this.sorting_field.split("_")[2];

            } else if (getCurrentState() == STATE2) {
                this.state_stateTime.put("state3", currentTimestamp);
                //this.sorting_field = this.sorting_field.split("_")[0] + "_state3_" + this.sorting_field.split("_")[2];
                this.sorting_field = "state3_" + this.sorting_field.split("_")[1];
            }
            return true;
        }
    }
}