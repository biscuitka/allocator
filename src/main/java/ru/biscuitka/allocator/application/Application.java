package ru.biscuitka.allocator.application;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.users.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    Long id;

    @Column(name = "application_type")
    @Enumerated(EnumType.STRING)
    ApplicationType type;

    @Column(name = "application_status")
    @Enumerated(EnumType.STRING)
    ApplicationStatus status;

    @Column(name = "application_created")
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    User applicant;

}
