package com.flexe.feedservice.Consumer;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums.*;
import com.flexe.feedservice.Entity.enums.UserInteractionEnums;
import com.flexe.feedservice.Entity.enums.UserInteractionEnums.*;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import com.flexe.feedservice.Entity.interactions.UserInteraction;
import com.flexe.feedservice.Entity.Nodes.UserNode;
import com.flexe.feedservice.Service.PostFeedService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Autowired private PostFeedService postFeedService;

    @KafkaListener(topics = "user-node-action", groupId = "flexe-feed-service", containerFactory = "kafkaUserListenerContainerFactory")
    public void UserConsumer(ConsumerRecord<String, UserNode> userMessage) {
        UserNode user = userMessage.value();
        UserNodeModificationEnum action = UserNodeModificationEnum.valueOf(userMessage.key());

        if(!UserInteractionEnums.FeedServiceRelevantUserNodeActions.contains(action)) return;

        if(action == UserNodeModificationEnum.DELETE){
            postFeedService.handleUserAccountDeletion(user);
        }
    }

    @KafkaListener(topics = "post-node-action", groupId = "flexe-feed-service", containerFactory = "kafkaPostListenerContainerFactory")
    public void PostConsumer(ConsumerRecord<String, PostNode> postMessage){
        PostNode post = postMessage.value();
        PostNodeModificationEnum action = PostNodeModificationEnum.valueOf(postMessage.key());

        switch (action){
            case SAVE -> postFeedService.addPostToAllRecipientFeed(post);
            case DELETE -> postFeedService.removePostFromAllRecipients(post);
            default -> throw new IllegalArgumentException("Invalid Post Node Action");
        }
    }

    @KafkaListener(topics = "post-interaction", groupId = "flexe-feed-service", containerFactory = "kafkaPostInteractionListenerContainerFactory")
    public void PostInteractionConsumer(ConsumerRecord<String, PostInteraction> postInteractionMessage){
        PostInteraction interaction = postInteractionMessage.value();
        PostInteractionEnum action = PostInteractionEnum.valueOf(postInteractionMessage.key());

        if(!PostInteractionEnums.FeedServiceRelevantUserActions.contains(action)) return;

        switch (action){
            case VIEW -> postFeedService.UserViewedPost(interaction);
            case LIKE, REPOST -> postFeedService.UserInteractedWithPost(interaction, action);
            case UNLIKE, UNSAVE -> postFeedService.RemoveUserInteractionFeedRecipients(interaction);
            default -> throw new IllegalArgumentException("Invalid Post Interaction Action");
        }
    }

    @KafkaListener(topics = "user-interaction", groupId = "flexe-feed-service", containerFactory = "kafkaUserInteractionListenerContainerFactory")
    public void UserInteractionConsumer(ConsumerRecord<String, UserInteraction> userInteractionMessage){
        UserInteraction interaction = userInteractionMessage.value();
        UserInteractionEnum action = UserInteractionEnum.valueOf(userInteractionMessage.key());

        if(!UserInteractionEnums.FeedServiceRelevantUserActions.contains(action)) return;

        switch (action){
            case FOLLOW -> postFeedService.addTargetPostsToUserFeed(interaction);
            case UNFOLLOW, BLOCK -> postFeedService.removeTargetsPostsFromUserFeed(interaction);
            default -> throw new IllegalArgumentException("Invalid User Interaction Action");
        }

    }

}