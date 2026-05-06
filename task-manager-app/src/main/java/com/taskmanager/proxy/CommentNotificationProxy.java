package com.taskmanager.proxy;

import com.taskmanager.model.Comment;

/**
 * In standard real-world applications, when implementing objects whose responsibility is
 * to establish communication with something outside the app, we name these objects
 * proxies, so let’s name the object whose responsibility is sending the email CommentNotificationProxy.
 */
public interface CommentNotificationProxy {
    void sendComment(Comment comment);
}
