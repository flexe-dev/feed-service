package com.flexe.feedservice.Service;

import com.flexe.feedservice.Configuration.HttpService;
import com.flexe.feedservice.Entity.Feed.FeedRecipient;
import com.flexe.feedservice.Entity.Nodes.PostNode;
import com.flexe.feedservice.Entity.enums.PostInteractionEnums;
import com.flexe.feedservice.Entity.interactions.PostInteraction;
import com.flexe.feedservice.Entity.relationships.CreationRelationship;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class NodeService {

    @Autowired
    private HttpService httpService;

    private WebClient interactionWebClient;

    @PostConstruct
    public void init(){
        this.interactionWebClient = httpService.generateWebClient(HttpService.TargetController.NODE);
    }

    public Long getDaysAgo(Long daysAgo){
        return daysAgo * 24 * 60 * 60 * 1000;
    }

    public List<CreationRelationship<PostNode>> FetchUserPosts(String userId){
        ResponseEntity<CreationRelationship<PostNode>[]> response = httpService
                .get(interactionWebClient, "/node/post/user/" + userId,
                        new ParameterizedTypeReference<CreationRelationship<PostNode>[]>(){});

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed to get user post from post service");
        }

        //Filter Posts to Only Include Posts From The Past 30 Days
        return Arrays.stream(response.getBody())
                .filter(post -> post.getCreatedAt().getTime() > new Date().getTime() - getDaysAgo(30L))
                .toList();
    }

    public FeedRecipient[] getRelevantRecipients(PostNode post){
        ResponseEntity<FeedRecipient[]> response = httpService.post(interactionWebClient, "/node/post/feed/recipients", post, FeedRecipient[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed To Get Relevant Post Recipients");
        }

        return response.getBody();
    }

    public FeedRecipient[] getRelevantRecipients(PostInteraction interaction, PostInteractionEnums.PostInteractionEnum action){
        ResponseEntity<FeedRecipient[]> response = httpService.post(interactionWebClient,
                "/node/post/interaction/" + action.name() + "/recipients",
                interaction, FeedRecipient[].class);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new IllegalArgumentException("Failed To Get Relevant Post Recipients");
        }

        return response.getBody();
    }

}
