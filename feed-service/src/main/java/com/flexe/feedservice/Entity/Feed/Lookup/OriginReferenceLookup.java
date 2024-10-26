package com.flexe.feedservice.Entity.Feed.Lookup;

import com.flexe.feedservice.Entity.Feed.FeedRecipient;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.Feed.FeedRecipient.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Table("reference_lookup")
public class OriginReferenceLookup {

    @PrimaryKey
    private OriginReferenceKey key;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("reference_date")
    private Date referenceDate = new Date();

    public OriginReferenceLookup() {
    }

    public OriginReferenceLookup(PostCreationRelationship post, UserInteraction interaction, RecipientType type){
        this.key = new OriginReferenceKey(post, interaction);
    }

    public OriginReferenceLookup(PostNode post, FeedRecipient recipient){
        this.key = new OriginReferenceKey(post, recipient);
    }

    public OriginReferenceLookup(PostNode post){
        this.key = new OriginReferenceKey(post);
    }

    public Integer setPostRecipientType(RecipientType type){
        return type.getValue();
    }

    public String getPostId(){
        return this.key.getPostId();
    }

    public static List<OriginReferenceLookup> FromGeneratedRecipients(FeedRecipient[] userRecipients, PostNode post) {
        return Arrays.stream(userRecipients).map(recipient -> new OriginReferenceLookup(post, recipient)).toList();
    }

    public static List<OriginReferenceLookup> FromUserFollowRelation(PostCreationRelationship[] posts, UserInteraction interaction){
        return Arrays.stream(posts).map(post -> new OriginReferenceLookup(post, interaction, RecipientType.NETWORK)).toList();
    }
}
