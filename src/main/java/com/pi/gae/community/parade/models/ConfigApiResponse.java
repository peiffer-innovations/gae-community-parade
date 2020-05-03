package com.pi.gae.community.parade.models;

public class ConfigApiResponse {
  private String attachCommunity;
  private String communityFromCoordinates;
  private String communityFromUuid;
  private String config;
  private String createAccount;
  private String firebaseToken;
  private String login;

  public String getAttachCommunity() {
    return attachCommunity;
  }

  public String getCommunityFromCoordinates() {
    return communityFromCoordinates;
  }

  public String getCommunityFromUuid() {
    return communityFromUuid;
  }

  public String getConfig() {
    return config;
  }

  public String getCreateAccount() {
    return createAccount;
  }

  public String getFirebaseToken() {
    return firebaseToken;
  }

  public String getLogin() {
    return login;
  }

  public void setAttachCommunity(String attachCommunity) {
    this.attachCommunity = attachCommunity;
  }

  public void setCommunityFromCoordinates(String communityFromCoordinates) {
    this.communityFromCoordinates = communityFromCoordinates;
  }

  public void setCommunityFromUuid(String communityFromUuid) {
    this.communityFromUuid = communityFromUuid;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public void setCreateAccount(String createAccount) {
    this.createAccount = createAccount;
  }

  public void setFirebaseToken(String firebaseToken) {
    this.firebaseToken = firebaseToken;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  @Override
  public String toString() {
    return String.join("|", attachCommunity, communityFromCoordinates, communityFromUuid, config,
        createAccount, login);
  }
}
