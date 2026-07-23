package com.bwm.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userrole")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Integer userRoleId;

    @Column(name = "user_role", nullable = false, length = 45, unique = true)
    private String userRole;
}
