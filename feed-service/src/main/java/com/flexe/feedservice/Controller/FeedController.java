package com.flexe.feedservice.Controller;

import com.flexe.feedservice.Entity.Feed.FeedDisplay;
import com.flexe.feedservice.Entity.Feed.UserFeed;
import com.flexe.feedservice.Exceptions.FeedNotFoundException;
import com.flexe.feedservice.Service.PostFeedService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:8080"})
@RestController
@RequestMapping("api/feed")
public class FeedController {

    @Autowired
    private PostFeedService postFeedService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FeedDisplay>> GetUserFeed(@PathVariable String userId){
        List<FeedDisplay> feed = postFeedService.getUserFeed(userId);

        if(feed == null){
            throw new FeedNotFoundException("Error retrieving user feed");
        }

        return ResponseEntity.ok(feed);
    }

}
