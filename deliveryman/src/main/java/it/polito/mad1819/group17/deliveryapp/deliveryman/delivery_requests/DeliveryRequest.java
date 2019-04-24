package it.polito.mad1819.group17.deliveryapp.deliveryman.delivery_requests;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DeliveryRequest implements Serializable {

    public final static String STATE1 = "Assigned";
    public final static String STATE2 = "Accepted";
    public final static String STATE3 = "Delivered";

    private String id;
    private String address;
    private String customer_name;
    private String customer_phone;
    private String notes;
    private String sorting_field;
    private String timestamp;
    private HashMap<String, String> state_stateTime;

    public DeliveryRequest() {

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

    public String getDelivery_time(){
        return timestamp.split(" ")[1];
    }

    public String getDelivery_date(){
        return timestamp.split(" ")[0];
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
                state_history = getState_stateTime().get("state1") + " " + STATE1;
                state_history += "\n" + "----/--/-- --:--" + " " + STATE2;
                state_history += "\n" + "----/--/-- --:--" + " " + STATE3;
                break;
            case STATE2:
                state_history = getState_stateTime().get("state1") + "  " + STATE1;
                state_history += "\n" + getState_stateTime().get("state2") + "  " + STATE2;
                state_history += "\n" + "----/--/-- --:--" + "  " + STATE3;
                break;
            case STATE3:
                state_history = getState_stateTime().get("state1") + "  " + STATE1;
                state_history += "\n" + getState_stateTime().get("state2") + "  " + STATE2;
                state_history += "\n" + getState_stateTime().get("state3") + "  " + STATE3;
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
