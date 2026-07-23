package edu.cwru.messaging.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class User {
    public Long id;
    public String username, emailAddress, pubkey;

    public User(Long id, String username, String pubkey) {
        this.id = id;
        this.username = username;
        this.pubkey = pubkey;
    }

    // This is really important as this is what will show in the list
    @Override
    public String toString() {
        return username;
    }
}
