package com.flexe.feedservice.Repository;

import com.flexe.feedservice.Entity.Feed.Lookup.OriginReferenceLookup;
import com.flexe.feedservice.Entity.Feed.Lookup.OriginReferenceKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface ReferenceLookupRepository extends CassandraRepository<OriginReferenceLookup, OriginReferenceKey> {
    List<OriginReferenceLookup> findByKeyUserId(String userId);
    List<OriginReferenceLookup> findByKeyOriginatorUserIdAndKeyUserId(String originatorUserId, String userId);
    List<OriginReferenceLookup> findByKeyOriginatorUserIdAndKeyPostId(String originatorUserId, String postId);
    List<OriginReferenceLookup> findByKeyOriginatorUserIdAndKeyPostIdAndKeyPostReferenceType(String originatorUserId, String postId, Integer postReferenceType);
}


