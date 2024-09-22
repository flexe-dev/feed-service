package com.flexe.feedservice.Entity.Feed;

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
    Map<RecipientType, List<PostFeedReference>> recipientReferences;

    public FeedDisplay(){}

    public FeedDisplay(UserFeed userFeed){
        this.userFeed = userFeed;
    }

    public FeedDisplay(UserFeed userFeed, List<PostFeedReference> recipientReferences){
        this.userFeed = userFeed;
        this.recipientReferences = FromPostReferences(recipientReferences);
    }

    public static Map<RecipientType, List<PostFeedReference>> FromPostReferences(List<PostFeedReference> recipientReferences){
        return recipientReferences.stream().collect(Collectors.groupingBy(PostFeedReference::getPostRecipientType));
    }
}
