package com.pi.gae.community.parade.models;

public class UserLoginResponse
{
  private FirebaseLoginTokenResponse firebaseToken;
  private UserToken userToken;

  public FirebaseLoginTokenResponse getFirebaseToken()
  {
    return firebaseToken;
  }

  public void setFirebaseToken(FirebaseLoginTokenResponse firebaseToken)
  {
    this.firebaseToken = firebaseToken;
  }

  public UserToken getUserToken()
  {
    return userToken;
  }

  public void setUserToken(UserToken userToken)
  {
    this.userToken = userToken;
  }
}
