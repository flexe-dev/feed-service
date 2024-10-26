package com.flexe.feedservice.Repository;

import com.flexe.feedservice.Entity.Feed.FeedKey;
import com.flexe.feedservice.Entity.Feed.UserFeed;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserFeedRepository extends CassandraRepository<UserFeed, FeedKey> {

    List<UserFeed> findByKeyUserId(String userId);

    @Query("UPDATE user_feed SET read_status = true WHERE user_id = ?0 AND post_id = ?1")
    void markPostAsRead(String userId, String postId);
}
