package com.flexe.feedservice.Repository;

import com.flexe.feedservice.Entity.Feed.PostRecipient;
import com.flexe.feedservice.Entity.Feed.RecipientKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface PostRecipientRepository extends CassandraRepository<PostRecipient, RecipientKey> {
    List<PostRecipient> findByKeyPostId(String postId);

    List<PostRecipient> findByKeyPostIdAndKeyOriginatorUserId(String postId, String originatorUserId);
}
