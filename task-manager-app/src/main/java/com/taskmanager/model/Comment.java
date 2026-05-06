package com.taskmanager.model;

import com.taskmanager.repository.CommentRepository;

/**
 * we’ll also have to represent the comment somehow.
 * The responsibility of this type of object is
 * simply to model the data the app uses, and we call it model.
 *
 * A POJO is a simple object without dependencies, only described by its
 * attributes and methods.We just need to
 * write a small POJO class for defining the comment.
 *
 * I’ll consider a comment
 * that has two attributes: a text and an author.
 *
 *
 */
public class Comment {
    private String author;
    private String text;


    // getters and setters
    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }
}
