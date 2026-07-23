package edu.cwru.messaging.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ApiClient {

    // Get/Set headers for running session after login
    static private String authHeader;
    static public void setAuthHeader(String authHeader) {
        ApiClient.authHeader = authHeader;
    }
    static public String getAuthHeader() {
        return ApiClient.authHeader;
    }


    // API Calls
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    // Universal Get
    static public HttpResponse<String> get(String uri, String header) throws IOException, InterruptedException {
        String encodedCredentials = Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Basic " + encodedCredentials)
                .GET()
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // Universal Post
    static public HttpResponse<String> post(String uri, String body, String header) throws IOException, InterruptedException {
        String encodedCredentials = Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // User Post + Verification Patch
    static public HttpResponse<String> postNoAuth(String uri, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    static public HttpResponse<String> patch(String uri, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
