package com.flexe.feedservice.Entity.interactions;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class PostInteraction {

    @NonNull
    private PostNode post;
    @NonNull
    private String userId;
    @Nullable
    private String targetId;


    public PostInteraction() {
    }

}
