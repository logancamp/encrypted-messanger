package dev.camp.MessageApp.services;

import dev.camp.MessageApp.models.UserRepository;
import dev.camp.MessageApp.models.types.Message;
import dev.camp.MessageApp.models.MessageRepository;
import dev.camp.MessageApp.models.types.User;
import dev.camp.MessageApp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/securemsg")
public class MessagesService {
    @Value("${app.key.password}")
    private String password;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessagesService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public void save(@RequestBody Message message, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        }
        Long from_id = principal.getId();
        User toUser = userRepository.findByUsername(message.to.username);

        message.createdAt = LocalDateTime.now();
        message.from = new User();
        message.from.id = from_id;
        message.to = toUser;

        this.messageRepository.save(message);
    }

    @GetMapping
    public List<Message> findAllByOrderByIdAsc(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        }
        String username = principal.getUsername();

        return this.messageRepository.findAllByToUsernameOrderByIdAsc(username, username);
    }

}