package com.flexe.feedservice.Repository;

import com.flexe.feedservice.Entity.Feed.PostFeedReference;
import com.flexe.feedservice.Entity.Feed.PostReferenceKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface PostFeedReferenceRepository extends CassandraRepository<PostFeedReference, PostReferenceKey> {
    List<PostFeedReference> findByKeyPostId(String postId);
    List<PostFeedReference> findByKeyOriginatorUserId(String userId);
    List<PostFeedReference> findByKeyOriginatorUserIdAndKeyPostId(String userId, String postId);
    List<PostFeedReference> findByKeyOriginatorUserIdAndUserId(String originatorUserId, String userId);
    List<PostFeedReference> findByUserId(String userId);

    void deleteByKeyOriginatorUserId(String userId);
    void deleteByKeyOriginatorUserIdAndKeyPostId(String userId, String postId);
    void deleteByKeyOriginatorUserIdAndUserId(String originatorUserId, String userId);

}
