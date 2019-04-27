package it.polito.mad1819.group17.deliveryapp.customer.restaurants;

public class FoodItemModel {

    public FoodItemModel() {
    }

    public FoodItemModel(String id, String title, String description, String photo, String price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.price = price;
    }

    public String id, title, description, photo, price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id==null)id="unknown";
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title==null)title="unknown";
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description==null)description="unknown";
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        if(photo==null)photo="unknown";
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if (photo==null)photo="";
        this.price = price;
    }
}
