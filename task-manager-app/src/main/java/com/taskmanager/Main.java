package com.taskmanager;

import com.taskmanager.model.Comment;
import com.taskmanager.proxy.CommentNotificationProxy;
import com.taskmanager.proxy.EmailCommentNotificationProxy;
import com.taskmanager.repository.CommentRepository;
import com.taskmanager.repository.DBCommentRepository;
import com.taskmanager.service.CommentService;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        var commentRepository = new DBCommentRepository();
        var commentNotificationProxy = new EmailCommentNotificationProxy();
        var commentService = new CommentService(commentRepository, commentNotificationProxy);

        var comment = new Comment();
        comment.setAuthor("Ayush Chachan");
        comment.setText("Demo Comment");
        commentService.publishComment(comment);

        System.out.println("Hello World!");
    }
}
