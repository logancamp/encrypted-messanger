package dev.camp.MessageApp.models.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="messages")
public class Message {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(length = 1000)
    public String msg;
    public LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    public User from;

    @ManyToOne
    @JoinColumn(nullable = false)
    public User to;
}
