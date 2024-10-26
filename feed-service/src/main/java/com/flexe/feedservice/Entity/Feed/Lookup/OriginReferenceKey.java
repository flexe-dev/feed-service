package com.flexe.feedservice.Entity.Feed.Lookup;

import com.flexe.feedservice.Entity.Feed.FeedRecipient;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import com.flexe.feedservice.Entity.Feed.FeedRecipient.RecipientType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Getter
@Setter
@PrimaryKeyClass
public class OriginReferenceKey {
    @PrimaryKeyColumn(name = "originator_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String originatorUserId;
    @PrimaryKeyColumn(name = "post_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String postId;
    @PrimaryKeyColumn(name= "post_reference_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private Integer postReferenceType;
    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String userId;

    public OriginReferenceKey() {
    }

    public OriginReferenceKey(PostNode post){
        this.postId = post.getPostId();
        this.originatorUserId = post.getUserId();
        this.userId = post.getUserId();
        this.postReferenceType = RecipientType.AUTHOR.getValue();
    }

    public OriginReferenceKey(PostCreationRelationship post, UserInteraction interaction){
        this.postId = post.getPost().getPostId();
        this.originatorUserId = post.getPost().getUserId();
        this.userId = interaction.getUserId();
        this.postReferenceType = RecipientType.NETWORK.getValue();
    }

    public OriginReferenceKey(PostNode post, FeedRecipient recipient){
        this.postId = post.getPostId();
        this.originatorUserId = recipient.getRecipient_reference_id();
        this.userId = recipient.getUser().getUserId();
        this.postReferenceType = recipient.getRecipientType().getValue();
    }

    public FeedRecipient.RecipientType getPostRecipientType(){
        return FeedRecipient.RecipientType.fromValue(this.postReferenceType);
    }

}
