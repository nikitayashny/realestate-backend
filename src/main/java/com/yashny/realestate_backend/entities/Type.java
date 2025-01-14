package com.yashny.realestate_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_id_seq")
    @SequenceGenerator(name = "type_id_seq", sequenceName = "type_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    private String typeName;
}
