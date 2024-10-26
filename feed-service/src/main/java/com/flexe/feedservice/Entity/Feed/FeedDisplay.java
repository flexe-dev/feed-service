package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Feed.Lookup.OriginReferenceLookup;
import lombok.Getter;
import lombok.Setter;
import com.flexe.feedservice.Entity.Feed.FeedRecipient.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class FeedDisplay {
    UserFeed userFeed;
    List<OriginReferenceLookup> recipientReferences;

    public FeedDisplay(UserFeed userFeed, List<OriginReferenceLookup> recipientReferences){
        this.userFeed = userFeed;
        this.recipientReferences = recipientReferences;
    }


}
