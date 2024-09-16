package com.flexe.feedservice.Entity.enums;

import java.util.EnumSet;
import java.util.Set;

public class PostInteractionEnums {

    public static enum PostNodeModificationEnum{
        SAVE,
        DELETE
    }

    public static enum PostInteractionEnum{
        LIKE,
        UNLIKE,
        SAVE,
        UNSAVE,
        SHARE,
        COMMENT,
        VIEW,
        REPOST,
        UNREPOST
    }

    public static final Set<PostInteractionEnum> FeedServiceRelevantUserActions = Set.of(
            PostInteractionEnum.LIKE,
            PostInteractionEnum.UNLIKE,
            PostInteractionEnum.VIEW,
            PostInteractionEnum.REPOST
    );
}
