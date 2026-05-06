package com.taskmanager.repository;

import com.taskmanager.model.Comment;

/**
 * When we have an object working directly with a database, we generally name such
 * an object repository. Let’s name the object that implements the storing comment
 * responsibility CommentRepository.
 */
public interface CommentRepository {
    void storeComment(Comment comment);
}
