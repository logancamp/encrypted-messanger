package dev.camp.MessageApp.services;

import dev.camp.MessageApp.models.types.User;
import dev.camp.MessageApp.models.UserRepository;
import dev.camp.MessageApp.security.Roles;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("[a-zA-Z0-9_-]+");
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        if (!isValidUsername(user.username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username may only contain letters, numbers, underscores, and hyphens.");
        }

        if (userRepository.findByUsername(user.username) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid username or email");
        }

        if (userRepository.findByEmail(user.email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid username or email");
        }

        if (user.password.length() < 15) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password must be at least 15 characters long");
        }

        if (user.pubkey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Must provide a public key");
        }

        user.password = passwordEncoder.encode(user.password);
        user.role = Roles.USER;
        user.verified = false;
        user.verificationDeadline = LocalDateTime.now().plusMinutes(10);

        SecureRandom secureRandom = new SecureRandom();
        user.pin = "123456"; // temp for testing
//        user.pin = String.value0f(secureRandom.nextInt(100000, 999999));

        return userRepository.save(user);
    }

    @PatchMapping("/{username}")
    public User patchUser(@PathVariable String username, @RequestBody User user) {
        User existing = userRepository.findByUsername(username);
        String auth_pin = existing.pin;
        LocalDateTime verificationDeadline = existing.verificationDeadline;

        if (Objects.equals(user.pin, auth_pin) && LocalDateTime.now().isBefore(verificationDeadline)) {
            existing.verified = true;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid PIN");
        }

        return userRepository.save(existing);
    }

    @GetMapping
    public List<User> findAllByOrderByIdAsc() {
        return userRepository.findByUsernameNotNullAndPubkeyNotNull();
    }
}