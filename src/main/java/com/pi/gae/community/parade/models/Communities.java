package com.pi.gae.community.parade.models;

import java.util.Map;

public class Communities {
  private Map<String, CommunityMetadata> communities;

  public Map<String, CommunityMetadata> getCommunities() {
    return communities;
  }

  public void setCommunities(Map<String, CommunityMetadata> communities) {
    this.communities = communities;
  }
}
