package edu.cwru.messaging.model;

import java.time.LocalDateTime;

// This is a similar message class for our Spring application
public class Message {
    public Long id;

    public User to;

    public User from;

    public String msg;
    public LocalDateTime creationDate;
}
