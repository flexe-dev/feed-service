package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import jdk.dynalink.beans.StaticClass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@PrimaryKeyClass
public class FeedKey {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "post_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Date postDate;

    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String postId;



    public FeedKey(){}

    //constructor
    public FeedKey(PostCreationRelationship post, String userId){
        this.userId = userId;
        this.postId = post.getPost().getPostId();
        this.postDate = post.getCreatedAt();
    }

    public FeedKey(PostNode post){
        this.userId = post.getUserId();
        this.postId = post.getPostId();
        this.postDate = post.getPostDate();
    }

    // equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedKey that = (FeedKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(postDate, that.postDate) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postDate, postId);
    }

}