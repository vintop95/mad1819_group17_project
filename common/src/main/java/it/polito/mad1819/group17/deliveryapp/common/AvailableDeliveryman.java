package it.polito.mad1819.group17.deliveryapp.common;

import com.firebase.geofire.GeoLocation;

public class AvailableDeliveryman implements Comparable {
    private String id;
    private GeoLocation geoLocation;
    private GeoLocation referenceGeoLocation;

    public AvailableDeliveryman() {

    }

    public AvailableDeliveryman(String id, GeoLocation geoLocation, GeoLocation referenceGeoLocation) {
        this.id = id;
        this.geoLocation = geoLocation;
        this.referenceGeoLocation = referenceGeoLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public GeoLocation getReferenceGeoLocation() {
        return referenceGeoLocation;
    }

    public void setReferenceGeoLocation(GeoLocation referenceGeoLocation) {
        this.referenceGeoLocation = referenceGeoLocation;
    }

    public Double getHaversineDistanceFromReference() {
        if (geoLocation == null || referenceGeoLocation == null)
            return null;


        // distance between latitudes and longitudes
        Double dLat = Math.toRadians(geoLocation.latitude - referenceGeoLocation.latitude);
        Double dLon = Math.toRadians(geoLocation.longitude - referenceGeoLocation.longitude);

        // convert to radians
        double refLat = Math.toRadians(referenceGeoLocation.latitude);
        double lat = Math.toRadians(geoLocation.latitude);

        // apply formulae
        Double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(refLat) *
                        Math.cos(lat);
        Double rad = new Double(6371);
        Double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    @Override
    public String toString() {
        return "AvailableDeliveryman{" +
                "id='" + id + '\'' +
                ", latitude=" + geoLocation.longitude +
                ", longitude=" + geoLocation.longitude +
                ", dist=" + getHaversineDistanceFromReference() +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (this.getHaversineDistanceFromReference() < ((AvailableDeliveryman) o).getHaversineDistanceFromReference())
            return -1;
        else
            return 1;
    }
}
