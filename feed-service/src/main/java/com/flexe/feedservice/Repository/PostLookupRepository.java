package com.flexe.feedservice.Repository;

import com.flexe.feedservice.Entity.Feed.Lookup.PostLookup;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface PostLookupRepository extends CassandraRepository<PostLookup, String> {
    List<PostLookup> findByPostId(String postId);
}
