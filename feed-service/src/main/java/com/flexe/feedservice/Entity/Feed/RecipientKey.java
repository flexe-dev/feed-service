package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Getter
@Setter
@PrimaryKeyClass
public class RecipientKey {
    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String postId;

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String userId;

    @PrimaryKeyColumn(name = "originator_user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String originatorUserId;

    public RecipientKey() {
    }

    public RecipientKey(String postId, String userId, String originatorUserId) {
        this.postId = postId;
        this.userId = userId;
        this.originatorUserId = originatorUserId;
    }

    public RecipientKey(PostNode post, String userId){
        this.postId = post.getPostId();
        this.userId = userId;
        this.originatorUserId = post.getUserId();
    }

}
