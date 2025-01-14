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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dealType_id_seq")
    @SequenceGenerator(name = "dealType_id_seq", sequenceName = "dealType_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    private String dealTypeName;
}
