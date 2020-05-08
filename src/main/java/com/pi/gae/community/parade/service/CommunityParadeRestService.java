package com.pi.gae.community.parade.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pi.gae.community.parade.models.AttachRequest;
import com.pi.gae.community.parade.models.Communities;
import com.pi.gae.community.parade.models.CommunityMetadata;
import com.pi.gae.community.parade.models.CommunityResponse;
import com.pi.gae.community.parade.models.ConfigApiResponse;
import com.pi.gae.community.parade.models.ConfigFirebaseResponse;
import com.pi.gae.community.parade.models.ConfigResponse;
import com.pi.gae.community.parade.models.CreateAccountRequest;
import com.pi.gae.community.parade.models.FirebaseLoginTokenResponse;
import com.pi.gae.community.parade.models.GeoPoint;
import com.pi.gae.community.parade.models.LoginRequest;
import com.pi.gae.community.parade.models.PassFailResponse;
import com.pi.gae.community.parade.models.UserLoginResponse;
import com.pi.gae.community.parade.models.UserProfile;
import com.pi.gae.community.parade.models.UserToken;
import com.pi.gae.community.parade.util.FirebaseManager;
import com.pi.gae.community.parade.util.ProvidenceCrypto;

@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class CommunityParadeRestService
{
  @POST
  @Path("/community/attach")
  public PassFailResponse attachCommunity(AttachRequest request) throws Exception
  {
    if (!ProvidenceCrypto.validateUserToken(request.getUserToken()))
    {
      throw new RuntimeException();
    }

    Communities communities = FirebaseManager
        .getInstance()
        .value("/data/community_metadata", Communities.class);

    boolean success = false;
    String error = "Community not found";
    for (Map.Entry<String, CommunityMetadata> entry : communities
        .getCommunities()
        .entrySet())
    {

      if (request.getCommunityId().equals(entry.getKey()))
      {
        FirebaseManager
            .getInstance()
            .set("/data/users/" + request.getUserToken().getUserId()
                + "/communities/" + request.getCommunityId(), true);
        success = true;
        error = null;
        break;
      }

    }

    return new PassFailResponse(success, error);
  }

  @GET
  @Path("/community/from-coordinates/{latitude}/{longitude}")
  public CommunityResponse getCommunityFromCoordinates(
      @PathParam("latitude") String latitude,
      @PathParam("longitude") String longitude) throws Exception
  {
    Communities communities = FirebaseManager
        .getInstance()
        .value("/data/community_metadata", Communities.class);

    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(latitude),
        Double.parseDouble(longitude));
    List<CommunityMetadata> found = new ArrayList<>();
    for (Map.Entry<String, CommunityMetadata> entry : communities
        .getCommunities()
        .entrySet())
    {

      if (entry.getValue().getBounds().contains(geoPoint))
      {
        found.add(entry.getValue().withId(entry.getKey()));
      }

    }

    return new CommunityResponse(found);
  }

  @GET
  @Path("/community/from-uuid/{uuid}")
  public CommunityResponse getCommunityFromUuid(@PathParam("uuid") String uuid)
      throws Exception
  {
    Communities communities = FirebaseManager
        .getInstance()
        .value("/data/community_metadata", Communities.class);

    List<CommunityMetadata> found = new ArrayList<>();
    for (Map.Entry<String, CommunityMetadata> entry : communities
        .getCommunities()
        .entrySet())
    {

      if (uuid.equals(entry.getKey()))
      {
        found.add(entry.getValue().withId(entry.getKey()));
      }

    }

    return new CommunityResponse(found);
  }

  @GET
  @Path("/config")
  public Response getConfig(@HeaderParam("etag") String requestEtag)
  {
    String prefix = System.getenv("base-url");
    if (prefix == null)
    {
      prefix = "https://community-parade-1.appspot.com/api";
    }

    String basePath = System.getenv("firebase-data-root");
    if (basePath == null)
    {
      basePath = "production";
    }

    ConfigResponse configResponse = new ConfigResponse();
    ConfigApiResponse api = new ConfigApiResponse();
    ConfigFirebaseResponse fb = new ConfigFirebaseResponse();
    configResponse.setApi(api);
    configResponse.setFirebase(fb);

    api.setAttachCommunity(prefix + "/community/attach");
    api
        .setCommunityFromCoordinates(
            prefix + "/community/from-coordinates/{latitude}/{longitude}");
    api.setCommunityFromUuid(prefix + "/community/from-uuid/{uuid}");
    api.setCreateAccount(prefix + "/user/create-account");
    api.setFirebaseToken(prefix + "/user/firebase-token");
    api.setLogin(prefix + "/user/login");

    fb.setBasePath(basePath);

    int status = 200;
    String etag = configResponse.etag();
    Object entity = configResponse;
    if (etag.equalsIgnoreCase(requestEtag))
    {
      status = 204;
      entity = null;
    }

    return Response
        .status(status)
        .header("if-none-match", etag)
        .type(MediaType.APPLICATION_JSON)
        .entity(entity)
        .build();
  }

  @POST
  @Path("/user/create-account")
  public Response userCreateAccount(CreateAccountRequest request) throws Exception
  {
    if (request.getUsername() == null)
    {
      throw new BadRequestException("Username missing");
    }
    else if (request.getPassword() == null || request.getPassword().length() < 8)
    {
      throw new BadRequestException("Invalid password");
    }

    int status = 403;
    String userId = ProvidenceCrypto.createUserId(request.getUsername());
    FirebaseManager fb = FirebaseManager.getInstance();
    UserProfile profile = fb
        .value("/data/users/" + userId + "/profile", UserProfile.class);
    UserLoginResponse loginResponse = null;
    if (profile == null)
    {
      status = 200;
      profile = new UserProfile();
      profile.setFirstName(request.getFirstName());
      profile.setLastName(request.getLastName());
      profile.setUserId(userId);
      fb.set("/data/users/" + userId + "/profile", profile);

      fb
          .set("/data/users/" + userId + "/password",
              ProvidenceCrypto.encryptPassword(request.getPassword()));

      UserToken userToken = ProvidenceCrypto
          .generateUserToken(request.getFirstName(), request.getLastName(), userId);

      FirebaseLoginTokenResponse token = new FirebaseLoginTokenResponse();
      String jwt = FirebaseManager.getInstance().createJwt(userToken.getUserId());

      token.setExpires(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
      token.setToken(jwt);
      token.setValid(true);

      loginResponse = new UserLoginResponse();
      loginResponse.setFirebaseToken(token);
      loginResponse.setUserToken(userToken);

    }

    return Response
        .status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(loginResponse)
        .build();
  }

  @POST
  @Path("/user/firebase-token")
  public FirebaseLoginTokenResponse userFirebaseToken(UserToken userToken)
      throws Exception
  {
    boolean valid = ProvidenceCrypto.validateUserToken(userToken);
    if (!valid)
    {
      throw new BadRequestException("Invalid token");
    }

    FirebaseLoginTokenResponse token = new FirebaseLoginTokenResponse();
    String jwt = FirebaseManager.getInstance().createJwt(userToken.getUserId());

    token.setExpires(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
    token.setToken(jwt);
    token.setValid(true);

    return token;
  }

  @POST
  @Path("/user/login")
  public UserLoginResponse userLogin(LoginRequest request) throws Exception
  {
    String userId = ProvidenceCrypto.createUserId(request.getUsername());
    FirebaseManager fb = FirebaseManager.getInstance();
    String expected = fb.value("/data/users/" + userId + "/password", String.class);
    boolean valid = ProvidenceCrypto.validatePassword(request.getPassword(), expected);
    if (!valid)
    {
      throw new BadRequestException("Login failed");
    }

    UserProfile profile = fb
        .value("/data/users/" + userId + "/profile", UserProfile.class);
    UserToken userToken = ProvidenceCrypto
        .generateUserToken(profile.getFirstName(),
            profile.getLastName(), profile.getUserId());

    FirebaseLoginTokenResponse token = new FirebaseLoginTokenResponse();
    String jwt = FirebaseManager.getInstance().createJwt(userToken.getUserId());

    token.setExpires(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
    token.setToken(jwt);
    token.setValid(true);

    UserLoginResponse loginResponse = new UserLoginResponse();
    loginResponse.setFirebaseToken(token);
    loginResponse.setUserToken(userToken);

    return loginResponse;
  }
}
