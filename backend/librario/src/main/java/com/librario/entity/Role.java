package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. ROLE_MEMBER, ROLE_ADMIN
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}
