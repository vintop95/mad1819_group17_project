package it.polito.mad1819.group17.deliveryapp.customer.restaurants.dailyoffers;

public class FoodItemModel {

    public String id = "",
            title = "",
            description = "",
            photo = "",
            price = "";
    Integer availableQty = 0;

    public FoodItemModel() {
    }

    public FoodItemModel(String id, String title, String description, String photo, String price,
                         Integer availableQty) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setPhoto(photo);
        setPrice(price);
        setAvailableQty(availableQty);
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        if(id==null) return;
        this.id = id;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }
    public void setAvailableQty(Integer availableQty) {
        if(availableQty==null) return;
        this.availableQty = availableQty;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        if(title==null) return;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        if(description==null) return;
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        if(photo==null) return;
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        if (price==null) return;
        this.price = price;
    }
}
