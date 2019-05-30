package it.polito.mad1819.group17.deliveryapp.restaurateur.reviews;

public class Review {
    private Float restaurant_rate = null;
    private Float service_rate = null;
    private String comment = null;

    public Float getRestaurant_rate() {
        return restaurant_rate;
    }

    public void setRestaurant_rate(Float restaurant_rate) {
        this.restaurant_rate = restaurant_rate;
    }

    public Float getService_rate() {
        return service_rate;
    }

    public void setService_rate(Float service_rate) {
        this.service_rate = service_rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
