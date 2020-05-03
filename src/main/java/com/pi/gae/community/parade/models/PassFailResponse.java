package com.pi.gae.community.parade.models;

public class PassFailResponse {
  private String error;
  private boolean success;

  public PassFailResponse() {
    this(true);
  }

  public PassFailResponse(boolean success) {
    this(success, null);
  }

  public PassFailResponse(String error) {
    this(error == null, error);
  }

  public PassFailResponse(boolean success, String error) {
    this.error = error;
    this.success = success;
  }

  public String getError() {
    return error;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
