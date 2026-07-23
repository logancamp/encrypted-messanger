package edu.cwru.messaging.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cwru.messaging.utils.ApiClient;
import edu.cwru.messaging.utils.CryptoUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.http.HttpResponse;


public class UserModel {
    // This allows you to use this list directly within the Controller
    private final ObservableList<User> users = FXCollections.observableArrayList();

    private void loadUsers() {
        String credentials = ApiClient.getAuthHeader();

        try {
            HttpResponse<String> response = ApiClient.get("http://localhost:8080/users", credentials);

            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());

            for (com.fasterxml.jackson.databind.JsonNode node : root) {
                String username = node.get("username").asText();
                String pubkey = node.get("pubkey").asText();
                users.add(new User(null, username, pubkey));
            }
        }
        catch(Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

    }

    public UserModel() {
        loadUsers();
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public User findUserByUsername(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                return u;
            }
        }
        return null;
    }
}
