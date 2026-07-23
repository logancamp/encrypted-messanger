package edu.cwru.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cwru.messaging.utils.ApiClient;
import edu.cwru.messaging.utils.CryptoUtility;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class InitialController implements Initializable {
    @FXML private Label pinLabel;
    @FXML private Button registerButton;
    @FXML private Button loginButton;

    @FXML private Text or;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField pinField;

    @FXML private Label error;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: if you need to do anything after the view loads, do it here
    }

    @FXML
    protected void loginButtonClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String credentials = username + ":" + password;

        try {
            HttpResponse<String> response = ApiClient.get("http://localhost:8080/users", credentials);

            if (response.statusCode() == 200) {
                ApiClient.setAuthHeader(credentials);
                navigateToMain();
            } else {
                error.setText("Authentication failed. Status: " + response.statusCode());
                error.setVisible(true);
            }
        }
        catch(Exception e) {
            error.setText(e.getMessage());
        }
    }

    private boolean awaitingPin = false;

    @FXML
    protected void registerButtonClicked() throws IOException, InterruptedException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String credentials = username + ":" + password;

        if (!awaitingPin) {
            loginButton.setVisible(false);
            or.setVisible(false);

            String pubKeyString;
            try {
                java.security.KeyPair keyPair = CryptoUtility.generateKeyPair(username);
                pubKeyString = CryptoUtility.keyToString(keyPair.getPublic());

            } catch (Exception e) {
                error.setText("Key generation failed");
                error.setVisible(true);
                return;
            }

            String email = username + "@gmail.com";

            ObjectMapper mapper = new ObjectMapper();
            var root = mapper.createObjectNode();
            root.put("username", username);
            root.put("email", email);
            root.put("password", password);
            root.put("pubkey", pubKeyString);

            String jsonBody = mapper.writeValueAsString(root);
            HttpResponse<String> response = ApiClient.postNoAuth("http://localhost:8080/users", jsonBody);

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                awaitingPin = true;
                registerButton.setText("Verify");

                pinLabel.setVisible(true);
                pinField.setVisible(true);
                pinField.setManaged(true);

                pinField.setPromptText("Enter your PIN");
            } else {
                error.setText("Registration failed: " + response.statusCode());
                error.setVisible(true);
            }
            return;
        }

        String pin = pinField.getText();

        ObjectMapper mapper = new ObjectMapper();
        var pinRoot = mapper.createObjectNode();
        pinRoot.put("pin", pin);
        String pinJson = mapper.writeValueAsString(pinRoot);

        HttpResponse<String> response = ApiClient.patch("http://localhost:8080/users/" + username, pinJson);

        if (response.statusCode() == 200) {
            ApiClient.setAuthHeader(credentials);
            navigateToMain();
        } else {
            error.setText("Verification failed: " + response.statusCode());
            error.setVisible(true);
        }
    }

    private void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            E2EClient.primaryStage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}