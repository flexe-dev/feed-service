package com.flexe.feedservice.Entity.interactions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
public class UserInteraction {
    @NonNull
    private String userId;
    @NonNull
    private String targetId;

    public UserInteraction() {
    }

}
