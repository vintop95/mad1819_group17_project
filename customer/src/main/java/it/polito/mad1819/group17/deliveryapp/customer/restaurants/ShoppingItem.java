package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

public class ShoppingItem {
    String name;
    String price;

    public ShoppingItem(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public ShoppingItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
