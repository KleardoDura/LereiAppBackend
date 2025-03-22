package com.lerei.store.enums;

public enum PostStatus {
    DRAFT,            // The post is saved as a draft and not yet submitted for review.
    SUBMITTED,        // The post has been uploaded and is under review.

    REJECTED,         // The post did not pass review and was rejected.

    PUBLISHED,        // The post is live and visible to the audience.
    UNPUBLISHED,      // The post was published but has been taken down.
    ARCHIVED,         // The post is no longer active but is stored for reference or history.
    DELETED           // The post has been removed permanently.
}