package ru.biscuitka.allocator.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.application.Application;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.resource.Resource;

@Getter
@Setter
@Entity
@Table(name = "resource_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_request_id")
    Long id;

    @Column(name = "resource_request_status")
    @Enumerated(EnumType.STRING)
    ResourceStatus status;

    @Column(name = "resource_request_quantity")
    Long quantity;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    Resource resource;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    @ManyToOne
    @JoinColumn(name = "application_id")
    Application application;
}
