package it.polito.mad1819.group17.lab02.dailyoffer;

class ModelFood {

    private int image;
    private String name,price,place;

    public ModelFood(int image, String name, String price, String place) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.place = place;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
