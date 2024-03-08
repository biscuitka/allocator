package ru.biscuitka.allocator.resource;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "resources")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    Long id;

    @Column(name = "resource_name")
    String name;
}
