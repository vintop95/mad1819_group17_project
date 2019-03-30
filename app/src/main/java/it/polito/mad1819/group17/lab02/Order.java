package it.polito.mad1819.group17.lab02;

public class Order {

    private int number;
    private String customer_name;
    private String customer_phone;
    private String delivery_time;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Order(int number, String customer_name, String customer_phone, String delivery_time, String state) {
        this.number = number;
        this.customer_phone = customer_name;
        this.customer_name = customer_phone;
        this.delivery_time = delivery_time;
        this.state = state;
    }


    //private HashMap<String, Integer> orderedItems_orderedQty;
    //private HashMap<String, String> state_stateTime;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    /*public HashMap<String, Integer> getOrderedItems_orderedQty() {
        return orderedItems_orderedQty;
    }

    public void setOrderedItems_orderedQty(HashMap<String, Integer> orderedItems_orderedQty) {
        this.orderedItems_orderedQty = orderedItems_orderedQty;
    }

    public HashMap<String, String> getState_stateTime() {
        return state_stateTime;
    }

    public void setState_stateTime(HashMap<String, String> state_stateTime) {
        this.state_stateTime = state_stateTime;
    }*/

    public Order(int number, String user_phone) {
        this.number = number;
        this.customer_phone = user_phone;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    /*public int getTotalOrderdItems(){
        int totOrderedItems = 0;
        for(String orderedItem:orderedItems_orderedQty.keySet())
            totOrderedItems += orderedItems_orderedQty.get(orderedItem);
        return totOrderedItems;
    }*/
}
