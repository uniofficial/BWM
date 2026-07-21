package com.bwm.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_uuid", nullable = false, unique = true, length = 36)
    private String userUuid;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 45)
    private String nickname;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id")
    )
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @PrePersist
    public void generateUuid() {
        if (this.userUuid == null) {
            this.userUuid = java.util.UUID.randomUUID().toString();
        }
    }
}
