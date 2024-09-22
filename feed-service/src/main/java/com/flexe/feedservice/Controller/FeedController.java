package com.flexe.feedservice.Controller;

import com.flexe.feedservice.Entity.Feed.FeedDisplay;
import com.flexe.feedservice.Entity.Feed.UserFeed;
import com.flexe.feedservice.Service.PostFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:8080"})
@RestController
@RequestMapping("api/feed")
public class FeedController {

    @Autowired
    private PostFeedService postFeedService;

    @GetMapping("/feed/{userId}")
    public List<FeedDisplay> GetUserFeed(@PathVariable String userId){
        return postFeedService.getUserFeed(userId);
    }

}
