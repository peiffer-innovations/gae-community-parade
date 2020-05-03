package com.pi.gae.community.parade.models;

public class FirebaseLoginTokenResponse {
  private long expires;
  private String token;
  private boolean valid;

  public long getExpires() {
    return expires;
  }

  public void setExpires(long expires) {
    this.expires = expires;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

}
