package com.yashny.realestate_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_codes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String code;
}
