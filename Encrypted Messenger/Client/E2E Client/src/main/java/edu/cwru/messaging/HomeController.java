package edu.cwru.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cwru.messaging.model.User;
import edu.cwru.messaging.model.UserModel;
import edu.cwru.messaging.utils.ApiClient;
import edu.cwru.messaging.utils.CryptoUtility;

import atlantafx.base.controls.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;

import java.net.http.HttpResponse;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private final UserModel userModel = new UserModel();
    private List<edu.cwru.messaging.model.Message> allMessages = new ArrayList<>();

    @FXML private ListView<User> contactListView;
    @FXML private ListView<Message> messageListView;

    @FXML private TextField usernameField;
    @FXML private TextField messageToSend;


    public HomeController() {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String credentials = ApiClient.getAuthHeader();

        try {
            HttpResponse<String> response = ApiClient.get("http://localhost:8080/securemsg", credentials);
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode jsonTree = mapper.readTree(response.body());

                List<edu.cwru.messaging.model.Message> parsed = new ArrayList<>();
                for (com.fasterxml.jackson.databind.JsonNode node : jsonTree) {
                    String fromUsername = node.get("from").get("username").asText();
                    String toUsername = node.get("to").get("username").asText();

                    PrivateKey privateKey = CryptoUtility.loadPrivateKey(credentials.split(":")[0]);
                    String msgText = CryptoUtility.decrypt(privateKey, node.get("msg").asText());

                    User fromUser = userModel.findUserByUsername(fromUsername);
                    User toUser = userModel.findUserByUsername(toUsername);

                    edu.cwru.messaging.model.Message message = new edu.cwru.messaging.model.Message();
                    message.from = fromUser;
                    message.to = toUser;
                    message.msg = msgText;

                    parsed.add(message);
                }
                allMessages = parsed;
            }
        }
        catch(Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        contactListView.setItems(userModel.getUsers());
        contactListView.setOnMouseClicked(mouseEvent -> {
            loadConversation();
        });
    }

    private void loadConversation() {
        // Show the detail of the password
        int index = contactListView.getSelectionModel().getSelectedIndex();

        // Load the user
        User user = userModel.getUsers().get(index);
        usernameField.setDisable(true);
        usernameField.setText(user.username);

        // You have the index of the conversation, use this to load the conversation in the main view
        messageListView.getItems().clear();
        for (var message : allMessages) {
            if (message.from != null && message.from.username.equals(user.username)) {
                messageListView.getItems().add(new Message(message.from.username, message.msg));
            }
        }
    }


    @FXML
    protected void newMessage() {
        usernameField.clear();
        usernameField.setDisable(false);
        messageListView.getItems().clear();
        messageToSend.clear();
    }

    @FXML
    protected void sendMessage() {
        String credentials = ApiClient.getAuthHeader();
        String recipientUsername = usernameField.getText();
        String msg = messageToSend.getText();

        User recipient = userModel.findUserByUsername(recipientUsername);
        User sender = userModel.findUserByUsername(credentials.split(":")[0]);

        try {
            PublicKey recipientPublicKey = CryptoUtility.publicKeyFromString(recipient.pubkey);
            String encryptedMsg = CryptoUtility.encrypt(recipientPublicKey, msg);

            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
            com.fasterxml.jackson.databind.node.ObjectNode to = mapper.createObjectNode();
            to.put("username", recipientUsername);
            root.set("to", to);
            root.put("msg", encryptedMsg);

            String jsonBody = mapper.writeValueAsString(root);

            HttpResponse<String> response = ApiClient.post("http://localhost:8080/securemsg", jsonBody, credentials);
            if (response.statusCode() == 200) {
                edu.cwru.messaging.model.Message sentMessage = new edu.cwru.messaging.model.Message();
                sentMessage.from = sender;
                sentMessage.to = recipient;
                sentMessage.msg = msg;
                allMessages.add(sentMessage);

                messageListView.getItems().add(new Message(credentials.split(":")[0], msg));
                messageToSend.clear();
            }
        }
        catch(Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

}
