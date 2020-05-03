package com.pi.gae.community.parade.models;

public class GeoBounds {
  private GeoPoint northEast;
  private GeoPoint southWest;

  public boolean contains(GeoPoint point) {
    boolean containsLatitude = northEast.getLatitude() >= point.getLatitude()
        && southWest.getLatitude() <= point.getLatitude();

    boolean containsLongitude = northEast.getLongitude() >= point.getLongitude()
        && southWest.getLongitude() <= point.getLongitude();

    return containsLatitude && containsLongitude;
  }

  public GeoPoint getNorthEast() {
    return northEast;
  }

  public GeoPoint getSouthWest() {
    return southWest;
  }

  public void setNorthEast(GeoPoint northEast) {
    this.northEast = northEast;
  }

  public void setSouthWest(GeoPoint southWest) {
    this.southWest = southWest;
  }
}
