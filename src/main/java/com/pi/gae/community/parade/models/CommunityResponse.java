package com.pi.gae.community.parade.models;

import java.util.List;

public class CommunityResponse {
  private List<CommunityMetadata> communities;

  public CommunityResponse() {}

  public CommunityResponse(List<CommunityMetadata> communities) {
    this.communities = communities;
  }

  public List<CommunityMetadata> getCommunities() {
    return communities;
  }

  public void setCommunities(List<CommunityMetadata> communities) {
    this.communities = communities;
  }
}
