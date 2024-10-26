package com.flexe.feedservice.Entity.Feed.Lookup;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("post_lookup")
public class PostLookup {
    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String postId;

    @PrimaryKeyColumn(name = "originator_user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String originatorUserId;

    public PostLookup() {
    }

    public PostLookup(PostNode post) {
        this.postId = post.getPostId();
        this.originatorUserId = post.getUserId();
    }

    public PostLookup(PostInteraction interaction){
        this.postId = interaction.getPost().getPostId();
        this.originatorUserId = interaction.getUserId();
    }

}
