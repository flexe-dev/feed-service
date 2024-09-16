package com.flexe.feedservice.Entity.enums;

import java.util.Set;

public class UserInteractionEnums {

    public static enum UserNodeModificationEnum{
        SAVE,
        DELETE
    }

    public static enum UserInteractionEnum{
        FOLLOW,
        UNFOLLOW,
        VIEW,
        BLOCK
    }

    public static final Set<UserInteractionEnum> FeedServiceRelevantUserActions = Set.of(
            UserInteractionEnum.BLOCK,
            UserInteractionEnum.FOLLOW,
            UserInteractionEnum.UNFOLLOW
    );

    public static final Set<UserNodeModificationEnum> FeedServiceRelevantUserNodeActions = Set.of(
            UserNodeModificationEnum.DELETE
    );
}

