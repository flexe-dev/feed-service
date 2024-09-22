package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Getter
@Setter
@PrimaryKeyClass
public class PostReferenceKey {
    @PrimaryKeyColumn(name = "originator_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String originatorUserId;
    @PrimaryKeyColumn(name = "post_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String postId;

    public PostReferenceKey() {
    }

    public PostReferenceKey(String postId, String originatorUserId) {
        this.postId = postId;
        this.originatorUserId = originatorUserId;
    }

    public PostReferenceKey(PostNode post){
        this.postId = post.getPostId();
        this.originatorUserId = post.getUserId();
    }

}
