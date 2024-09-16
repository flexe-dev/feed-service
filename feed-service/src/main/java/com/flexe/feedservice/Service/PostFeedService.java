package com.flexe.feedservice.Service;

import com.flexe.feedservice.Configuration.HttpService;
import com.flexe.feedservice.Entity.Feed.*;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.Nodes.UserNode;
import com.flexe.feedservice.Repository.PostRecipientRepository;
import com.flexe.feedservice.Repository.UserFeedRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class PostFeedService {

    @Autowired
    private HttpService httpService;

    @Autowired
    private UserFeedRepository userFeedRepository;

    private WebClient interactionWebClient;
    @Autowired
    private PostRecipientRepository postRecipientRepository;

    @PostConstruct
    public void init(){
        this.interactionWebClient = httpService.generateWebClient(HttpService.TargetController.NODE);
    }

    //Get Users Feed From The Past 30 Days
    public List<UserFeed> getUserFeed(String userId, Long daysAgo){
        //Get Date 30 Days Ago
        Date startDate = new Date(System.currentTimeMillis() - getDaysAgo(daysAgo));
        Date endDate = new Date();

        return userFeedRepository.findByUserIdAndDateAddedBetween(userId, startDate, endDate);
    }

        public Long getDaysAgo(Long daysAgo){
        return daysAgo * 24 * 60 * 60 * 1000;
    }

    public void handleUserAccountDeletion(UserNode user){

    }

    public void removeTargetsPostsFromUserFeed(UserInteraction interaction){
        PostCreationRelationship[] targetUsersPosts = FetchUserPosts(interaction.getTargetId());

        List<FeedKey> feedKeys = Arrays.stream(targetUsersPosts)
                .map(post -> new FeedKey(post, interaction.getUserId()))
                .toList();

        List<RecipientKey> recipientKeys = Arrays.stream(targetUsersPosts)
                .map(post -> new RecipientKey(post.getPost(), interaction.getUserId()))
                .toList();

        userFeedRepository.deleteAllById(feedKeys);
        postRecipientRepository.deleteAllById(recipientKeys);
    }

    //This Will Add all of a Target User's Posts to the User's Feed
    public void addTargetPostsToUserFeed(UserInteraction interaction){
        PostCreationRelationship[] targetUsersPosts = FetchUserPosts(interaction.getTargetId());

        //Generate Feed From Posts
        List<UserFeed> generatedFeed = UserFeed.FromPostNodes(interaction.getUserId(), targetUsersPosts);

        //Add User as a Recipient to all of the posts
        List<PostRecipient> recipient = PostRecipient.FromUserFollowRelation(targetUsersPosts, interaction);

        postRecipientRepository.saveAll(recipient);
        userFeedRepository.saveAll(generatedFeed);
    }

    public PostCreationRelationship[] FetchUserPosts(String userId){
        ResponseEntity<PostCreationRelationship[]> response = httpService.get(interactionWebClient, "/node/post/user/" + userId, PostCreationRelationship[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed to get posts from post service");
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

        List<UserFeed> generatedFeed = UserFeed.FromRecipientResponse(post, response.getBody());
        generatedFeed.add(new UserFeed(post));

        List<PostRecipient> generatedRecipients = PostRecipient.FromGeneratedRecipients(response.getBody(), post);
        generatedRecipients.add(new PostRecipient(post, post.getUserId()));

        userFeedRepository.saveAll(generatedFeed);
        postRecipientRepository.saveAll(generatedRecipients);
    }


    public void removePostFromAllRecipients(PostNode postNode){
        //Find All Recipients of the Post
        List<PostRecipient> recipients = postRecipientRepository.findByKeyPostId(postNode.getPostId());
        //Remove From Feed table
        List<FeedKey> recipientFeedKeys = FeedKey.FromRecipient(postNode, recipients);
        userFeedRepository.deleteAllById(recipientFeedKeys);
        //Remove From Recipients Table
        postRecipientRepository.deleteAll(recipients);
    }

    public void UserViewedPost(PostInteraction interaction){
        userFeedRepository.markPostAsRead(interaction.getUserId(), interaction.getPost().getPostId());
    }

    public void UserInteractedWithPost(PostInteraction interaction, PostInteractionEnums.PostInteractionEnum action){
        //Get relevant recipients
        ResponseEntity<FeedRecipient[]> response = httpService.post(interactionWebClient,
                "/node/post/interaction/" + action.name().toLowerCase() + "/recipients",
                interaction.getPost(), FeedRecipient[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed To Get Relevant Post Recipients");
        }

        //Filter out users who have already been a recipient of this post
        List<PostRecipient> existingRecipients = postRecipientRepository.findByKeyPostId(interaction.getPost().getPostId());
        List<FeedRecipient> newPostRecipients = Arrays.stream(response.getBody())
                .filter(recipient -> existingRecipients.stream().noneMatch(r -> r.getKey().getUserId().equals(recipient.getUser().getUserId())))
                .toList();

        //Add new recipients to the post
        List<PostRecipient> newPostRecipient = PostRecipient.FromGeneratedRecipients(newPostRecipients.toArray(new FeedRecipient[0]), interaction.getPost());
        List<UserFeed> newFeed = UserFeed.FromRecipientResponse(interaction.getPost(), newPostRecipients.toArray(new FeedRecipient[0]));
        postRecipientRepository.saveAll(newPostRecipient);
        userFeedRepository.saveAll(newFeed);
    }

    //Remove a Post from Relevant Users feed who only received it from a prior interaction (ie. Liking, Reposting)
    public void RemoveUserInteractionFeedRecipients(PostInteraction interaction){
        List<PostRecipient> userSpecificRecipients = postRecipientRepository.findByKeyPostIdAndKeyOriginatorUserId(interaction.getPost().getPostId(), interaction.getUserId());
        List<FeedKey> feedKeys = FeedKey.FromRecipient(interaction.getPost(), userSpecificRecipients);

        userFeedRepository.deleteAllById(feedKeys);
        postRecipientRepository.deleteAll(userSpecificRecipients);
    }
}
