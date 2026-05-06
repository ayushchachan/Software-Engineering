package com.taskmanager.service;

import com.taskmanager.model.Comment;
import com.taskmanager.proxy.CommentNotificationProxy;
import com.taskmanager.repository.CommentRepository;

/**
 * In standard real-world applications, we usually refer to the objects implementing
 * uses cases as services, and that’s what we’ll do here. We’ll need a service that
 * implements the “publish comment” use case. Let’s name this object CommentService.
 *
 * When analyzing the requirement again, we observe that the use case consists of
 * two actions: storing the comment and sending the comment by mail. As they are quite
 * different from one another, we consider these actions to be two different
 * responsibilities, and thus we need to implement two different objects.
 */
public class CommentService {

    private CommentRepository commentRepository;
    private CommentNotificationProxy commentNotificationProxy;

    public CommentService(CommentRepository commentRepository, CommentNotificationProxy commentNotificationProxy) {
        this.commentRepository = commentRepository;
        this.commentNotificationProxy = commentNotificationProxy;
    }

    public void publishComment(Comment comment) {
        this.commentRepository.storeComment(comment);
        this.commentNotificationProxy.sendComment(comment);
    }
}
