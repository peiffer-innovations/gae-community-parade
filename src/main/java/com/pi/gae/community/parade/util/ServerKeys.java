package com.pi.gae.community.parade.util;

import java.io.InputStream;
import org.apache.cxf.helpers.IOUtils;
import org.json.JSONObject;

public class ServerKeys {
  private static final ServerKeys INSTANCE = createServerKeys();

  public static ServerKeys getInstance() {
    return INSTANCE;
  }

  private static ServerKeys createServerKeys() {
    try {
      try (InputStream in =
          ServerKeys.class.getClassLoader().getResourceAsStream("secure/server_keys.json")) {
        String data = IOUtils.readStringFromStream(in);
        JSONObject json = new JSONObject(data);

        return new ServerKeys(json.getString("salt"), json.getString("user"));
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final String salt;
  private final String user;

  private ServerKeys(String salt, String user) {
    this.salt = salt;
    this.user = user;
  }

  public String getSalt() {
    return salt;
  }

  public String getUser() {
    return user;
  }


}
