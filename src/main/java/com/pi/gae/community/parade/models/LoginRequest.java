package com.pi.gae.community.parade.models;

public class LoginRequest {
  private String password;
  private String username;

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
