package dev.camp.MessageApp.models;

import dev.camp.MessageApp.models.types.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByUsernameNotNullAndPubkeyNotNull();
}