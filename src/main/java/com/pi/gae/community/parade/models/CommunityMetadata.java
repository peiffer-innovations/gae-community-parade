package com.pi.gae.community.parade.models;

public class CommunityMetadata {
  private GeoBounds bounds;
  private String id;
  private String location;
  private String name;


  public GeoBounds getBounds() {
    return bounds;
  }

  public String getId() {
    return id;
  }

  public String getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public void setBounds(GeoBounds bounds) {
    this.bounds = bounds;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CommunityMetadata withId(String id) {
    CommunityMetadata md = new CommunityMetadata();

    md.setBounds(bounds);
    md.setId(id);
    md.setLocation(location);
    md.setName(name);

    return md;
  }
}
