package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.Nodes.PostNode.*;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.Feed.FeedRecipient.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.*;
import java.util.stream.Collectors;

@Table("feed")
@Getter
@Setter
public class UserFeed {

    @PrimaryKey
    private FeedKey key;
    @Column("read_status")
    private Boolean readStatus;
    @Column("post_type")
    private Integer postType;
    @Column("recipient_type")
    private RecipientType feedRecipientType;
    @Column("recipient_reference_id")
    private String recipientReferenceId;

    public UserFeed(){

    }

    public UserFeed(String targetId, PostCreationRelationship post){
        this.key = new FeedKey(post, targetId);
        this.readStatus = false;
        this.postType = parsePostType(post.getPost().getType());
        this.feedRecipientType = RecipientType.NETWORK;
    }

    public UserFeed(PostNode post, FeedRecipient recipient){
        PostCreationRelationship newPost = new PostCreationRelationship(post);
        this.key = new FeedKey(newPost, recipient.getUser().getUserId());
        this.readStatus = false;
        this.postType = parsePostType(post.getType());
        this.feedRecipientType = recipient.getRecipientType();
        this.recipientReferenceId = recipient.getRecipient_reference_id();
    }

    public UserFeed(PostNode post){
        this.key = new FeedKey(post);
        this.readStatus = false;
        this.postType = parsePostType(post.getType());
        this.feedRecipientType = RecipientType.AUTHOR;
    }

    public Integer parsePostType(PostType type){
        return switch (type) {
            case TEXT -> 0;
            case MEDIA -> 1;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFeed userFeed = (UserFeed) o;
        return Objects.equals(key, userFeed.key) && Objects.equals(readStatus, userFeed.readStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, readStatus);
    }


    public static List<UserFeed> FromPostNodes(String targetId, PostCreationRelationship[] posts){
        return Arrays.stream(posts).map(post -> new UserFeed(targetId, post)).collect(Collectors.toList());
    }

    public static List<UserFeed> FromRecipientResponse(PostNode post, FeedRecipient[] recipients){
        return Arrays.stream(recipients).map(recipient -> new UserFeed(post, recipient)).collect(Collectors.toList());
    }

}
