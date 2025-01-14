package com.yashny.realestate_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dealTypes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private String dealTypeName;
}
