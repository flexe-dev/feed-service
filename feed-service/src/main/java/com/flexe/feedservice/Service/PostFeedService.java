package com.flexe.feedservice.Service;

import com.flexe.feedservice.Entity.Feed.*;
import com.flexe.feedservice.Entity.Feed.Lookup.OriginReferenceLookup;
import com.flexe.feedservice.Entity.Feed.Lookup.PostLookup;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.Nodes.UserNode;
import com.flexe.feedservice.Repository.PostLookupRepository;
import com.flexe.feedservice.Repository.ReferenceLookupRepository;
import com.flexe.feedservice.Repository.UserFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostFeedService {

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private ReferenceLookupRepository referenceLookupRepository;

    @Autowired
    private PostLookupRepository postLookupRepository;

    @Autowired
    private NodeService nodeService;

    public List<FeedDisplay> getUserFeed(String userId){
        //Retrieve All Posts for a users feed
        List<UserFeed> userFeedPosts = userFeedRepository.findByKeyUserId(userId);
        if(userFeedPosts.isEmpty()) return List.of();


        //Retrieve All Active origins for a feed's post
        List<OriginReferenceLookup> feedOriginReferences= referenceLookupRepository.findByKeyUserId(userId).stream().filter(OriginReferenceLookup::getIsActive).toList();
        if(feedOriginReferences.isEmpty()) return List.of();

        //Group each reference via its post id
        Map<String, List<OriginReferenceLookup>> postReferenceMap = feedOriginReferences.stream().collect(Collectors.groupingBy(OriginReferenceLookup::getPostId));
        return userFeedPosts.stream().map(
                feed -> new FeedDisplay(feed, postReferenceMap.get(feed.getKey().getPostId())))
                .filter(display -> display.getRecipientReferences() != null && !display.getRecipientReferences().isEmpty()).toList();
    }

    public Stream<FeedDisplay> SortFeedByNearestReferenceDate(Stream<FeedDisplay> feed){
        return feed.sorted(Comparator.comparing(a ->
                Collections.max(a.getRecipientReferences().stream().map(OriginReferenceLookup::getReferenceDate).toList())));
    }

    //User Account Deletion - Remove All User Posts and Interactions from All User Feed
    public void handleUserAccountDeletion(UserNode user){

    }

    //User Unfollow - Remove Target User's Posts from User Feed
    public void removeTargetFromUserFeed(UserInteraction interaction){
        List<OriginReferenceLookup> targetReferences = referenceLookupRepository.findByKeyOriginatorUserIdAndKeyUserId(interaction.getUserId(), interaction.getTargetId());
        //todo: Add batch jobs
        targetReferences.forEach(reference -> {
            reference.setIsActive(false);
            referenceLookupRepository.save(reference);
        });
    }

    //User Follow - Add Followed User's Posts to User Feed
    public void addTargetPostsToUserFeed(UserInteraction interaction){
        PostCreationRelationship[] targetUsersPosts = nodeService.FetchUserPosts(interaction.getTargetId());
        if(targetUsersPosts.length == 0) return;

        //Add New Entry to User Feed
        List<UserFeed> generatedFeed = UserFeed.FromUserInteraction(interaction, targetUsersPosts);

        //Add a reference Lookup for each post
        List<OriginReferenceLookup> referenceLookups = OriginReferenceLookup.FromUserFollowRelation(targetUsersPosts, interaction);

        //Add New Entry to Post Lookup
        List<PostLookup> postLookups = Arrays.stream(targetUsersPosts).map(post -> new PostLookup(post.getPost())).toList();

        userFeedRepository.saveAll(generatedFeed);
        referenceLookupRepository.saveAll(referenceLookups);
        postLookupRepository.saveAll(postLookups);
    }

    //Post Creation - Add Post To Recipient's Feed
    public void addPostToAllRecipientFeed(PostNode post){
        FeedRecipient[] postRecipients = nodeService.getRelevantRecipients(post);
        if(postRecipients.length == 0) return;

        List<UserFeed> generatedFeed = Stream.concat(
                UserFeed.FromPost(post, postRecipients).stream(),
                Stream.of(new UserFeed(post))).toList();

        List<OriginReferenceLookup> generatedRecipientReferences = Stream.concat(
                OriginReferenceLookup.FromGeneratedRecipients(postRecipients, post).stream(),
                Stream.of(new OriginReferenceLookup(post))).toList();

        PostLookup postLookup = new PostLookup(post);

        userFeedRepository.saveAll(generatedFeed);
        referenceLookupRepository.saveAll(generatedRecipientReferences);
        postLookupRepository.save(postLookup);
    }

    //Post Deletion - Remove Post From All Recipients Feed
    public void removePostFromAllRecipients(PostNode postNode){
        //Find All References From Post
        List<PostLookup> postLookups = postLookupRepository.findByPostId(postNode.getPostId());

        //Find All Origin references from Post
        postLookups.forEach(postReference -> {
            List<OriginReferenceLookup> originReferences = referenceLookupRepository.findByKeyOriginatorUserIdAndKeyPostId(postReference.getOriginatorUserId(), postReference.getPostId());
            originReferences.forEach(reference -> {
                reference.setIsActive(false);
                referenceLookupRepository.save(reference);
            });
        });

        postLookupRepository.deleteAll(postLookups);
    }

    //Post Interaction - Update User Feed Status
    public void UserViewedPost(PostInteraction interaction){
        userFeedRepository.markPostAsRead(interaction.getUserId(), interaction.getPost().getPostId());
    }

    //Post Interaction - (ie. Like, Repost,etc) Add Interaction to other User's Feed
    public void UserInteractedWithPost(PostInteraction interaction, PostInteractionEnums.PostInteractionEnum action){
        // Check if user interacted with his own post (fucking weirdo)
        if(interaction.getUserId().equals(interaction.getPost().getUserId())) return;

        // Determine if relevant interaction can be shared publicly through user permissions
        if(action != PostInteractionEnums.PostInteractionEnum.REPOST){
            //todo: Check User Preferences to determine post interaction visibility
        }

        // Retrieve relevant post recipients (And exclude original post creator)
        FeedRecipient[] postRecipients = Arrays.stream(nodeService.getRelevantRecipients(interaction, action))
                .filter(recipient -> !interaction.getPost().getUserId().equals(recipient.getUser().getUserId())).toArray(FeedRecipient[]::new);

        if(postRecipients.length == 0) return;

        List<OriginReferenceLookup> newPostRecipient = OriginReferenceLookup.FromGeneratedRecipients(postRecipients, interaction.getPost());
        List<UserFeed> newFeed = UserFeed.FromPost(interaction.getPost(), postRecipients);
        PostLookup postLookup = new PostLookup(interaction);

        referenceLookupRepository.saveAll(newPostRecipient);
        userFeedRepository.saveAll(newFeed);
        postLookupRepository.save(postLookup);
    }

    //Removing Post Interaction - Remove All references from User's Feed
    public void RemoveUserInteractionFeedRecipients(PostInteraction interaction, PostInteractionEnums.PostInteractionEnum action){
        //Find Users references to the post

        if(interaction.getPost().getUserId().equals(interaction.getUserId())) return;

        List<OriginReferenceLookup> postActionReference = referenceLookupRepository.
                findByKeyOriginatorUserIdAndKeyPostIdAndKeyPostReferenceType(
                        interaction.getUserId(),
                        interaction.getPost().getPostId(),
                        FeedRecipient.RecipientType.FromPostAction(action).getValue());

        postActionReference.forEach(reference -> {
            reference.setIsActive(false);
            referenceLookupRepository.save(reference);
        });

    }
}
