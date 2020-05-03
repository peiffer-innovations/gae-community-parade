package com.pi.gae.community.parade.models;

public class AttachRequest {
  private String communityId;
  private UserToken userToken;

  public String getCommunityId() {
    return communityId;
  }

  public UserToken getUserToken() {
    return userToken;
  }

  public void setCommunityId(String communityId) {
    this.communityId = communityId;
  }

  public void setUserToken(UserToken userToken) {
    this.userToken = userToken;
  }
}
