package com.yashny.realestate_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @Column(name = "price")
    private int price;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private Type type;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "dealType_id")
    private DealType dealType;
    @Column(name = "roomsCount")
    private int roomsCount;
    @Column(name = "country")
    private String country;
    @Column(name = "city")
    private String city;
    @Column(name = "street")
    private String street;
    @Column(name = "house")
    private String house;
    @Column(name = "article", columnDefinition = "text")
    private String article;
    @Column(name = "area")
    private int area;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "views", columnDefinition = "bigint default 0")
    private Long views;
    @Column(name = "reposts", columnDefinition = "bigint default 0")
    private Long reposts;
    @Column(name = "likes", columnDefinition = "bigint default 0")
    private Long likes;
    private LocalDateTime dateOfCreated;
    private Long repair;
    private Long floor;

    @PrePersist
    private void onCreate() {
        dateOfCreated = LocalDateTime.now();
    }
}
