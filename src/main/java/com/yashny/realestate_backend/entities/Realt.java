package com.yashny.realestate_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "realts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Realt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "images")
    @CollectionTable(name = "realt_images")
    @ElementCollection
    private List<String> images;
}
