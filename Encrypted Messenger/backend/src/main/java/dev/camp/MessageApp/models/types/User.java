package dev.camp.MessageApp.models.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @JsonIgnore
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    public Long id;

    public String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String password;

    @Column(length = 1000)
    public String pubkey;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String pin;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Boolean verified;

    @JsonIgnore
    public LocalDateTime verificationDeadline;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String role;

    public User() {}
}