package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.Nodes.PostNode.*;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.CreationRelationship;
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
    private Boolean readStatus = false;

    @Column("group_id")
    private String groupId;

    @Column("creator_id")
    private String creatorId;

    @Column("post_type")
    private Integer postType;

    public UserFeed(){

    }

    public UserFeed(PostNode post){
        this.key = new FeedKey(post);
        this.postType = parsePostType(post.getType());
        this.creatorId = post.getUserId();
    }

    public UserFeed(UserInteraction interaction, PostNode post){
        this.key = new FeedKey(interaction.getUserId(), post);
        this.postType = parsePostType(post.getType());
        this.creatorId = post.getUserId();
    }

    public UserFeed(PostNode post, FeedRecipient recipient){
        this.key = new FeedKey(recipient.getUser().getUserId(), post);
        this.postType = parsePostType(post.getType());
        this.creatorId = post.getUserId();
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

    public static List<UserFeed> FromUserInteraction(UserInteraction interaction, List<CreationRelationship<PostNode>> posts){
        return posts.stream().map(post -> new UserFeed(interaction, post.getRoot())).collect(Collectors.toList());
    }

    public static List<UserFeed> FromPost(PostNode post, FeedRecipient[] recipients){
        return Arrays.stream(recipients).map(recipient -> new UserFeed(post, recipient)).collect(Collectors.toList());
    }
}
