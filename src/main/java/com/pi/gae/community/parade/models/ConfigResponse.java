package com.pi.gae.community.parade.models;

import com.pi.gae.community.parade.util.ProvidenceCrypto;

public class ConfigResponse {
  private ConfigApiResponse api;

  public String etag() {
    String result = api.toString();

    return ProvidenceCrypto.digest(result);
  }

  public ConfigApiResponse getApi() {
    return api;
  }

  public void setApi(ConfigApiResponse api) {
    this.api = api;
  }
}
