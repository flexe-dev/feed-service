package com.flexe.feedservice.Service;

import com.flexe.feedservice.Configuration.HttpService;
import com.flexe.feedservice.Entity.Feed.*;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.Nodes.UserNode;
import com.flexe.feedservice.Repository.PostFeedReferenceRepository;
import com.flexe.feedservice.Repository.UserFeedRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostFeedService {

    @Autowired
    private HttpService httpService;

    @Autowired
    private UserFeedRepository userFeedRepository;

    private WebClient interactionWebClient;
    @Autowired
    private PostFeedReferenceRepository postFeedReferenceRepository;

    @PostConstruct
    public void init(){
        this.interactionWebClient = httpService.generateWebClient(HttpService.TargetController.NODE);
    }

    //Get Users Feed From The Past 30 Days
    public List<FeedDisplay> getUserFeed(String userId){
        List<UserFeed> userFeedPosts = userFeedRepository.findByKeyUserId(userId);
        List<PostFeedReference> postReferences = postFeedReferenceRepository.findByUserId(userId);
        //Do stuff
        return null;
    }

    public Long getDaysAgo(Long daysAgo){
        return daysAgo * 24 * 60 * 60 * 1000;
    }

    public void handleUserAccountDeletion(UserNode user){
        postFeedReferenceRepository.deleteByKeyOriginatorUserId(user.getUserId());
    }

    public void removeTargetFromUserFeed(UserInteraction interaction){
        //Find All Instances where the originator was the unfollowed target
        List<PostFeedReference> targetUserReferences = postFeedReferenceRepository.findByKeyOriginatorUserIdAndUserId(interaction.getUserId(), interaction.getTargetId());

        postFeedReferenceRepository.deleteAll(targetUserReferences);
        //Remove all posts from the target user
        targetUserReferences.forEach(ref -> {
            userFeedRepository.deleteUserFeedByKeyUserIdAndKeyPostDateAndKeyPostId(ref.getUserId(), ref.getPostDate(), ref.getKey().getPostId());
            }
        );
    }

    //This Will Add all recent Posts from the Target to the User's Feed
    public void addTargetPostsToUserFeed(UserInteraction interaction){
        PostCreationRelationship[] targetUsersPosts = FetchUserPosts(interaction.getTargetId());

        //Generate Feed From Posts
        List<UserFeed> generatedFeed = UserFeed.FromPostNodes(interaction.getUserId(), targetUsersPosts);

        //Add User as a Recipient to all of the posts
        List<PostFeedReference> recipient = PostFeedReference.FromUserFollowRelation(targetUsersPosts, interaction);

        userFeedRepository.saveAll(generatedFeed);
        postFeedReferenceRepository.saveAll(recipient);
    }

    public PostCreationRelationship[] FetchUserPosts(String userId){
        ResponseEntity<PostCreationRelationship[]> response = httpService.get(interactionWebClient, "/node/post/user/" + userId, PostCreationRelationship[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed to get user post from post service");
        }

        //Filter Posts to Only Include Posts From The Past 30 Days
        return Arrays.stream(response.getBody())
                .filter(p -> p.getCreatedAt().after(new Date(System.currentTimeMillis() - getDaysAgo(30L))))
                .toArray(PostCreationRelationship[]::new);
    }

    public void addPostToAllRecipientFeed(PostNode post){
        //Retrieve All Appropriate Recipients for this post
        ResponseEntity<FeedRecipient[]> response = httpService.post(interactionWebClient, "/node/post/feed/recipients", post, FeedRecipient[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed To Get Relevant Post Recipients");
        }

        FeedRecipient[] postRecipients = response.getBody();

        List<UserFeed> generatedFeed = Stream.concat(
                UserFeed.FromRecipientResponse(post, postRecipients).stream(),
                Stream.of(new UserFeed(post))
        ).toList();

        List<PostFeedReference> generatedRecipientReferences = Stream.concat(
                PostFeedReference.FromGeneratedRecipients(postRecipients, post).stream(),
                Stream.of(new PostFeedReference(post))).toList();

        userFeedRepository.saveAll(generatedFeed);
        postFeedReferenceRepository.saveAll(generatedRecipientReferences);

    }

    public void removePostFromAllRecipients(PostNode postNode){
        //Remove Post From All Recipients
        List<PostFeedReference> postReferences = postFeedReferenceRepository.findByKeyOriginatorUserIdAndKeyPostId(postNode.getUserId(), postNode.getPostId());
        postReferences.forEach(ref -> userFeedRepository.
                deleteUserFeedByKeyUserIdAndKeyPostDateAndKeyPostId(ref.getUserId(), ref.getPostDate(), ref.getKey().getPostId()));

        postFeedReferenceRepository.deleteByKeyOriginatorUserIdAndKeyPostId(postNode.getUserId(), postNode.getPostId());
    }

    public void UserViewedPost(PostInteraction interaction){
        userFeedRepository.markPostAsRead(interaction.getUserId(), interaction.getPost().getPostDate(), interaction.getPost().getPostId());
    }

    public void UserInteractedWithPost(PostInteraction interaction, PostInteractionEnums.PostInteractionEnum action){
        if(action != PostInteractionEnums.PostInteractionEnum.REPOST){
            //todo: Check User Preferences to determine post interaction visibility
        }

        //Get relevant recipients
        ResponseEntity<FeedRecipient[]> response = httpService.post(interactionWebClient,
                "/node/post/interaction/" + action.name().toLowerCase() + "/recipients",
                interaction.getPost(), FeedRecipient[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed To Get Relevant Post Recipients");
        }

        //Filter out the post creator from the recipients
        FeedRecipient[] postRecipients = Arrays.stream(response.getBody())
                .filter(recipient -> !interaction.getPost().getUserId().equals(recipient.getUser().getUserId())).toArray(FeedRecipient[]::new);

        //Add new recipients to the post
        List<PostFeedReference> newPostRecipient = PostFeedReference.FromGeneratedRecipients(postRecipients, interaction.getPost());
        List<UserFeed> newFeed = UserFeed.FromRecipientResponse(interaction.getPost(), postRecipients);

        postFeedReferenceRepository.saveAll(newPostRecipient);
        userFeedRepository.saveAll(newFeed);
    }

    //Remove a Post from Relevant Users feed who only received it from a prior interaction (ie. Liking, Reposting)
    public void RemoveUserInteractionFeedRecipients(PostInteraction interaction){
        postFeedReferenceRepository.deleteByKeyOriginatorUserIdAndKeyPostId(interaction.getUserId(), interaction.getPost().getPostId());
    }
}
