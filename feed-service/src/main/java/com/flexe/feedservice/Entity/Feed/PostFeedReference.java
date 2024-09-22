package com.flexe.feedservice.Entity.Feed;

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
@Table("post_feed_reference")
public class PostFeedReference {

    @PrimaryKey
    private PostReferenceKey key;

    @Column("user_id")
    private String userId;

    @Column("post_reference_type")
    private Integer postReferenceType;

    @Column("post_date")
    private Date postDate;

    public PostFeedReference() {
    }

    public PostFeedReference(PostCreationRelationship post, UserInteraction interaction, RecipientType type){
        this.key = new PostReferenceKey(post.getPost());
        this.userId = interaction.getUserId();
        this.postDate = post.getPost().getPostDate();
        this.postReferenceType = type.getValue();
    }

    public PostFeedReference(PostNode post, FeedRecipient recipient){
        this.key = new PostReferenceKey(post);
        this.userId = recipient.getUser().getUserId();
        this.postReferenceType = recipient.getRecipientType().getValue();
        this.postDate = post.getPostDate();
    }

    public PostFeedReference(PostNode post){
        this.key = new PostReferenceKey(post);
        this.userId = post.getUserId();
        this.postReferenceType = RecipientType.AUTHOR.getValue();
        this.postDate = post.getPostDate();
    }

    public RecipientType getPostRecipientType(){
        return FeedRecipient.RecipientType.fromValue(this.postReferenceType);
    }

    public Integer setPostRecipientType(RecipientType type){
        return type.getValue();
    }

    public static List<PostFeedReference> FromGeneratedRecipients(FeedRecipient[] userRecipients, PostNode post) {
        return Arrays.stream(userRecipients).map(recipient -> new PostFeedReference(post, recipient)).toList();
    }

    public static List<PostFeedReference> FromUserFollowRelation(PostCreationRelationship[] posts, UserInteraction interaction){
        return Arrays.stream(posts).map(post -> new PostFeedReference(post, interaction, RecipientType.NETWORK)).toList();
    }
}
