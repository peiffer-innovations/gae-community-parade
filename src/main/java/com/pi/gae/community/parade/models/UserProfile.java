package com.pi.gae.community.parade.models;

public class UserProfile {
  private String firstName;
  private String lastName;
  private String userId;

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUserId() {
    return userId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
