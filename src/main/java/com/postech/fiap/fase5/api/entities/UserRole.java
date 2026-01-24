package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role", schema = "public")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "user_role_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

}
