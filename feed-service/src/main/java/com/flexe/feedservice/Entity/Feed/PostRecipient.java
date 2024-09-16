package com.flexe.feedservice.Entity.Feed;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Table("post_recipient")
public class PostRecipient {

    @PrimaryKey
    private RecipientKey key;

    @Column("post_date")
    private Date postDate;

    public PostRecipient() {
    }

    public PostRecipient(PostCreationRelationship post, UserInteraction interaction){
        this.key = new RecipientKey(post.getPost(), interaction.getUserId());
        this.postDate = post.getCreatedAt();
    }

    public PostRecipient(PostNode post, String userId){
        this.key = new RecipientKey(post, userId);
        this.postDate = new Date();
    }

    public static List<PostRecipient> FromGeneratedRecipients(FeedRecipient[] userRecipients, PostNode post) {
        return Arrays.stream(userRecipients).map(recipient -> new PostRecipient(post, recipient.getUser().getUserId())).toList();
    }

    public static List<PostRecipient> FromUserFollowRelation(PostCreationRelationship[] posts, UserInteraction interaction){
        return Arrays.stream(posts).map(post -> new PostRecipient(post, interaction)).toList();
    }
}
